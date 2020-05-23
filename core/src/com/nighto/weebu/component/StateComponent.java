package com.nighto.weebu.component;

import com.nighto.weebu.entity.character.State;

public class StateComponent extends Component{
    private State state;
    private State previousState;
    private State subState;
    private State previousSubState;

    public boolean inState(State... states) {
        boolean isInState = false;

        for (State state : states) {
            isInState |= (this.state == state);
        }

        return isInState;
    }

    public boolean inSubState(State... subStates) {
        boolean isInSubState = false;

        for (State subState : subStates) {
            isInSubState |= (this.subState == subState);
        }

        return isInSubState;
    }

    public boolean inStateAndSubState(State state, State subState) {
        return inState(state) && inSubState(subState);
    }

    public void enterState(State state) {
        previousState = this.state;
        this.state = state;
    }

    public void enterSubState(State subState) {
        previousSubState = this.subState;
        this.subState = subState;
    }

    public void enterState(State state, State subState) {
        enterState(state);

        previousSubState = this.subState;
        this.subState = subState;
    }

    public State getState() {
        return state;
    }

    public State getPreviousState() {
        return previousState;
    }

    public State getSubState() {
        return subState;
    }

    public State getPreviousSubState() {
        return previousSubState;
    }

    public void revertState() {
        state = previousState;


    }
}
