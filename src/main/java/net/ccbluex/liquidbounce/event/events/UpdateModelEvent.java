package net.ccbluex.liquidbounce.event.events;

import net.ccbluex.liquidbounce.event.Event;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;

public class UpdateModelEvent extends Event {
    private EntityPlayer player;
    private ModelPlayer model;

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public ModelPlayer getModel() {
        return this.model;
    }

    public UpdateModelEvent(EntityPlayer player, ModelPlayer model) {
        this.player = player;
        this.model = model;
    }
}
