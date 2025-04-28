package me.xpyex.plugin.invactions.bukkit.util;

import java.io.File;
import me.xpyex.lib.xplib.bukkit.config.ConfigUtil;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsConfig;
import org.bukkit.entity.Player;

public class SettingsUtil {
    public static InvActionsConfig getConfig(Player player) {
        InvActionsConfig config = ConfigUtil.getConfig(InvActions.getInstance(), "players" + File.separator + player.getUniqueId(), InvActionsConfig.class);
        return config == null ? InvActionsConfig.fuckFakePlayers() : config;
    }
}
