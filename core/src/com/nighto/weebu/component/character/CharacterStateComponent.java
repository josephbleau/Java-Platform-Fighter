package com.nighto.weebu.component.character;

import com.nighto.weebu.component.Component;
import com.nighto.weebu.entity.character.CharacterState;

public class CharacterStateComponent extends Component{
    private CharacterState state;
    private CharacterState previousState;
    private CharacterState subState;
    private CharacterState previousSubState;

    public boolean inState(CharacterState... states) {
        boolean isInState = false;

        for (CharacterState state : states) {
            isInState |= (this.state == state);
        }

        return isInState;
    }

    public boolean inSubState(CharacterState... subStates) {
        boolean isInSubState = false;

        for (CharacterState subState : subStates) {
            isInSubState |= (this.subState == subState);
        }

        return isInSubState;
    }

    public boolean inStateAndSubState(CharacterState state, CharacterState subState) {
        return inState(state) && inSubState(subState);
    }

    public void enterState(CharacterState state) {
        previousState = this.state;
        this.state = state;
    }

    public void enterSubState(CharacterState subState) {
        previousSubState = this.subState;
        this.subState = subState;
    }

    public void enterState(CharacterState state, CharacterState subState) {
        enterState(state);

        previousSubState = this.subState;
        this.subState = subState;
    }

    public CharacterState getState() {
        return state;
    }

    public CharacterState getPreviousState() {
        return previousState;
    }

    public CharacterState getSubState() {
        return subState;
    }

    public CharacterState getPreviousSubState() {
        return previousSubState;
    }

    public void revertState() {
        state = previousState;
    }

    public void revertSubState() {
        subState = previousSubState;
    }

    @Override
    public Component save() {
        CharacterStateComponent stateComponent = new CharacterStateComponent();

        stateComponent.state = this.state;
        stateComponent.previousState = this.previousState;
        stateComponent.subState = this.subState;
        stateComponent.previousSubState = this.previousSubState;

        return stateComponent;
    }

    @Override
    public void load(Component component) {
        CharacterStateComponent stateComponent = (CharacterStateComponent) component;
        this.state = stateComponent.state;
        this.previousState = stateComponent.previousState;
        this.subState = stateComponent.subState;
        this.previousSubState = stateComponent.previousSubState;
    }
}
