package io.mhetko.lor.kafka;

import io.mhetko.lor.repository.TopicRepository;
import io.mhetko.lor.repository.VoteCountRepository;
import io.mhetko.lor.entity.VoteCount;
import io.mhetko.lor.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoteEventListener {

    private final TopicRepository topicRepository;
    private final VoteCountRepository voteCountRepository;

    /**
     * Jeden Consumer, który obsługuje wszystkie typy VoteEvent.
     * Zarejestrowany pod bindingiem "vote-events-in".
     */
    @Bean
    public Consumer<VoteEvent> voteEventsIn() {
        return event -> {
            log.debug("Received event: type={} topic={} user={}", event.type(), event.topicId(), event.userId());

            switch (event) {
                case VoteCreatedEvent e -> handleCreated(e);
                case VoteRemovedEvent e -> handleRemoved(e);
                case VoteUpdatedEvent e -> handleUpdated(e);
                default -> log.warn("Unknown event type: {}", event.type());
            }
        };
    }

    private void handleCreated(VoteCreatedEvent e) {
        // Minimalny koszt – inkrementacja popularności na bazie bieżącego VoteCount
        VoteCount vc = voteCountRepository.findByTopicId(e.topicId())
                .orElseThrow(() -> new ResourceNotFoundException("VoteCount not found for topic " + e.topicId()));
        // Popularność = suma obu stron; ale aby uniknąć zbieżności, robimy po prostu "+1"
        topicRepository.incrementPopularityScore(e.topicId());
    }

    private void handleRemoved(VoteRemovedEvent e) {
        topicRepository.decrementPopularityScore(e.topicId());
    }

    private void handleUpdated(VoteUpdatedEvent e) {
        // Zmiana strony nie zmienia łącznej popularności – nic nie robimy.
        // Jeśli chcesz – można emitować side-specific metryki do innego systemu.
        log.trace("Vote side changed from {} to {} for topic {}", e.oldSide(), e.newSide(), e.topicId());
    }
}

