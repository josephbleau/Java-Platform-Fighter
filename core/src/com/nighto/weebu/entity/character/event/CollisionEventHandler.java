package com.nighto.weebu.entity.character.event;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.nighto.weebu.component.PhysicalComponent;
import com.nighto.weebu.component.character.CharacterDataComponent;
import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.component.character.StateComponent;
import com.nighto.weebu.controller.GameInput;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.attack.Attack;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.State;
import com.nighto.weebu.entity.stage.Stage;
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

        if (collisionEvent.entity1.getUuid() == character.getUuid()) {
            us = collisionEvent.entity1;
            ourShape = collisionEvent.shape1;
            them = collisionEvent.entity2;
            theirShape = collisionEvent.shape2;
        }

        if (collisionEvent.entity2.getUuid() == character.getUuid()) {
            us = collisionEvent.entity2;
            ourShape = collisionEvent.shape2;
            them = collisionEvent.entity1;
            theirShape = collisionEvent.shape1;
        }

        Gdx.app.debug("Collision", collisionEvent.entity1.getTag() + " collided with " + collisionEvent.entity2.getTag());

        // TODO: Refactor differing types of collision into their own handlers
        if (us != null) {
            if (them instanceof Stage) {
                handleCollisionWithStage(us, (Stage) them, theirShape);
            }

            if (them instanceof Attack) {
                Attack attack = (Attack) them;
                Character character = (Character) us;

                if (!attack.getOwner().equals(character)) {
                    Gdx.app.debug("Attack", character.getTag() + " was struck by an attack.");
                    character.enterKnockback(attack);
                }
            }
        }
    }

    private void handleCollisionWithStage(Entity entity, Stage stage, Shape2D stageShape) {
        PhysicalComponent physical = character.getComponent(PhysicalComponent.class);
        CharacterDataComponent characterData = character.getComponent(CharacterDataComponent.class);
        StateComponent state = character.getComponent(StateComponent.class);
        ControllerComponent controller = character.getComponent(ControllerComponent.class);

        /* Determine which direction we were heading so that we can set our position correctly. */
        boolean falling = physical.velocity.y < 0;

        Rectangle r1 = new Rectangle(physical.position.x, physical.position.y, physical.boundingBox.width, physical.boundingBox.height);
        Rectangle pr1 = new Rectangle(physical.prevPosition.x, physical.prevPosition.y, physical.prevBoundingBox.width, physical.prevBoundingBox.height);

        Rectangle stageRect = ((Rectangle) stageShape);

        /* Collide with the stage from the top */
        if (stage.isGround(stageShape)) {
            if (collidedFromTop(r1, pr1, stageRect)) {
                Gdx.app.debug("Collision", character.getTag() + " landed on the stage.");

                physical.prevPosition.y = physical.position.y;
                physical.position.y = (stageRect.y + stageRect.height);

                characterData.getActiveAttributes().resetJumps();
                characterData.getActiveAttributes().resetAirDodges();

                if (state.inState(State.AIRBORNE)) {
                    state.enterState(State.STANDING);
                    state.enterSubState(State.SUBSTATE_DEFAULT);
                }

                physical.prevVelocity.y = physical.velocity.y;
                physical.velocity.y = Math.max(0, physical.velocity.y);
                physical.floorStandingOn = stageRect;

                // Reduce sidestep timer additionally when touching ground
                // TODO: Change to separate timer?
                if (state.inState(State.DIRECTIONAL_AIRDODGE)) {
                    float t = characterData.getTimers().getSidestepTimeRemaining();
                    characterData.getTimers().setSidestepTimeRemaining(t/4f);
                }
            } else if (collidedFromBottom(r1, pr1, stageRect)) {
                Gdx.app.debug("Collision", character.getTag() + " bumped their head.");

                physical.prevPosition.y = physical.position.y;
                physical.position.y = stageRect.y - r1.height;

                state.enterState(State.AIRBORNE);
            } else if (collidedFromRight(r1, pr1, stageRect)) {
                Gdx.app.debug("Collision", character.getTag() + " collided with a wall.");

                if (falling && (controller.isPressed(GameInput.ControlLeftLight) || controller.isPressed(GameInput.ControlLeftHard))) {
                    state.enterState(State.WALLSLIDING, State.SUBSTATE_WALLSLIDING_LEFT);
                } else {
                    state.inState(State.AIRBORNE, State.SUBSTATE_DEFAULT);
                }

                physical.velocity.x = 0;
                physical.position.x = (stageRect.x + stageRect.width);
                physical.wallSlidingOn = stageRect;
                characterData.getActiveAttributes().incrementJumps();
            } else if (collidedFromLeft(r1, pr1, stageRect)) {
                Gdx.app.debug("Collision", character.getTag() + " collided with a wall.");

                // Move character out of the wall
                float characterWidth = physical.boundingBox.width;
                float difference = Math.abs((physical.position.x + characterWidth) - stageRect.x);

                physical.position.x -= difference;

                if (falling && (controller.isPressed(GameInput.ControlRightLight) || controller.isPressed(GameInput.ControlRightHard))) {
                    state.enterState(State.WALLSLIDING);
                    state.enterSubState(State.SUBSTATE_WALLSLIDING_RIGHT);
                } else {
                    state.inState(State.AIRBORNE, State.SUBSTATE_DEFAULT);
                }

                physical.velocity.x = 0;
                physical.wallSlidingOn = stageRect;
            }
        }

        Ledge ledge = stage.ifLedgeGetLedge(stageShape);
        if (falling && ledge != null) {
            Gdx.app.debug("Collision", character.getTag() + " snapped to a ledge.");
            character.snapToLedge(ledge);
        }
    }

    private boolean collidedFromLeft(Rectangle r1, Rectangle pr1, Rectangle r2) {
        float leftSide = r2.x;

        return
                /* Was */ pr1.x + pr1.width <= leftSide &&
                /* Now */ r1.x + r1.width > leftSide;
    }

    private boolean collidedFromRight(Rectangle r1, Rectangle pr1, Rectangle r2) {
        float rightSide = r2.x + r2.width;

        return
                /* Was */ pr1.x >= rightSide &&
                /* Now */ r1.x < rightSide;
    }

    private boolean collidedFromTop(Rectangle r1, Rectangle pr1, Rectangle r2) {
        float top = r2.y + r2.height;

        return
                /* Was */ pr1.y >= top &&
                /* Now */ r1.y < top;

    }

    private boolean collidedFromBottom(Rectangle r1, Rectangle pr1, Rectangle r2) {
        float bottom = r2.y;

        return
                /* Was */ pr1.y + pr1.height <= bottom &&
                /* Now */ r1.y + r1.height > bottom;
    }
}