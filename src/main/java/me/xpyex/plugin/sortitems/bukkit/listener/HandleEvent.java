package me.xpyex.plugin.sortitems.bukkit.listener;

import java.util.HashSet;
import me.xpyex.plugin.sortitems.bukkit.util.SortUtil;
import me.xpyex.plugin.xplib.bukkit.util.MsgUtil;
import me.xpyex.plugin.xplib.bukkit.util.NameUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;

public class HandleEvent implements Listener {
    private final static HashSet<Material> IGNORES = new HashSet<>();
    static {
        for (Material m : Material.values()) {
            switch (m) {
                case TORCH:  //火把
                case WALL_TORCH:
                case SOUL_TORCH:  //灵魂火把
                case SOUL_WALL_TORCH:
                case REDSTONE_WIRE:  //红石线
                case REDSTONE_TORCH:  //红石火把
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
            } else if (m.toString().endsWith("_VINES")) {  //藤蔓系列
                IGNORES.add(m);
            } else if (m.toString().endsWith("_TRAPDOOR")) {  //活板门
                IGNORES.add(m);
            } else if (m.toString().endsWith("_FENCE")) {  //栅栏
                IGNORES.add(m);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPressFWithoutInv(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().isSneaking()) {  //Shift+F
            Block target = event.getPlayer().getTargetBlock(IGNORES, 10);
            if (target.getState() instanceof Container) {  //看向容器了
                event.setCancelled(true);
                SortUtil.sortInv(((Container) target.getState()).getInventory());
                event.getPlayer().sendMessage(MsgUtil.getColorMsg("&a已整理你看向的 &f" + NameUtil.getTranslationName(target.getType())));
            } else if (event.getPlayer().getLocation().getPitch() == 90f) {
                event.setCancelled(true);
                SortUtil.sortInv(event.getPlayer().getInventory());
                event.getPlayer().sendMessage(MsgUtil.getColorMsg("&a已整理你的背包"));
            }
        }
    }

    @EventHandler
    public void onPressFWithInv(InventoryClickEvent event) {
        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {  //仅允许整理自己的背包
            if (event.getClick() == ClickType.SWAP_OFFHAND) {
                event.setCancelled(true);
                SortUtil.sortInv(event.getWhoClicked().getInventory());
                event.getWhoClicked().sendMessage(MsgUtil.getColorMsg("&a已整理你的背包"));
            }
        }
    }
}
