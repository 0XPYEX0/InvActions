package me.xpyex.plugin.sortitems.bukkit.listener;

import me.xpyex.plugin.sortitems.bukkit.util.SortUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HighVerListener implements Listener {

    @EventHandler
    public void onPressFWithInv(InventoryClickEvent event) {
        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {  //仅允许整理自己的背包
            if (event.getClick() == ClickType.SWAP_OFFHAND) {  //不知道哪个版本加上的
                event.setCancelled(true);
                SortUtil.sortInv(event.getWhoClicked().getInventory());
                event.getWhoClicked().sendMessage(MsgUtil.getColorMsg("&a已整理你的背包"));
            }
        }
    }
}
