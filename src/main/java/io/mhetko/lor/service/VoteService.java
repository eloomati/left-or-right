package io.mhetko.lor.service;

import io.mhetko.lor.dto.VoteCountDTO;
import io.mhetko.lor.dto.VoteDTO;
import io.mhetko.lor.entity.enums.Side;
import io.mhetko.lor.service.command.VoteCommandService;
import io.mhetko.lor.service.query.VoteQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class
VoteService {

    private final VoteCommandService commandService;
    private final VoteQueryService queryService;

    // Command methods
    public void vote(Long userId, Long topicId, Side side) { commandService.vote(userId, topicId, side); }
    public void unvote(Long userId, Long topicId) { commandService.unvote(userId, topicId); }
    public void updateUserVote(Long userId, Long topicId, Side newSide) { commandService.updateUserVote(userId, topicId, newSide); }
    public void removeAllVotesByUser(Long userId) { commandService.removeAllVotesByUser(userId); }
    public void voteOnProposedTopic(Long userId, Long proposedTopicId, Side side) {
        commandService.voteOnProposedTopic(userId, proposedTopicId, side);
    }

    // Query methods
    public boolean hasUserVoted(Long userId, Long topicId) { return queryService.hasUserVoted(userId, topicId); }
    public List<VoteDTO> getUserVotesDto(Long userId) { return queryService.getUserVotes(userId); }
    public Optional<Side> getUserVoteSide(Long userId, Long topicId) { return queryService.getUserVoteSide(userId, topicId); }
    public int countVotesBySide(Long topicId, Side side) { return queryService.countVotesBySide(topicId, side); }
    public List<Long> getMostPopularTopics(int limit) { return queryService.getMostPopularTopics(limit); }
    public VoteCountDTO getVoteCount(Long topicId) { return queryService.getVoteCount(topicId); }
}
