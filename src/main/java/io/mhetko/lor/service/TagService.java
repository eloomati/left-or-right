package io.mhetko.lor.service;

import io.mhetko.lor.dto.TagDTO;
import io.mhetko.lor.entity.Tag;
import io.mhetko.lor.mapper.TagMapper;
import io.mhetko.lor.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public List<TagDTO> getAllTags(){
        log.info("Fetching all active tags");
        return tagRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(tagMapper::toDto)
                .toList();
    }

    public Optional<TagDTO> getTagById(Long id){
        log.info("Fetching tag with id {}", id);
        return tagRepository.findById(id)
                .map(tagMapper::toDto);
    }

    public TagDTO createTag(TagDTO tagDTO){
        log.info("Creating tag {}", tagDTO);
        if (tagRepository.findByNameAndDeletedAtIsNull(tagDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("A tag with this name already exists");
        }
        Tag tag = tagMapper.toEntity(tagDTO);
        tag.setCreatedAt(LocalDateTime.now());
        Tag saved = tagRepository.save(tag);
        log.info("Tag '{}' created successfully", saved.getName());
        return tagMapper.toDto(saved);
    }

    public void deleteTag(Long id) {
        log.info("Deleting tag with ID: {}", id);
        tagRepository.softDeleteById(id, LocalDateTime.now());
        log.info("Tag with ID '{}' deleted successfully", id);
    }

    public TagDTO updateTag(Long id, TagDTO tagDTO) {
        log.info("Updating tag with ID: {}", id);
        Optional<Tag> maybeTag = tagRepository.findById(id);
        if (maybeTag.isPresent()) {
            Tag tagUpdate = maybeTag.get();
            tagUpdate.setName(tagDTO.getName());
            Tag updatedTag = tagRepository.save(tagUpdate);
            log.info("Tag with ID '{}' updated successfully", updatedTag.getId());
            return tagMapper.toDto(updatedTag);
        }
        throw new IllegalArgumentException("Tag not found");
    }
}
