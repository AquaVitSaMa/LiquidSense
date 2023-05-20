package net.ccbluex.liquidbounce.script.api;

import jdk.nashorn.api.scripting.ScriptUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import jdk.nashorn.api.scripting.JSObject;

import java.util.List;

public class ScriptTab extends CreativeTabs {
    private JSObject tabObject;
    private ItemStack[] items;

    public ScriptTab(JSObject tabObject) {
        super((String) tabObject.getMember("name"));
        this.tabObject = tabObject;
        this.items = (ItemStack[]) ScriptUtils.convert(tabObject.getMember("items"), ItemStack[].class);
    }

    @Override
    public Item getTabIconItem() {
        try {
            return (Item) Items.class.getField((String) tabObject.getMember("icon")).get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getTranslatedTabLabel() {
        return (String) tabObject.getMember("name");
    }

    @Override
    public void displayAllReleventItems(List<ItemStack> list) {
        for (ItemStack item : items) {
            if (item != null) {
                list.add(item);
            }
        }
    }
}
