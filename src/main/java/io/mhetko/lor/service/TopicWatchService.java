package io.mhetko.lor.service;

import io.mhetko.lor.dto.WatchedTopicDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.TopicWatch;
import io.mhetko.lor.mapper.WatchedProposedTopicMapper;
import io.mhetko.lor.mapper.WatchedTopicMapper;
import io.mhetko.lor.repository.ProposedTopicRepository;
import io.mhetko.lor.repository.TopicRepository;
import io.mhetko.lor.repository.TopicWatchRepository;
import io.mhetko.lor.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TopicWatchService {

    private final TopicWatchRepository topicWatchRepository;
    private final TopicRepository topicRepository;
    private final ProposedTopicRepository proposedTopicRepository;
    private final UserUtils userUtils;
    private final WatchedTopicMapper watchedTopicMapper;
    private final WatchedProposedTopicMapper watchedProposedTopicMapper;

    public void watchTopic(Long topicId) {
        AppUser user = userUtils.getCurrentUser().orElseThrow();
        var topic = topicRepository.findById(topicId).orElseThrow();
        if (topicWatchRepository.existsByUserAndTopic(user, topic)) {
            return;
        }
        var watch = new TopicWatch();
        watch.setUser(user);
        watch.setTopic(topic);
        watch.setCreatedAt(LocalDateTime.now());
        topicWatchRepository.save(watch);
    }

    public List<WatchedTopicDTO> getWatchedTopicsDtoForCurrentUser() {
        AppUser user = userUtils.getCurrentUser().orElseThrow();
        return topicWatchRepository.findAllByUser(user)
                .stream()
                .map(watch -> {
                    if (watch.getTopic() != null) {
                        return watchedTopicMapper.toDto(watch.getTopic());
                    } else if (watch.getProposedTopic() != null) {
                        return watchedProposedTopicMapper.toDto(watch.getProposedTopic());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public void watchProposedTopic(Long proposedTopicId) {
        AppUser user = userUtils.getCurrentUser().orElseThrow();
        var proposedTopic = proposedTopicRepository.findById(proposedTopicId).orElseThrow();
        if (topicWatchRepository.existsByUserAndProposedTopic(user, proposedTopic)) {
            return;
        }
        var watch = new TopicWatch();
        watch.setUser(user);
        watch.setProposedTopic(proposedTopic);
        watch.setCreatedAt(LocalDateTime.now());
        topicWatchRepository.save(watch);
    }
}