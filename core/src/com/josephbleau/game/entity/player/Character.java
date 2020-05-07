package com.josephbleau.game.entity.player;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.josephbleau.game.entity.Entity;
import com.josephbleau.game.entity.stage.Stage;
import com.josephbleau.game.event.events.CollissionEvent;
import com.josephbleau.game.event.events.DeathEvent;
import com.josephbleau.game.event.events.Event;

public class Character extends Entity {

    protected boolean grounded = false;

    /** The maximum y-velocity that can be reached via gravity. Being knocked toward the ground will ignore this value. **/
    protected float maximumNaturalDownwardVelocity = -10;

    /** The maximum x-velocity that can be reached via controller inputs while grounded. **/
    protected float maximumNaturalGroundSpeed = 10;

    /** The maximum x-velocity that can be reached via controller inputs while in the air. **/
    protected float maximumNaturalAirSpeed = 5;

    protected Shield shield;

    protected Stage stage;

    public Character(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void update(float delta) {
        this.grounded = false;

        /* Apply gravity */
        if (this.yVel > maximumNaturalDownwardVelocity) {
            this.yVel = Math.max(maximumNaturalDownwardVelocity, this.yVel - this.stage.getGravity());
        }

        shield.update(delta);

        super.update(delta);
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
                if (falling && theirShape instanceof Rectangle) {
                    this.yPos = ((Rectangle)theirShape).y + ((Rectangle)theirShape).height;
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

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        super.render(shapeRenderer);

        shield.render(shapeRenderer);
    }

    @Override
    public CollissionEvent intersects(Entity otherEntity) {
        CollissionEvent shieldCollision = shield.intersects(otherEntity);
        if (shieldCollision != null) {
            return shieldCollision;
        }

        return super.intersects(otherEntity);
    }
}
