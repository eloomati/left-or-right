package io.mhetko.lor.service;

import io.mhetko.lor.dto.CreateTopicRequestDTO;
import io.mhetko.lor.entity.*;
import io.mhetko.lor.mapper.TopicMapper;
import io.mhetko.lor.repository.*;
import io.mhetko.lor.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.Optional;
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

    public Topic createTopic(CreateTopicRequestDTO dto) {

        if (topicRepository.findByTitle(dto.getTitle()).isPresent()) {
            throw new IllegalStateException("Topic with this title already exists");
        }

        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new IllegalStateException("Country not found"));
        Continent continent = continentRepository.findById(dto.getContinentId())
                .orElseThrow(() -> new IllegalStateException("Continent not found"));
        AppUser currentUser = userUtils.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalStateException("Category not found"));
        Set<Tag> tags = new HashSet<>(tagRepository.findAllById(dto.getTagIds()));
        if (tags.isEmpty()) {
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

        return topicRepository.save(topic);
    }
}