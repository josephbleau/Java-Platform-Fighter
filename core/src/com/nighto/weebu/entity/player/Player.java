package com.nighto.weebu.entity.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.screen.StageScreen;
import com.nighto.weebu.system.GameContext;
import com.nighto.weebu.system.inputhandlers.StateBasedInputHandler;

import java.util.ArrayList;
import java.util.List;

public class Player extends Character {

    private Rectangle rect;
    private List<StateBasedInputHandler> stateBasedInputHandlers;

    public Player(StageScreen parentScreen, GameContext gameContext) {
        super(parentScreen, gameContext);

        stateBasedInputHandlers = new ArrayList<>();

        rect = new Rectangle(0, 0, width, height);

        shield = new Shield(
                getStageScreen(),
                gameContext,
                new Circle(10, 30, 30),
                new Color(Color.PINK.r, Color.PINK.g, Color.PINK.b, .7f)
        );

        getRects().add(rect);
        spawn(1920/2, 400);
    }
}
