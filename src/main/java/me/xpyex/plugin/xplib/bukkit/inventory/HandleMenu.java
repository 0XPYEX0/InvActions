package me.xpyex.plugin.xplib.bukkit.inventory;

import me.xpyex.plugin.xplib.bukkit.inventory.button.Button;
import me.xpyex.plugin.xplib.bukkit.inventory.button.ButtonClickEffect;
import me.xpyex.plugin.xplib.bukkit.inventory.button.ButtonReturnItem;
import me.xpyex.plugin.xplib.bukkit.inventory.button.ModifiableButton;
import me.xpyex.plugin.xplib.bukkit.inventory.button.UnmodifiableButton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class HandleMenu implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player whoClicked = (Player) event.getWhoClicked();
        Menu menu = Menu.getOpeningMenu(whoClicked);
        if (menu == null) {
            return;
        }
        if (event.getClickedInventory() == null) {
            event.setCancelled(true);
            return;
        }
        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
            if (event.isShiftClick()) {
                event.setCancelled(true);  //不允许用Shift把东西放入菜单
            }
            return;
        }
        if (event.getClickedInventory() != whoClicked.getInventory()) {  //玩家点击的是Menu，才能拿到Button，接下去的操作才有意义
            Button button = menu.getButton(event.getSlot());
            if (button == null) {  //假设点击的不是Menu里的Button，那就是装饰格
                event.setCancelled(true);
                return;
            }
            //往下，即玩家点击的是Menu里的Button格

            ButtonClickEffect clickEffect = button.getClickEffect();
            if (clickEffect != null) {
                try {
                    clickEffect.click(whoClicked, event.getClick(), event.getCurrentItem());
                } catch (Throwable e) {
                    throw new IllegalStateException(e);
                }
            }
            if (button instanceof UnmodifiableButton) {
                event.setCancelled(true);
            } else if (button instanceof ModifiableButton) {
                ButtonReturnItem returnItemEffect = ((ModifiableButton) button).getReturnItem();
                if (returnItemEffect != null) {
                    ItemStack item;
                    try {
                        item = returnItemEffect.returnItem(whoClicked, event.getClick(), event.getCurrentItem());
                    } catch (Throwable e) {
                        throw new IllegalStateException(e);
                    }
                    ((ModifiableButton) button).setStack(item);
                    event.getClickedInventory().setItem(event.getSlot(), item);
                } else {
                    ((ModifiableButton) button).setStack(event.getCurrentItem());  //光标道具放到格子里
                    event.setCurrentItem(null);  //清除光标道具
                }
            }
        }
        menu.updateInventory();  //实时更新
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Menu.closed((Player) event.getPlayer());
        //
    }
}
