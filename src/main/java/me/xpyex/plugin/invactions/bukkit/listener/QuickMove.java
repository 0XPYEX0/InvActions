package me.xpyex.plugin.invactions.bukkit.listener;

import me.xpyex.plugin.invactions.bukkit.util.InvUtil;
import me.xpyex.plugin.invactions.bukkit.util.SortUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class QuickMove implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getClick() == ClickType.MIDDLE) {  //鼠标中键
            if (event.getCursor() == null) {  //光标拿着的物品
                return;
            }
            ItemStack tool = new ItemStack(event.getWhoClicked().getInventory().getItemInMainHand());  //保存手上道具，待会set回去，下面丢出会覆盖主手道具
            ItemStack item = new ItemStack(event.getCursor());
            if (event.getClickedInventory() == null) {  //这就是丢出
                for (ItemStack content : event.getWhoClicked().getInventory().getStorageContents()) {
                    if (content == null) continue;

                    if (ItemUtil.equals(content, item)) {
                        ItemStack copied = new ItemStack(content);
                        event.getWhoClicked().getInventory().setItemInMainHand(copied);
                        content.setAmount(0);
                        event.getWhoClicked().dropItem(true);
                    }
                }

                if (event.getWhoClicked().getOpenInventory().getTopInventory() != event.getWhoClicked().getInventory()) {
                    for (ItemStack content : event.getWhoClicked().getOpenInventory().getTopInventory()) {
                        if (content == null) continue;

                        if (ItemUtil.equals(content, item)) {
                            ItemStack copied = new ItemStack(content);
                            event.getWhoClicked().getInventory().setItemInMainHand(copied);
                            content.setAmount(0);
                            event.getWhoClicked().dropItem(true);
                        }
                    }
                }

                event.getCursor().setAmount(0);
                event.getWhoClicked().getInventory().setItemInMainHand(item);
                event.getWhoClicked().dropItem(true);  //丢出光标道具

                event.getWhoClicked().getInventory().setItemInMainHand(tool);
                ((Player) event.getWhoClicked()).updateInventory();
                MsgUtil.sendActionBar((Player) event.getWhoClicked(), "&a已丢出所有相同道具. " + SortUtil.SETTING_HELP);
            } else if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
                if (event.getWhoClicked().getOpenInventory().getTopInventory() == event.getWhoClicked().getInventory()) {
                    return;
                }
                if (InvUtil.isNotMenu(event.getWhoClicked().getOpenInventory().getTopInventory())) {
                    for (ItemStack content : event.getWhoClicked().getOpenInventory().getTopInventory().getStorageContents()) {
                        int firstEmpty = event.getWhoClicked().getInventory().firstEmpty();
                        if (firstEmpty == -1) break;

                        if (content == null) continue;

                        if (ItemUtil.equals(content, item)) {
                            ItemStack copied = new ItemStack(content);
                            event.getWhoClicked().getInventory().setItem(firstEmpty, copied);
                            content.setAmount(0);
                        }
                    }
                    MsgUtil.sendActionBar((Player) event.getWhoClicked(), "&a已将所有相同道具移至你背包. " + SortUtil.SETTING_HELP);
                }
            } else if (event.getClickedInventory() == event.getWhoClicked().getOpenInventory().getTopInventory()) {
                if (InvUtil.isNotMenu(event.getClickedInventory())) {
                    for (ItemStack content : event.getWhoClicked().getInventory().getStorageContents()) {
                        int firstEmpty = event.getWhoClicked().getOpenInventory().getTopInventory().firstEmpty();
                        if (firstEmpty == -1) break;

                        if (content == null) continue;

                        if (ItemUtil.equals(content, item)) {
                            ItemStack copied = new ItemStack(content);
                            event.getWhoClicked().getOpenInventory().getTopInventory().setItem(firstEmpty, copied);
                            content.setAmount(0);
                        }
                    }
                    MsgUtil.sendActionBar((Player) event.getWhoClicked(), "&a已将所有相同道具移至容器内. " + SortUtil.SETTING_HELP);
                }
            }
        }
    }
}
