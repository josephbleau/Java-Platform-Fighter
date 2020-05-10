package com.nighto.weebu.screen;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.controller.NoopGamecubeController;
import com.nighto.weebu.entity.Entity;
import com.nighto.weebu.entity.player.Enemy;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.stage.Stage;
import com.nighto.weebu.entity.stage.TestStage;
import com.nighto.weebu.event.EventHandler;
import com.nighto.weebu.event.events.CollissionEvent;
import com.nighto.weebu.event.events.DeathEvent;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements Screen {
    final Game game;
    final Stage stage;
    final Player player;
    final Enemy enemy;

    private List<Entity> entities;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private EventHandler eventHandler;

    private List<GamecubeController> controllers;

    public MainScreen(Game game) {
        this.game = game;
        stage = new TestStage();
        player = new Player(stage);
        enemy = new Enemy(stage);

        entities = new ArrayList<>();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 640);
        shapeRenderer = new ShapeRenderer();

        entities.add(stage);
        entities.add(player);
        entities.add(enemy);

        eventHandler = new EventHandler();
        eventHandler.registerListeners(entities);

        // Register controllers
        controllers = new ArrayList<>();

        for(Controller controller : Controllers.getControllers()) {
            if (controller.getName().equals(GamecubeController.MAYFLASH_ADAPTER_ID)) {
                controllers.add(new GamecubeController(controller));
            }
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        /* Input Loop */
        if (controllers.size() > 0) {
            player.handleInput(controllers.get(0));
        } else {
            player.handleInput(new NoopGamecubeController());
        }

        enemy.handleInput();

        /* Update Loop: All entities will run their physics calculations here. */
        for (Entity entity : entities) {
            entity.update(Gdx.graphics.getDeltaTime());
        }

        /* Outcome Loop: Collisions and interactions will be resolved here. */
        for (Entity outerEntity : entities) {
            if (!stage.inBounds(outerEntity)) {
                DeathEvent deathEvent = new DeathEvent(outerEntity);
                eventHandler.publish(deathEvent);
            }

            for (Entity innerEntity : entities) {
                if (outerEntity == innerEntity) {
                    continue;
                }

                if (innerEntity.isCollidable() && outerEntity.isCollidable()) {
                    CollissionEvent collissionEvent = outerEntity.intersects(innerEntity);

                    if (collissionEvent != null) {
                        eventHandler.publish(collissionEvent);
                    }
                }
            }
        }

        /* Render Loop **/
        shapeRenderer.setProjectionMatrix(camera.combined);

        stage.render(shapeRenderer);

        for (Entity entity : entities) {
            entity.render(shapeRenderer);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
