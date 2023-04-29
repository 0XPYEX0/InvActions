package me.xpyex.plugin.invactions.bukkit.util;

import com.google.gson.JsonPrimitive;
import java.util.Optional;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.enums.ItemType;
import me.xpyex.plugin.xplib.bukkit.api.Pair;
import me.xpyex.plugin.xplib.bukkit.util.Util;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

    public static void swapSlotToMainHand(Player player, int slot) {
        Util.checkNull("Player为空，请联系开发者修复", player);

        if (player.getInventory().getHeldItemSlot() == slot) {
            return;
        }

        ItemStack copiedTool = new ItemStack(player.getInventory().getItemInMainHand());
        ItemStack copiedSlot = new ItemStack(Optional.ofNullable(player.getInventory().getItem(slot)).orElse(new ItemStack(Material.AIR)));
        player.getInventory().setItem(slot, copiedTool);
        player.getInventory().setItemInMainHand(copiedSlot);
        MsgUtil.sendActionBar(player, "&a已自动切换为合适的工具. " + SettingsUtil.SETTING_HELP);
    }

    public static int getFastestToolSlot(Player player, Block block, ItemType.ToolType type) {
        if (player.getInventory().getItemInMainHand().getType().toString().contains("_" + type)) return player.getInventory().getHeldItemSlot();
        Pair<Float, Integer> fastest = new Pair<>(0f, -1);  //速度, Slot
        ItemStack before = new ItemStack(player.getInventory().getItemInMainHand());
        int slot;
        for (slot = 0; slot < player.getInventory().getStorageContents().length; slot++) {
            ItemStack content = player.getInventory().getStorageContents()[slot];
            if (content == null) continue;

            if (content.getType().toString().contains("_" + type)) {
                player.getInventory().setItemInMainHand(content);
                float breakSpeed = block.getBreakSpeed(player);
                if (fastest.getKey() < breakSpeed) {
                    fastest = new Pair<>(breakSpeed, slot);
                }
            }
        }
        player.getInventory().setItemInMainHand(before);
        return fastest.getValue() == -1 ? player.getInventory().getHeldItemSlot() : fastest.getValue();
    }

}
