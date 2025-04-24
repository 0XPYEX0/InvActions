package me.xpyex.lib.xplib.bukkit.inventory.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * 当从ModifiableButton格子中取出/放下物品时执行的方法
 */
public interface ButtonReturnItem {
    ItemStack returnItem(Player player, ClickType action, ItemStack i) throws Throwable;
}
