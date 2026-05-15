package com.trainingplatform.service;

import com.trainingplatform.entity.Recommendation;
import com.trainingplatform.entity.Topic;
import com.trainingplatform.entity.User;
import com.trainingplatform.enums.TopicStatus;
import com.trainingplatform.exception.InvalidLifecycleException;
import com.trainingplatform.exception.UnauthorizedActionException;
import com.trainingplatform.repository.RecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final TopicService topicService;

    public RecommendationService(RecommendationRepository recommendationRepository, TopicService topicService) {
        this.recommendationRepository = recommendationRepository;
        this.topicService = topicService;
    }

    @Transactional
    public void toggle(Long topicId, User user) {
        Topic topic = topicService.get(topicId);
        if (topic.getRequester().getId().equals(user.getId())) throw new UnauthorizedActionException("You cannot recommend your own topic");
        if (topic.getStatus() != TopicStatus.OPEN && topic.getStatus() != TopicStatus.CLAIMED) {
            throw new InvalidLifecycleException("Only Open or Claimed topics can be recommended");
        }
        recommendationRepository.findByTopicIdAndUserId(topicId, user.getId()).ifPresentOrElse(
                recommendationRepository::delete,
                () -> {
                    Recommendation recommendation = new Recommendation();
                    recommendation.setTopic(topic);
                    recommendation.setUser(user);
                    recommendationRepository.save(recommendation);
                }
        );
    }
}
