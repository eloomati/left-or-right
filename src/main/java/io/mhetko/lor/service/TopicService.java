package io.mhetko.lor.service;

import io.mhetko.lor.dto.CreateTopicRequestDTO;
import io.mhetko.lor.dto.TopicDTO;
import io.mhetko.lor.entity.*;
import io.mhetko.lor.entity.enums.TopicStatus;
import io.mhetko.lor.mapper.TopicMapper;
import io.mhetko.lor.repository.*;
import io.mhetko.lor.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {

    private final AppUserRepository appUserRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final TopicRepository topicRepository;
    private final TopicWatchRepository topicWatchRepository;
    private final TopicMapper topicMapper;
    private final CountryRepository countryRepository;
    private final ContinentRepository continentRepository;
    private final UserUtils userUtils;

    @Transactional
    public TopicDTO createTopic(CreateTopicRequestDTO dto) {
        log.info("Attempting to create topic with title: {}", dto.getTitle());

        validateUniqueTitle(dto.getTitle());

        Country country = findCountryOrThrow(dto.getCountryId());
        Continent continent = findContinentOrThrow(dto.getContinentId());
        AppUser currentUser = getCurrentUser();
        Category category = findCategoryOrThrow(dto.getCategoryId());
        Set<Tag> tags = findTagsOrThrow(dto.getTagIds());

        Topic topic = buildTopic(dto, currentUser, category, tags, country, continent);

        Topic savedTopic = topicRepository.save(topic);
        log.info("Topic created successfully with id: {}", savedTopic.getId());

        return topicMapper.toDto(savedTopic);
    }

    private void validateUniqueTitle(String title) {
        if (topicRepository.findByTitle(title).isPresent()) {
            log.warn("Topic with title '{}' already exists", title);
            throw new IllegalStateException("Topic with this title already exists");
        }
    }

    private Country findCountryOrThrow(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Country not found"));
    }

    private Continent findContinentOrThrow(Long id) {
        return continentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Continent not found"));
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalStateException("Category not found or deleted"));
    }

    private Set<Tag> findTagsOrThrow(Set<Long> ids) {
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(ids));
        if (tags.isEmpty()) throw new IllegalStateException("No tags found");
        return tags;
    }

    private AppUser getCurrentUser() {
        return userUtils.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }

    private Topic buildTopic(CreateTopicRequestDTO dto, AppUser user, Category category, Set<Tag> tags, Country country, Continent continent) {
        Topic topic = new Topic();
        topic.setTitle(dto.getTitle());
        topic.setDescription(dto.getDesctription());
        topic.setStatus(TopicStatus.NEW);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());
        topic.setPopularityScore(0);
        topic.setCreatedBy(user);
        topic.setCategory(category);
        topic.setTags(tags);
        topic.setIsArchive(false);
        topic.setCountry(country);
        topic.setContinent(continent);
        return topic;
    }

    public Page<TopicDTO> getAllTopicsSortedByPopularity(Pageable pageable) {
        final Set<Long> watchedIds;
        var userOpt = userUtils.getCurrentUser();
        if (userOpt.isPresent()) {
            watchedIds = topicWatchRepository.findAllByUser(userOpt.get())
                    .stream()
                    .map(w -> w.getTopic() != null ? w.getTopic().getId() : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } else {
            watchedIds = Set.of();
        }
        return topicRepository.findAllByOrderByPopularityScoreDesc(pageable)
                .map(topic -> {
                    TopicDTO dto = topicMapper.toDto(topic);
                    dto.setWatched(watchedIds.contains(topic.getId()));
                    return dto;
                });
    }
}
