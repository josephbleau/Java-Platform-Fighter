package com.nighto.weebu.component.character;

import com.nighto.weebu.component.Component;
import com.nighto.weebu.controller.GameController;
import com.nighto.weebu.controller.GameInput;

public class ControllerComponent extends Component {
    private GameController gameController;

    /** True if the player is in active control of their character (they are currently or have very recently
     * provided inputs). This is used to signal idle animations among other places. */
    private boolean activelyControlling;

    public ControllerComponent(GameController gameController) {
        this.gameController = gameController;
    }

    public void registerController(GameController gameController) {
        this.gameController = gameController;
    }

    public void poll() {
        gameController.poll();
    }

    /** Convenience pass-thru to gameController **/
    public Boolean isPressed(GameInput input) {
        return gameController.isPressed(input);
    }

    /** Convenience pass-thru to gameController **/
    public Boolean hasChangedSinceLastFrame(GameInput input) {
        return gameController.hasChangedSinceLastFrame(input);
    }

    public boolean isActivelyControlling() {
        return activelyControlling;
    }

    public void setActivelyControlling(boolean activelyControlling) {
        this.activelyControlling = activelyControlling;
    }

    @Override
    public Component save() {
        return this;
    }

    @Override
    public void load(Component component) {
        // No-op
    }
}
