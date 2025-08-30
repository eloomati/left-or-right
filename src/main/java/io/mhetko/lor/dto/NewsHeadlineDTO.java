package io.mhetko.lor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsHeadlineDTO {
    private int index;
    private String title;
    private String description;
}
