package me.xpyex.plugin.sortitems.bukkit.listener;

import com.google.gson.JsonObject;
import java.util.HashSet;
import me.xpyex.plugin.sortitems.bukkit.SortItems;
import me.xpyex.plugin.sortitems.bukkit.command.HandleCmd;
import me.xpyex.plugin.sortitems.bukkit.util.SortUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.GsonUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.NameUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class HandleEvent implements Listener {
    private final static HashSet<Material> IGNORES = new HashSet<>();
    static {
        for (Material m : Material.values()) {
            switch (m) {
                case REDSTONE_WIRE:  //红石线
                case REDSTONE_WALL_TORCH:
                case SNOW:  //雪层
                case LAVA:  //岩浆
                case WATER:  //水
                    IGNORES.add(m);
                    break;
            }
            if (m.toString().endsWith("_BUTTON")) {  //按钮
                IGNORES.add(m);
            } else if (m.toString().endsWith("_PRESSURE_PLATE")) {  //压力板
                IGNORES.add(m);
            } else if (m.isAir()) {  //任意空气
                IGNORES.add(m);
            } else if (m.toString().endsWith("_SIGN")) {  //牌子
                IGNORES.add(m);
            } else if (m.toString().contains("VINE")) {  //藤蔓系列
                IGNORES.add(m);
            } else if (m.toString().endsWith("_TRAPDOOR")) {  //活板门
                IGNORES.add(m);
            } else if (m.toString().endsWith("_FENCE")) {  //栅栏
                IGNORES.add(m);
            } else if (m.toString().endsWith("FIRE")) {  //火、灵魂火
                IGNORES.add(m);
            } else if (m.toString().contains("TORCH")) {  //各种火把，为了兼容没有灵魂火把的版本
                IGNORES.add(m);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent event) {
        SortUtil.replaceTool(event.getPlayer(), new ItemStack(event.getItem()));
        //
    }

    @EventHandler(ignoreCancelled = true)
    public void onPressFWithoutInv(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().isSneaking()) {  //Shift+F
            Block target = event.getPlayer().getTargetBlock(IGNORES, 10);
            if (target.getState() instanceof Container) {  //看向容器了
                event.setCancelled(true);
                SortUtil.sortInv(((Container) target.getState()).getInventory());
                MsgUtil.sendActionBar(event.getPlayer(), "&a已整理你看向的 &f" + NameUtil.getTranslationName(target.getType()));
            } else if (event.getPlayer().getLocation().getPitch() == 90f) {
                event.setCancelled(true);
                SortUtil.sortInv(event.getPlayer().getInventory());
                MsgUtil.sendActionBar(event.getPlayer(), "&a已整理你的背包");
            }
        }
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
            JsonObject o = ConfigUtil.getConfig(SortItems.getInstance(), "players/" + event.getPlayer().getUniqueId());
            switch (slot) {
                case HAND:
                case OFF_HAND:
                    if (!o.get("ReplaceBrokenTool").getAsBoolean()) {  //如果玩家未开启替换手中道具
                        return;
                    }
                default:
                    if (!o.get("ReplaceBrokenArmor").getAsBoolean()) {  //如果玩家未开启替换盔甲
                        return;
                    }
            }
            EquipmentSlot finalSlot = slot;
            ItemStack brokenItem = new ItemStack(event.getBrokenItem());
            Bukkit.getScheduler().runTaskLater(SortItems.getInstance(), () -> {
                for (ItemStack content : event.getPlayer().getInventory().getContents()) {
                    if (content == null) continue;

                    if (content.getType() == brokenItem.getType()) {  //同一种类型的道具
                        ItemStack out = new ItemStack(content);
                        content.setAmount(0);
                        event.getPlayer().getInventory().setItem(finalSlot, out);
                        MsgUtil.sendActionBar(event.getPlayer(), "&a您的道具已损毁，从背包补全. &e该功能在 &f/SortItems &e中调整");
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

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(SortItems.getInstance(), () -> {  //异步操作文件
            ConfigUtil.saveConfig(SortItems.getInstance(), "players/" + event.getPlayer().getUniqueId(), GsonUtil.parseStr(HandleCmd.DEFAULT_SETTINGS), false);
            //修复没打开过GUI设定的玩家使用道具会抛错
            JsonObject before = ConfigUtil.getConfig(SortItems.getInstance(), "players/" + event.getPlayer().getUniqueId());
            JsonObject out = HandleCmd.DEFAULT_SETTINGS.deepCopy();
            for (String setting : HandleCmd.DEFAULT_SETTINGS.keySet()) {
                if (before.has(setting)) {
                    out.add(setting, before.get(setting));
                }
            }  //更新设定. 如果以后有新设定，玩家进服便可直接使用
            ConfigUtil.saveConfig(SortItems.getInstance(), "players/" + event.getPlayer().getUniqueId(), GsonUtil.parseStr(out), true);
        });
    }
}
