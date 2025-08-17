package io.mhetko.lor.service.query;

import io.mhetko.lor.dto.VoteCountDTO;
import io.mhetko.lor.dto.VoteDTO;
import io.mhetko.lor.entity.VoteCount;
import io.mhetko.lor.entity.enums.Side;
import io.mhetko.lor.exception.ResourceNotFoundException;
import io.mhetko.lor.mapper.VoteCountMapper;
import io.mhetko.lor.mapper.VoteMapper;
import io.mhetko.lor.repository.VoteCountRepository;
import io.mhetko.lor.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteQueryService {

    private final VoteRepository voteRepository;
    private final VoteCountRepository voteCountRepository;
    private final VoteMapper voteMapper;
    private final VoteCountMapper voteCountMapper;

    public boolean hasUserVoted(Long userId, Long topicId) {
        return voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId).isPresent();
    }

    public List<VoteDTO> getUserVotes(Long userId) {
        return voteRepository.findAllByUserIdAndIsDeletedFalse(userId)
                .stream().map(voteMapper::toDto).toList();
    }

    public Optional<Side> getUserVoteSide(Long userId, Long topicId) {
        return voteRepository.findByUserIdAndTopicIdAndIsDeletedFalse(userId, topicId)
                .map(v -> v.getSide());
    }

    public int countVotesBySide(Long topicId, Side side) {
        VoteCount vc = voteCountRepository.findByTopicId(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("VoteCount not found"));
        return side == Side.LEFT ? vc.getLeftCount() : vc.getRightCount();
    }

    public List<Long> getMostPopularTopics(int limit) {
        return voteCountRepository.findMostPopularTopics(PageRequest.of(0, limit));
    }

    @Cacheable(value = "voteCount", key = "#topicId")
    public VoteCountDTO getVoteCount(Long topicId) {
        VoteCount vc = voteCountRepository.findByTopicId(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("VoteCount not found"));
        return voteCountMapper.toDto(vc);
    }
}
