package com.trainingplatform.controller;

import com.trainingplatform.dto.RatingDto;
import com.trainingplatform.service.RatingService;
import com.trainingplatform.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/topics/{topicId}/ratings")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public String rate(@PathVariable Long topicId,
                       @Valid @ModelAttribute RatingDto ratingDto,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Rating must be an integer from 1 to 5.");
            return "redirect:/topics/" + topicId;
        }
        ratingService.rate(topicId, ratingDto, AuthUtil.currentUser());
        redirectAttributes.addFlashAttribute("success", "Rating saved.");
        return "redirect:/topics/" + topicId;
    }
}
