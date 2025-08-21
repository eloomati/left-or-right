package io.mhetko.lor.service;

import io.mhetko.lor.dto.ProposedTopicDTO;
import io.mhetko.lor.dto.TopicDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.Tag;
import io.mhetko.lor.entity.Category;
import io.mhetko.lor.entity.ProposedTopic;
import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.entity.enums.ProposedTopicSource;
import io.mhetko.lor.entity.enums.Side;
import io.mhetko.lor.mapper.ProposedTopicMapper;
import io.mhetko.lor.mapper.ProposedTopicToTopicMapper;
import io.mhetko.lor.mapper.TopicMapper;
import io.mhetko.lor.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ProposedTopicService {

    private final ProposedTopicRepository proposedTopicRepository;
    private final AppUserRepository appUserRepository;
    private final CategoryRepository categoryRepository;
    private final ProposedTopicMapper proposedTopicMapper;
    private final TopicRepository topicRepository;
    private final ProposedTopicToTopicMapper proposedTopicToTopicMapper;
    private final TopicMapper topicMapper;
    private final VoteService voteService;
    private final VoteCountRepository voteCountRepository;
    private final TagRepository tagRepository;

    @Transactional
    public void vote(Long userId, Long proposedTopicId, Side side) {
        voteService.voteOnProposedTopic(userId, proposedTopicId, side);
    }

    @Transactional
    public TopicDTO moveToTopic(Long proposedTopicId) {
        ProposedTopic proposed = findProposedTopicOrThrow(proposedTopicId);
        Topic topic = proposedTopicToTopicMapper.toTopic(proposed);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setCreatedBy(proposed.getProposedBy());
        topic.setIsArchive(false);

        Topic saved = topicRepository.save(topic);
        softDelete(proposedTopicId);

        return topicMapper.toDto(saved);
    }

    public List<ProposedTopicDTO> getAll() {
        return proposedTopicRepository.findAll()
                .stream()
                .map(this::mapWithPopularity)
                .collect(Collectors.toList());
    }

    public ProposedTopicDTO getById(Long id) {
        return mapWithPopularity(findProposedTopicOrThrow(id));
    }

    @Transactional
    public ProposedTopicDTO create(ProposedTopicDTO dto) {
        if (dto.getProposedById() == null) {
            throw new IllegalArgumentException("proposedById nie może być nullem");
        }

        AppUser user = findUserOrThrow(dto.getProposedById());

        List<Category> categories = dto.getCategories() != null
                ? dto.getCategories().stream()
                .map(catDto -> {
                    if (catDto.getId() == null) throw new IllegalArgumentException("Category id nie może być nullem");
                    return findCategoryOrThrow(catDto.getId());
                })
                .collect(Collectors.toList())
                : Collections.emptyList();

        List<Tag> tags = dto.getTags() != null
                ? dto.getTags().stream()
                .map(tagDto -> {
                    if (tagDto.getId() == null) throw new IllegalArgumentException("Tag id nie może być nullem");
                    return tagRepository.findById(tagDto.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Tag not found: " + tagDto.getId()));
                })
                .collect(Collectors.toList())
                : Collections.emptyList();

        ProposedTopic entity = proposedTopicMapper.toEntity(dto);
        entity.setSource(ProposedTopicSource.USER);
        entity.setProposedBy(user);
        entity.setCategories(categories);
        entity.setTags(tags);
        entity.setCreatedAt(LocalDateTime.now());

        ProposedTopic saved = proposedTopicRepository.save(entity);
        return mapWithPopularity(saved);
    }

    @Transactional
    public void softDelete(Long id) {
        ProposedTopic entity = findProposedTopicOrThrow(id);
        entity.setDeletedAt(LocalDateTime.now());
        proposedTopicRepository.save(entity);
    }

    private ProposedTopicDTO mapWithPopularity(ProposedTopic entity) {
        ProposedTopicDTO dto = proposedTopicMapper.toDto(entity);
        dto.setPopularityScore(getPopularityScore(entity.getId()));
        return dto;
    }

    private ProposedTopic findProposedTopicOrThrow(Long id) {
        return proposedTopicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProposedTopic not found"));
    }

    private AppUser findUserOrThrow(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    private int getPopularityScore(Long proposedTopicId) {
        return voteCountRepository.findByProposedTopicId(proposedTopicId)
                .map(vc -> vc.getLeftCount() + vc.getRightCount())
                .orElse(0);
    }
}
