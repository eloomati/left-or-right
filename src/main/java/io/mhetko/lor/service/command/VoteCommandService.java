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

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteCommandService {

    private final VoteRepository voteRepository;
    private final VoteCountRepository voteCountRepository;
    private final TopicRepository topicRepository;
    private final AppUserRepository userRepository;
    private final VoteEventPublisher eventPublisher;

    private static final int MAX_RETRY_ATTEMPTS = 3;

    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    public void vote(Long userId, Long topicId, Side side) {
        retryable(() -> doVote(userId, topicId, side));
    }

    private void doVote(Long userId, Long topicId, Side side) {
        voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId)
                .ifPresentOrElse(
                        v -> updateUserVote(userId, topicId, side),   // to też emituje event
                        () -> createNewVote(userId, topicId, side)     // a tu emitujemy "created"
                );
    }

    private void createNewVote(Long userId, Long topicId, Side side) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setTopic(topic);
        vote.setSide(side);
        vote.setIsDeleted(false);
        vote.setDeletedAt(null);
        vote.setUpdatedAt(LocalDateTime.now());
        voteRepository.save(vote);

        VoteCount vc = voteCountRepository.findByTopicId(topicId).orElseGet(() -> {
            VoteCount newVC = new VoteCount();
            newVC.setTopic(topic);
            newVC.setLeftCount(0);
            newVC.setRightCount(0);
            return voteCountRepository.save(newVC);
        });

        vc.increment(side);
        voteCountRepository.save(vc);

        // 🔊 emit do Kafka
        eventPublisher.publishCreated(userId, topicId, side);
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

            // 🔊 emit do Kafka
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

                // 🔊 emit do Kafka
                eventPublisher.publishUpdated(userId, topicId, oldSide, newSide);
            }
        });
    }

    @Transactional
    public void removeAllVotesByUser(Long userId) {
        // bulk soft-delete w repozytorium (JPQL/SQL), bez per-row save
        voteRepository.softDeleteByUserId(userId);
        // tu możesz rozważyć emisję zbiorczego eventu UserVotesRemoved, jeśli potrzebne
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
