package com.trainingplatform.controller;

import com.trainingplatform.service.RecommendationService;
import com.trainingplatform.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/topics/{topicId}/recommend")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public String toggle(@PathVariable Long topicId, RedirectAttributes redirectAttributes) {
        recommendationService.toggle(topicId, AuthUtil.currentUser());
        redirectAttributes.addFlashAttribute("success", "Recommendation updated.");
        return "redirect:/topics/" + topicId;
    }
}
