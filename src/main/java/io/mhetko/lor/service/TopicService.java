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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {

    private final AppUserRepository appUserRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;
    private final CountryRepository countryRepository;
    private final ContinentRepository continentRepository;
    private final UserUtils userUtils;

    public TopicDTO createTopic(CreateTopicRequestDTO dto) {
        log.info("Attempting to create topic with title: {}", dto.getTitle());

        validateUniqueTitle(dto.getTitle());
        Country country = getCountry(dto.getCountryId());
        Continent continent = getContinent(dto.getContinentId());
        AppUser currentUser = getCurrentUser();
        Category category = getCategory(dto.getCategoryId());
        Set<Tag> tags = getTags(dto.getTagIds());

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

    private Country getCountry(Long countryId) {
        return countryRepository.findById(countryId)
                .orElseThrow(() -> {
                    log.error("Country not found for id: {}", countryId);
                    return new IllegalStateException("Country not found");
                });
    }

    private Continent getContinent(Long continentId) {
        return continentRepository.findById(continentId)
                .orElseThrow(() -> {
                    log.error("Continent not found for id: {}", continentId);
                    return new IllegalStateException("Continent not found");
                });
    }

    private AppUser getCurrentUser() {
        return userUtils.getCurrentUser()
                .orElseThrow(() -> {
                    log.error("Current user not found");
                    return new IllegalStateException("Current user not found");
                });
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> {
                    log.error("Category not found or deleted for id: {}", categoryId);
                    return new IllegalStateException("Category not found or deleted");
                });
    }

    private Set<Tag> getTags(Set<Long> tagIds) {
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(tagIds));
        if (tags.isEmpty()) {
            log.error("No tags found for ids: {}", tagIds);
            throw new IllegalStateException("No tags found");
        }
        return tags;
    }

    private Topic buildTopic(CreateTopicRequestDTO dto, AppUser currentUser, Category category, Set<Tag> tags, Country country, Continent continent) {
        Topic topic = new Topic();
        topic.setTitle(dto.getTitle());
        topic.setDescription(dto.getDesctription());
        topic.setStatus(TopicStatus.NEW);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());
        topic.setPopularityScore(0);
        topic.setCreatedBy(currentUser);
        topic.setCategory(category);
        topic.setTags(tags);
        topic.setIsArchive(false);
        topic.setCountry(country);
        topic.setContinent(continent);
        return topic;
    }
}