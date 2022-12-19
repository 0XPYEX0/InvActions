package me.xpyex.plugin.invactions.bukkit.listener;

import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.invactions.bukkit.util.SortUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class ReplaceBroken implements Listener {


    @EventHandler(ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent event) {
        SortUtil.replaceTool(event.getPlayer(), new ItemStack(event.getItem()));
        //
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemBreak(PlayerItemBreakEvent event) {
        EquipmentSlot slot = null;
        for (EquipmentSlot value : EquipmentSlot.values()) {
            if (event.getBrokenItem().equals(event.getPlayer().getInventory().getItem(value))) {
                slot = value;
                break;
            }
        }
        if (slot != null) {
            switch (slot) {
                case HAND:
                case OFF_HAND:
                    if (!SettingsUtil.getSetting(event.getPlayer(), "ReplaceBrokenTool")) {  //如果玩家未开启替换手中道具
                        return;
                    }
                default:
                    if (!SettingsUtil.getSetting(event.getPlayer(), "ReplaceBrokenArmor")) {  //如果玩家未开启替换盔甲
                        return;
                    }
            }
            EquipmentSlot finalSlot = slot;
            ItemStack brokenItem = new ItemStack(event.getBrokenItem());
            Bukkit.getScheduler().runTaskLater(InvActions.getInstance(), () -> {
                for (ItemStack content : event.getPlayer().getInventory().getContents()) {
                    if (content == null) continue;

                    if (content.getType() == brokenItem.getType()) {  //同一种类型的道具
                        ItemStack out = new ItemStack(content);
                        content.setAmount(0);
                        event.getPlayer().getInventory().setItem(finalSlot, out);
                        MsgUtil.sendActionBar(event.getPlayer(), "&a您的道具已损毁，从背包补全. " + SortUtil.SETTING_HELP);
                        return;
                    }
                }
            }, 2L);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShoot(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {  //玩家射东西了
            if (event.getEntity() instanceof ThrowableProjectile && event.getEntity().getType() != EntityType.TRIDENT) {  //扔的还是投掷物
                ItemStack before = new ItemStack(((ThrowableProjectile) event.getEntity()).getItem());
                Player p = (Player) event.getEntity().getShooter();
                SortUtil.replaceTool(p, before);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }

        if (event.getItem() == null) return;

        if (event.getItem().hasItemMeta() && event.getItem().getItemMeta() instanceof Damageable)
            return;  //可破坏的物品留给上面处理，不在这处理

        if (event.getItem().getType() == Material.EGG || event.getItem().getType() == Material.ENDER_PEARL || event.getItem().getType() == Material.SPLASH_POTION || event.getItem().getType() == Material.SNOWBALL) {
            return;  //蛋、末影珍珠、喷溅型药水、雪球，  在onShoot方法处理
        }

        ItemStack before = new ItemStack(event.getItem());
        SortUtil.replaceTool(event.getPlayer(), before);
    }
}
