package com.university.booking.service.event;

import com.pengrad.telegrambot.request.SendMessage;
import com.university.booking.client.BackendClient;
import com.university.booking.commands.user.EventFormatter;
import com.university.booking.dto.event.EventPageDto;
import com.university.booking.service.dialog.StateService;
import com.university.booking.state.Context;
import com.university.booking.ui.Buttons;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventFeedService {

    public static final int PAGE_SIZE = 10;

    private final BackendClient client;
    private final StateService stateService;

    public SendMessage first(long chatId, String title, String query) {
        Context ctx = stateService.get(chatId);
        ctx.setEventsQuery(query);
        ctx.setEventsTitle(title);
        ctx.setEventsNextToken(null);
        return render(chatId, ctx, client.getEvents(query, null), true);
    }

    public SendMessage next(long chatId) {
        Context ctx = stateService.get(chatId);
        if (ctx.getEventsQuery() == null || ctx.getEventsNextToken() == null) {
            return new SendMessage(chatId, "Больше страниц нет. Начни новый запрос: /history, /myactions или /events");
        }
        return render(chatId, ctx, client.getEvents(ctx.getEventsQuery(), ctx.getEventsNextToken()), false);
    }

    private static SendMessage render(long chatId, Context ctx, EventPageDto page, boolean first) {
        ctx.setEventsNextToken(page.getNextPageToken());
        SendMessage message = new SendMessage(chatId, EventFormatter.formatPage(ctx.getEventsTitle(), page, first));
        return page.getNextPageToken() == null
                ? message
                : message.replyMarkup(Buttons.row(Buttons.button("Ещё " + PAGE_SIZE, "/more")));
    }
}
