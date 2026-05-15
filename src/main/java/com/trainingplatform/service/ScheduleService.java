package com.trainingplatform.service;

import com.trainingplatform.dto.ScheduleDto;
import com.trainingplatform.entity.Topic;
import com.trainingplatform.entity.User;
import com.trainingplatform.enums.TopicStatus;
import com.trainingplatform.exception.InvalidLifecycleException;
import com.trainingplatform.exception.UnauthorizedActionException;
import com.trainingplatform.exception.ValidationException;
import com.trainingplatform.repository.EnrollmentRepository;
import com.trainingplatform.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class ScheduleService {
    private final TopicService topicService;
    private final TopicRepository topicRepository;
    private final EnrollmentRepository enrollmentRepository;

    public ScheduleService(TopicService topicService, TopicRepository topicRepository, EnrollmentRepository enrollmentRepository) {
        this.topicService = topicService;
        this.topicRepository = topicRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public void schedule(Long topicId, ScheduleDto dto, User user) {
        Topic topic = topicService.get(topicId);
        if (topic.getStatus() != TopicStatus.CLAIMED && topic.getStatus() != TopicStatus.SCHEDULED) {
            throw new InvalidLifecycleException("Only Claimed or Scheduled topics can be scheduled");
        }
        if (topic.getSpeaker() == null || !topic.getSpeaker().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("Only the speaker can schedule this topic");
        }
        if (dto.getScheduledAt() == null || !dto.getScheduledAt().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Scheduled date and time must be in the future");
        }
        long enrolled = enrollmentRepository.countByTopicId(topicId);
        if (dto.getCapacity() != null && dto.getCapacity() < enrolled) {
            throw new ValidationException("Capacity cannot be lower than current enrolled count: " + enrolled);
        }
        topic.setScheduledAt(dto.getScheduledAt());
        topic.setDurationMinutes(dto.getDurationMinutes());
        topic.setLocation(dto.getLocation());
        topic.setCapacity(dto.getCapacity());
        topic.setStatus(TopicStatus.SCHEDULED);
        topicRepository.save(topic);
    }
}
