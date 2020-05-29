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
    public Rectangle floorStandingOn;

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

    @Override
    public Component save() {
        PhysicalComponent physicalComponent = new PhysicalComponent();
        physicalComponent.position = new Vector2(position);
        physicalComponent.prevPosition = new Vector2(prevPosition);
        physicalComponent.velocity = new Vector2(velocity);
        physicalComponent.prevVelocity = new Vector2(prevVelocity);

        physicalComponent.boundingBox = new Rectangle(boundingBox);
        physicalComponent.prevBoundingBox = new Rectangle(prevBoundingBox);

        physicalComponent.wallSlidingOn = wallSlidingOn;
        physicalComponent.floorStandingOn = floorStandingOn;

        return physicalComponent;
    }

    @Override
    public void load(Component component) {
        PhysicalComponent physicalComponent = (PhysicalComponent) component;

        this.position = new Vector2(physicalComponent.position);
        this.prevPosition = new Vector2(physicalComponent.prevPosition);
        this.velocity = new Vector2(physicalComponent.velocity);
        this.prevVelocity = new Vector2(physicalComponent.prevVelocity);

        this.boundingBox = new Rectangle(physicalComponent.boundingBox);
        this.prevBoundingBox = new Rectangle(physicalComponent.prevBoundingBox);

        this.wallSlidingOn = physicalComponent.wallSlidingOn;
        this.floorStandingOn = physicalComponent.floorStandingOn;
    }
}
