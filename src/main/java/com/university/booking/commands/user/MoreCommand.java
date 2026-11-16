package com.university.booking.commands.user;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.university.booking.commands.Command;
import com.university.booking.commands.CommandType;
import com.university.booking.service.event.EventFeedService;
import org.springframework.stereotype.Component;

@Component
public class MoreCommand extends Command {

    private final EventFeedService eventFeed;

    public MoreCommand(EventFeedService eventFeed) {
        super(CommandType.MORE);
        this.eventFeed = eventFeed;
    }

    @Override
    public SendMessage handle(Update update, long chatId, String text) {
        return eventFeed.next(chatId);
    }
}
