package me.xpyex.plugin.invactions.bukkit.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.GsonUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SettingsUtil {
    public static final JsonObject DEFAULT_SETTINGS = new JsonObject();
    public static final JsonObject DEFAULT_SERVER_SETTINGS;
    public static final String SETTING_HELP = "&e该功能在 &f/InvActions &e中调整";

    static {
        DEFAULT_SETTINGS.addProperty("AutoFarmer", true);
        DEFAULT_SETTINGS.addProperty("AutoTool", true);
        DEFAULT_SETTINGS.addProperty("CraftDrop", true);
        DEFAULT_SETTINGS.addProperty("DefaultF", false);
        DEFAULT_SETTINGS.addProperty("QuickDrop", false);
        DEFAULT_SETTINGS.addProperty("QuickMove", true);
        DEFAULT_SETTINGS.addProperty("ReplaceBrokenArmor", true);
        DEFAULT_SETTINGS.addProperty("ReplaceBrokenTool", true);
        DEFAULT_SETTINGS.addProperty("DynamicLight", true);

        DEFAULT_SERVER_SETTINGS = GsonUtil.copy(DEFAULT_SETTINGS);
        DEFAULT_SERVER_SETTINGS.addProperty("AutoFarmer_AllowPumpkinAndMelon", false);
        DEFAULT_SERVER_SETTINGS.addProperty("Debug", false);
        DEFAULT_SERVER_SETTINGS.add("AllowInvs", new JsonArray());
    }

    public static boolean getSetting(Player player, String setting) {
        JsonObject o = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + player.getUniqueId());
        return getServerSetting(setting) && o.has(setting) && o.get(setting).getAsBoolean();
    }

    public static void turnSetting(Player player, String setting) {
        JsonObject o = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + player.getUniqueId());
        boolean futureMode = !o.get(setting).getAsBoolean();
        o.addProperty(setting, futureMode);
        ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), GsonUtil.parseStr(o), true);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
    }

    public static boolean getServerSetting(String setting) {
        JsonObject o = ConfigUtil.getConfig(InvActions.getInstance());
        return o.has(setting) && o.get(setting).getAsBoolean();
    }

    public static int getMenuShow(Player player, String setting) {
        if (!getServerSetting(setting)) return -1;

        return getSetting(player, setting) ? 1 : 0;
    }
}
