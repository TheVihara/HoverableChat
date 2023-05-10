package network.ranked.hoverablechat.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.ChatMetaNode;
import net.luckperms.api.node.types.PrefixNode;
import network.ranked.hoverablechat.HoverableChat;
import network.ranked.hoverablechat.models.Cache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PlayerChatListener implements Listener {
    private LuckPerms api = LuckPermsProvider.get();
    private Cache cache;

    public PlayerChatListener(Cache cache) {
        this.cache = cache;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) throws ExecutionException, InterruptedException {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Group group = getGroup(uuid).get();
        String groupName = group.getName();
        String prefix = getGroupPrefix(group);
        String lines = cache.group(groupName);
        String format = event.getFormat();
        Set<Player> recipients = event.getRecipients();
        String currentMessage = event.getMessage();
        Component message = null;

        String[] splitFormat = null;
        if (prefix != null) {
            splitFormat = format.split(prefix);
        }

        if (splitFormat != null) {
            String newFormat = legacyToMiniMessage("&", splitFormat[0]);
            message = MiniMessage.miniMessage().deserialize("<hover:show_text:'" + lines + "'>" + newFormat + "</hover>");
            message = MiniMessage.miniMessage().deserialize(message + splitFormat[1] + currentMessage);
        }

        if (message != null) {
            HoverableChat.getAdventure().all().sendMessage(message);
        }
    }

    private CompletableFuture<Group> getGroup(UUID who) {
        return api.getUserManager().loadUser(who)
                .thenApplyAsync(user -> {
                    Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());

                    Optional<Group> highestWeightGroup = inheritedGroups.stream()
                            .max(Comparator.comparingInt(group -> group.getWeight().orElse(0)));

                    if (highestWeightGroup.isPresent()) {
                        Group group = highestWeightGroup.get();
                        return group;
                    }

                    return null;
                });
    }

    private String getGroupPrefix(Group group) {
        Optional<PrefixNode> prefix = group.getNodes(NodeType.PREFIX).stream().max(Comparator.comparingInt(ChatMetaNode::getPriority));

        if (prefix.isPresent()) {
            return prefix.toString();
        }

        return null;
    }

    private String legacyToMiniMessage(String ch, String text) {
        return text
                .replaceAll(ch + "0", "<black>")
                .replaceAll(ch + "1", "<dark_blue>")
                .replaceAll(ch + "2", "<dark_green>")
                .replaceAll(ch + "3", "<dark_aqua>")
                .replaceAll(ch + "4", "<dark_red>")
                .replaceAll(ch + "5", "<dark_purple>")
                .replaceAll(ch + "6", "<gold>")
                .replaceAll(ch + "7", "<grey>")
                .replaceAll(ch + "8", "<dark_grey>")
                .replaceAll(ch + "9", "<blue>")
                .replaceAll(ch + "a", "<green>")
                .replaceAll(ch + "b", "<aqua>")
                .replaceAll(ch + "c", "<red>")
                .replaceAll(ch + "d", "<light_purple>")
                .replaceAll(ch + "e", "<yellow>")
                .replaceAll(ch + "f", "<white>")
                .replaceAll(ch + "m", "<st>")
                .replaceAll(ch + "k", "<obf>")
                .replaceAll(ch + "o", "<i>")
                .replaceAll(ch + "l", "<b>")
                .replaceAll(ch + "r", "<r>")
                .replaceAll("/" + ch + "#([0-9a-fA-F]{6})/g", "<#$1>");
    }
}
