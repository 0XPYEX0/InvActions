package me.xpyex.plugin.invactions.bukkit.module;

import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.util.InvUtil;
import me.xpyex.plugin.xplib.bukkit.strings.MsgUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ReplaceBrokenArmor extends RootModule {
    @EventHandler(ignoreCancelled = true)
    public void onItemBreak(PlayerItemBreakEvent event) {
        EquipmentSlot slot = null;
        for (EquipmentSlot value : EquipmentSlot.values()) {
            if (event.getBrokenItem().equals(event.getPlayer().getInventory().getItem(value))) {
                slot = value;
                break;
            }
        }
        if (slot != null) {
            switch (slot) {
                case HAND:
                case OFF_HAND:
                    return;
                default:
                    if (!serverEnabled() || !playerEnabled(event.getPlayer())) {  //如果玩家未开启替换盔甲
                        return;
                    }
            }
            EquipmentSlot finalSlot = slot;
            ItemStack brokenItem = new ItemStack(event.getBrokenItem());
            Bukkit.getScheduler().runTaskLater(InvActions.getInstance(), () -> {
                for (int i = 0; i < event.getPlayer().getInventory().getContents().length; i++) {
                    ItemStack content = event.getPlayer().getInventory().getItem(i);
                    if (content == null) continue;

                    if (content.getType() == brokenItem.getType()) {  //同一种类型的道具
                        InvUtil.swapSlot(event.getPlayer(), finalSlot, i);
                        MsgUtil.sendActionBar(event.getPlayer(), getMessageWithSuffix("broken"));
                        return;
                    }
                }
            }, 2L);
        }
    }
}
