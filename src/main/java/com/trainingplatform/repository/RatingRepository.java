package com.trainingplatform.repository;

import com.trainingplatform.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByTopicIdAndUserId(Long topicId, Long userId);
    List<Rating> findByUserId(Long userId);
}
