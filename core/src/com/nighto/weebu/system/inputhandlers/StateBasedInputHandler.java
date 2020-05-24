package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.StateComponent;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.State;

public abstract class StateBasedInputHandler {
    private State[] supportedStates;
    private State[] blockingSubStates;

    public StateBasedInputHandler(State[] supportedStates, State[] blockingSubStates) {
        this.supportedStates = supportedStates;
        this.blockingSubStates = blockingSubStates;
    }

    public StateBasedInputHandler(State[] supportedStates) {
        this(supportedStates, new State[]{});
    }

    /**
     * Return true if the state input handler supports the given state and substate.
     */
    private boolean supports(Character character)  {
        StateComponent stateComponent = character.getComponent(StateComponent.class);

        return stateComponent != null && stateComponent.inState(supportedStates) && !stateComponent.inSubState(blockingSubStates);
    }

    /**
     * If the given state is supported then we will execute the implemented doHandleInput, otherwise
     * we will return false.
     * @return true if the input handler chain should continue to be executed, otherwise
     * false if this input should end the chain.
     */
    public boolean handleInput(Character character) {
        if (supports(character)) {
            return doHandleInput(character);
        }

        return true;
    }

    /**
     * Returns true if the input evaluation chain should continue, and false if
     * no other inputs should be considered.
     */
    protected abstract boolean doHandleInput(Character character);
}
