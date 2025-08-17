package io.mhetko.lor.service;

import io.mhetko.lor.dto.VoteCountDTO;
import io.mhetko.lor.dto.VoteDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.entity.Vote;
import io.mhetko.lor.entity.VoteCount;
import io.mhetko.lor.entity.enums.Side;
import io.mhetko.lor.mapper.VoteCountMapper;
import io.mhetko.lor.repository.AppUserRepository;
import io.mhetko.lor.repository.TopicRepository;
import io.mhetko.lor.repository.VoteCountRepository;
import io.mhetko.lor.repository.VoteRepository;
import io.mhetko.lor.mapper.VoteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.mhetko.lor.exception.ResourceNotFoundException;

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

    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    public void vote(Long userId, Long topicId, Side side) {
        if (voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId).isPresent()) {
            throw new IllegalStateException("Użytkownik już zagłosował na ten temat.");
        }
        log.info("User {} votes on topic {} on side {}", userId, topicId, side);
        Vote vote = saveOrUpdateVote(userId, topicId, side);
        VoteCount vc = updateVoteCount(topicId, side);
        updateTopicPopularity(topicId, vc);
        log.debug("Vote saved: {}, count: {}", vote, vc);
    }

    private Vote saveOrUpdateVote(Long userId, Long topicId, Side side) {
        return findActiveVote(userId, topicId)
                .map(vote -> updateVote(vote, side))
                .orElseGet(() -> restoreOrCreateVote(userId, topicId, side));
    }

    private Optional<Vote> findActiveVote(Long userId, Long topicId) {
        return voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId);
    }

    private Vote updateVote(Vote vote, Side side) {
        vote.setSide(side);
        vote.setIsDeleted(false);
        vote.setDeletedAt(null);
        vote.setUpdatedAt(LocalDateTime.now());
        return voteRepository.save(vote);
    }

    private Vote restoreOrCreateVote(Long userId, Long topicId, Side side) {
        Optional<Vote> deletedVote = voteRepository.findByUserIdAndTopicId(userId, topicId)
                .filter(v -> Boolean.TRUE.equals(v.getIsDeleted()));
        if (deletedVote.isPresent()) {
            return updateVote(deletedVote.get(), side);
        }
        return createNewVote(userId, topicId, side);
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

    private VoteCount updateVoteCount(Long topicId, Side side) {
        VoteCount vc = voteCountRepository.findByTopicId(topicId)
                .orElseGet(() -> {
                    Topic topic = topicRepository.findById(topicId)
                            .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + topicId));
                    VoteCount c = new VoteCount();
                    c.setTopic(topic);
                    c.setLeftCount(0);
                    c.setRightCount(0);
                    return c;
                });
        if (side == Side.LEFT) {
            vc.setLeftCount(vc.getLeftCount() + 1);
        } else {
            vc.setRightCount(vc.getRightCount() + 1);
        }
        return voteCountRepository.save(vc);
    }

    private void updateTopicPopularity(Long topicId, VoteCount vc) {
        int popularity = vc.getLeftCount() + vc.getRightCount();
        topicRepository.updatePopularityScore(topicId, popularity);
        log.debug("Topic {} popularity updated to {}", topicId, popularity);
    }

    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    public void unvote(Long userId, Long topicId) {
        log.info("User {} withdraws vote from topic {}", userId, topicId);
        Vote vote = getVoteOrThrow(userId, topicId);
        Side previousSide = vote.getSide();
        removeVote(vote);
        VoteCount vc = decrementVoteCount(topicId, previousSide);
        updateTopicPopularity(topicId, vc);
        log.debug("Vote withdrawn: {}, updated count: {}", vote, vc);
    }

    private Vote getVoteOrThrow(Long userId, Long topicId) {
        return voteRepository.findByUserIdAndTopicId(userId, topicId)
                .filter(v -> !Boolean.TRUE.equals(v.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("Vote not found or has been deleted"));
    }

    private void removeVote(Vote vote) {
        vote.setIsDeleted(true);
        vote.setDeletedAt(LocalDateTime.now());
        vote.setUpdatedAt(LocalDateTime.now());
        voteRepository.save(vote);
    }

    private VoteCount decrementVoteCount(Long topicId, Side side) {
        VoteCount vc = voteCountRepository.findByTopicId(topicId)
                .orElseThrow(() -> new RuntimeException("Vote count not found"));
        if (side == Side.LEFT) {
            vc.setLeftCount(Math.max(0, vc.getLeftCount() - 1));
        } else {
            vc.setRightCount(Math.max(0, vc.getRightCount() - 1));
        }
        return voteCountRepository.save(vc);
    }

    public boolean hasUserVoted(Long userId, Long topicId) {
        boolean voted = voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId).isPresent();
        log.debug("User {} voted on topic {}: {}", userId, topicId, voted);
        return voted;
    }

    public List<Vote> getUserVotes(Long userId) {
        log.info("Fetching all votes for user {}", userId);
        return voteRepository.findAllByUserIdAndIsDeletedFalse(userId);
    }

    public Optional<Side> getUserVoteSide(Long userId, Long topicId) {
        Optional<Vote> vote = voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId);
        log.debug("User {} vote on topic {}: {}", userId, topicId, vote.map(Vote::getSide).orElse(null));
        return vote.map(Vote::getSide);
    }

    public int countVotesBySide(Long topicId, Side side) {
        VoteCount vc = voteCountRepository.findByTopicId(topicId)
                .orElseThrow(() -> new RuntimeException("Vote count not found"));
        return side == Side.LEFT ? vc.getLeftCount() : vc.getRightCount();
    }

    @Transactional
    public void removeAllVotesByUser(Long userId) {
        List<Vote> votes = voteRepository.findAllByUserIdAndIsDeletedFalse(userId);
        for (Vote vote : votes) {
            unvote(userId, vote.getTopic().getId());
        }
    }

    public List<Long> getMostPopularTopics(int limit) {
        return voteCountRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(
                        b.getLeftCount() + b.getRightCount(),
                        a.getLeftCount() + a.getRightCount()))
                .limit(limit)
                .map(vc -> vc.getTopic().getId())
                .toList();
    }

    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    public void updateUserVote(Long userId, Long topicId, Side newSide) {
        Vote vote = getVoteOrThrow(userId, topicId);
        if (vote.getSide() != newSide) {
            Side oldSide = vote.getSide();
            vote.setSide(newSide);
            voteRepository.save(vote);
            decrementVoteCount(topicId, oldSide);
            updateVoteCount(topicId, newSide);
            VoteCount vc = voteCountRepository.findByTopicId(topicId).orElseThrow();
            updateTopicPopularity(topicId, vc);
        }
    }

    public List<VoteDTO> getUserVotesDto(Long userId) {
        return voteRepository.findAllByUserIdAndIsDeletedFalse(userId)
                .stream()
                .map(voteMapper::toDto)
                .toList();
    }

    @Cacheable(value = "voteCount", key = "#topicId")
    public VoteCountDTO getVoteCount(Long topicId) {
        log.info("Fetching vote count for topic {}", topicId);
        VoteCount voteCount = voteCountRepository.findByTopicId(topicId)
                .orElseThrow(() -> new RuntimeException("Vote count not found for topic ID: " + topicId));
        return voteCountMapper.toDto(voteCount);
    }
}