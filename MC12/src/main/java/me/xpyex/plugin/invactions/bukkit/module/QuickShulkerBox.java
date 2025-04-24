package me.xpyex.plugin.invactions.bukkit.module;

import me.xpyex.plugin.invactions.bukkit.InvActions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class QuickShulkerBox extends RootModule {
    private static final String METADATA_KEY = "InvActions_Shulker";

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (serverEnabled() && playerEnabled((Player) event.getWhoClicked())) {
            if (event.isShiftClick() && event.isRightClick()) {
                if (event.getCursor() != null || event.getCursor().getType() != Material.AIR) return;
                if (event.getCurrentItem() == null) return;
                if (event.getCurrentItem().getAmount() != 1) return;

                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta instanceof BlockStateMeta) {  //此处同时判断 != null
                    BlockState state = ((BlockStateMeta) meta).getBlockState();
                    if (state instanceof ShulkerBox) {
                        event.setCancelled(true);
                        Inventory boxInv = ((ShulkerBox) state).getInventory();
                        try {
                            event.getWhoClicked().openInventory(boxInv);
                        } catch (Throwable e) {
                            Inventory inventory = Bukkit.createInventory(event.getWhoClicked(), 27);
                            inventory.setContents(boxInv.getContents());
                            event.getWhoClicked().openInventory(inventory);
                        }
                        event.getWhoClicked().setMetadata(METADATA_KEY, new FixedMetadataValue(InvActions.getInstance(), event.getCurrentItem()));
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1f, 1f);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent event) {
        if (!serverEnabled() || !playerEnabled(event.getPlayer())) {
            return;
        }
        if (event.getPlayer().isSneaking() && event.getAction().toString().startsWith("RIGHT_")) {
            if (event.getItem() != null) {
                if (event.getItem().getAmount() != 1) return;
                ItemMeta meta = event.getItem().getItemMeta();
                if (meta instanceof BlockStateMeta) {  //判断not null
                    BlockState state = ((BlockStateMeta) meta).getBlockState();
                    if (state instanceof ShulkerBox) {
                        event.setCancelled(true);
                        Inventory boxInv = ((ShulkerBox) state).getInventory();
                        try {
                            event.getPlayer().openInventory(boxInv);
                        } catch (Throwable e) {
                            Inventory inventory = Bukkit.createInventory(event.getPlayer(), 27);
                            inventory.setContents(boxInv.getContents());
                            event.getPlayer().openInventory(inventory);
                        }
                        event.getPlayer().setMetadata(METADATA_KEY, new FixedMetadataValue(InvActions.getInstance(), event.getItem()));
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1f, 1f);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().hasMetadata(METADATA_KEY)) {
            if (event.getItemDrop().getItemStack().getType().toString().endsWith("SHULKER_BOX")) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTaskLater(InvActions.getInstance(), () -> event.getPlayer().updateInventory(), 2L);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked().hasMetadata(METADATA_KEY)) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType().toString().endsWith("SHULKER_BOX")) {
                event.setCancelled(true);
            }
            if (event.getAction().toString().startsWith("HOTBAR_")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (event.getPlayer().hasMetadata(METADATA_KEY)) {
            ItemStack stack = (ItemStack) event.getPlayer().getMetadata(METADATA_KEY).get(0).value();
            if (stack != null) {
                ItemMeta meta = stack.getItemMeta();
                if (meta instanceof BlockStateMeta) {  //此处同时判断 != null
                    BlockState state = ((BlockStateMeta) meta).getBlockState();
                    if (state instanceof ShulkerBox) {
                        ((ShulkerBox) state).getInventory().setContents(event.getInventory().getContents());
                        ((BlockStateMeta) meta).setBlockState(state);
                        stack.setItemMeta(meta);
                        event.getPlayer().removeMetadata(METADATA_KEY, InvActions.getInstance());
                        ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, 1f, 1f);
                        Bukkit.getScheduler().runTaskLater(InvActions.getInstance(), () -> {
                            ((Player) event.getPlayer()).updateInventory();
                        }, 2L);
                    }
                }
            }
        }
    }
}
