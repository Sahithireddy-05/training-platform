package com.trainingplatform.controller;

import com.trainingplatform.dto.ScheduleDto;
import com.trainingplatform.service.ScheduleService;
import com.trainingplatform.service.TopicService;
import com.trainingplatform.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/topics/{topicId}/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final TopicService topicService;

    public ScheduleController(ScheduleService scheduleService, TopicService topicService) {
        this.scheduleService = scheduleService;
        this.topicService = topicService;
    }

    @GetMapping
    public String form(@PathVariable Long topicId, Model model) {
        var topic = topicService.get(topicId);
        ScheduleDto dto = new ScheduleDto();
        dto.setScheduledAt(topic.getScheduledAt());
        dto.setDurationMinutes(topic.getDurationMinutes());
        dto.setLocation(topic.getLocation());
        dto.setCapacity(topic.getCapacity());
        model.addAttribute("topic", topic);
        model.addAttribute("scheduleDto", dto);
        return "topics/schedule";
    }

    @PostMapping
    public String schedule(@PathVariable Long topicId,
                           @Valid @ModelAttribute ScheduleDto scheduleDto,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("topic", topicService.get(topicId));
            return "topics/schedule";
        }
        scheduleService.schedule(topicId, scheduleDto, AuthUtil.currentUser());
        redirectAttributes.addFlashAttribute("success", "Session scheduled.");
        return "redirect:/topics/" + topicId;
    }
}
