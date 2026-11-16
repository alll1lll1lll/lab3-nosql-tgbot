package com.university.booking.commands.user;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.university.booking.client.BackendClient;
import com.university.booking.commands.Command;
import com.university.booking.commands.CommandType;
import com.university.booking.exception.NotRegisteredException;
import com.university.booking.service.dialog.StateService;
import com.university.booking.service.event.EventFeedService;
import org.springframework.stereotype.Component;

@Component
public class MyActionsCommand extends Command {

    private final BackendClient client;
    private final StateService stateService;
    private final EventFeedService eventFeed;

    public MyActionsCommand(BackendClient client, StateService stateService, EventFeedService eventFeed) {
        super(CommandType.MYACTIONS);
        this.client = client;
        this.stateService = stateService;
        this.eventFeed = eventFeed;
    }

    @Override
    public SendMessage handle(Update update, long chatId, String text) {
        String personId = stateService.get(chatId).getPersonId();
        if (personId == null) {
            throw new NotRegisteredException();
        }
        return eventFeed.first(chatId, "Мои действия", client.actorEventsQuery(personId, EventFeedService.PAGE_SIZE));
    }
}
