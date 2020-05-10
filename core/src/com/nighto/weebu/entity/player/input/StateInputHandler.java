package com.nighto.weebu.entity.player.input;

import com.nighto.weebu.controller.GamecubeController;
import com.nighto.weebu.entity.player.Player;
import com.nighto.weebu.entity.player.State;

import java.util.HashMap;
import java.util.Map;

public abstract class StateInputHandler {

    private InputPriority priority;
    private Player player;
    private Map<State, Boolean> supportedStates;

    public StateInputHandler(Player player, InputPriority priority, State[] supportedStates) {
        this.player = player;
        this.priority = priority;
        this.supportedStates = new HashMap<>();

        for (State supportedState : supportedStates) {
            this.supportedStates.put(supportedState, true);
        }
    }

    /**
     * Return true if the state input handler supports the given state.
     */
    private boolean supports(State state)  {
        return supportedStates.get(state) != null && supportedStates.get(state);
    }

    /**
     * If the given state is supported then we will execute the implemented doHandleInput, otherwise
     * we will return false.
     * @return true if the input handler chain should continue to be executed, otherwise
     * false if this input should end the chain.
     */
    public boolean handleInput(State state, GamecubeController gamecubeController) {
        if (supports(state)) {
            return doHandleInput(state, gamecubeController);
        }

        return true;
    }

    /**
     * Returns true if the input evaluation chain should continue, and false if
     * no other inputs should be considered.
     */
    protected abstract boolean doHandleInput(State state, GamecubeController gamecubeController);

    public InputPriority getPriority() {
        return priority;
    }

    public Player getPlayer() {
        return player;
    }

    /** Convenience pass-through function that invokes the players inState method **/
    protected boolean inState(State ... states) {
        return getPlayer().inState(states);
    }

    /** Convenience pass-through function that invokes the players enterState method **/
    protected void enterState(State state) {
        getPlayer().enterState(state);
    }
}
