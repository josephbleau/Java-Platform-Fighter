package com.josephbleau.game.entity.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.josephbleau.game.control.GamecubeController;
import com.josephbleau.game.entity.Entity;
import com.josephbleau.game.entity.attack.Attack;
import com.josephbleau.game.entity.parts.Ledge;
import com.josephbleau.game.entity.stage.Stage;
import com.josephbleau.game.event.events.CollissionEvent;
import com.josephbleau.game.event.events.DeathEvent;
import com.josephbleau.game.event.events.Event;

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
    protected float shortHopVelocity = 35;

    /** The initial velocity boost of a full hop **/
    protected float fullHopVelocity = 50;

    /** The amount of time until you jump after pressing jump (from the ground) in seconds. **/
    protected float jumpSquatTime = 0.05f;

    /** The amount of time left in jumpsquat. **/
    protected float jumpSquatTimeRemaining = this.jumpSquatTime;

    /** The total amount of time spent in sidestep **/
    protected float sidestepTime = 0.37f;

    /** The amount of time left in the current sidestep. **/
    protected float sidestepTimeRemaining = this.sidestepTime;

    protected Shield shield;

    protected Stage stage;

    protected State prevState;
    protected State state;

    protected State substate;
    protected State prevSubstate;

    protected Color sidestepColor;

    protected boolean facingRight = true;

    protected List<Attack> attacks = new ArrayList<>();

    public Character(Stage stage) {
        this.stage = stage;
        this.sidestepColor = Color.LIGHT_GRAY;

        enterState(State.CLEAR);
        enterSubstate(State.SUBSTATE_CLEAR);
    }

    @Override
    public void update(float delta) {
        /* Apply gravity */
        if (this.yVel > maximumNaturalDownwardVelocity) {
            this.yVel = Math.max(maximumNaturalDownwardVelocity, this.yVel - this.stage.getGravity());
        }

        if (state == State.JUMPSQUAT) {
            jumpSquatTimeRemaining -= delta;
        }

        if (state == State.SIDESTEPPING) {
            sidestepTimeRemaining -= delta;
        }

        if (sidestepTimeRemaining <= 0) {
            enterState(this.prevState);
            this.currentColor = this.defaultColor;
            this.sidestepTimeRemaining = this.sidestepTime;
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
            if (attack.isPlaying()) {
                attackPlaying = true;
            }
            if (!attack.isActive()) {
                attackIterator.remove();
            }
        }
        if (!attackPlaying && inSubstate(State.SUBSTATE_ATTACKING)) {
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

            if (them == this.stage) {
                /* Determine which direction we were heading so that we can set our position correctly. */
                boolean falling = yVel < 0;

                /* Reset our x/y to be the x/y of the top of the rect that we collided with (so x, y+height) */
                if (falling && this.stage.isGround((Rectangle) theirShape)) {
                    this.yPos = ((Rectangle)theirShape).y + ((Rectangle)theirShape).height;
                } else if (falling && this.stage.isLedge((Ledge) theirShape)){
                    snapToLedge((Ledge) theirShape);
                }

                if (inState(State.AIRBORNE)) {
                    enterState(State.STANDING);
                    enterSubstate(State.SUBSTATE_CLEAR);
                }

                this.yVel = 0;
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
            this.currentColor = this.sidestepColor;
        } else {
            this.currentColor = this.defaultColor;
        }

        if (inState(State.SHIELDING)) {
            this.shield.setActive(true);
        } else {
            this.shield.setActive(false);
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

    protected void enterState(State newState) {
        this.prevState = this.state;
        this.state = newState;
    }

    protected void enterSubstate(State newSubstate) {
        this.prevSubstate = this.substate;
        this.substate = newSubstate;
    }

    protected boolean inState(State ... state) {
        boolean outcome = false;

        for (State s : state) {
            outcome |= this.state == s;
        }

        return outcome;
    }

    protected boolean inSubstate(State... substate) {
        for(State s : substate) {
            if (this.substate.equals(s)) {
                return true;
            }
        }
        return false;
    }

    protected void startAttack(Attack attack) {
        attack.spawn(xPos, yPos);
        if (attack.isPlaying()) {
            enterSubstate(State.SUBSTATE_ATTACKING);
        }
        attacks.add(attack);
    }

    private void snapToLedge(Ledge ledge) {
        this.setActive(false); // prevents gravity from applying
        enterState(State.HANGING);

        Rectangle playerRect = this.getRects().get(0);

        if (ledge.hangLeft) {
            this.substate = State.SUBSTATE_HANGING_LEFT;
            this.teleport(ledge.x - playerRect.width, ledge.y + ledge.height - playerRect.height, false);
        } else {
            this.substate = State.SUBSTATE_HANGING_RIGHT;
            this.teleport(ledge.x + ledge.width, ledge.y + ledge.height - playerRect.height, false);
        }
    }
}
