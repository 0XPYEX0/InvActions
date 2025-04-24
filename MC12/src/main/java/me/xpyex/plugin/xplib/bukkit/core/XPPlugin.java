package me.xpyex.plugin.xplib.bukkit.core;

import me.xpyex.plugin.xplib.bukkit.bstats.Metrics;
import me.xpyex.plugin.xplib.util.value.ValueUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class XPPlugin extends JavaPlugin {
    public Metrics hookBStats(int id) {
        return new Metrics(this, id);
        //
    }

    public void registerCmd(String command, @Nullable CommandExecutor executor, @Nullable TabCompleter completer) throws IllegalArgumentException {
        PluginCommand cmd = this.getCommand(command);
        ValueUtil.notNull("未在 plugin.yml 内注册命令: " + command, cmd);

        cmd.setExecutor(executor);
        cmd.setTabCompleter(completer);
    }

    public void registerCmd(String cmd, @NotNull TabCompleter completer) {
        registerCmd(cmd, null, completer);
        //
    }

    public void registerCmd(String cmd, @NotNull CommandExecutor executor) {
        registerCmd(cmd, executor, null);
        //
    }

    public void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
        //
    }
}
