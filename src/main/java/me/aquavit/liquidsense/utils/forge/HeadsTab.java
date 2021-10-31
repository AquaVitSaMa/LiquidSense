package me.aquavit.liquidsense.utils.forge;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kotlin.jvm.internal.Intrinsics;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import me.aquavit.liquidsense.utils.item.ItemUtils;
import net.ccbluex.liquidbounce.utils.misc.HttpUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

@SideOnly(Side.CLIENT)
public class HeadsTab extends CreativeTabs {

    /**
     * Constructor of heads tab
     */
    public HeadsTab() {
        super("Heads");
        this.setBackgroundImageName("item_search.png");
        this.loadHeads();
    }

    // List of heads
    private final ArrayList<ItemStack> heads = new ArrayList<>();

    /**
     * @return searchbar status
     */
    @Override
    public boolean hasSearchBar() {
        return true;
    }

    /**
     * Return name of tab
     *
     * @return tab name
     */
    @Override
    public String getTranslatedTabLabel() {
        return "Heads";
    }

    /**
     * Return icon item of tab
     *
     * @return icon item
     */
    @Override
    public Item getTabIconItem() {
        return Items.skull;
    }

    /**
     * Add all items to tab
     *
     * @param itemList list of tab items
     */
    @Override
    public void displayAllReleventItems(List<ItemStack> itemList) {
        Intrinsics.checkParameterIsNotNull(itemList, "itemList");
        itemList.addAll(this.heads);
    }

    /**
     * Load all heads from the database
     */
    private void loadHeads() {
        try {
            ClientUtils.getLogger().info("Loading heads...");
            JsonElement headsConfiguration = (new JsonParser()).parse(HttpUtils.get(LiquidBounce.CLIENT_RESOURCE + "heads.json"));

            if (!headsConfiguration.isJsonObject()) return;

            JsonObject headsConf = headsConfiguration.getAsJsonObject();

            if (headsConf.get("enabled").getAsBoolean()) {
                String url = headsConf.get("url").getAsString();

                ClientUtils.getLogger().info("Loading heads from " + url + "...");

                JsonElement headsElement = new JsonParser().parse(HttpUtils.get(url));

                if (!headsElement.isJsonObject()) {
                    ClientUtils.getLogger().error("Something is wrong, the heads json is not a JsonObject!");
                    return;
                }

                JsonObject headsObject = headsElement.getAsJsonObject();

                for (Map.Entry<String, JsonElement> value : headsObject.entrySet()) {
                    JsonObject headElement = value.getValue().getAsJsonObject();

                    heads.add(ItemUtils.createItem("skull 1 3 {display:{Name:"
                            + headElement.get("name").getAsString() + "},SkullOwner:{Id:"
                            + headElement.get("uuid").getAsString() + ",Properties:{textures:[{Value:"
                            + headElement.get("value").getAsString() + "}]}}}"));
                }

                ClientUtils.getLogger().info("Loaded " + heads.size() + " heads from HeadDB.");
            } else {
                ClientUtils.getLogger().info("Heads are disabled.");
            }

        } catch (Exception e) {
            ClientUtils.getLogger().error("Error while reading heads.", e);
        }
    }

}
