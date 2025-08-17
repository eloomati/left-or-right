package io.mhetko.lor.service;

import io.mhetko.lor.dto.VoteCountDTO;
import io.mhetko.lor.dto.VoteDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.entity.Vote;
import io.mhetko.lor.entity.VoteCount;
import io.mhetko.lor.entity.enums.Side;
import io.mhetko.lor.exception.ResourceNotFoundException;
import io.mhetko.lor.mapper.VoteCountMapper;
import io.mhetko.lor.mapper.VoteMapper;
import io.mhetko.lor.repository.AppUserRepository;
import io.mhetko.lor.repository.TopicRepository;
import io.mhetko.lor.repository.VoteCountRepository;
import io.mhetko.lor.repository.VoteRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteCountRepository voteCountRepository;
    private final TopicRepository topicRepository;
    private final AppUserRepository userRepository;
    private final VoteCountMapper voteCountMapper;
    private final VoteMapper voteMapper;

    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * Public API: oddanie głosu (z retry przy OptimisticLockException)
     */
    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    public void vote(Long userId, Long topicId, Side side) {
        retryable(() -> doVote(userId, topicId, side));
    }

    private void doVote(Long userId, Long topicId, Side side) {
        Optional<Vote> existingVote = voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId);

        if (existingVote.isPresent()) {
            updateUserVote(userId, topicId, side);
            return;
        }

        log.info("User {} votes on topic {} on side {}", userId, topicId, side);
        createNewVote(userId, topicId, side);

        VoteCount voteCount = getOrCreateVoteCount(topicId);
        voteCount.increment(side);
        voteCountRepository.save(voteCount);

        updateTopicPopularity(topicId, voteCount);
    }

    private Vote createNewVote(Long userId, Long topicId, Side side) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));

        Vote v = new Vote();
        v.setUser(user);
        v.setTopic(topic);
        v.setSide(side);
        v.setIsDeleted(false);
        v.setDeletedAt(null);
        v.setUpdatedAt(LocalDateTime.now());
        return voteRepository.save(v);
    }

    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    public void unvote(Long userId, Long topicId) {
        retryable(() -> {
            log.info("User {} withdraws vote from topic {}", userId, topicId);
            Vote vote = getVoteOrThrow(userId, topicId);
            Side previousSide = vote.getSide();

            vote.setIsDeleted(true);
            vote.setDeletedAt(LocalDateTime.now());
            vote.setUpdatedAt(LocalDateTime.now());
            voteRepository.save(vote);

            VoteCount voteCount = getVoteCountOrThrow(topicId);
            voteCount.decrement(previousSide);
            voteCountRepository.save(voteCount);

            updateTopicPopularity(topicId, voteCount);
        });
    }

    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    public void updateUserVote(Long userId, Long topicId, Side newSide) {
        retryable(() -> {
            Vote vote = getVoteOrThrow(userId, topicId);
            if (vote.getSide() != newSide) {
                Side oldSide = vote.getSide();
                vote.setSide(newSide);
                vote.setUpdatedAt(LocalDateTime.now());
                voteRepository.save(vote);

                VoteCount voteCount = getVoteCountOrThrow(topicId);
                voteCount.decrement(oldSide);
                voteCount.increment(newSide);
                voteCountRepository.save(voteCount);

                updateTopicPopularity(topicId, voteCount);
            }
        });
    }

    private Vote getVoteOrThrow(Long userId, Long topicId) {
        return voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found for user " + userId + " and topic " + topicId));
    }

    private VoteCount getVoteCountOrThrow(Long topicId) {
        return voteCountRepository.findByTopicId(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("VoteCount not found for topic " + topicId));
    }

    private VoteCount getOrCreateVoteCount(Long topicId) {
        return voteCountRepository.findByTopicId(topicId).orElseGet(() -> {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));
            VoteCount vc = new VoteCount();
            vc.setTopic(topic);
            vc.setLeftCount(0);
            vc.setRightCount(0);
            return voteCountRepository.save(vc);
        });
    }

    private void updateTopicPopularity(Long topicId, VoteCount vc) {
        int popularity = vc.getTotal();
        topicRepository.updatePopularityScore(topicId, popularity);
    }

    public boolean hasUserVoted(Long userId, Long topicId) {
        return voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId).isPresent();
    }

    public List<VoteDTO> getUserVotes(Long userId) {
        return voteRepository.findAllByUserIdAndIsDeletedFalse(userId)
                .stream()
                .map(voteMapper::toDto)
                .toList();
    }

    public Optional<Side> getUserVoteSide(Long userId, Long topicId) {
        return voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId)
                .map(Vote::getSide);
    }

    public int countVotesBySide(Long topicId, Side side) {
        VoteCount vc = getVoteCountOrThrow(topicId);
        return side == Side.LEFT ? vc.getLeftCount() : vc.getRightCount();
    }

    @Transactional
    public void removeAllVotesByUser(Long userId) {
        voteRepository.softDeleteByUserId(userId);
    }

    public List<Long> getMostPopularTopics(int limit) {
        return voteCountRepository.findMostPopularTopics(PageRequest.of(0, limit));
    }

    @Cacheable(value = "voteCount", key = "#topicId")
    public VoteCountDTO getVoteCount(Long topicId) {
        VoteCount voteCount = getVoteCountOrThrow(topicId);
        return voteCountMapper.toDto(voteCount);
    }

    /**
     * Retry wrapper for optimistic locking
     */
    private void retryable(Runnable action) {
        int attempts = MAX_RETRY_ATTEMPTS;
        while (attempts-- > 0) {
            try {
                action.run();
                return;
            } catch (OptimisticLockException e) {
                if (attempts == 0) throw e;
                log.warn("Retrying due to optimistic lock conflict, attempts left={}", attempts);
            }
        }
    }
}
