package com.josephbleau.game.screen;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.josephbleau.game.entity.Entity;
import com.josephbleau.game.entity.Player;
import com.josephbleau.game.entity.stage.Stage;
import com.josephbleau.game.entity.stage.TestStage;
import com.josephbleau.game.event.EventHandler;
import com.josephbleau.game.event.events.CollissionEvent;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements Screen {
    final Game game;
    final Stage stage;
    final Player player;

    private List<Entity> entities;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private EventHandler eventHandler;

    public MainScreen(Game game) {
        this.game = game;
        this.stage = new TestStage();
        this.player = new Player(this.stage);

        this.entities = new ArrayList<>();

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 640);
        this.shapeRenderer = new ShapeRenderer();

        this.entities.add(this.stage);
        this.entities.add(player);

        this.eventHandler = new EventHandler();
        this.eventHandler.registerEntities(this.entities);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        /* Update Loop: All entities will run their physics calculations here. */
        for (Entity entity : entities) {
            entity.update(Gdx.graphics.getDeltaTime());
        }

        /* Outcome Loop: Collisions and interactions will be resolved here. */
        for (Entity outerEntity : entities) {
            for (Entity innerEntity : entities) {
                if (outerEntity == innerEntity) {
                    continue;
                }

                if (innerEntity.isCollidable() && outerEntity.isCollidable() && outerEntity.intersects(innerEntity)) {
                    this.eventHandler.publish(new CollissionEvent(outerEntity, innerEntity));
                }
            }
        }

        /* Render Loop **/
        this.shapeRenderer.setProjectionMatrix(this.camera.combined);

        for (Entity entity : entities) {
            entity.render(this.shapeRenderer);
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
