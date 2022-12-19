package me.xpyex.plugin.invactions.bukkit.listener;

import java.util.ArrayList;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.invactions.bukkit.util.SortUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.NameUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AutoFarmer implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent event) {
        if (!SettingsUtil.getSetting(event.getPlayer(), "AutoFarmer")) {  //如果玩家没开启自动收割
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (ItemUtil.typeIsOr(event.getClickedBlock(), Material.MELON, Material.PUMPKIN)) {
                if (event.getItem() != null && event.getItem().getType() == Material.SHEARS) {
                    return;
                }
                BlockBreakEvent blockBreakEvent = new BlockBreakEvent(event.getClickedBlock(), event.getPlayer());
                Bukkit.getPluginManager().callEvent(blockBreakEvent);
                if (blockBreakEvent.isCancelled()) {
                    return;  //防止收割被保护的地方
                }
                event.setCancelled(true);
                MsgUtil.sendActionBar(event.getPlayer(), "&a已为您自动收获 &f" + NameUtil.getTranslationName(event.getClickedBlock().getType()) + "&a. " + SortUtil.SETTING_HELP);
                event.getClickedBlock().breakNaturally(event.getPlayer().getInventory().getItemInMainHand());
                return;
            }

            String blockData = event.getClickedBlock().getBlockData().getAsString();
            Material type = event.getClickedBlock().getType();
            int age;

            boolean canCut = false;
            if (type == Material.COCOA) {
                String cocoaAge = blockData.split(",")[0];
                age = Integer.parseInt(cocoaAge.split("")[cocoaAge.length() - 1]);
                if (age == 2) {
                    canCut = true;
                }
            } else if (blockData.contains("[age=")) {
                age = Integer.parseInt(blockData.split("")[blockData.length() - 2]);
                if (ItemUtil.typeIsOr(type, Material.WHEAT, Material.CARROTS, Material.POTATOES)) {
                    if (age == 7) {
                        canCut = true;
                    }
                } else if (ItemUtil.typeIsOr(type, Material.NETHER_WART, Material.BEETROOTS)) {
                    if (age == 3) {
                        canCut = true;
                    }
                }
            } else {
                return;
            }
            if (canCut) {
                String data = event.getClickedBlock().getBlockData().getAsString();
                data = data.replace("age=" + age, "age=0");
                BlockBreakEvent blockBreakEvent = new BlockBreakEvent(event.getClickedBlock(), event.getPlayer());
                Bukkit.getPluginManager().callEvent(blockBreakEvent);
                if (blockBreakEvent.isCancelled()) {
                    return;  //防止收割被保护的地方
                }
                BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(event.getClickedBlock(), event.getClickedBlock().getState(), event.getClickedBlock(), event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer(), true, event.getHand());
                Bukkit.getPluginManager().callEvent(blockPlaceEvent);
                if (blockPlaceEvent.isCancelled()) {
                    return;  //防止收割被保护的地方
                }
                event.setCancelled(true);
                ArrayList<ItemStack> result = new ArrayList<>(event.getClickedBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand()));
                for (ItemStack drop : result) {
                    if (ItemUtil.typeIsOr(drop, Material.WHEAT, Material.BEETROOT)) continue;

                    drop.setAmount(drop.getAmount() - 1);
                }
                event.getClickedBlock().setBlockData(Bukkit.createBlockData(data));
                for (ItemStack drop : result) {
                    event.getClickedBlock().getLocation().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), drop);
                }
                MsgUtil.sendActionBar(event.getPlayer(), "&a已为您自动收获并种植 &f" + NameUtil.getTranslationName(event.getClickedBlock().getType()) + "&a. " + SortUtil.SETTING_HELP);
            }
        }
    }
}
