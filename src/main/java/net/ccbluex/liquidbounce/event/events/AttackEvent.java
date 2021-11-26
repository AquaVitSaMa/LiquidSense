package net.ccbluex.liquidbounce.event.events;

import net.ccbluex.liquidbounce.event.Event;
import net.minecraft.entity.Entity;

public class AttackEvent extends Event {
    private Entity targetEntity;

    public AttackEvent(Entity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Entity getTargetEntity() {
        return this.targetEntity;
    }
}
