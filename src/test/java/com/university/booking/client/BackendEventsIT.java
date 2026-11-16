package com.university.booking.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.university.booking.commands.user.EventFormatter;
import com.university.booking.dto.event.EventDto;
import com.university.booking.dto.event.EventPageDto;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@EnabledIfEnvironmentVariable(named = "BACKEND_IT", matches = "1")
class BackendEventsIT {

    private static final String ADMIN_ID = "100000";
    private static final int MAX_PAGES = 50;

    private final BackendClient client = client();

    private static BackendClient client() {
        RestTemplate rest = new RestTemplate();
        rest.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("Person-Id", PersonIdContext.get());
            return execution.execute(request, body);
        });
        BackendClient c = new BackendClient(rest);
        ReflectionTestUtils.setField(c, "baseUrl", System.getenv().getOrDefault("BACKEND_URL", "http://localhost:8080"));
        return c;
    }

    @BeforeEach
    void signIn() {
        PersonIdContext.set(ADMIN_ID);
    }

    @AfterEach
    void signOut() {
        PersonIdContext.clear();
    }

    @Test
    void typeQueryPagesThroughPeriodWithoutDuplicates() {
        String query = client.typeEventsQuery("BOOKING_CREATED", "2026-01-01", "2030-12-31", 2);
        Set<String> ids = new HashSet<>();
        EventPageDto page = client.getEvents(query, null);
        page.getItems().forEach(e -> ids.add(e.getEventId()));
        for (int pages = 1; page.getNextPageToken() != null && pages < MAX_PAGES; pages++) {
            page = client.getEvents(query, page.getNextPageToken());
            page.getItems().forEach(e -> assertThat(ids.add(e.getEventId())).as("дубль на странице").isTrue());
        }
        assertThat(ids).isNotEmpty();
    }

    @Test
    void actorQueryReturnsOnlyOwnEventsAndFormats() {
        EventPageDto page = client.getEvents(client.actorEventsQuery(ADMIN_ID, 5), null);
        assertThat(page.getItems()).isNotEmpty().allSatisfy(e -> assertThat(e.getActorId()).isEqualTo(ADMIN_ID));
        EventDto first = page.getItems().getFirst();
        assertThat(EventFormatter.format(first)).contains(first.getEventType(), first.getSummary());
    }

    @Test
    void eventTypesListed() {
        assertThat(client.getEventTypes()).extracting("type").contains("BOOKING_APPROVED", "ROOM_CREATED");
    }
}
