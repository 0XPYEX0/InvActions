package me.xpyex.plugin.invactions.bukkit.module;

import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.util.InvUtil;
import me.xpyex.plugin.xplib.bukkit.inventory.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;

public class BetterInfinity extends RootModule {
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (serverEnabled() && playerEnabled(event.getPlayer())) {
            if (event.getAction().toString().startsWith("RIGHT_") && event.getItem() != null && event.getHand() != null) {
                if (event.getItem().getType() == Material.BOW) {
                    PlayerInventory playerInv = event.getPlayer().getInventory();
                    if (InvUtil.hasItemType(playerInv, Material.ARROW)) return;
                    if (event.getItem().getEnchantmentLevel(Enchantment.ARROW_INFINITE) <= 0) return;

                    EquipmentSlot targetHand = event.getHand() == EquipmentSlot.HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
                    ItemStack stack = playerInv.getItem(targetHand).clone();
                    playerInv.setItem(targetHand, ItemUtil.getItemStack(Material.ARROW, "&7&o无限附魔辅助箭", "更好的无限附魔"));
                    event.getPlayer().setMetadata("InvActions_BetterInfinity", new FixedMetadataValue(InvActions.getInstance(), true));

                    Bukkit.getScheduler().runTaskLater(InvActions.getInstance(), () -> {
                        event.getPlayer().getInventory().setItem(targetHand, stack);
                        event.getPlayer().removeMetadata("InvActions_BetterInfinity", InvActions.getInstance());
                        event.getPlayer().updateInventory();
                    }, event.getAction() == Action.RIGHT_CLICK_BLOCK ? 5L : 1L);
                }
            }
        }
    }

    @EventHandler
    public void onChangeTool(PlayerItemHeldEvent event) {
        if (event.getPlayer().hasMetadata("InvActions_BetterInfinity")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().hasMetadata("InvActions_BetterInfinity")) {
            event.setCancelled(true);
        }
    }
}
