package com.trainingplatform.repository;

import com.trainingplatform.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Optional<Recommendation> findByTopicIdAndUserId(Long topicId, Long userId);
    boolean existsByTopicIdAndUserId(Long topicId, Long userId);
    long countByTopicId(Long topicId);
}
