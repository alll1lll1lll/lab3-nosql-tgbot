package com.university.booking.commands.user;

import com.university.booking.dto.event.EventDto;
import com.university.booking.dto.event.EventPageDto;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;

public final class EventFormatter {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final Set<String> SHOWN_KEYS = Set.of("reason", "text", "status", "name", "eventName");

    private EventFormatter() {}

    public static String formatPage(String title, EventPageDto page, boolean first) {
        StringBuilder sb = new StringBuilder(title).append(first ? "" : " (продолжение)").append("\n\n");
        if (page.getItems().isEmpty()) {
            sb.append(first ? "Событий нет.\n\n" : "Больше событий нет.\n\n");
        }
        page.getItems().forEach(e -> sb.append(format(e)).append("\n\n"));
        sb.append("Cassandra: ").append(page.getConsistency()).append(", ").append(page.getTookMs()).append(" мс");
        if (page.getBucketsScanned() != null && !page.getBucketsScanned().isEmpty()) {
            sb.append(", бакеты ").append(page.getBucketsScanned());
        }
        return sb.toString();
    }

    public static String format(EventDto e) {
        StringBuilder sb = new StringBuilder();
        sb.append(time(e.getOccurredAt())).append(" · ").append(e.getEventType()).append("\n   ")
          .append(e.getSummary()).append(", автор ").append(e.getActorId());
        if ("BOOKING".equals(e.getEntityType())) {
            sb.append("\n   заявка ").append(e.getEntityId());
        } else if (e.getEntityType() != null) {
            sb.append("\n   ").append(e.getEntityType()).append(" ").append(e.getEntityId());
        }
        Map<String, Object> payload = e.getPayload();
        if (payload != null) {
            if (payload.get("from") != null && payload.get("to") != null) {
                sb.append("\n   ").append(payload.get("from")).append(" → ").append(payload.get("to"));
            }
            payload.forEach((key, value) -> {
                if (SHOWN_KEYS.contains(key)) {
                    sb.append("\n   ").append(key).append(": ").append(value);
                }
            });
        }
        return sb.toString();
    }

    private static String time(String iso) {
        if (iso == null) {
            return "";
        }
        try {
            return OffsetDateTime.parse(iso).format(TIME);
        } catch (DateTimeParseException ex) {
            return iso;
        }
    }
}
