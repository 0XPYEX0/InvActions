package me.xpyex.plugin.sortitems.bukkit.listener;

import me.xpyex.plugin.sortitems.bukkit.util.SortUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.BlockInventoryHolder;

public class HighVerListener implements Listener {

    @EventHandler
    public void onPressFWithInv(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }
        if (event.getClick() == ClickType.SWAP_OFFHAND) {  //不知道哪个版本加上的
            if (event.getClickedInventory() == event.getWhoClicked().getInventory() || event.getClickedInventory().getHolder() instanceof BlockInventoryHolder || event.getClickedInventory().getHolder() instanceof DoubleChest) {  //仅允许整理自己的背包，或是方块的界面，不允许整理菜单
                event.setCancelled(true);
                SortUtil.sortInv(event.getClickedInventory());
            }
        }
    }
}
