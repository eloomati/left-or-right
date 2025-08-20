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

    private static final int MAX_RETRY_ATTEMPTS = 3;

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
    public void vote(Long userId, Long topicId, Side side) {
        retryable(() -> voteGeneric(
                userId,
                null,
                () -> topicRepository.findById(topicId)
                        .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId)),
                topicId,
                side,
                false
        ));
    }

    private void voteGeneric(Long userId,
                             Supplier<ProposedTopic> proposedTopicSupplier,
                             Supplier<Topic> topicSupplier,
                             Long refId,
                             Side side,
                             boolean isProposed) {
        AppUser user = getUserOrThrow(userId);

        Vote existingVote = findExistingVote(userId, refId, isProposed);
        if (existingVote != null) {
            if (existingVote.getSide() != side) {
                updateVoteSide(existingVote, side, refId, isProposed);
            }
            return;
        }

        if (isProposed) {
            ProposedTopic proposedTopic = proposedTopicSupplier.get();
            createVoteForProposed(user, proposedTopic, side);
        } else {
            Topic topic = topicSupplier.get();
            createVoteForTopic(user, topic, side);
        }
    }

    private AppUser getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private Vote findExistingVote(Long userId, Long refId, boolean isProposed) {
        return isProposed
                ? voteRepository.findByUserIdAndProposedTopicIdAndIsDeletedFalse(userId, refId).orElse(null)
                : voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, refId).orElse(null);
    }

    private void updateVoteSide(Vote vote, Side newSide, Long refId, boolean isProposed) {
        Side oldSide = vote.getSide();
        vote.setSide(newSide);
        vote.setUpdatedAt(LocalDateTime.now());
        voteRepository.save(vote);

        VoteCount vc = isProposed
                ? voteCountRepository.findByProposedTopicId(refId).orElseThrow()
                : voteCountRepository.findByTopicId(refId).orElseThrow();
        vc.decrement(oldSide);
        vc.increment(newSide);
        voteCountRepository.save(vc);
    }

    private void createVoteForProposed(AppUser user, ProposedTopic proposedTopic, Side side) {
        Vote vote = new Vote();
        vote.setUser(user);
        vote.setProposedTopic(proposedTopic);
        vote.setSide(side);
        vote.setIsDeleted(false);
        vote.setDeletedAt(null);
        vote.setUpdatedAt(LocalDateTime.now());
        voteRepository.save(vote);

        VoteCount vc = voteCountRepository.findByProposedTopicId(proposedTopic.getId())
                .orElseGet(() -> {
                    VoteCount newVC = new VoteCount();
                    newVC.setProposedTopic(proposedTopic);
                    newVC.setLeftCount(0);
                    newVC.setRightCount(0);
                    return voteCountRepository.save(newVC);
                });
        vc.increment(side);
        voteCountRepository.save(vc);

        proposedTopic.setPopularityScore(proposedTopic.getPopularityScore() + 1);
        proposedTopicRepository.save(proposedTopic);
    }

    private void createVoteForTopic(AppUser user, Topic topic, Side side) {
        Vote vote = new Vote();
        vote.setUser(user);
        vote.setTopic(topic);
        vote.setSide(side);
        vote.setIsDeleted(false);
        vote.setDeletedAt(null);
        vote.setUpdatedAt(LocalDateTime.now());
        voteRepository.save(vote);

        topic.setPopularityScore(topic.getPopularityScore() + 1);
        topicRepository.save(topic);

        VoteCount vc = voteCountRepository.findByTopicId(topic.getId())
                .orElseGet(() -> {
                    VoteCount newVC = new VoteCount();
                    newVC.setTopic(topic);
                    newVC.setLeftCount(0);
                    newVC.setRightCount(0);
                    return voteCountRepository.save(newVC);
                });
        vc.increment(side);
        voteCountRepository.save(vc);

        eventPublisher.publishCreated(user.getId(), topic.getId(), side);
    }

    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    public void unvote(Long userId, Long topicId) {
        retryable(() -> {
            Vote vote = voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vote not found"));
            Side side = vote.getSide();
            vote.setIsDeleted(true);
            vote.setDeletedAt(LocalDateTime.now());
            vote.setUpdatedAt(LocalDateTime.now());
            voteRepository.save(vote);

            VoteCount vc = voteCountRepository.findByTopicId(topicId)
                    .orElseThrow(() -> new ResourceNotFoundException("VoteCount not found"));
            vc.decrement(side);
            voteCountRepository.save(vc);

            eventPublisher.publishRemoved(userId, topicId, side);
        });
    }

    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    public void updateUserVote(Long userId, Long topicId, Side newSide) {
        retryable(() -> {
            Vote vote = voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vote not found"));
            if (vote.getSide() != newSide) {
                Side oldSide = vote.getSide();
                vote.setSide(newSide);
                vote.setUpdatedAt(LocalDateTime.now());
                voteRepository.save(vote);

                VoteCount vc = voteCountRepository.findByTopicId(topicId)
                        .orElseThrow(() -> new ResourceNotFoundException("VoteCount not found"));
                vc.decrement(oldSide);
                vc.increment(newSide);
                voteCountRepository.save(vc);

                eventPublisher.publishUpdated(userId, topicId, oldSide, newSide);
            }
        });
    }

    @Transactional
    public void removeAllVotesByUser(Long userId) {
        voteRepository.softDeleteByUserId(userId);
    }

    private void retryable(Runnable action) {
        int attempts = MAX_RETRY_ATTEMPTS;
        while (attempts-- > 0) {
            try {
                action.run();
                return;
            } catch (OptimisticLockException e) {
                if (attempts == 0) throw e;
                log.warn("Retry due to optimistic lock, attempts left={}", attempts);
            }
        }
    }
}