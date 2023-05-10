package network.ranked.hoverablechat.models;

import network.ranked.hoverablechat.HoverableChat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cache {
    private final HoverableChat instance;
    private Map<String, String> groupHovers = new HashMap<>();

    public Cache(HoverableChat instance) {
        this.instance = instance;
        cache();
    }

    private void cache() {
        FileConfiguration config = instance.getConfig();
        ConfigurationSection section = config.getConfigurationSection("");

        for (String group : section.getKeys(false)) {
            List<String> stringList = section.getStringList(group);
            StringBuilder lines = new StringBuilder();

            for (String string : stringList) {
                lines.append(string).append("\n");
            }

            groupHovers.put(group, lines.toString());
        }
    }

    public String group(String name) {
        return groupHovers.get(name);
    }

    public void reload() {
        instance.reloadConfig();
        groupHovers.clear();
        cache();
    }
}
