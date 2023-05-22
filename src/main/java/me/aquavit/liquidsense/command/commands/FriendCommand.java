package me.aquavit.liquidsense.command.commands;

import me.aquavit.liquidsense.utils.misc.StringUtils;
import net.ccbluex.liquidbounce.LiquidBounce;
import me.aquavit.liquidsense.command.Command;
import me.aquavit.liquidsense.file.configs.FriendsConfig;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FriendCommand extends Command {

    public FriendCommand(){
        super("friend", "friends");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length > 1) {
            final FriendsConfig friendsConfig = LiquidBounce.fileManager.friendsConfig;
            if (args[1].equalsIgnoreCase("add")) {
                if (args.length > 2) {
                    final String name = args[2];

                    if (name.isEmpty()) {
                        this.chat("The name is empty.");
                        return;
                    }

                    if (args.length > 3 ? friendsConfig.addFriend(name, StringUtils.toCompleteString(args, 3)) : friendsConfig.addFriend(name)) {
                        LiquidBounce.fileManager.saveConfig(friendsConfig);
                        this.chat("§a§l" + name + "§3 was added to your friend list.");
                        playEdit();
                    } else {
                        chat("The name is already in the list.");
                    }
                }
                chatSyntax("friend add <name> [alias]");
                return;
            }
            else if (args[1].equalsIgnoreCase("remove")) {
                if (args.length > 2) {
                    final String name = args[2];
                    if (friendsConfig.removeFriend(name)) {
                        LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.friendsConfig);
                        this.chat("§a§l" + name + "§3 was removed from your friend list.");
                        playEdit();
                    } else {
                        this.chat("This name is not in the list.");
                    }
                    return;
                }
                this.chatSyntax(".friend remove <name>");
                return;
            } else {
                if (args[1].equalsIgnoreCase("clear")) {
                    final int friends = LiquidBounce.fileManager.friendsConfig.getFriends().size();
                    LiquidBounce.fileManager.friendsConfig.clearFriends();
                    LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.friendsConfig);
                    this.chat("Removed " + friends + " friend(s).");
                    return;
                }
                if (args[1].equalsIgnoreCase("list")) {
                    this.chat("Your Friends:");
                    for (final FriendsConfig.Friend friend : friendsConfig.getFriends()) {
                        this.chat("§7> §a§l" + friend.getPlayerName() + " §c(§7§l" + friend.getAlias() + "§c)");
                    }
                    this.chat("You have §c" + friendsConfig.getFriends().size() + "§3 friends.");
                    return;
                }
            }
        }
        this.chatSyntax("friend <add/remove/list/clear>");
    }

    @Override
    public List<String> tabComplete(String[] args) {
        if (args.length == 0) return new ArrayList<>();

        switch (args.length) {
            case 1:
                return Arrays.stream(new String[]{"add", "remove", "list", "clear"})
                        .filter(it -> it.startsWith(args[0]))
                        .collect(Collectors.toList());
            case 2:{
                switch (args[0].toLowerCase()){
                    case "add":
                        return mc.theWorld.playerEntities.stream().map(EntityPlayer::getName).filter(it -> it.startsWith(args[1])).collect(Collectors.toList());
                    case "remove":
                        return LiquidBounce.fileManager.friendsConfig.getFriends().stream().map(FriendsConfig.Friend::getPlayerName).filter(it -> it.startsWith(args[1])).collect(Collectors.toList());
                    default:
                        return new ArrayList<>();
                }
            }
            default:
                return new ArrayList<>();
        }
    }
}
