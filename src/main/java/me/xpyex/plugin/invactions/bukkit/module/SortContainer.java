package me.xpyex.plugin.invactions.bukkit.module;

import java.util.HashSet;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.enums.ItemType;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.invactions.bukkit.util.SortUtil;
import me.xpyex.plugin.xplib.bukkit.language.LangUtil;
import me.xpyex.plugin.xplib.bukkit.strings.MsgUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class SortContainer extends RootModule {
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
            } else if (ItemType.isAir(m)) {  //任意空气
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
    public void onPressFWithoutInv(PlayerSwapHandItemsEvent event) {
        if (!InvActionsServerConfig.getConfig().DefaultF) {
            return;
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(InvActions.getInstance(), () -> {
            event.getPlayer().updateInventory();  //修复物品暂时不可见(实际还存在)的Bug
        }, 2L);
        Block target = event.getPlayer().getTargetBlock(IGNORES, 10);
        if (target.getState() instanceof Container && event.getPlayer().isSneaking()) {  //看向容器了, Shift+F
            event.setCancelled(true);
            SortUtil.sortInv(((Container) target.getState()).getInventory());
            MsgUtil.sendActionBar(event.getPlayer(), getMessageWithSuffix("target", LangUtil.getItemName(InvActions.getInstance(), target.getType())));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            return;
        }
        if (SettingsUtil.getConfig(event.getPlayer()).DefaultF || (event.getPlayer().isSneaking() && event.getPlayer().getLocation().getPitch() == 90)) {
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
}
