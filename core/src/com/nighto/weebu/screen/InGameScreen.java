package com.nighto.weebu.screen;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.nighto.weebu.component.character.ControllerComponent;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.loader.Characters;
import com.nighto.weebu.entity.stage.Stage;
import com.nighto.weebu.entity.stage.Stages;
import com.nighto.weebu.event.EventPublisher;
import com.nighto.weebu.system.System;
import com.nighto.weebu.system.*;

import java.util.ArrayList;
import java.util.List;

public class InGameScreen implements Screen {

    private final GameContext gameContext;
    private final List<System> systems;

    public InGameScreen() {
        Character player = new Character(Characters.ROBBY);
        player.setTag("Player");

        Character computer = new Character(Characters.ROBBY);
        computer.setTag("Computer");

        registerControllerForPlayer(player, computer);

        gameContext = new GameContext();
        gameContext.registerEntity(player);
        gameContext.registerEntity(computer);
        gameContext.registerStage(new Stage(Stages.TEST_STAGE));

        EventPublisher eventPublisher = new EventPublisher();
        eventPublisher.registerListeners(gameContext.getEntities());

        systems = new ArrayList<>();

        systems.add(new StateBasedInputSystem(gameContext, eventPublisher));
        systems.add(new CharacterTimerSystem(gameContext, eventPublisher));

        systems.add(new PhysicsSystem(gameContext, eventPublisher));
        systems.add(new AnimationSystem(gameContext, eventPublisher));
        systems.add(new CollisionSystem(gameContext, eventPublisher));

        systems.add(new AttackGenerationSystem(gameContext, eventPublisher));

        systems.add(new RenderingSystem(gameContext, eventPublisher));
        systems.add(new DebugRenderingSystem(gameContext, eventPublisher));
        systems.add(new DebugSystem(gameContext, eventPublisher));

        systems.add(new CameraSystem(gameContext, eventPublisher));
    }

    @Override
    public void render(float delta) {
        if (gameContext.frameAdvanceMode) {
            gameContext.setFrameDelta(1/60f);
        } else {
            gameContext.setFrameDelta(delta);
        }

        if (gameContext.frameAdvanceMode) {
            if (gameContext.advanceFrame) {
                for (System system : systems) {
                    if (system.isTimeBased()) {
                        system.process();
                    }
                }

                gameContext.advanceFrame = false;
            } else {
                for (System system : systems) {
                    if (!system.isTimeBased()) {
                        system.process();
                    }
                }
            }
        } else {
            for (System system : systems) {
                system.process();
            }
        }
    }

    // TODO: Eventually move this to a much more robust controller system that supports port
    // TODO: swapping/key mapping/etc. For now we just find the adapter and assign one of it's ports.
    private void registerControllerForPlayer(Character player, Character player2) {
        // Register controllers
        List<GamecubeController> controllers = new ArrayList<>();

        for (Controller controller : Controllers.getControllers()) {
            if (controller.getName().equals(GamecubeController.MAYFLASH_ADAPTER_ID)) {
                controllers.add(new GamecubeController(controller));
            }
        }

        GamecubeController gamecubeController = null;
        GamecubeController gamecubeController2 = null;

        if (controllers.size() > 0) {
            gamecubeController = controllers.get(0);
            gamecubeController2 = controllers.get(3);
        }

        GameController gameController = new GameController(gamecubeController, false);
        ControllerComponent controllerComponent = new ControllerComponent(gameController);
        player.registerComponent(ControllerComponent.class, controllerComponent);

        GameController gameController2 = new GameController(gamecubeController2, false);
        ControllerComponent controllerComponent2 = new ControllerComponent(gameController2);
        player2.registerComponent(ControllerComponent.class, controllerComponent2);
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
