package me.xpyex.plugin.invactions.bukkit.module;

import java.util.Collection;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import me.xpyex.plugin.xplib.bukkit.util.language.LangUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.StrUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AutoFarmer extends RootModule {
    @EventHandler(ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent event) {
        if (InvActionsServerConfig.getConfig().AutoFarmer && SettingsUtil.getConfig(event.getPlayer()).AutoFarmer) {  //如果玩家开启自动收割
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (ItemUtil.typeIsOr(event.getClickedBlock(), Material.MELON, Material.PUMPKIN)) {
                    if (InvActionsServerConfig.getConfig().AutoFarmer_AllowPumpkinAndMelon) return;

                    if (event.getItem() != null && event.getItem().getType() == Material.SHEARS) return;

                    BlockBreakEvent blockBreakEvent = new BlockBreakEvent(event.getClickedBlock(), event.getPlayer());
                    Bukkit.getPluginManager().callEvent(blockBreakEvent);
                    if (blockBreakEvent.isCancelled()) {
                        return;  //防止收割被保护的地方
                    }
                    event.setCancelled(true);
                    MsgUtil.sendActionBar(event.getPlayer(), getMessageWithSuffix("harvest", LangUtil.getItemName(InvActions.getInstance(), event.getClickedBlock().getType())));
                    event.getClickedBlock().breakNaturally(event.getPlayer().getInventory().getItemInMainHand());
                    return;
                }

                BlockData blockData = event.getClickedBlock().getBlockData();
                if (blockData instanceof Ageable && ((Ageable) blockData).getAge() >= ((Ageable) blockData).getMaximumAge()) {
                    if (StrUtil.endsWithIgnoreCaseOr(event.getClickedBlock().getType().toString(), "_STEM", "FIRE")) {  //西瓜、南瓜的茎; 火
                        return;
                    }
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
                    Collection<ItemStack> result = event.getClickedBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand());
                    for (ItemStack drop : result) {
                        if (ItemUtil.typeIsOr(drop, Material.WHEAT, Material.BEETROOT, Material.POISONOUS_POTATO))
                            continue;

                        drop.setAmount(drop.getAmount() - 1);
                    }
                    ((Ageable) blockData).setAge(0);  //先判断能不能处理，再收割
                    event.getClickedBlock().setBlockData(blockData);
                    for (ItemStack drop : result) {
                        if (drop == null || drop.getAmount() == 0) {
                            continue;
                        }
                        event.getClickedBlock().getLocation().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), drop);
                    }
                    MsgUtil.sendActionBar(event.getPlayer(), getMessageWithSuffix("harvest_and_plant", LangUtil.getItemName(InvActions.getInstance(), event.getClickedBlock().getType())));
                }
            }
        }
    }
}
