package network.ranked.hoverablechat;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import network.ranked.hoverablechat.commands.ReloadCommand;
import network.ranked.hoverablechat.listener.PlayerChatListener;
import network.ranked.hoverablechat.models.Cache;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class HoverableChat extends JavaPlugin {
    private Cache cache;
    private static BukkitAudiences adventure;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PluginManager pluginManager = getServer().getPluginManager();
        adventure = BukkitAudiences.create(this);
        this.cache = new Cache(this);
        pluginManager.registerEvents(new PlayerChatListener(cache), this);
        getCommand("hchatreload").setExecutor(new ReloadCommand(cache));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Cache getCache() {
        return cache;
    }

    public static BukkitAudiences getAdventure() {
        return adventure;
    }
}
