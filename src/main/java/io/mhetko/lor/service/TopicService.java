package io.mhetko.lor.service;

import io.mhetko.lor.dto.CreateTopicRequestDTO;
import io.mhetko.lor.dto.TopicDTO;
import io.mhetko.lor.entity.*;
import io.mhetko.lor.mapper.TopicMapper;
import io.mhetko.lor.repository.*;
import io.mhetko.lor.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        if (topicRepository.findByTitle(dto.getTitle()).isPresent()) {
            log.warn("Topic with title '{}' already exists", dto.getTitle());
            throw new IllegalStateException("Topic with this title already exists");
        }

        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> {
                    log.error("Country not found for id: {}", dto.getCountryId());
                    return new IllegalStateException("Country not found");
                });
        Continent continent = continentRepository.findById(dto.getContinentId())
                .orElseThrow(() -> {
                    log.error("Continent not found for id: {}", dto.getContinentId());
                    return new IllegalStateException("Continent not found");
                });
        AppUser currentUser = userUtils.getCurrentUser()
                .orElseThrow(() -> {
                    log.error("Current user not found");
                    return new IllegalStateException("Current user not found");
                });
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(dto.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category not found or deleted for id: {}", dto.getCategoryId());
                    return new IllegalStateException("Category not found or deleted");
                });
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(dto.getTagIds()));
        if (tags.isEmpty()) {
            log.error("No tags found for ids: {}", dto.getTagIds());
            throw new IllegalStateException("No tags found");
        }

        Topic topic = new Topic();
        topic.setTitle(dto.getTitle());
        topic.setDescription(dto.getDesctription());
        topic.setCountry(country);
        topic.setContinent(continent);
        topic.setCreatedBy(currentUser);
        topic.setCategory(category);
        topic.setTags(tags);

        Topic savedTopic = topicRepository.save(topic);
        log.info("Topic created successfully with id: {}", savedTopic.getId());
        return topicMapper.toDto(savedTopic);
    }
}