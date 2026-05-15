package com.trainingplatform.service;

import com.trainingplatform.dto.RatingDto;
import com.trainingplatform.entity.Rating;
import com.trainingplatform.entity.Topic;
import com.trainingplatform.entity.User;
import com.trainingplatform.enums.TopicStatus;
import com.trainingplatform.exception.InvalidLifecycleException;
import com.trainingplatform.exception.UnauthorizedActionException;
import com.trainingplatform.repository.EnrollmentRepository;
import com.trainingplatform.repository.RatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TopicService topicService;

    public RatingService(RatingRepository ratingRepository, EnrollmentRepository enrollmentRepository, TopicService topicService) {
        this.ratingRepository = ratingRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.topicService = topicService;
    }

    @Transactional
    public void rate(Long topicId, RatingDto dto, User user) {
        Topic topic = topicService.get(topicId);
        if (topic.getStatus() != TopicStatus.COMPLETED) throw new InvalidLifecycleException("Only Completed sessions can be rated");
        if (topic.getSpeaker() != null && topic.getSpeaker().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("The speaker cannot rate their own session");
        }
        var enrollment = enrollmentRepository.findByTopicIdAndUserId(topicId, user.getId())
                .orElseThrow(() -> new UnauthorizedActionException("Only enrolled attendees can rate this session"));
        if (topic.getScheduledAt() != null && enrollment.getEnrolledAt().isAfter(topic.getScheduledAt())) {
            throw new UnauthorizedActionException("Only users enrolled before the session start can rate");
        }
        Rating rating = ratingRepository.findByTopicIdAndUserId(topicId, user.getId()).orElseGet(Rating::new);
        rating.setTopic(topic);
        rating.setUser(user);
        rating.setStars(dto.getStars());
        rating.setComment(dto.getComment());
        rating.setUpdatedAt(LocalDateTime.now());
        ratingRepository.save(rating);
    }
}
