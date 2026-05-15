package com.trainingplatform.service;

import com.trainingplatform.entity.Enrollment;
import com.trainingplatform.entity.Topic;
import com.trainingplatform.entity.User;
import com.trainingplatform.enums.TopicStatus;
import com.trainingplatform.exception.CapacityExceededException;
import com.trainingplatform.exception.InvalidLifecycleException;
import com.trainingplatform.exception.UnauthorizedActionException;
import com.trainingplatform.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final TopicService topicService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, TopicService topicService) {
        this.enrollmentRepository = enrollmentRepository;
        this.topicService = topicService;
    }

    @Transactional
    public void enroll(Long topicId, User user) {
        Topic topic = topicService.get(topicId);
        if (topic.getStatus() != TopicStatus.SCHEDULED) throw new InvalidLifecycleException("Only Scheduled sessions can be enrolled in");
        if (topic.getSpeaker().getId().equals(user.getId())) throw new UnauthorizedActionException("The speaker cannot enroll in their own session");
        if (enrollmentRepository.existsByTopicIdAndUserId(topicId, user.getId())) return;
        long count = enrollmentRepository.countByTopicId(topicId);
        if (topic.getCapacity() != null && count >= topic.getCapacity()) {
            throw new CapacityExceededException("This session is full. Capacity is " + topic.getCapacity());
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setTopic(topic);
        enrollment.setUser(user);
        enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void unenroll(Long topicId, User user) {
        Topic topic = topicService.get(topicId);
        if (topic.getStatus() != TopicStatus.SCHEDULED || topic.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new InvalidLifecycleException("Enrollment changes are only allowed before the session starts");
        }
        enrollmentRepository.findByTopicIdAndUserId(topicId, user.getId()).ifPresent(enrollmentRepository::delete);
    }
}
