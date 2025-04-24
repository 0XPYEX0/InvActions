package me.xpyex.plugin.invactions.bukkit.module;

import me.xpyex.plugin.xplib.bukkit.strings.MsgUtil;
import me.xpyex.plugin.xplib.util.reflect.MethodUtil;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class QuickDrop extends RootModule {
    @Override
    protected boolean canLoad() {
        return MethodUtil.exist(HumanEntity.class, "dropItem");
        //
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (!serverEnabled()) return;  //服务端未启用
        if (!playerEnabled(event.getPlayer())) return;  //玩家未启用
        if (!isEndedCooldown(event.getPlayer(), 500)) return;  //每一秒只能触发两次
        if (event.getPlayer().isSneaking()) {
            ItemStack drop = event.getItemDrop().getItemStack();
            if (event.getPlayer().getInventory().contains(drop.getType())) {
                for (ItemStack content : event.getPlayer().getInventory().getStorageContents()) {
                    if (content == null) continue;

                    if (content.isSimilar(drop)) {
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
