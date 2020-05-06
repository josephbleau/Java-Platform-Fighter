package com.josephbleau.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.josephbleau.game.control.Controllable;
import com.josephbleau.game.entity.Entity;
import com.josephbleau.game.entity.stage.Stage;
import com.josephbleau.game.event.events.CollissionEvent;
import com.josephbleau.game.event.events.DeathEvent;
import com.josephbleau.game.event.events.Event;

public class Player extends Entity implements Controllable {

    private Stage stage;

    private Boolean grounded;

    /** The maximum y-velocity that can be reached via gravity. Being knocked toward the ground will ignore this value. **/
    private float maximumNaturalDownwardVelocity = -10;

    /** The maximum x-velocity that can be reached via controller inputs while grounded. **/
    private float maximumNaturalGroundSpeed = 10;

    /** The maximum x-velocity that can be reached via controller inputs while in the air. **/
    private float maximumNaturalAirSpeed = 5;

    public Player(Stage stage) {
        this.getRects().add(new Rectangle(0, 0, 20, 60));

        this.stage = stage;

        this.grounded = false;

        this.spawn(400, 400);
    }

    @Override
    public void handleInput() {
        if (grounded) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                this.xVel = -maximumNaturalGroundSpeed;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                this.xVel = maximumNaturalGroundSpeed;
            } else {
                this.xVel = 0;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                this.grounded = false;
                this.yVel += 40;
            }
        }

        if (!grounded) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                this.xVel = -maximumNaturalAirSpeed;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                this.xVel = maximumNaturalAirSpeed;
            } else {
                this.xVel = 0;
            }
        }
    }

    @Override
    public void update(float delta) {
        this.grounded = false;

        /* Apply gravity */
        if (this.yVel > maximumNaturalDownwardVelocity) {
            this.yVel = Math.max(maximumNaturalDownwardVelocity, this.yVel - this.stage.getGravity());
        }

        super.update(delta);
    }

    @Override
    public void notify(Event event) {
        super.notify(event);

        if (event instanceof CollissionEvent) {
            CollissionEvent collissionEvent = (CollissionEvent) event;

            Entity us = null;
            Entity them = null;
            Rectangle ourRect = null;
            Rectangle theirRect = null;

            if (collissionEvent.entity1 == this) {
                us = collissionEvent.entity1;
                ourRect = collissionEvent.rectangle1;
                them = collissionEvent.entity2;
                theirRect = collissionEvent.rectangle2;
            }

            if (collissionEvent.entity2 == this) {
                us = collissionEvent.entity2;
                ourRect = collissionEvent.rectangle2;
                them = collissionEvent.entity1;
                theirRect = collissionEvent.rectangle1;
            }

            if (them == this.stage) {
                /* Determine which direction we were heading so that we can set our position correctly. */
                boolean falling = yVel < 0;

                /* Reset our x/y to be the x/y of the top of the rect that we collided with (so x, y+height) */
                if (falling) {
                    this.yPos = theirRect.y + theirRect.height;
                }

                this.grounded = true;
                this.yVel = 0;
            }
        }

        if (event instanceof DeathEvent) {
            if (((DeathEvent) event).entity == this) {
                spawn(400, 400);
            }
        }
    }
}
