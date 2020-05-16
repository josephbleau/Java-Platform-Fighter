package com.nighto.weebu.entity.character.event;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.stage.parts.Ledge;
import com.nighto.weebu.event.EventHandler;
import com.nighto.weebu.event.events.CollissionEvent;
import com.nighto.weebu.event.events.Event;

public class CollisionEventHandler implements EventHandler {
    private Character character;

    public CollisionEventHandler(Character character) {
        this.character = character;
    }

    @Override
    public boolean supports(Event event) {
        return event instanceof CollissionEvent;
    }

    @Override
    public void handle(Event event) {
        CollissionEvent collissionEvent = (CollissionEvent) event;

        Entity us = null;
        Entity them = null;
        Shape2D ourShape = null;
        Shape2D theirShape = null;

        if (collissionEvent.entity1 == character) {
            us = collissionEvent.entity1;
            ourShape = collissionEvent.shape1;
            them = collissionEvent.entity2;
            theirShape = collissionEvent.shape2;
        }

        if (collissionEvent.entity2 == character) {
            us = collissionEvent.entity2;
            ourShape = collissionEvent.shape2;
            them = collissionEvent.entity1;
            theirShape = collissionEvent.shape1;
        }

        if (them == character.getStage()) {
            /* Determine which direction we were heading so that we can set our position correctly. */
            boolean falling = character.getyVel() < 0;
            Rectangle stageRect = ((Rectangle)theirShape);

            /* Reset our x/y to be the x/y of the top of the rect that we collided with (so x, y+height) */
            if (falling && character.getyPrevPos() >= stageRect.y + stageRect.height && character.getyPos() <= stageRect.y + stageRect.height && character.getStage().isGround(stageRect)) {
                character.setyPos(stageRect.y + stageRect.height);
                character.setJumpCount(0);

                if (character.inState(State.AIRBORNE)) {
                    character.enterState(State.STANDING);
                    character.enterSubstate(State.SUBSTATE_DEFAULT);
                }

                character.setyVel(0);
            } else if (falling && theirShape instanceof Ledge && character.getStage().isLedge((Ledge) theirShape)){
                character.snapToLedge((Ledge) theirShape);
            } else {
                if (character.getxVel() < 0) {
                    character.setxPos(stageRect.x + stageRect.width);
                } else if (character.getxVel() > 0) {
                    character.setxPos(stageRect.x - 20);
                }

                character.setxVel(0);
            }
        }
    }
}
