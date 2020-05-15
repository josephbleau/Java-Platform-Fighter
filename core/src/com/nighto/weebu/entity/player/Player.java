package com.nighto.weebu.entity.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.controller.Controllable;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.player.input.StateInputHandler;
import com.nighto.weebu.entity.player.input.handlers.*;
import com.nighto.weebu.entity.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Player extends Character implements Controllable {

    private Rectangle rect;

    private List<StateInputHandler> stateInputHandlers;

    public Player(Stage stage) {
        super(stage);

        rect = new Rectangle(
                0, 0,
                getCharacterData().getHurtboxes().get(State.DEFAULT).width,
                getCharacterData().getHurtboxes().get(State.DEFAULT).height
        );

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

        for (StateInputHandler stateInputHandler : stateInputHandlers) {
            if (!stateInputHandler.handleInput(gamecubeController)) {
                break;
            }
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (state == State.CROUCHING) {
            height = getCharacterData().getHurtboxes().get(State.CROUCHING).height;
        } else {
            height = getCharacterData().getHurtboxes().get(State.DEFAULT).height;
        }

        rect.height = height;
    }

    public void spawnShield() {
        shield.spawn(xPos,yPos);
    }
}
