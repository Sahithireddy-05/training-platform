package com.trainingplatform.controller;

import com.trainingplatform.service.EnrollmentService;
import com.trainingplatform.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/topics/{topicId}")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/enroll")
    public String enroll(@PathVariable Long topicId, RedirectAttributes redirectAttributes) {
        enrollmentService.enroll(topicId, AuthUtil.currentUser());
        redirectAttributes.addFlashAttribute("success", "Enrollment updated.");
        return "redirect:/topics/" + topicId;
    }

    @PostMapping("/unenroll")
    public String unenroll(@PathVariable Long topicId, RedirectAttributes redirectAttributes) {
        enrollmentService.unenroll(topicId, AuthUtil.currentUser());
        redirectAttributes.addFlashAttribute("success", "You have unenrolled.");
        return "redirect:/topics/" + topicId;
    }
}
