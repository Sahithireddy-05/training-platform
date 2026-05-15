package com.trainingplatform.util;

import com.trainingplatform.entity.Topic;
import com.trainingplatform.enums.TopicStatus;
import java.time.LocalDateTime;

public final class TopicLifecycleUtil {
    private TopicLifecycleUtil() {}

    public static boolean shouldComplete(Topic topic) {
        return topic.getStatus() == TopicStatus.SCHEDULED
                && topic.getScheduledAt() != null
                && topic.getScheduledAt().isBefore(LocalDateTime.now());
    }
}
