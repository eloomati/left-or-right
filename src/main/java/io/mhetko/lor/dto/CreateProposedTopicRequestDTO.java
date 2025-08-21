package io.mhetko.lor.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateProposedTopicRequestDTO {
    private String title;
    private String description;
    private List<Long> categories;
    private List<Long> tags;
    // inne pola wg potrzeb
}