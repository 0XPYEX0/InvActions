package me.xpyex.plugin.invactions.bukkit.listener;

import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class QuickShulkerBox implements Listener {
    private static final String METADATA_KEY = "InvActions_Shulker";

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (InvActionsServerConfig.getConfig().QuickShulkerBox) {
            if (SettingsUtil.getConfig((Player) event.getWhoClicked()).QuickShulkerBox) {
                if (event.isShiftClick() && event.isRightClick()) {
                    if (event.getCursor() != null && event.getCursor().getType() != Material.AIR)
                        return;
                    if (event.getCurrentItem() == null) return;
                    ItemMeta meta = event.getCurrentItem().getItemMeta();
                    if (meta instanceof BlockStateMeta) {  //此处同时判断 != null
                        BlockState state = ((BlockStateMeta) meta).getBlockState();
                        if (state instanceof ShulkerBox) {
                            event.setCancelled(true);
                            event.getWhoClicked().openInventory(((ShulkerBox) state).getInventory());
                            event.getWhoClicked().setMetadata(METADATA_KEY, new FixedMetadataValue(InvActions.getInstance(), event.getCurrentItem()));
                            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1f, 1f);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!InvActionsServerConfig.getConfig().QuickShulkerBox) {
            return;
        }
        if (event.getPlayer().isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() != null && event.getItem().hasItemMeta()) {
                ItemMeta meta = event.getItem().getItemMeta();
                if (meta instanceof BlockStateMeta) {
                    BlockState state = ((BlockStateMeta) meta).getBlockState();
                    if (state instanceof ShulkerBox) {
                        event.setCancelled(true);
                        event.getPlayer().openInventory(((ShulkerBox) state).getInventory());
                        event.getPlayer().setMetadata(METADATA_KEY, new FixedMetadataValue(InvActions.getInstance(), event.getItem()));
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1f, 1f);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().hasMetadata(METADATA_KEY)) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
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

    @EventHandler
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
                        }, 1L);
                    }
                }
            }
        }
    }
}
