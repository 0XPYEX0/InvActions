package me.xpyex.plugin.invactions.bukkit.util;

import com.google.gson.JsonObject;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.GsonUtil;
import org.bukkit.entity.Player;

public class SettingsUtil {
    public static boolean getSetting(Player player, String setting) {
        JsonObject o = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + player.getUniqueId());
        return o.has(setting) && o.get(setting).getAsBoolean();
    }

    public static void turnSetting(Player player, String setting) {
        JsonObject o = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + player.getUniqueId());  //不放到外面是为了实时更新
        boolean futureMode = !o.get(setting).getAsBoolean();
        o.addProperty(setting, futureMode);
        ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), GsonUtil.parseStr(o), true);
    }
}
