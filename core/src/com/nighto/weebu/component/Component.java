package com.nighto.weebu.component;

public abstract class Component {
    protected boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public abstract Component save();
    public abstract void load(Component component);
}