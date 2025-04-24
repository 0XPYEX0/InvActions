package me.xpyex.plugin.invactions.bukkit.util;

import lombok.Getter;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.enums.ToolType;
import me.xpyex.plugin.xplib.api.Pair;
import me.xpyex.plugin.xplib.bukkit.strings.MsgUtil;
import me.xpyex.plugin.xplib.bukkit.version.VersionUtil;
import me.xpyex.plugin.xplib.util.reflect.MethodUtil;
import me.xpyex.plugin.xplib.util.strings.StrUtil;
import me.xpyex.plugin.xplib.util.value.ValueUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InvUtil {
    public static final ItemStack AIR_STACK = new ItemStack(Material.AIR);

    public static boolean isNotMenu(Inventory inv) {
        String className = inv.getHolder() != null ? inv.getHolder().getClass().getName() : "null";
        String simpleName = inv.getHolder() != null ? inv.getHolder().getClass().getSimpleName() : "null";
        MsgUtil.debugLog(InvActions.getInstance(), "InvUtil.isNotMenu(): " + className);
        if (InvActionsServerConfig.getConfig().AllowInvs.contains(simpleName)) {
            return true;
        }
        if (inv.getHolder() == null || inv.getHolder() instanceof Player) {
            return false;
        }
        return className.startsWith("org.bukkit.");
    }

    public static void swapSlot(Player player, int equipmentSlot, int slot) {
        ValueUtil.notNull("参数为空，请联系开发者修复", player, equipmentSlot);

        if (player.getInventory().getHeldItemSlot() == slot) {
            return;
        }

        ItemStack copiedTool = new ItemStack(player.getInventory().getItem(equipmentSlot));
        ItemStack copiedSlot = new ItemStack(ValueUtil.getOrDefault(player.getInventory().getItem(slot), AIR_STACK));
        player.getInventory().setItem(slot, copiedTool);
        player.getInventory().setItem(equipmentSlot, copiedSlot);
    }

    public static void swapSlot(Player player, EquipmentSlot equipmentSlot, int slot) {
        swapSlot(player, getEquipmentSlotId(player, equipmentSlot), slot);
    }

    public static int getEquipmentSlotId(Player player, EquipmentSlot slot) {
        int equip = -999;
        switch (slot) {
            case HEAD:
                equip = 39;
                break;
            case CHEST:
                equip = 38;
                break;
            case LEGS:
                equip = 37;
                break;
            case FEET:
                equip = 36;
                break;
            case HAND:
                equip = player.getInventory().getHeldItemSlot();
                break;
            case OFF_HAND:
                equip = 40;
                break;
        }
        return equip;
    }

    public static boolean hasItemType(Inventory inv, Material type) {
        for (ItemStack stack : inv) {
            if (stack == null) continue;

            if (type == stack.getType())
                return true;
        }
        return false;
    }
}
