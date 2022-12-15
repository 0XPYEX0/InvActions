package me.xpyex.plugin.sortitems.bukkit;

import me.xpyex.plugin.sortitems.bukkit.listener.HandleEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SortItems extends JavaPlugin {
    private static SortItems INSTANCE;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        if (!getServer().getPluginManager().isPluginEnabled("XPLib")) {
            getLogger().warning("本插件需要XPLib作为前置...");
            getLogger().warning("请在 https://github.com/0XPYEX0/XPLib/releases 下载后，再加载插件");
            getServer().getPluginManager().disablePlugin(INSTANCE);
            return;
        }
        getServer().getPluginManager().registerEvents(new HandleEvent(), getInstance());
        getLogger().info("已加载");
    }

    @Override
    public void onDisable() {
        getLogger().info("已卸载");
        //
    }

    public static SortItems getInstance() {
        return INSTANCE;
        //
    }
}
