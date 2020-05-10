package com.nighto.weebu.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.controller.Controllable;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.attack.Projectile;
import com.nighto.weebu.entity.player.input.StateInputHandler;
import com.nighto.weebu.entity.player.input.handlers.*;
import com.nighto.weebu.entity.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Player extends Character implements Controllable {

    private float baseWidth;
    private float baseHeight;
    private float width;
    private float height;

    private Rectangle rect;

    private List<StateInputHandler> stateInputHandlers;

    public Player(Stage stage) {
        super(stage);

        baseWidth = 20;
        baseHeight = 60;
        width = baseWidth;
        height = baseHeight;
        rect = new Rectangle(0, 0, baseWidth, baseHeight);

        getRects().add(rect);
        shield = new Shield(new Circle(10, 30, 30), new Color(Color.PINK.r, Color.PINK.g, Color.PINK.b, .7f));
        spawn(200, 400);

        // Register state input handlers
        stateInputHandlers = new ArrayList<>();
        stateInputHandlers.add(new NoInputStateInputHandler(this));
        stateInputHandlers.add(new JumpSquatExitStateInputHandler(this));
        stateInputHandlers.add(new HangingStateInputHandler(this));
        stateInputHandlers.add(new ShieldStateInputHandler(this));
        stateInputHandlers.add(new GroundedStateInputHandler(this));
    }

    @Override
    public void handleInput(GamecubeController gamecubeController) {
        super.handleInput(gamecubeController); // Clears character-based attributes

        System.out.println(state);

        for (StateInputHandler stateInputHandler : stateInputHandlers) {
            if (!stateInputHandler.handleInput(state, gamecubeController)) {
                return;
            }
        }

        // TODO: Move state handling logic to individual state input handlers.
        if (inState(State.AIRBORNE)) {
            // Neutral air dodge
            if (!inSubstate(State.SUBSTATE_ATTACKING) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.LEFT_BUMPER_CLICK) ||
                    gamecubeController.buttonPressed(GamecubeController.Button.RIGHT_BUMPER_CLICK))) {
                enterState(State.SIDESTEPPING);
                this.xVel = 0;
                return;
            }

            // Aerial drift
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || gamecubeController.getControlStick().x < -0.02f) {
                this.xVel = -maximumNaturalAirSpeed;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || gamecubeController.getControlStick().x > 0.02f) {
                this.xVel = maximumNaturalAirSpeed;
            } else {
                this.xVel = 0;
            }

            // Use neutral special
            if (!inState(State.SIDESTEPPING) && !inSubstate(State.SUBSTATE_ATTACKING) &&
                    (Gdx.input.isKeyPressed(Input.Keys.S) || gamecubeController.buttonPressed(GamecubeController.Button.B))) {
                startAttack(new Projectile(facingRight));
            }
        }

        if (inState(State.CROUCHING)) {
            if(!Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                enterState(State.STANDING);
                return;
            }
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (state == State.CROUCHING) {
            height = baseHeight / 2;
        } else {
            height = baseHeight;
        }

        rect.height = height;
    }

    public void spawnShield() {
        shield.spawn(xPos,yPos);
    }
}
