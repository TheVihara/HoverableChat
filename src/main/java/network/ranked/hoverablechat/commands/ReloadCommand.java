package network.ranked.hoverablechat.commands;

import network.ranked.hoverablechat.models.Cache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final Cache cache;

    public ReloadCommand(Cache cache) {
        this.cache = cache;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1) {
            return false;
        }

        long time = System.currentTimeMillis();
        cache.reload();
        time = System.currentTimeMillis() - time;
        sender.sendMessage("Reloaded configuration. Took: " + time + "ms");

        return true;
    }
}
