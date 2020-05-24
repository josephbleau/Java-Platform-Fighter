package com.nighto.weebu.component;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PhysicalComponent extends Component {
    public Vector2 position;
    public Vector2 prevPosition;
    public Vector2 velocity;
    public Vector2 prevVelocity;

    public Rectangle boundingBox;
    public Rectangle prevBoundingBox;

    public Rectangle wallSlidingOn;

    public boolean facingRight;

    public PhysicalComponent() {
        position = new Vector2();
        prevPosition = new Vector2();
        velocity = new Vector2();
        prevVelocity = new Vector2();
        boundingBox = new Rectangle();
        prevBoundingBox = new Rectangle();
    }

    public PhysicalComponent(Vector2 startingPosition, Vector2 startingVelocity) {
        position = new Vector2(startingPosition);
        velocity = new Vector2(startingVelocity);

        prevPosition = new Vector2();
        prevVelocity = new Vector2();

        boundingBox = new Rectangle();
        prevBoundingBox = new Rectangle();
    }
}
