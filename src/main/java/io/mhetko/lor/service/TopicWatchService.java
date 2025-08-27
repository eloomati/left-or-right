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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    public Page<WatchedTopicDTO> getWatchedTopicsDtoForCurrentUser(Pageable pageable) {
        AppUser user = userUtils.getCurrentUser().orElseThrow();
        List<WatchedTopicDTO> all = topicWatchRepository.findAllByUser(user)
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

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<WatchedTopicDTO> pageContent = start > end ? List.of() : all.subList(start, end);

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, all.size());
    }

    public void unwatchTopic(Long topicId) {
        AppUser user = userUtils.getCurrentUser().orElseThrow();
        var topic = topicRepository.findById(topicId).orElseThrow();
        var watch = topicWatchRepository.findByUserAndTopic(user, topic);
        watch.ifPresent(topicWatchRepository::delete);
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

    public void unwatchProposedTopic(Long proposedTopicId) {
        AppUser user = userUtils.getCurrentUser().orElseThrow();
        var proposedTopic = proposedTopicRepository.findById(proposedTopicId).orElseThrow();
        var watch = topicWatchRepository.findByUserAndProposedTopic(user, proposedTopic);
        watch.ifPresent(topicWatchRepository::delete);
    }
}