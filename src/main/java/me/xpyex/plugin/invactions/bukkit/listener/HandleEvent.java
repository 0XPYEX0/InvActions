package me.xpyex.plugin.invactions.bukkit.listener;

import com.google.gson.JsonObject;
import java.util.HashSet;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.enums.ItemType;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.invactions.bukkit.util.SortUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.GsonUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.NameUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

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
        Block target = event.getPlayer().getTargetBlock(IGNORES, 10);
        if (target.getState() instanceof Container && event.getPlayer().isSneaking()) {  //看向容器了, Shift+F
            event.setCancelled(true);
            SortUtil.sortInv(((Container) target.getState()).getInventory());
            MsgUtil.sendActionBar(event.getPlayer(), "&a已整理你看向的 &f" + NameUtil.getTranslationName(target.getType()));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            return;
        }
        if (!SettingsUtil.getSetting(event.getPlayer(), "DefaultF") && !(event.getPlayer().isSneaking() && event.getPlayer().getLocation().getPitch() == 90)) {
            return;
        }
        event.setCancelled(true);
        SortUtil.sortInv(event.getPlayer().getInventory());
        MsgUtil.sendActionBar(event.getPlayer(), "&a已整理你的背包");
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(InvActions.getInstance(), () -> {  //异步操作文件
            ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + event.getPlayer().getUniqueId(), GsonUtil.parseStr(SettingsUtil.DEFAULT_SETTINGS), false);
            //修复没打开过GUI设定的玩家使用道具会抛错
            JsonObject before = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + event.getPlayer().getUniqueId());
            JsonObject out = GsonUtil.copy(SettingsUtil.DEFAULT_SETTINGS);
            for (String setting : GsonUtil.getKeysOfJsonObject(SettingsUtil.DEFAULT_SETTINGS)) {
                if (before.has(setting)) {
                    out.add(setting, before.get(setting));
                }
            }  //更新设定. 如果以后有新设定，玩家进服便可直接使用
            ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + event.getPlayer().getUniqueId(), GsonUtil.parseStr(out), true);
        });
    }
}
