package com.trainingplatform.repository;

import com.trainingplatform.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByTopicIdAndUserId(Long topicId, Long userId);
    boolean existsByTopicIdAndUserId(Long topicId, Long userId);
    long countByTopicId(Long topicId);
    List<Enrollment> findByUserId(Long userId);
}
