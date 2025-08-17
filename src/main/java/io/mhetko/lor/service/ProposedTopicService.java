package io.mhetko.lor.service;

import io.mhetko.lor.dto.ProposedTopicDTO;
import io.mhetko.lor.dto.TopicDTO;
import io.mhetko.lor.entity.AppUser;
import io.mhetko.lor.entity.Category;
import io.mhetko.lor.entity.ProposedTopic;
import io.mhetko.lor.entity.Topic;
import io.mhetko.lor.mapper.ProposedTopicMapper;
import io.mhetko.lor.mapper.ProposedTopicToTopicMapper;
import io.mhetko.lor.mapper.TopicMapper;
import io.mhetko.lor.repository.AppUserRepository;
import io.mhetko.lor.repository.CategoryRepository;
import io.mhetko.lor.repository.ProposedTopicRepository;
import io.mhetko.lor.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public TopicDTO moveToTopic(Long proposedTopicId) {
        ProposedTopic proposed = proposedTopicRepository.findById(proposedTopicId)
                .orElseThrow(() -> new EntityNotFoundException("ProposedTopic not found"));
        Topic topic = proposedTopicToTopicMapper.toTopic(proposed);
        topic.setCreatedAt(LocalDateTime.now());
        Topic saved = topicRepository.save(topic);

        softDelete(proposedTopicId);

        return topicMapper.toDto(saved);
    }

    public List<ProposedTopicDTO> getAll() {
        return proposedTopicRepository.findAll()
                .stream()
                .map(proposedTopicMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProposedTopicDTO getById(Long id) {
        ProposedTopic entity = proposedTopicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProposedTopic not found"));
        return proposedTopicMapper.toDto(entity);
    }

    public ProposedTopicDTO create(ProposedTopicDTO dto) {
        AppUser user = appUserRepository.findById(dto.getProposedById())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        ProposedTopic entity = proposedTopicMapper.toEntity(dto);
        entity.setProposedBy(user);
        entity.setCategory(category);
        entity.setCreatedAt(LocalDateTime.now());
        ProposedTopic saved = proposedTopicRepository.save(entity);
        return proposedTopicMapper.toDto(saved);
    }

    public void softDelete(Long id) {
        ProposedTopic entity = proposedTopicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProposedTopic not found"));
        entity.setDeletedAt(LocalDateTime.now());
        proposedTopicRepository.save(entity);
    }
}