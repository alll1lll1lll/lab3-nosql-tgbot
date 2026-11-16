package com.university.booking.commands.user;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.university.booking.client.BackendClient;
import com.university.booking.commands.Command;
import com.university.booking.commands.CommandType;
import com.university.booking.service.event.EventFeedService;
import com.university.booking.service.routing.CommandParser;
import com.university.booking.ui.Buttons;
import org.springframework.stereotype.Component;

@Component
public class HistoryCommand extends Command {

    private final BackendClient client;
    private final CommandParser parser;
    private final EventFeedService eventFeed;

    public HistoryCommand(BackendClient client, CommandParser parser, EventFeedService eventFeed) {
        super(CommandType.HISTORY);
        this.client = client;
        this.parser = parser;
        this.eventFeed = eventFeed;
    }

    @Override
    public boolean acceptsArguments() {
        return true;
    }

    @Override
    public SendMessage handle(Update update, long chatId, String text) {
        String id = parser.extractArguments(text);
        if (id.isBlank()) {
            return new SendMessage(chatId, "Укажи ID заявки: /history <id>")
                    .replyMarkup(Buttons.row(Buttons.button("Мои заявки", "/bookings")));
        }
        return eventFeed.first(chatId, "История заявки " + id,
                client.entityEventsQuery("BOOKING", id, EventFeedService.PAGE_SIZE));
    }
}
