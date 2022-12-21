package me.xpyex.plugin.invactions.bukkit.util;

import com.google.gson.JsonPrimitive;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InvUtil {
    public static boolean isNotMenu(Inventory inv) {
        String className = inv.getHolder() != null ? inv.getHolder().getClass().getName() : "null";
        String simpleName = inv.getHolder() != null ? inv.getHolder().getClass().getSimpleName() : "null";
        MsgUtil.debugLog(InvActions.getInstance(), "InvUtil.isNotMenu(): " + className);
        if (ConfigUtil.getConfig(InvActions.getInstance()).get("AllowInvs").getAsJsonArray().contains(new JsonPrimitive(simpleName))) {
            return true;
        }
        if (inv.getHolder() == null || inv.getHolder() instanceof Player) {
            return false;
        }
        return className.startsWith("org.bukkit.");
    }
}
