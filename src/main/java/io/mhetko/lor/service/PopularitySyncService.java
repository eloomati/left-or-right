package io.mhetko.lor.service;

import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.entity.VoteCount;
import io.mhetko.lor.repository.TopicRepository;
import io.mhetko.lor.repository.VoteCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PopularitySyncService {

    private final TopicRepository topicRepository;
    private final VoteCountRepository voteCountRepository;

    @Scheduled(fixedDelay = 60000) // co 60s, dostosuj
    @Transactional
    public void syncTopicPopularity() {
        for (Topic t : topicRepository.findAll()) {
            voteCountRepository.findByTopicId(t.getId()).ifPresent(vc -> {
                t.setPopularityScore(vc.getLeftCount() + vc.getRightCount());
            });
        }
    }
}
