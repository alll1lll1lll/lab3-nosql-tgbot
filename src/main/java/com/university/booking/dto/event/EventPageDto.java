package com.university.booking.dto.event;

import java.util.List;
import lombok.Data;

@Data
public class EventPageDto {
    private List<EventDto> items = List.of();
    private int size;
    private String nextPageToken;
    private List<Integer> bucketsScanned;
    private String consistency;
    private double tookMs;
}
