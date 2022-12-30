package me.xpyex.plugin.invactions.bukkit.listener;

import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class QuickDrop implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (SettingsUtil.getSetting(event.getPlayer(), "QuickDrop")) {
            if (event.getPlayer().isSneaking()) {
                ItemStack drop = event.getItemDrop().getItemStack();
                if (event.getPlayer().getInventory().contains(drop.getType())) {
                    for (ItemStack content : event.getPlayer().getInventory().getStorageContents()) {
                        if (content == null) continue;

                        if (ItemUtil.equals(content, drop)) {
                            ItemStack copied = new ItemStack(content);
                            event.getPlayer().getInventory().setItemInMainHand(copied);
                            content.setAmount(0);
                            event.getPlayer().dropItem(true);
                        }
                    }
                    MsgUtil.sendActionBar(event.getPlayer(), "&a已丢出背包所有相同道具. " + SettingsUtil.SETTING_HELP);
                    event.getPlayer().updateInventory();
                }
            }
        }
    }
}
