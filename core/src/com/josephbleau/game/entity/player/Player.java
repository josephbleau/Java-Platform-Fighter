package com.josephbleau.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.josephbleau.game.control.Controllable;
import com.josephbleau.game.control.GamecubeController;
import com.josephbleau.game.entity.stage.Stage;

public class Player extends Character implements Controllable {

    public Player(Stage stage) {
        super(stage);

        this.getRects().add(new Rectangle(0, 0, 20, 60));

        shield = new Shield(new Circle(10, 30, 30), new Color(Color.PINK.r, Color.PINK.g, Color.PINK.b, .7f));

        this.spawn(200, 400);
    }

    @Override
    public void handleInput(GamecubeController gamecubeController) {
        shield.setActive(false);
        if (grounded) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.LEFT_BUMPER_CLICK) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.RIGHT_BUMPER_CLICK)) {
                this.xVel = 0;
                shield.spawn(xPos, yPos);
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || gamecubeController.getControlStick().x < -0.02f) {
                this.xVel = -maximumNaturalGroundSpeed;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || gamecubeController.getControlStick().x > 0.02f) {
                this.xVel = maximumNaturalGroundSpeed;
            } else {
                this.xVel = 0;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.Y) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.X)) {
                this.grounded = false;
                this.yVel += 40;
                shield.setActive(false);
            }
        }

        if (!grounded) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || gamecubeController.getControlStick().x < -0.02f) {
                this.xVel = -maximumNaturalAirSpeed;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || gamecubeController.getControlStick().x > 0.02f) {
                this.xVel = maximumNaturalAirSpeed;
            } else {
                this.xVel = 0;
            }
        }
    }
}
