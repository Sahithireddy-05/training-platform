package com.trainingplatform.controller;

import com.trainingplatform.service.DashboardService;
import com.trainingplatform.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("user", AuthUtil.currentUser());
        dashboardService.fillDashboard(model, AuthUtil.currentUser());
        return "dashboard/dashboard";
    }
}
