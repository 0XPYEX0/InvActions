package me.xpyex.plugin.invactions.bukkit.module;

import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class QuickDrop extends RootModule {
    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (!serverEnabled()) return;  //服务端未启用
        if (!playerEnabled(event.getPlayer())) return;  //玩家未启用
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
                MsgUtil.sendActionBar(event.getPlayer(), getMessageWithSuffix("drop"));
                event.getPlayer().updateInventory();
            }
        }
    }
}
