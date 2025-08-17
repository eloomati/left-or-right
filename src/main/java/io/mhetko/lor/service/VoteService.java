package io.mhetko.lor.service;

import io.mhetko.lor.dto.VoteCountDTO;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @CacheEvict(value = "voteCount", key = "#topicId")
    public void vote(Long userId, Long topicId, Side side) {
        log.info("User {} votes on topic {} on side {}", userId, topicId, side);
        Vote vote = saveOrUpdateVote(userId, topicId, side);
        VoteCount vc = updateVoteCount(topicId, side);
        updateTopicPopularity(topicId, vc);
        log.debug("Vote saved: {}, count: {}", vote, vc);
    }

    private Vote saveOrUpdateVote(Long userId, Long topicId, Side side) {
        Vote vote = voteRepository.findByUserIdAndTopicId(userId, topicId)
                .orElseGet(() -> {
                    Vote v = new Vote();
                    AppUser user = userRepository.findById(userId).orElseThrow();
                    Topic topic = topicRepository.findById(topicId).orElseThrow();
                    v.setUser(user);
                    v.setTopic(topic);
                    return v;
                });
        vote.setSide(side);
        return voteRepository.save(vote);
    }

    private VoteCount updateVoteCount(Long topicId, Side side) {
        VoteCount vc = voteCountRepository.findById(topicId)
                .orElseGet(() -> {
                    VoteCount c = new VoteCount();
                    c.setId(topicId);
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
                .orElseThrow(() -> new RuntimeException("Vote not found"));
    }

    private void removeVote(Vote vote) {
        voteRepository.delete(vote);
    }

    private VoteCount decrementVoteCount(Long topicId, Side side) {
        VoteCount vc = voteCountRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Vote count not found"));
        if (side == Side.LEFT) {
            vc.setLeftCount(Math.max(0, vc.getLeftCount() - 1));
        } else {
            vc.setRightCount(Math.max(0, vc.getRightCount() - 1));
        }
        return voteCountRepository.save(vc);
    }

    public boolean hasUserVoted(Long userId, Long topicId) {
        boolean voted = voteRepository.findByUserIdAndTopicId(userId, topicId).isPresent();
        log.debug("User {} voted on topic {}: {}", userId, topicId, voted);
        return voted;
    }

    public List<Vote> getUserVotes(Long userId) {
        log.info("Fetching all votes for user {}", userId);
        return voteRepository.findAllByUserId(userId);
    }

    public Optional<Side> getUserVoteSide(Long userId, Long topicId) {
        Optional<Vote> vote = voteRepository.findByUserIdAndTopicId(userId, topicId);
        log.debug("User {} vote on topic {}: {}", userId, topicId, vote.map(Vote::getSide).orElse(null));
        return vote.map(Vote::getSide);
    }

    public int countVotesBySide(Long topicId, Side side) {
        VoteCount vc = voteCountRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Vote count not found"));
        return side == Side.LEFT ? vc.getLeftCount() : vc.getRightCount();
    }

    @Transactional
    public void removeAllVotesByUser(Long userId) {
        List<Vote> votes = voteRepository.findAllByUserId(userId);
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
                .map(VoteCount::getId)
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
            VoteCount vc = voteCountRepository.findById(topicId).orElseThrow();
            updateTopicPopularity(topicId, vc);
        }
    }

    @Cacheable(value = "voteCount", key = "#topicId")
    public VoteCountDTO getVoteCount(Long topicId) {
        log.info("Fetching vote count for topic {}", topicId);
        VoteCount voteCount = voteCountRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Vote count not found for topic ID: " + topicId));
        return voteCountMapper.toDto(voteCount);
    }
}