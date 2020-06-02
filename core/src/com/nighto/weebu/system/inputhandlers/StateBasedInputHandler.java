package com.nighto.weebu.system.inputhandlers;

import com.nighto.weebu.component.character.CharacterStateComponent;
import com.nighto.weebu.entity.character.Character;
import com.nighto.weebu.entity.character.CharacterState;

public abstract class StateBasedInputHandler {
    private CharacterState[] supportedStates;
    private CharacterState[] blockingSubStates;

    public StateBasedInputHandler(CharacterState[] supportedStates, CharacterState[] blockingSubStates) {
        this.supportedStates = supportedStates;
        this.blockingSubStates = blockingSubStates;
    }

    public StateBasedInputHandler(CharacterState[] supportedStates) {
        this(supportedStates, new CharacterState[]{});
    }

    /**
     * Return true if the state input handler supports the given state and substate.
     */
    private boolean supports(Character character)  {
        CharacterStateComponent stateComponent = character.getComponent(CharacterStateComponent.class);

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
