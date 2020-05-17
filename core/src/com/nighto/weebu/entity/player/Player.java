package com.nighto.weebu.entity.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.controller.Controllable;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.player.input.StateInputHandler;
import com.nighto.weebu.entity.player.input.handlers.*;
import com.nighto.weebu.screen.StageScreen;

import java.util.ArrayList;
import java.util.List;

public class Player extends Character implements Controllable {

    private Rectangle rect;
    private List<StateInputHandler> stateInputHandlers;

    public Player(StageScreen parentScreen, GameController gameController) {
        super(parentScreen);

        this.gameController = gameController;
        stateInputHandlers = new ArrayList<>();
        stateInputHandlers.add(new NoInputStateInputHandler(this));
        stateInputHandlers.add(new WallslideStateInputHandler(this));
        stateInputHandlers.add(new JumpSquatExitStateInputHandler(this));
        stateInputHandlers.add(new HangingStateInputHandler(this));
        stateInputHandlers.add(new ShieldStateInputHandler(this));
        stateInputHandlers.add(new GroundedStateInputHandler(this));
        stateInputHandlers.add(new CrouchingStateInputHandler(this));
        stateInputHandlers.add(new AirborneStateInputHandler(this));


        rect = new Rectangle(
                0, 0,
                getCharacterData().getHurtboxes().get(State.DEFAULT).width,
                getCharacterData().getHurtboxes().get(State.DEFAULT).height
        );

        shield = new Shield(
                getStageScreen(),
                new Circle(10, 30, 30),
                new Color(Color.PINK.r, Color.PINK.g, Color.PINK.b, .7f)
        );

        getRects().add(rect);
        spawn(1920/2, 400);
    }

    @Override
    public void handleInput() {
        gameController.poll();

        for (StateInputHandler stateInputHandler : stateInputHandlers) {
            if (!stateInputHandler.handleInput(gameController)) {
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
