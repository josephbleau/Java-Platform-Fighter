package com.nighto.weebu.screen;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.nighto.weebu.component.ControllerComponent;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.stage.TestStage;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.system.System;
import com.nighto.weebu.system.*;

import java.util.ArrayList;
import java.util.List;

public class InGameScreen implements Screen {

    private final GameContext gameContext;
    private final List<System> systems;

    public InGameScreen() {
        Character player = new Character();
        player.setTag("Player");
        registerControllerForPlayer(player);

        Character computer = new Character();
        computer.setTag("Computer");

        gameContext = new GameContext();
        gameContext.registerEntity(player);
        gameContext.registerEntity(computer);
        gameContext.registerStage(new TestStage());

        EventPublisher eventPublisher = new EventPublisher();
        eventPublisher.registerListeners(gameContext.getEntities());

        systems = new ArrayList<>();
        systems.add(new StateBasedInputSystem(gameContext, eventPublisher));
        systems.add(new CharacterTimerSystem(gameContext, eventPublisher));
        systems.add(new AnimationSystem(gameContext, eventPublisher));
        systems.add(new PhysicsSystem(gameContext, eventPublisher));
        systems.add(new CollisionSystem(gameContext, eventPublisher));
        systems.add(new RenderingSystem(gameContext, eventPublisher));
        systems.add(new DebugRenderingSystem(gameContext, eventPublisher));
        systems.add(new DebugPrinterSystem(gameContext, eventPublisher));
    }

    @Override
    public void render(float delta) {
        gameContext.setFrameDelta(delta);

        for (System system : systems) {
            system.process();
        }
    }

    // TODO: Eventually move this to a much more robust controller system that supports port
    // TODO: swapping/key mapping/etc. For now we just find the adapter and assign one of it's ports.
    private void registerControllerForPlayer(Character player) {
        // Register controllers
        List<GamecubeController> controllers = new ArrayList<>();

        for(Controller controller : Controllers.getControllers()) {
            if (controller.getName().equals(GamecubeController.MAYFLASH_ADAPTER_ID)) {
                controllers.add(new GamecubeController(controller));
            }
        }

        GamecubeController gamecubeController = null;

        if (controllers.size() > 0) {
            gamecubeController = controllers.get(0);
        }

        ((ControllerComponent)player.getComponent(ControllerComponent.class)).registerController(new GameController(gamecubeController, true));
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
