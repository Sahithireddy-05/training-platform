package com.trainingplatform.repository;

import com.trainingplatform.entity.Topic;
import com.trainingplatform.enums.TopicStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    Page<Topic> findByStatus(TopicStatus status, Pageable pageable);
    Page<Topic> findByRequesterId(Long requesterId, Pageable pageable);
    Page<Topic> findBySpeakerId(Long speakerId, Pageable pageable);
    Page<Topic> findByEnrollmentsUserId(Long userId, Pageable pageable);

    @Query(value = "select t from Topic t where t.status in (com.trainingplatform.enums.TopicStatus.OPEN, com.trainingplatform.enums.TopicStatus.CLAIMED) order by size(t.recommendations) desc",
            countQuery = "select count(t) from Topic t where t.status in (com.trainingplatform.enums.TopicStatus.OPEN, com.trainingplatform.enums.TopicStatus.CLAIMED)")
    Page<Topic> findMostWanted(Pageable pageable);

    Page<Topic> findByStatusOrderByScheduledAtAsc(TopicStatus status, Pageable pageable);
    Page<Topic> findByStatusOrderByScheduledAtDesc(TopicStatus status, Pageable pageable);
}
