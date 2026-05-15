package com.trainingplatform.service;

import com.trainingplatform.dto.TopicCreateDto;
import com.trainingplatform.dto.TopicUpdateDto;
import com.trainingplatform.entity.Topic;
import com.trainingplatform.entity.User;
import com.trainingplatform.enums.TopicStatus;
import com.trainingplatform.exception.InvalidLifecycleException;
import com.trainingplatform.exception.ResourceNotFoundException;
import com.trainingplatform.exception.UnauthorizedActionException;
import com.trainingplatform.repository.TopicRepository;
import com.trainingplatform.util.TopicLifecycleUtil;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TopicService {
    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Transactional
    public Topic create(TopicCreateDto dto, User user) {
        Topic topic = new Topic();
        topic.setTitle(dto.getTitle().trim());
        topic.setDescription(dto.getDescription());
        topic.setRequester(user);
        topic.setStatus(TopicStatus.OPEN);
        return topicRepository.save(topic);
    }

    @Transactional
    public Topic get(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        completeIfPast(topic);
        return topic;
    }

    @Transactional
    public void completeIfPast(Topic topic) {
        if (TopicLifecycleUtil.shouldComplete(topic)) {
            topic.setStatus(TopicStatus.COMPLETED);
            topicRepository.save(topic);
        }
    }

    @Transactional
    public Page<Topic> list(String status, String role, User user, int page, String sort) {
        Pageable pageable = PageRequest.of(page, 20, sortFor(sort));
        Page<Topic> result;
        if ("requested".equals(role)) result = topicRepository.findByRequesterId(user.getId(), pageable);
        else if ("speaking".equals(role)) result = topicRepository.findBySpeakerId(user.getId(), pageable);
        else if ("enrolled".equals(role)) result = topicRepository.findByEnrollmentsUserId(user.getId(), pageable);
        else if (status != null && !status.isBlank()) result = topicRepository.findByStatus(TopicStatus.valueOf(status), pageable);
        else result = topicRepository.findAll(pageable);
        result.forEach(this::completeIfPast);
        return result;
    }

    public Page<Topic> mostWanted(int page) {
        Pageable pageable = PageRequest.of(page, 20);
        return topicRepository.findMostWanted(pageable);
    }

    public Page<Topic> upcoming(int page) {
        return topicRepository.findByStatusOrderByScheduledAtAsc(TopicStatus.SCHEDULED, PageRequest.of(page, 20));
    }

    public Page<Topic> past(int page) {
        return topicRepository.findByStatusOrderByScheduledAtDesc(TopicStatus.COMPLETED, PageRequest.of(page, 20));
    }

    @Transactional
    public void update(Long id, TopicUpdateDto dto, User user) {
        Topic topic = get(id);
        if (!topic.getRequester().getId().equals(user.getId())) throw new UnauthorizedActionException("Only the requester can edit this topic");
        if (topic.getStatus() != TopicStatus.OPEN) throw new InvalidLifecycleException("Topics can only be edited while Open");
        topic.setTitle(dto.getTitle().trim());
        topic.setDescription(dto.getDescription());
        topicRepository.save(topic);
    }

    @Transactional
    public void claim(Long id, User user) {
        Topic topic = get(id);
        if (topic.getStatus() != TopicStatus.OPEN) throw new InvalidLifecycleException("Only Open topics can be claimed");
        topic.setSpeaker(user);
        topic.setStatus(TopicStatus.CLAIMED);
        topicRepository.save(topic);
    }

    @Transactional
    public void unclaim(Long id, User user) {
        Topic topic = get(id);
        if (topic.getStatus() != TopicStatus.CLAIMED) throw new InvalidLifecycleException("Only Claimed topics can be unclaimed");
        if (!topic.getSpeaker().getId().equals(user.getId())) throw new UnauthorizedActionException("Only the speaker can unclaim this topic");
        topic.setSpeaker(null);
        topic.setStatus(TopicStatus.OPEN);
        topicRepository.save(topic);
    }

    @Transactional
    public void cancel(Long id, User user) {
        Topic topic = get(id);
        boolean requester = topic.getRequester().getId().equals(user.getId());
        boolean speaker = topic.getSpeaker() != null && topic.getSpeaker().getId().equals(user.getId());
        if (topic.getStatus() == TopicStatus.COMPLETED || topic.getStatus() == TopicStatus.CANCELLED) {
            throw new InvalidLifecycleException("Completed and Cancelled topics cannot be changed");
        }
        if ((topic.getStatus() == TopicStatus.OPEN || topic.getStatus() == TopicStatus.CLAIMED) && !requester) {
            throw new UnauthorizedActionException("Only the requester can cancel Open or Claimed topics");
        }
        if (topic.getStatus() == TopicStatus.SCHEDULED && !(requester || speaker)) {
            throw new UnauthorizedActionException("Only the requester or speaker can cancel this session");
        }
        topic.setStatus(TopicStatus.CANCELLED);
        topicRepository.save(topic);
    }

    private Sort sortFor(String sort) {
        if ("recommendations".equals(sort)) return Sort.by(Sort.Direction.DESC, "createdAt");
        if ("scheduledAt".equals(sort)) return Sort.by(Sort.Direction.ASC, "scheduledAt");
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}
