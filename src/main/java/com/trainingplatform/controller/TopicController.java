package com.trainingplatform.controller;

import com.trainingplatform.dto.TopicCreateDto;
import com.trainingplatform.dto.TopicUpdateDto;
import com.trainingplatform.enums.TopicStatus;
import com.trainingplatform.service.TopicService;
import com.trainingplatform.util.AuthUtil;
import com.trainingplatform.util.PaginationUtil;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/topics")
public class TopicController {
    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String status,
                       @RequestParam(required = false) String role,
                       @RequestParam(defaultValue = "createdAt") String sort,
                       @RequestParam(defaultValue = "0") Integer page,
                       Model model) {
        model.addAttribute("topics", topicService.list(status, role, AuthUtil.currentUser(), PaginationUtil.safePage(page), sort));
        model.addAttribute("statuses", TopicStatus.values());
        model.addAttribute("status", status);
        model.addAttribute("role", role);
        model.addAttribute("sort", sort);
        return "topics/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("topicCreateDto", new TopicCreateDto());
        return "topics/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute TopicCreateDto topicCreateDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "topics/create";
        var topic = topicService.create(topicCreateDto, AuthUtil.currentUser());
        redirectAttributes.addFlashAttribute("success", "Topic request created.");
        return "redirect:/topics/" + topic.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("topic", topicService.get(id));
        model.addAttribute("currentUser", AuthUtil.currentUser());
        model.addAttribute("ratingDto", new com.trainingplatform.dto.RatingDto());
        return "topics/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var topic = topicService.get(id);
        TopicUpdateDto dto = new TopicUpdateDto();
        dto.setTitle(topic.getTitle());
        dto.setDescription(topic.getDescription());
        model.addAttribute("topic", topic);
        model.addAttribute("topicUpdateDto", dto);
        return "topics/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute TopicUpdateDto topicUpdateDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("topic", topicService.get(id));
            return "topics/edit";
        }
        topicService.update(id, topicUpdateDto, AuthUtil.currentUser());
        redirectAttributes.addFlashAttribute("success", "Topic updated.");
        return "redirect:/topics/" + id;
    }

    @PostMapping("/{id}/claim")
    public String claim(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        topicService.claim(id, AuthUtil.currentUser());
        redirectAttributes.addFlashAttribute("success", "You are now the speaker.");
        return "redirect:/topics/" + id;
    }

    @PostMapping("/{id}/unclaim")
    public String unclaim(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        topicService.unclaim(id, AuthUtil.currentUser());
        redirectAttributes.addFlashAttribute("success", "Topic returned to Open.");
        return "redirect:/topics/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        topicService.cancel(id, AuthUtil.currentUser());
        redirectAttributes.addFlashAttribute("success", "Topic cancelled.");
        return "redirect:/topics/" + id;
    }

    @GetMapping("/most-wanted")
    public String mostWanted(@RequestParam(defaultValue = "0") Integer page, Model model) {
        model.addAttribute("topics", topicService.mostWanted(PaginationUtil.safePage(page)));
        return "topics/most-wanted";
    }

    @GetMapping("/upcoming")
    public String upcoming(@RequestParam(defaultValue = "0") Integer page, Model model) {
        model.addAttribute("topics", topicService.upcoming(PaginationUtil.safePage(page)));
        return "topics/upcoming";
    }

    @GetMapping("/past")
    public String past(@RequestParam(defaultValue = "0") Integer page, Model model) {
        model.addAttribute("topics", topicService.past(PaginationUtil.safePage(page)));
        return "topics/past";
    }
}
