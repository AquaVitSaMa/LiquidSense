package me.aquavit.liquidsense.module.modules.ghost;

import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.Render3DEvent;
import me.aquavit.liquidsense.module.Module;
import me.aquavit.liquidsense.module.ModuleCategory;
import me.aquavit.liquidsense.module.ModuleInfo;
import me.aquavit.liquidsense.injection.implementations.IItemStack;
import me.aquavit.liquidsense.utils.client.InventoryUtils;
import me.aquavit.liquidsense.utils.entity.MovementUtils;
import me.aquavit.liquidsense.utils.item.ArmorComparator;
import me.aquavit.liquidsense.utils.item.ArmorPiece;
import me.aquavit.liquidsense.utils.timer.TimeUtils;
import me.aquavit.liquidsense.value.BoolValue;
import me.aquavit.liquidsense.value.IntegerValue;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ModuleInfo(name = "AutoArmor", description = "Automatically equips the best armor in your inventory.", category = ModuleCategory.GHOST)
public class AutoArmor extends Module {

    public static final ArmorComparator ARMOR_COMPARATOR = new ArmorComparator();

    private final IntegerValue maxDelayValue = new IntegerValue("MaxDelay", 200, 0, 400) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int minDelay = minDelayValue.get();

            if (minDelay > newValue) set(minDelay);
        }
    };
    private final IntegerValue minDelayValue = new IntegerValue("MinDelay", 100, 0, 400) {

        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int maxDelay = maxDelayValue.get();

            if (maxDelay < newValue) set(maxDelay);
        }
    };
    private final BoolValue invOpenValue = new BoolValue("InvOpen", false);
    private final BoolValue simulateInventory = new BoolValue("SimulateInventory", true);
    private final BoolValue noMoveValue = new BoolValue("NoMove", false);
    private final IntegerValue itemDelayValue = new IntegerValue("ItemDelay", 0, 0, 5000);
    private final BoolValue hotbarValue = new BoolValue("Hotbar", true);

    private long delay;

    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        if ((mc.thePlayer.capabilities.allowFlying && mc.thePlayer.capabilities.isFlying) || !InventoryUtils.CLICK_TIMER.hasTimePassed(delay) ||
                (mc.thePlayer.openContainer != null && mc.thePlayer.openContainer.windowId != 0))
            return;

        // Find best armor
        final Map<Integer, List<ArmorPiece>> armorPieces = IntStream.range(0, 36).filter(i -> {
                    final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);

                    return itemStack != null
                            && itemStack.getItem() instanceof ItemArmor
                            && (i < 9 || System.currentTimeMillis() - ((IItemStack) (Object) itemStack).getItemDelay() >= itemDelayValue.get());
                })
                .mapToObj(i -> new ArmorPiece(mc.thePlayer.inventory.getStackInSlot(i), i))
                .collect(Collectors.groupingBy(ArmorPiece::getArmorType));

        final ArmorPiece[] bestArmor = new ArmorPiece[4];

        for (final Map.Entry<Integer, List<ArmorPiece>> armorEntry : armorPieces.entrySet()) {
            bestArmor[armorEntry.getKey()] = armorEntry.getValue().stream()
                    .max(ARMOR_COMPARATOR).orElse(null);
        }

        // Swap armor
        for (int i = 0; i < 4; i++) {
            final ArmorPiece armorPiece = bestArmor[i];

            if (armorPiece == null)
                continue;

            int armorSlot = 3 - i;

            final ArmorPiece oldArmor = new ArmorPiece(mc.thePlayer.inventory.armorItemInSlot(armorSlot), -1);

            if (oldArmor.getItemStack() == null || !(oldArmor.getItemStack().getItem() instanceof ItemArmor) ||
                    ARMOR_COMPARATOR.compare(oldArmor, armorPiece) < 0) {
                if (oldArmor.getItemStack() != null && move(8 - armorSlot, true))
                    return;

                if (move(armorPiece.getSlot(), false))
                    return;
            }
        }
    }

    /**
     * Shift+Left clicks the specified item
     *
     * @param item        Slot of the item to click
     * @param isArmorSlot
     * @return True if it is unable to move the item
     */
    private boolean move(int item, boolean isArmorSlot) {
        if (!isArmorSlot && item < 9 && hotbarValue.get()) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(item));
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(item).getStack()));
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get());

            return true;
        } else if (!(noMoveValue.get() && MovementUtils.isMoving()) && (!invOpenValue.get() || mc.currentScreen instanceof GuiInventory) && item != -1) {
            final boolean openInventory = simulateInventory.get() && !(mc.currentScreen instanceof GuiInventory);

            if (openInventory)
                mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));

            mc.playerController.windowClick(
                    mc.thePlayer.inventoryContainer.windowId, isArmorSlot ? item : (item < 9 ? item + 36 : item), 0, 1,
                    mc.thePlayer);

            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get());

            if (openInventory)
                mc.getNetHandler().addToSendQueue(new C0DPacketCloseWindow());

            return true;
        }
        
        return false;
    }

}
