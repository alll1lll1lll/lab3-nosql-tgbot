package com.university.booking.dto.event;

import java.util.Map;
import lombok.Data;

@Data
public class EventDto {
    private String eventId;
    private String occurredAt;
    private String eventType;
    private String entityType;
    private String entityId;
    private String actorId;
    private String summary;
    private Map<String, Object> payload;
    private Integer bucket;
    private Long seq;
}
