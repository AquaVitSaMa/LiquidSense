package net.ccbluex.liquidbounce.event.events;

import net.ccbluex.liquidbounce.event.Event;
import net.minecraft.entity.Entity;

public class EntityMovementEvent extends Event {
    private Entity movedEntity;

    public Entity getMovedEntity() {
        return this.movedEntity;
    }

    public EntityMovementEvent(Entity movedEntity) {
        this.movedEntity = movedEntity;
    }
}
