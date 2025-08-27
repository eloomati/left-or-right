// io/mhetko/lor/service/command/VoteCommandService.java
package io.mhetko.lor.service.command;

import io.mhetko.lor.entity.*;
import io.mhetko.lor.entity.enums.Side;
import io.mhetko.lor.exception.ResourceNotFoundException;
import io.mhetko.lor.kafka.VoteEventPublisher;
import io.mhetko.lor.repository.*;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteCommandService {

    private final VoteRepository voteRepository;
    private final VoteCountRepository voteCountRepository;
    private final TopicRepository topicRepository;
    private final AppUserRepository userRepository;
    private final ProposedTopicRepository proposedTopicRepository;
    private final VoteEventPublisher eventPublisher;

    // ----------------------------
    // PUBLIC API
    // ----------------------------

    @Transactional
    public void voteOnProposedTopic(Long userId, Long proposedTopicId, Side side) {
        voteGeneric(
                userId,
                () -> proposedTopicRepository.findById(proposedTopicId)
                        .orElseThrow(() -> new ResourceNotFoundException("ProposedTopic not found: " + proposedTopicId)),
                null,
                proposedTopicId,
                side,
                true
        );
    }

    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void vote(Long userId, Long topicId, Side side) {
        voteGeneric(
                userId,
                null,
                () -> topicRepository.findById(topicId)
                        .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId)),
                topicId,
                side,
                false
        );
    }

    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void unvote(Long userId, Long topicId) {
        Vote vote = voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found"));

        Side side = vote.getSide();
        vote.setIsDeleted(true);
        vote.setDeletedAt(LocalDateTime.now());
        voteRepository.save(vote); // preUpdate zaktualizuje updatedAt

        VoteCount vc = voteCountRepository.findByTopicId(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("VoteCount not found"));
        vc.decrement(side);
        voteCountRepository.save(vc);

        eventPublisher.publishRemoved(userId, topicId, side);
    }

    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void updateUserVote(Long userId, Long topicId, Side newSide) {
        Vote vote = voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found"));

        if (vote.getSide() == newSide) return;

        Side oldSide = vote.getSide();
        vote.setSide(newSide);
        voteRepository.save(vote);

        VoteCount vc = voteCountRepository.findByTopicId(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("VoteCount not found"));
        vc.decrement(oldSide);
        vc.increment(newSide);
        voteCountRepository.save(vc);

        eventPublisher.publishUpdated(userId, topicId, oldSide, newSide);
    }

    @Transactional
    public void removeAllVotesByUser(Long userId) {
        voteRepository.softDeleteByUserId(userId);
    }

    // ----------------------------
    // CORE
    // ----------------------------

    private void voteGeneric(Long userId,
                             Supplier<ProposedTopic> proposedTopicSupplier,
                             Supplier<Topic> topicSupplier,
                             Long refId,
                             Side side,
                             boolean isProposed) {

        AppUser user = getUserOrThrow(userId);

        Vote existingVote = findExistingVote(userId, refId, isProposed);
        if (existingVote != null) {
            // zmiana strony głosu
            if (existingVote.getSide() != side) {
                Side oldSide = existingVote.getSide();
                existingVote.setSide(side);
                voteRepository.save(existingVote);

                VoteCount vc = isProposed
                        ? getOrCreateVoteCountForProposed(refId)
                        : getOrCreateVoteCountForTopic(refId);
                vc.decrement(oldSide);
                vc.increment(side);
                voteCountRepository.save(vc);

                // zdarzenia tylko dla Topic (jak wcześniej)
                if (!isProposed) {
                    eventPublisher.publishUpdated(userId, refId, oldSide, side);
                }
            }
            return;
        }

// nowy głos
        if (isProposed) {
            ProposedTopic proposedTopic = proposedTopicSupplier.get();
            Vote vote = Vote.forProposed(user, proposedTopic, side);
            voteRepository.save(vote);

            // Zwiększ popularityScore
            Integer score = proposedTopic.getPopularityScore();
            proposedTopic.setPopularityScore(score == null ? 1 : score + 1);
            proposedTopicRepository.save(proposedTopic);

            VoteCount vc = voteCountRepository.findByProposedTopicId(proposedTopic.getId())
                    .orElseGet(() -> voteCountRepository.save(VoteCount.forProposed(proposedTopic)));
            vc.increment(side);
            voteCountRepository.save(vc);
        } else {
            Topic topic = topicSupplier.get();
            Vote vote = Vote.forTopic(user, topic, side);
            voteRepository.save(vote);

            // Zwiększ popularityScore dla Topic
            Integer score = topic.getPopularityScore();
            topic.setPopularityScore(score == null ? 1 : score + 1);
            topicRepository.save(topic);

            VoteCount vc = voteCountRepository.findByTopicId(topic.getId())
                    .orElseGet(() -> voteCountRepository.save(VoteCount.forTopic(topic)));
            vc.increment(side);
            voteCountRepository.save(vc);

            eventPublisher.publishCreated(user.getId(), topic.getId(), side);
        }
    }

    // ----------------------------
    // Helpery
    // ----------------------------

    private AppUser getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private Vote findExistingVote(Long userId, Long refId, boolean isProposed) {
        return isProposed
                ? voteRepository.findByUserIdAndProposedTopicIdAndIsDeletedFalse(userId, refId).orElse(null)
                : voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, refId).orElse(null);
    }

    private VoteCount getOrCreateVoteCountForTopic(Long topicId) {
        return voteCountRepository.findByTopicId(topicId)
                .orElseGet(() -> {
                    Topic t = topicRepository.findById(topicId)
                            .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));
                    return voteCountRepository.save(VoteCount.forTopic(t));
                });
    }

    private VoteCount getOrCreateVoteCountForProposed(Long proposedId) {
        return voteCountRepository.findByProposedTopicId(proposedId)
                .orElseGet(() -> {
                    ProposedTopic p = proposedTopicRepository.findById(proposedId)
                            .orElseThrow(() -> new ResourceNotFoundException("ProposedTopic not found: " + proposedId));
                    return voteCountRepository.save(VoteCount.forProposed(p));
                });
    }
}
