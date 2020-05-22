package com.nighto.weebu.component;

import com.nighto.weebu.entity.player.State;

public class StateComponent extends Component{
    private State state;
    private State previousState;
    private State subState;
    private State previousSubState;

    public Boolean inState(State... states) {
        Boolean isInState = false;

        for (State state : states) {
            isInState |= (this.state == state);
        }

        return isInState;
    }

    public Boolean inSubState(State... subStates) {
        Boolean isInSubState = false;

        for (State subState : subStates) {
            isInSubState |= (this.subState == subState);
        }

        return isInSubState;
    }

    public Boolean inStateAndSubState(State state, State subState) {
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

    public void revertState() {
        state = previousState;
    }
}
