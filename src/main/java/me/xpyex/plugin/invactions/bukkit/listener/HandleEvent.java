package me.xpyex.plugin.invactions.bukkit.listener;

import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsConfig;
import me.xpyex.plugin.invactions.bukkit.util.SchedulerUtil;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HandleEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        SchedulerUtil.runTaskAsync(() -> {  //异步操作文件
            ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + event.getPlayer().getUniqueId(), InvActionsConfig.getDefault(), false, false);
            //修复没打开过GUI设定的玩家使用道具会抛错
            ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + event.getPlayer().getUniqueId(), SettingsUtil.getConfig(event.getPlayer()), true);
            //更新设定. 如果以后有新设定，玩家进服便可直接使用
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        SchedulerUtil.runTaskAsync(() -> {
            ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + event.getPlayer().getUniqueId(), SettingsUtil.getConfig(event.getPlayer()), true);
        });
    }
}
