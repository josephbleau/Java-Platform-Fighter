package com.nighto.weebu.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.control.Controllable;
import com.nighto.weebu.control.GamecubeController;
import com.nighto.weebu.entity.attack.Projectile;
import com.nighto.weebu.entity.stage.Stage;

public class Player extends Character implements Controllable {

    public Player(Stage stage) {
        super(stage);
        this.getRects().add(new Rectangle(0, 0, 20, 60));
        this.shield = new Shield(new Circle(10, 30, 30), new Color(Color.PINK.r, Color.PINK.g, Color.PINK.b, .7f));
        this.spawn(200, 400);
    }

    @Override
    public void handleInput(GamecubeController gamecubeController) {
        super.handleInput(gamecubeController); // Clears character-based attributes

        if (inState(State.SHIELDING)) {
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || gamecubeController.getControlStick().y > 0.5f) {
                enterState(State.SIDESTEPPING);
            } else if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.Y) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.X)) {
                enterState(State.JUMPSQUAT);
            } else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.LEFT_BUMPER_CLICK) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.RIGHT_BUMPER_CLICK)) {
                xVel = 0;
                enterState(State.SHIELDING);
                shield.spawn(xPos, yPos);
            } else {
                enterState(State.STANDING);
            }
        }

        if (inState(State.SIDESTEPPING)) {
            return;
        }

        if (inState(State.JUMPSQUAT)) {
            if (jumpSquatTimeRemaining <= 0.0f) {
                enterState(State.AIRBORNE);
                this.jumpSquatTimeRemaining = this.jumpSquatTime;

                if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || gamecubeController.buttonPressed(GamecubeController.Button.X) || gamecubeController.buttonPressed((GamecubeController.Button.Y))) {
                    this.yVel += this.fullHopVelocity;
                } else {
                    this.yVel += this.shortHopVelocity;
                }
            }

            return;
        }

        if (inState(State.HANGING)) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.Y) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.X)) {

                this.setActive(true);
                this.yVel += fullHopVelocity;

                enterState(State.AIRBORNE);
                enterSubstate(State.SUBSTATE_CLEAR);
            }

            return;
        }

        if (inState(State.CLEAR, State.RUNNING, State.STANDING) && !inSubstate(State.SUBSTATE_ATTACKING)) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.LEFT_BUMPER_CLICK) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.RIGHT_BUMPER_CLICK)) {
                enterState(State.SHIELDING);
                shield.spawn(xPos, yPos);
                this.xVel = 0;
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || gamecubeController.getControlStick().x < -0.02f) {
                enterState(State.RUNNING);
                this.xVel = -maximumNaturalGroundSpeed;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || gamecubeController.getControlStick().x > 0.02f) {
                enterState(State.RUNNING);
                this.state = State.RUNNING;
                this.xVel = maximumNaturalGroundSpeed;
            } else {
                enterState(State.STANDING);
                this.xVel = 0;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.Y) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.X)) {
                enterState(State.JUMPSQUAT);
            }

            if (!inState(State.SHIELDING) && (Gdx.input.isKeyPressed(Input.Keys.S) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.B))) {
                startAttack(new Projectile(facingRight));
                this.xVel = 0;
            }

            return;
        }

        if (inState(State.AIRBORNE)) {
            if (!inSubstate(State.SUBSTATE_ATTACKING) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.LEFT_BUMPER_CLICK) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.RIGHT_BUMPER_CLICK))) {
                enterState(State.SIDESTEPPING);
                this.xVel = 0;
                return;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || gamecubeController.getControlStick().x < -0.02f) {
                this.xVel = -maximumNaturalAirSpeed;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || gamecubeController.getControlStick().x > 0.02f) {
                this.xVel = maximumNaturalAirSpeed;
            } else {
                this.xVel = 0;
            }

            if (!inState(State.SIDESTEPPING) && !inSubstate(State.SUBSTATE_ATTACKING) &&
                    (Gdx.input.isKeyPressed(Input.Keys.S) || gamecubeController.buttonPressed(GamecubeController.Button.B))) {
                startAttack(new Projectile(facingRight));
            }
        }
    }
}
