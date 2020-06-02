package com.nighto.weebu.entity.character.event;

import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.event.EventHandler;
import com.nighto.weebu.event.game.DeathEvent;
import com.nighto.weebu.event.game.Event;

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
            character.spawn(15, 10);
        }
    }
}
