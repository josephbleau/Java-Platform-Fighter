package com.nighto.weebu.entity.character.event;

import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.event.EventHandler;
import com.nighto.weebu.event.events.DeathEvent;
import com.nighto.weebu.event.events.Event;

public class DeathhEventHandler implements EventHandler {
    private Character character;

    public DeathhEventHandler(Character character) {
        this.character = character;

    }
    @Override
    public boolean supports(Event event) {
        return event instanceof DeathEvent;
    }

    @Override
    public void handle(Event event) {
        if (((DeathEvent) event).entity == character) {
            character.spawn(1920/2, 400);
        }
    }
}
