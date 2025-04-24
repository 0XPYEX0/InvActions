package me.xpyex.plugin.invactions.bukkit.module;

import java.io.File;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsConfig;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.config.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HandleEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(InvActions.getInstance(), () -> {  //异步操作文件
            ConfigUtil.saveConfig(InvActions.getInstance(), "players" + File.separator + event.getPlayer().getUniqueId(), InvActionsConfig.getDefault(), false, false);
            //修复没打开过GUI设定的玩家使用道具会抛错
            ConfigUtil.saveConfig(InvActions.getInstance(), "players" + File.separator + event.getPlayer().getUniqueId(), SettingsUtil.getConfig(event.getPlayer()), true);
            //更新设定. 如果以后有新设定，玩家进服便可直接使用
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(InvActions.getInstance(), () -> {
            ConfigUtil.saveConfig(InvActions.getInstance(), "players" + File.separator + event.getPlayer().getUniqueId(), SettingsUtil.getConfig(event.getPlayer()), true);
        });
    }
}
