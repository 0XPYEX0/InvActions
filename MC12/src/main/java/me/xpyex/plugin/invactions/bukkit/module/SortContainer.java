package me.xpyex.plugin.invactions.bukkit.module;

import java.util.HashSet;
import me.xpyex.lib.xplib.bukkit.language.LangUtil;
import me.xpyex.lib.xplib.bukkit.strings.MsgUtil;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.enums.ItemType;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.invactions.bukkit.util.SortUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class SortContainer extends RootModule {
    private static final HashSet<Material> IGNORES = new HashSet<>();

    static {
        for (Material m : Material.values()) {
            switch (m) {
                case REDSTONE_WIRE:  //红石线
                case SNOW:  //雪层
                case LAVA:  //岩浆
                case WATER:  //水
                    IGNORES.add(m);
                    break;
            }
            String material = m.toString();
            if (material.endsWith("_BUTTON")) {  //按钮
                IGNORES.add(m);
            } else if (material.endsWith("_PRESSURE_PLATE")) {  //压力板
                IGNORES.add(m);
            } else if (ItemType.isAir(m)) {  //任意空气
                IGNORES.add(m);
            } else if (material.endsWith("_SIGN")) {  //牌子
                IGNORES.add(m);
            } else if (material.contains("VINE")) {  //藤蔓系列
                IGNORES.add(m);
            } else if (material.endsWith("_TRAPDOOR")) {  //活板门
                IGNORES.add(m);
            } else if (material.endsWith("_FENCE")) {  //栅栏
                IGNORES.add(m);
            } else if (material.endsWith("FIRE")) {  //火、灵魂火
                IGNORES.add(m);
            } else if (material.contains("TORCH")) {  //各种火把，为了兼容没有灵魂火把的版本
                IGNORES.add(m);
            } else if (material.equals("REDSTONE_WALL_TORCH")) {
                IGNORES.add(m);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPressFWithoutInv(PlayerSwapHandItemsEvent event) {
        if (!InvActionsServerConfig.getCurrent().isDefaultF()) return;
        if (!isEndedCooldown(event.getPlayer(), 1000)) return;  //每个玩家一秒内只能整理一次容器

        Bukkit.getScheduler().runTaskLaterAsynchronously(InvActions.getInstance(), () -> {
            event.getPlayer().updateInventory();  //修复物品暂时不可见(实际还存在)的Bug
        }, 2L);

        Block target = event.getPlayer().getTargetBlock(IGNORES, 10);
        if (target.getState() instanceof Container && event.getPlayer().isSneaking()) {  //看向容器了, Shift+F
            event.setCancelled(true);
            SortUtil.sortInv(((Container) target.getState()).getInventory());
            MsgUtil.sendActionBar(event.getPlayer(), getMessageWithSuffix("target", LangUtil.getItemName(target.getType())));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            return;
        }
        if (SettingsUtil.getConfig(event.getPlayer()).isDefaultF() || (event.getPlayer().isSneaking() && event.getPlayer().getLocation().getPitch() == 90)) {
            event.setCancelled(true);
            SortUtil.sortInv(event.getPlayer().getInventory());
            MsgUtil.sendActionBar(event.getPlayer(), getMessageWithSuffix("player"));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        }
    }

    @Override
    public String getName() {
        return "DefaultF";
    }

    @Override
    public boolean playerEnabled(Player player) {
        if (!canLoad) return false;
        if (InvActionsServerConfig.getCurrent().isPermCheck() && !player.hasPermission("InvActions.use.module." + getName()))
            return false;
        return true;
    }
}
