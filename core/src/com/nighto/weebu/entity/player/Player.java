package com.nighto.weebu.entity.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.config.WorldConstants;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.system.GameContext;

public class Player extends Character {

    public Player(GameContext gameContext) {
        super(gameContext);

        shield = new Shield(gameContext, new Circle(10, 30, 30), new Color(Color.PINK.r, Color.PINK.g, Color.PINK.b, .7f));

//        float width = characterDataComponent.getHurtboxes().get(stateComponent.getState()).width;
//        float height = characterDataComponent.getHurtboxes().get(stateComponent.getState()).height;

        getRects().add(new Rectangle(0, 0, 20, 60));

        teleport(WorldConstants.WORLD_WIDTH/2, WorldConstants.WORLD_HEIGHT);
    }
}
