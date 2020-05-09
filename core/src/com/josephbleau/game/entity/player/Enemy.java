package com.josephbleau.game.entity.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.josephbleau.game.entity.stage.Stage;

public class Enemy extends Character {

    public Enemy(Stage stage) {
        super(stage);

        this.getRects().add(new Rectangle(0, 0, 20, 60));

        shield = new Shield(new Circle(10, 30, 30), new Color(Color.BLUE.r, Color.BLUE.g, Color.BLUE.b, .7f));

        this.defaultColor = Color.FIREBRICK;

        this.maximumNaturalGroundSpeed = 5;

        this.maximumNaturalAirSpeed = 2.5f;

        this.spawn(600, 400);
    }

    public void handleInput() {
        float speed = inState(State.STANDING, State.RUNNING)  ? maximumNaturalGroundSpeed : maximumNaturalAirSpeed;

        if (xVel > 0) {
            xVel = speed;
            if (xPos >= 630) {
                xVel = -speed;
            }
        } else if (xVel < 0) {
            xVel = -speed;
            if (xPos <= 150) {
                xVel = speed;
            }
        } else {
            xVel = -speed;
        }

        if (grounded && Math.random() > .99) {
            enterState(State.STANDING);
            this.yVel += 40;
        }
    }
}
