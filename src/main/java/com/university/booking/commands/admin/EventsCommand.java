package com.university.booking.commands.admin;

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
public class EventsCommand extends Command {

    private final BackendClient client;
    private final CommandParser parser;
    private final EventFeedService eventFeed;

    public EventsCommand(BackendClient client, CommandParser parser, EventFeedService eventFeed) {
        super(CommandType.EVENTS);
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
        String[] args = parser.extractArguments(text).split("\\s+");
        if (args[0].isBlank()) {
            return new SendMessage(chatId, "Формат: /events <ТИП> [ГГГГ-ММ-ДД] [ГГГГ-ММ-ДД]\n"
                    + "Например: /events BOOKING_APPROVED 2026-11-01 2026-11-30")
                    .replyMarkup(Buttons.row(Buttons.button("Типы событий", "/eventtypes")));
        }
        String type = args[0].toUpperCase();
        String from = args.length > 1 ? args[1] : null;
        String to = args.length > 2 ? args[2] : null;
        String title = type + (from != null ? " с " + from : "") + (to != null ? " по " + to : "");
        return eventFeed.first(chatId, title, client.typeEventsQuery(type, from, to, EventFeedService.PAGE_SIZE));
    }
}
