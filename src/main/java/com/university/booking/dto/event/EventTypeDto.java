package com.university.booking.dto.event;

import lombok.Data;

@Data
public class EventTypeDto {
    private String type;
    private String entityType;
    private String title;
}
