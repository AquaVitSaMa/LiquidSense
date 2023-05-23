package me.aquavit.liquidsense.command.commands;

import me.aquavit.liquidsense.module.modules.render.CaveFinder;
import me.aquavit.liquidsense.LiquidBounce;
import me.aquavit.liquidsense.command.Command;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class XrayCommand extends Command {
    public XrayCommand() {
        super("xray");
    }

    CaveFinder xRay = (CaveFinder) LiquidBounce.moduleManager.getModule(CaveFinder.class);

    @Override
    public void execute(final String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("add")) {
                if (args.length > 2) {
                    try {
                        Block block = null;
                        try {
                            block = Block.getBlockById(Integer.parseInt(args[2]));
                        } catch (NumberFormatException exception) {
                            Block tmpBlock = Block.getBlockFromName(args[2]);

                            if (tmpBlock == null || Block.getIdFromBlock(tmpBlock) <= 0) {
                                chat("§7Block §8"+args[2]+"§7 does not exist!");
                                return;
                            }

                            block = tmpBlock;
                        }

                        if (block == null || xRay.xrayBlocks.contains(block)) {
                            chat("This block is already on the list.");
                            return;
                        }

                        xRay.xrayBlocks.add(block);
                        LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.xrayConfig);
                        chat("§7Added block §8"+block.getLocalizedName()+"§7.");
                        playEdit();
                    } catch (NumberFormatException exception) {
                        chatSyntaxError();
                    }

                    return;
                }

                chatSyntax("xray add <block_id>");
                return;
            }

            if (args[1].equalsIgnoreCase("remove")) {
                if (args.length > 2) {
                    try {
                        Block block = null;
                        try {
                            block = Block.getBlockById(Integer.parseInt(args[2]));
                        } catch (NumberFormatException exception) {
                            Block tmpBlock = Block.getBlockFromName(args[2]);

                            if (tmpBlock == null || Block.getIdFromBlock(tmpBlock) <= 0) {
                                chat("§7Block §8"+args[2]+"§7 does not exist!");
                                return;
                            }

                            block = tmpBlock;
                        }

                        if (block == null || !xRay.xrayBlocks.contains(block)) {
                            chat("This block is not on the list.");
                            return;
                        }
                        xRay.xrayBlocks.remove(block);
                        LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.xrayConfig);
                        chat("§7Removed block §8"+block.getLocalizedName()+"§7.");
                        playEdit();
                    } catch (NumberFormatException exception) {
                        chatSyntaxError();
                    }

                    return;
                }
                chatSyntax("xray remove <block_id>");
                return;
            }

            if (args[1].equalsIgnoreCase("list")) {
                this.chat("§8Xray-Blocks:");
                xRay.xrayBlocks.forEach(block -> this.chat("§8" + block.getLocalizedName() + " §7-§c " + Block.getIdFromBlock(block)));
                return;
            }
        }
        chatSyntax("xray <add, remove, list>");
    }

    @Override
    public List<String> tabComplete(String[] args) {
        if (args.length == 0) return new ArrayList<>();

        switch (args.length) {
            case 1:
                return Arrays.stream(new String[]{"add", "remove", "list"}).map(String::toLowerCase)
                        .filter(it -> it.startsWith(args[0])).collect(Collectors.toList());
            case 2:{
                switch (args[0].toLowerCase()) {
                    case "add":
                        return Block.blockRegistry.getKeys().stream()
                                .map(it -> it.getResourcePath().toLowerCase())
                                .filter(it -> Block.getBlockFromName(it.toLowerCase()) != null)
                                .filter(it -> xRay.xrayBlocks.contains(Block.getBlockFromName(it.toLowerCase())))
                                .filter(it -> it.startsWith(args[1])).collect(Collectors.toList());
                    case "remove":
                        return Block.blockRegistry.getKeys().stream()
                                .map(it -> it.getResourcePath().toLowerCase())
                                .filter(it -> xRay.xrayBlocks.contains(Block.getBlockFromName(it.toLowerCase())))
                                .filter(it -> it.startsWith(args[1])).collect(Collectors.toList());
                    default:
                        return new ArrayList<>();
                }
            }
            default:
                return new ArrayList<>();
        }
    }

}
