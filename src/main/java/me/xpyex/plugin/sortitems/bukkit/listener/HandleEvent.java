package me.xpyex.plugin.sortitems.bukkit.listener;

import com.google.gson.JsonObject;
import java.util.ArrayList;
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
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
    private static final HashSet<Material> IGNORES = new HashSet<>();
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
        Block target = event.getPlayer().getTargetBlock(IGNORES, 10);
        if (target.getState() instanceof Container && event.getPlayer().isSneaking()) {  //看向容器了, Shift+F
            event.setCancelled(true);
            SortUtil.sortInv(((Container) target.getState()).getInventory());
            MsgUtil.sendActionBar(event.getPlayer(), "&a已整理你看向的 &f" + NameUtil.getTranslationName(target.getType()));
            return;
        }
        JsonObject o = ConfigUtil.getConfig(SortItems.getInstance(), "players/" + event.getPlayer().getUniqueId());
        if (!o.get("DefaultF").getAsBoolean() && !event.getPlayer().isSneaking()) {
            return;
        }
        event.setCancelled(true);
        SortUtil.sortInv(event.getPlayer().getInventory());
        MsgUtil.sendActionBar(event.getPlayer(), "&a已整理你的背包");
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
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
    public void onRightClick(PlayerInteractEvent event) {;
        JsonObject o = ConfigUtil.getConfig(SortItems.getInstance(), "players/" + event.getPlayer().getUniqueId());
        if (!o.get("AutoFarmer").getAsBoolean()) {  //如果玩家没开启自动收割
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
