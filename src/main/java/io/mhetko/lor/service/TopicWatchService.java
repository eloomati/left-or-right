package io.mhetko.lor.service;

import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.entity.TopicWatch;
import io.mhetko.lor.repository.TopicRepository;
import io.mhetko.lor.repository.TopicWatchRepository;
import io.mhetko.lor.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TopicWatchService {

    private final TopicWatchRepository topicWatchRepository;
    private final TopicRepository topicRepository;
    private final UserUtils userUtils;

    public void watchTopic(Long topicId) {
        AppUser user = userUtils.getCurrentUser().orElseThrow();
        Topic topic = topicRepository.findById(topicId).orElseThrow();
        if (topicWatchRepository.existsByUserAndTopic(user, topic)) {
            return;
        }
        TopicWatch watch = new TopicWatch();
        watch.setUser(user);
        watch.setTopic(topic);
        watch.setCreatedAt(LocalDateTime.now());
        topicWatchRepository.save(watch);
    }
}