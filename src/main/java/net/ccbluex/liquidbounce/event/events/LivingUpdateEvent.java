package net.ccbluex.liquidbounce.event.events;

import net.ccbluex.liquidbounce.event.CancellableEvent;
import net.minecraft.entity.Entity;

public class LivingUpdateEvent extends CancellableEvent {
    private Entity entity;
    public LivingUpdateEvent(Entity entity) {
        super();
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
