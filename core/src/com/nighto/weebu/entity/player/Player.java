package com.nighto.weebu.entity.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.controller.Controllable;
import com.nighto.weebu.controller.GamecubeController;
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
        stateInputHandlers.add(new CrouchingStateInputHandler(this));
        stateInputHandlers.add(new AirborneStateInputHandler(this));
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
