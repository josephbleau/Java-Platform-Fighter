package com.nighto.weebu.entity.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.attack.Attack;
import com.nighto.weebu.entity.parts.Ledge;
import com.nighto.weebu.entity.stage.Stage;
import com.nighto.weebu.event.events.CollissionEvent;
import com.nighto.weebu.event.events.DeathEvent;
import com.nighto.weebu.event.events.Event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Character extends Entity {

    protected boolean grounded = false;

    /** The maximum y-velocity that can be reached via gravity. Being knocked toward the ground will ignore this value. **/
    protected float maximumNaturalDownwardVelocity = -10;

    /** The maximum x-velocity that can be reached via controller inputs while grounded. **/
    protected float maximumNaturalGroundSpeed = 10;

    /** The maximum x-velocity that can be reached via controller inputs while in the air. **/
    protected float maximumNaturalAirSpeed = 5;

    /** The initial velocity boost of a short hop **/
    protected float shortHopVel = 35;

    /** The initial velocity boost of a full hop **/
    protected float fullHopVel = 50;

    /** The amount of time until you jump after pressing jump (from the ground) in seconds. **/
    protected float jumpSquatTime = 0.05f;

    /** The amount of time left in jump squat. **/
    protected float jumpSquatTimeRemaining = jumpSquatTime;

    /** The total amount of time spent in sidestep **/
    protected float sidestepTime = 0.37f;

    /** The amount of time left in the current sidestep. **/
    protected float sidestepTimeRemaining = sidestepTime;

    protected Shield shield;

    protected Stage stage;

    protected State prevState;
    protected State state;

    protected State subState;
    protected State prevSubstate;

    protected Color sidestepColor;

    protected boolean facingRight = true;

    protected List<Attack> attacks = new ArrayList<>();

    public Character(Stage stage) {
        this.stage = stage;
        sidestepColor = Color.LIGHT_GRAY;

        enterState(State.CLEAR);
        enterSubstate(State.SUBSTATE_CLEAR);
    }

    @Override
    public void update(float delta) {
        /* Apply gravity */
        if (yVel > maximumNaturalDownwardVelocity) {
            yVel = Math.max(maximumNaturalDownwardVelocity, yVel - stage.getGravity());
        }

        if (state == State.JUMPSQUAT) {
            jumpSquatTimeRemaining -= delta;

            if (jumpSquatTimeRemaining <= 0) {
                jumpSquatTimeRemaining = jumpSquatTime;
                enterState(State.EXIT_JUMPSQUAT);
            }
        }

        if (state == State.SIDESTEPPING) {
            sidestepTimeRemaining -= delta;
        }

        if (sidestepTimeRemaining <= 0) {
            currentColor = defaultColor;
            sidestepTimeRemaining = sidestepTime;
            enterState(prevState);
        }

        shield.update(delta);
        updateAttacks(delta);

        if (!inState(State.HANGING, State.JUMPSQUAT, State.AIRBORNE, State.SIDESTEPPING)) {
            if (xVel > 0) {
                facingRight = true;
            } else if (xVel < 0) {
                facingRight = false;
            }
        }

        super.update(delta);
    }

    private void updateAttacks(float delta) {
        boolean attackPlaying = false;
        Iterator<Attack> attackIterator = attacks.iterator();
        while (attackIterator.hasNext()) {

            Attack attack = attackIterator.next();
            attack.update(delta);
            attackPlaying = attack.isPlaying();

            if (!attack.isActive()) {
                attackIterator.remove();
            }
        }

        if (!attackPlaying && inSubState(State.SUBSTATE_ATTACKING)) {
            enterSubstate(State.SUBSTATE_CLEAR);
        }
    }

    @Override
    public void notify(Event event) {
        super.notify(event);

        if (event instanceof CollissionEvent) {
            CollissionEvent collissionEvent = (CollissionEvent) event;

            Entity us = null;
            Entity them = null;
            Shape2D ourShape = null;
            Shape2D theirShape = null;

            if (collissionEvent.entity1 == this) {
                us = collissionEvent.entity1;
                ourShape = collissionEvent.shape1;
                them = collissionEvent.entity2;
                theirShape = collissionEvent.shape2;
            }

            if (collissionEvent.entity2 == this) {
                us = collissionEvent.entity2;
                ourShape = collissionEvent.shape2;
                them = collissionEvent.entity1;
                theirShape = collissionEvent.shape1;
            }

            if (them == stage) {
                /* Determine which direction we were heading so that we can set our position correctly. */
                boolean falling = yVel < 0;
                Rectangle stageRect = ((Rectangle)theirShape);

                /* Reset our x/y to be the x/y of the top of the rect that we collided with (so x, y+height) */
                if (falling && yPrevPos >= stageRect.y + stageRect.height && yPos <= stageRect.y + stageRect.height && stage.isGround(stageRect)) {
                    yPos = stageRect.y + stageRect.height;

                    if (inState(State.AIRBORNE)) {
                        enterState(State.STANDING);
                        enterSubstate(State.SUBSTATE_CLEAR);
                    }

                    yVel = 0;
                } else if (falling && theirShape instanceof Ledge && stage.isLedge((Ledge) theirShape)){
                    snapToLedge((Ledge) theirShape);
                } else {
                    if (xVel < 0) {
                        xPos = stageRect.x + stageRect.width;
                    } else if (xVel > 0) {
                        xPos = stageRect.x - 20;
                    }

                    xVel = 0;
                }
            }
        }

        if (event instanceof DeathEvent) {
            if (((DeathEvent) event).entity == this) {
                spawn(400, 400);
            }
        }
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        super.render(shapeRenderer);

        if (inState(State.SIDESTEPPING)) {
            currentColor = sidestepColor;
        } else {
            currentColor = defaultColor;
        }

        if (inState(State.SHIELDING)) {
            shield.setActive(true);
        } else {
            shield.setActive(false);
        }

        shield.render(shapeRenderer);
        attacks.forEach(attack -> attack.render(shapeRenderer));
    }

    @Override
    public CollissionEvent intersects(Entity otherEntity) {
        CollissionEvent shieldCollision = shield.intersects(otherEntity);

        if (shieldCollision != null) {
            return shieldCollision;
        }

        return super.intersects(otherEntity);
    }

    public void handleInput(GamecubeController gamecubeController) {}

    public void enterState(State newState) {
        prevState = state;
        state = newState;
    }

    public void enterSubstate(State newSubstate) {
        prevSubstate = subState;
        subState = newSubstate;
    }

    public boolean inState(State ... states) {
        boolean outcome = false;

        for (State checkingState : states) {
            outcome |= state == checkingState;
        }

        return outcome;
    }

    public boolean inSubState(State... substates) {
        for(State checkingState : substates) {
            if (subState.equals(checkingState)) {
                return true;
            }
        }
        return false;
    }

    public void startAttack(Attack attack) {
        attack.spawn(xPos, yPos);

        if (attack.isPlaying()) {
            enterSubstate(State.SUBSTATE_ATTACKING);
        }

        attacks.add(attack);
    }

    private void snapToLedge(Ledge ledge) {
        setActive(false); // prevents gravity from applying
        enterState(State.HANGING);

        Rectangle playerRect = getRects().get(0);

        if (ledge.hangLeft) {
            subState = State.SUBSTATE_HANGING_LEFT;
            teleport(ledge.x - playerRect.width, ledge.y + ledge.height - playerRect.height, false);
        } else {
            subState = State.SUBSTATE_HANGING_RIGHT;
            teleport(ledge.x + ledge.width, ledge.y + ledge.height - playerRect.height, false);
        }
    }

    public float getShortHopVel() {
        return shortHopVel;
    }

    public float getFullHopVel() {
        return fullHopVel;
    }

    public float getMaximumNaturalGroundSpeed() {
        return maximumNaturalGroundSpeed;
    }

    public boolean getFacingRight() {
        return facingRight;
    }

    public float getMaximumNaturalAirSpeed() {
        return maximumNaturalAirSpeed;
    }

    public State getState() {
        return state;
    }

    public State getSubState() {
        return subState;
    }
}
