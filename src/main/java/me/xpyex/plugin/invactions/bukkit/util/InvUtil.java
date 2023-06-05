package me.xpyex.plugin.invactions.bukkit.util;

import com.google.gson.JsonPrimitive;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.enums.ItemType;
import me.xpyex.plugin.xplib.bukkit.api.Pair;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.StrUtil;
import me.xpyex.plugin.xplib.bukkit.util.value.ValueUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InvUtil {

    public static final ItemStack AIR_STACK = new ItemStack(Material.AIR);

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

    public static void swapSlot(Player player, EquipmentSlot equipmentSlot, int slot) {
        ValueUtil.checkNull("参数为空，请联系开发者修复", player, equipmentSlot);

        if (player.getInventory().getHeldItemSlot() == slot) {
            return;
        }

        ItemStack copiedTool = new ItemStack(player.getInventory().getItem(equipmentSlot));
        ItemStack copiedSlot = new ItemStack(ValueUtil.getOrDefault(player.getInventory().getItem(slot), AIR_STACK));
        player.getInventory().setItem(slot, copiedTool);
        player.getInventory().setItem(equipmentSlot, copiedSlot);
    }

    public static int getFastestToolSlot(Player player, Block block, ItemType.ToolType type) {
        if (player.getInventory().getItemInMainHand().getType().toString().contains("_" + type))
            return player.getInventory().getHeldItemSlot();
        Pair<Float, Integer> fastest = new Pair<>(block.getBreakSpeed(player), player.getInventory().getHeldItemSlot());  //速度, Slot
        ItemStack before = new ItemStack(player.getInventory().getItemInMainHand());
        int slot;
        for (slot = 0; slot < player.getInventory().getStorageContents().length; slot++) {
            ItemStack content = player.getInventory().getStorageContents()[slot];
            if (content == null) continue;

            boolean shouldCompute = false;

            if (type == ItemType.ToolType.SHEARS && content.getType() == Material.SHEARS) {
                shouldCompute = true;
            } else if (StrUtil.endsWithIgnoreCaseOr(content.getType().toString(), "_" + type)) {
                shouldCompute = true;
            }

            if (shouldCompute) {
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
