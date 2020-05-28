package com.nighto.weebu.entity.character.event;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.attack.Attack;
import com.nighto.weebu.entity.attack.AttackData;
import com.nighto.weebu.entity.attack.MeleeAttack;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.event.EventHandler;
import com.nighto.weebu.event.game.AttackEvent;
import com.nighto.weebu.event.game.Event;

public class AttackEventListener implements EventHandler {
    private Character character;

    public AttackEventListener(Character character) {
        this.character = character;
    }

    @Override
    public boolean supports(Event event) {
        return event instanceof AttackEvent;
    }

    @Override
    public void handle(Event event) {
        AttackEvent attackEvent = (AttackEvent) event;

        // Attacks don't affect the player that spawned them (generally they wont due to the hitbox anyway, but
        // why bother checking?)
        if(attackEvent.owner.getUuid() == character.getUuid()) {
            return;
        }

        Polygon hitbox = attackEvent.hitbox;
        boolean collided = false;

        Rectangle boundingBox = hitbox.getBoundingRectangle();

        for (Rectangle characterCollidables : character.getCollidables()) {
            collided |= characterCollidables.overlaps(boundingBox);
        }

        if (collided) {
            handleAttackCollision(attackEvent);
        }
    }

    private void handleAttackCollision(AttackEvent attackEvent) {
        character.enterKnockback(attackEvent.attackData);
    }
}