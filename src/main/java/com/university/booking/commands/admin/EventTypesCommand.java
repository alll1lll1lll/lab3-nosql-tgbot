package com.university.booking.commands.admin;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.request.SendMessage;
import com.university.booking.client.BackendClient;
import com.university.booking.commands.Command;
import com.university.booking.commands.CommandType;
import com.university.booking.dto.event.EventTypeDto;
import com.university.booking.ui.Buttons;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EventTypesCommand extends Command {

    private final BackendClient client;

    public EventTypesCommand(BackendClient client) {
        super(CommandType.EVENTTYPES);
        this.client = client;
    }

    @Override
    public SendMessage handle(Update update, long chatId, String text) {
        List<EventTypeDto> types = client.getEventTypes().stream()
                .filter(t -> !"EXPERIMENT".equals(t.getEntityType()))
                .toList();
        StringBuilder sb = new StringBuilder("Типы событий:\n\n");
        types.forEach(t -> sb.append(t.getType()).append(" — ").append(t.getTitle()).append("\n"));
        sb.append("\nЗапрос за период: /events <ТИП> 2026-11-01 2026-11-30");
        List<InlineKeyboardButton> buttons = types.stream()
                .map(t -> Buttons.button(t.getType(), "/events " + t.getType()))
                .toList();
        return new SendMessage(chatId, sb.toString()).replyMarkup(Buttons.grid(buttons, 2));
    }
}
