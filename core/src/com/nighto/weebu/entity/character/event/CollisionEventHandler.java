package com.nighto.weebu.entity.character.event;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.attack.Attack;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.player.State;
import com.nighto.weebu.entity.stage.parts.Ledge;
import com.nighto.weebu.event.EventHandler;
import com.nighto.weebu.event.events.CollisionEvent;
import com.nighto.weebu.event.events.Event;

public class CollisionEventHandler implements EventHandler {
    private Character character;

    public CollisionEventHandler(Character character) {
        this.character = character;
    }

    @Override
    public boolean supports(Event event) {
        return event instanceof CollisionEvent;
    }

    @Override
    public void handle(Event event) {
        CollisionEvent collisionEvent = (CollisionEvent) event;

        Entity us = null;
        Entity them = null;
        Shape2D ourShape = null;
        Shape2D theirShape = null;

        if (collisionEvent.entity1 == character) {
            us = collisionEvent.entity1;
            ourShape = collisionEvent.shape1;
            them = collisionEvent.entity2;
            theirShape = collisionEvent.shape2;
        }

        if (collisionEvent.entity2 == character) {
            us = collisionEvent.entity2;
            ourShape = collisionEvent.shape2;
            them = collisionEvent.entity1;
            theirShape = collisionEvent.shape1;
        }

        // TODO: Refactor differing types of collision into their own handlers
        if (them == character.getStage()) {
            PhysicalComponent physicalComponent = character.getComponent(PhysicalComponent.class);

            /* Determine which direction we were heading so that we can set our position correctly. */
            boolean falling = physicalComponent.velocity.y < 0;
            Rectangle stageRect = ((Rectangle)theirShape);

            /* Reset our x/y to be the x/y of the top of the rect that we collided with (so x, y+height) */
            if (falling && physicalComponent.prevPosition.y >= stageRect.y + stageRect.height && physicalComponent.position.y <= stageRect.y + stageRect.height && character.getStage().isGround(stageRect)) {
                physicalComponent.position.y = (stageRect.y + stageRect.height);
                character.setJumpCount(0);

                if (character.inState(State.AIRBORNE)) {
                    character.enterState(State.STANDING);
                    character.enterSubstate(State.SUBSTATE_DEFAULT);
                }

                physicalComponent.velocity.y = 0;
            } else if (falling && theirShape instanceof Ledge && character.getStage().isLedge((Ledge) theirShape)){
                character.snapToLedge((Ledge) theirShape);
            } else {
                if (physicalComponent.velocity.x < 0) {
                    if(falling && (character.getGameController().isPressed(GameInput.ControlLeftLight) || character.getGameController().isPressed(GameInput.ControlLeftHard))) {
                        character.enterState(State.WALLSLIDING);
                        character.enterSubstate(State.SUBSTATE_WALLSLIDING_LEFT);
                    } else {
                        character.inState(State.AIRBORNE);
                        character.inState(State.SUBSTATE_DEFAULT);
                    }

                    physicalComponent.position.x = (stageRect.x + stageRect.width);
                } else if (physicalComponent.velocity.x > 0) {
                    // Move character out of the wall
                    float difference = Math.abs((physicalComponent.position.x + character.getWidth()) - stageRect.x);
                    physicalComponent.position.x -= difference;

                    if(falling && (character.getGameController().isPressed(GameInput.ControlRightLight) || character.getGameController().isPressed(GameInput.ControlRightHard))) {
                        character.enterState(State.WALLSLIDING);
                        character.enterSubstate(State.SUBSTATE_WALLSLIDING_RIGHT);
                    } else {
                        character.inState(State.AIRBORNE);
                        character.inState(State.SUBSTATE_DEFAULT);
                    }
                }

                physicalComponent.velocity.x = 0;
            }
        }

        if (them instanceof Attack) {
            Attack attack = (Attack) them;
            Character character = (Character) us;

            if (!attack.getOwner().equals(character)) {
                character.enterKnockback(attack);
            }
        }
    }
}
