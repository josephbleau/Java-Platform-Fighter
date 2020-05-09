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
        super.handleInput(gamecubeController); // Clears character-based attributes

        if (this.state == State.SHIELDING) {
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || gamecubeController.getControlStick().y > 0.5f) {
                this.state = State.SIDESTEPPING;
            } else if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.Y) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.X)) {
                this.state = State.JUMPSQUAT;
            } else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.LEFT_BUMPER_CLICK) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.RIGHT_BUMPER_CLICK)) {
                this.xVel = 0;
                this.state = State.SHIELDING;
                shield.spawn(xPos, yPos);
            } else {
                this.state = State.STANDING;
            }
        }

        if (this.state == State.SIDESTEPPING) {
            return;
        }

        if (this.state == State.JUMPSQUAT) {
            if (jumpSquatTimeRemaining <= 0.0f) {
                shield.setActive(false);
                this.state = State.AIRBORNE;
                this.jumpSquatTimeRemaining = this.jumpSquatTime;

                if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || gamecubeController.buttonPressed(GamecubeController.Button.X) || gamecubeController.buttonPressed((GamecubeController.Button.Y))) {
                    this.yVel += this.fullHopVelocity;
                } else {
                    this.yVel += this.shortHopVelocity;
                }
            }

            return;
        }

        if (this.state == State.HANGING) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.Y) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.X)) {

                this.setActive(true);
                this.yVel += fullHopVelocity;
                this.state = State.AIRBORNE;
                this.substate = State.SUBSTATE_CLEAR;
            }

            return;
        }

        if (this.state == State.CLEAR || this.state == State.RUNNING || this.state == State.STANDING) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.LEFT_BUMPER_CLICK) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.RIGHT_BUMPER_CLICK)) {
                this.xVel = 0;
                this.state = State.SHIELDING;
                shield.spawn(xPos, yPos);
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || gamecubeController.getControlStick().x < -0.02f) {
                this.xVel = -maximumNaturalGroundSpeed;
                this.state = State.RUNNING;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || gamecubeController.getControlStick().x > 0.02f) {
                this.xVel = maximumNaturalGroundSpeed;
                this.state = State.RUNNING;
            } else {
                this.xVel = 0;
                this.state = State.STANDING;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.Y) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.X)) {
                this.state = State.JUMPSQUAT;
            }

            return;
        }

        if (this.state == State.AIRBORNE) {
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
