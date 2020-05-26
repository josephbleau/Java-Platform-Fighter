package com.nighto.weebu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.nighto.weebu.config.WorldConstants;
import com.nighto.weebu.screen.InGameScreen;

public class BlockGame extends Game {
	@Override
	public void create () {
		Gdx.app.setLogLevel(WorldConstants.LOG_LEVEL);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		setScreen(new InGameScreen());
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(Color.LIGHT_GRAY.r, Color.LIGHT_GRAY.g, Color.LIGHT_GRAY.b, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.app.debug("Frame", "Start of Frame");
		super.render();
		Gdx.app.debug("Frame", "End of Frame");
	}
	
	@Override
	public void dispose () {}
}
