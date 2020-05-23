package com.nighto.weebu.entity.enemy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.player.Shield;
import com.nighto.weebu.screen.StageScreen;
import com.nighto.weebu.system.GameContext;

public class Enemy extends Character {

    public Enemy(StageScreen parentScreen, GameContext gameContext) {
        super(parentScreen, gameContext);

        getRects().add(new Rectangle(0, 0, width, height));

        shield = new Shield(
                getStageScreen(),
                getGameContext(),
                new Circle(10, 30, 30), new
                Color(Color.BLUE.r, Color.BLUE.g, Color.BLUE.b, .7f)
        );

        defaultColor = Color.FIREBRICK;

        spawn(1920/2, 400);
    }
}
