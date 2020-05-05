package com.josephbleau.game.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.josephbleau.game.control.Controllable;
import com.josephbleau.game.entity.stage.Stage;
import com.josephbleau.game.event.events.CollissionEvent;
import com.josephbleau.game.event.events.Event;

public class Player extends Entity implements Controllable {

    private Stage stage;
    private Boolean grounded;

    public Player(Stage stage) {
        this.spawn(400, 400);
        this.getRects().add(new Rectangle(0, 0, 20, 60));
        this.stage = stage;
        this.grounded = false;
    }

    @Override
    public void handleInput(Input.Keys key) {

    }

    @Override
    public void update(float delta) {
        super.update(delta);

        /* Apply gravity */
        if (!this.grounded) {
            this.applyForce(0, -this.stage.getGravity());
        }
    }

    @Override
    public void notify(Event event) {
        super.notify(event);

        if (event instanceof CollissionEvent) {
            CollissionEvent collissionEvent = (CollissionEvent) event;

            Entity us = null;
            Entity them = null;

            if (collissionEvent.entity1 == this) {
                us = collissionEvent.entity1;
                them = collissionEvent.entity2;
            }

            if (collissionEvent.entity2 == this) {
                us = collissionEvent.entity2;
                them = collissionEvent.entity1;
            }

            if (them == this.stage) {
                this.grounded = true;
            }
        }
    }
}
