package me.xpyex.plugin.invactions.bukkit.listener;

import com.google.gson.JsonObject;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.GsonUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HandleEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(InvActions.getInstance(), () -> {  //异步操作文件
            ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + event.getPlayer().getUniqueId(), GsonUtil.parseStr(SettingsUtil.DEFAULT_SETTINGS), false);
            //修复没打开过GUI设定的玩家使用道具会抛错
            JsonObject before = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + event.getPlayer().getUniqueId());
            JsonObject out = GsonUtil.copy(SettingsUtil.DEFAULT_SETTINGS);
            for (String setting : GsonUtil.getKeysOfJsonObject(SettingsUtil.DEFAULT_SETTINGS)) {
                if (before.has(setting)) {
                    out.add(setting, before.get(setting));
                }
            }  //更新设定. 如果以后有新设定，玩家进服便可直接使用
            ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + event.getPlayer().getUniqueId(), GsonUtil.parseStr(out), true);
        });
    }
}
