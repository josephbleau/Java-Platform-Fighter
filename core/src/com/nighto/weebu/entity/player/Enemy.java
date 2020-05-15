package com.nighto.weebu.entity.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.stage.Stage;

public class Enemy extends Character {

    public Enemy(Stage stage) {
        super(stage);

        getRects().add(new Rectangle(
                0, 0,
                getCharacterData().getHurtboxes().get(State.DEFAULT).width,
                getCharacterData().getHurtboxes().get(State.DEFAULT).height

        ));

        shield = new Shield(new Circle(10, 30, 30), new Color(Color.BLUE.r, Color.BLUE.g, Color.BLUE.b, .7f));
        defaultColor = Color.FIREBRICK;

        spawn(600, 400);
    }

    public void handleInput() {
        float speed = inState(State.STANDING, State.RUNNING)  ? getCharacterData().getAttributes().getGroundSpeed()/2 : getCharacterData().getAttributes().getAirSpeed()/2;

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

        if (!inState(State.AIRBORNE) && Math.random() > .99) {
            enterState(State.STANDING);
            yVel += getCharacterData().getAttributes().getShortHopSpeed();
        }
    }
}
