package com.trainingplatform.service;

import com.trainingplatform.entity.User;
import com.trainingplatform.repository.EnrollmentRepository;
import com.trainingplatform.repository.RatingRepository;
import com.trainingplatform.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class DashboardService {
    private final TopicRepository topicRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final RatingRepository ratingRepository;

    public DashboardService(TopicRepository topicRepository, EnrollmentRepository enrollmentRepository, RatingRepository ratingRepository) {
        this.topicRepository = topicRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.ratingRepository = ratingRepository;
    }

    public void fillDashboard(Model model, User user) {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        model.addAttribute("requested", topicRepository.findByRequesterId(user.getId(), pageable).getContent());
        model.addAttribute("speaking", topicRepository.findBySpeakerId(user.getId(), pageable).getContent());
        model.addAttribute("enrollments", enrollmentRepository.findByUserId(user.getId()));
        model.addAttribute("ratings", ratingRepository.findByUserId(user.getId()));
    }
}
