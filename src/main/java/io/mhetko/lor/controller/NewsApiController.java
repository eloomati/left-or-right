package io.mhetko.lor.controller;

import io.mhetko.lor.dto.NewsHeadlineDTO;
import io.mhetko.lor.service.NewsApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsApiController {

    private final NewsApiService newsApiService;

    @GetMapping("/headlines")
    public List<NewsHeadlineDTO> getNewsHeadlinesWithDescription(@RequestParam String country) {
        return newsApiService.fetchNewsHeadlinesWithDescription(country);
    }


}
