package me.xpyex.plugin.invactions.bukkit.module;

import me.xpyex.lib.xplib.api.Pair;
import me.xpyex.lib.xplib.bukkit.version.VersionUtil;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.util.InvUtil;
import me.xpyex.lib.xplib.bukkit.inventory.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;

public class BetterInfinity extends RootModule {
    private static final ItemStack THE_ARROW = ItemUtil.getItemStack(Material.ARROW, "&7&o无限附魔辅助箭", "更好的无限附魔");
    private static final String METADATA_KEY = "InvActions_BetterInfinity";

    @Override
    protected boolean canLoad() {
        return VersionUtil.getMainVersion() < 21;
    }

    @EventHandler(ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent event) {
        if (serverEnabled() && playerEnabled(event.getPlayer())) {
            if (event.getAction().toString().startsWith("RIGHT_") && event.getItem() != null && event.getHand() != null) {
                if (event.getItem().getType() == Material.BOW) {
                    if (!isEndedCooldown(event.getPlayer(), 200)) return;  //每4tick使用一次

                    PlayerInventory playerInv = event.getPlayer().getInventory();
                    if (InvUtil.hasItemType(playerInv, Material.ARROW)) return;
                    if (event.getItem().getEnchantmentLevel(Enchantment.ARROW_INFINITE) <= 0) return;

                    int equipmentSlotId = InvUtil.getEquipmentSlotId(event.getPlayer(), event.getHand() == EquipmentSlot.HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
                    ItemStack stack = playerInv.getItem(equipmentSlotId);
                    stack = stack == null ? InvUtil.AIR_STACK : stack.clone();
                    playerInv.setItem(equipmentSlotId, THE_ARROW);
                    event.getPlayer().setMetadata(METADATA_KEY, new FixedMetadataValue(InvActions.getInstance(), true));

                    ItemStack finalStack = stack;
                    Bukkit.getScheduler().runTaskLater(InvActions.getInstance(), () -> {
                        if (THE_ARROW.isSimilar(playerInv.getItem(equipmentSlotId))) {  //确保副手还是我们的模拟箭，才set。否则干脆扔地上自己捡
                            event.getPlayer().getInventory().setItem(equipmentSlotId, finalStack);
                        } else {
                            event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), finalStack);
                        }
                        event.getPlayer().removeMetadata(METADATA_KEY, InvActions.getInstance());
                        event.getPlayer().updateInventory();
                    }, event.getAction() == Action.RIGHT_CLICK_BLOCK ? 5L : 1L);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChangeTool(PlayerItemHeldEvent event) {
        if (event.getPlayer().hasMetadata(METADATA_KEY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().hasMetadata(METADATA_KEY)) {
            event.setCancelled(true);
        }
    }
}
