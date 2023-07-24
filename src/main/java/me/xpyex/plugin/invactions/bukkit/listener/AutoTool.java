package me.xpyex.plugin.invactions.bukkit.listener;

import java.util.HashMap;
import me.xpyex.plugin.invactions.bukkit.enums.ToolType;
import me.xpyex.plugin.invactions.bukkit.util.InvUtil;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.api.Pair;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.StrUtil;
import me.xpyex.plugin.xplib.bukkit.util.value.ValueUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AutoTool implements Listener {
    private static final HashMap<ToolType, ItemStack> TOOLS = new HashMap<>();

    static {
        TOOLS.put(ToolType.AXE, new ItemStack(Material.DIAMOND_AXE));
        TOOLS.put(ToolType.PICKAXE, new ItemStack(Material.DIAMOND_PICKAXE));
        TOOLS.put(ToolType.SHOVEL, new ItemStack(Material.DIAMOND_SHOVEL));
        TOOLS.put(ToolType.HOE, new ItemStack(Material.DIAMOND_HOE));  //割稻草块
        TOOLS.put(ToolType.SWORD, new ItemStack(Material.DIAMOND_SWORD));  //蜘蛛丝、树叶
        TOOLS.put(ToolType.SHEARS, new ItemStack(Material.SHEARS));  //剪刀: 羊毛 蜘蛛丝 树叶
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!SettingsUtil.getSetting(event.getPlayer(), "AutoTool")) return;

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR)
            return;

        if (StrUtil.endsWithIgnoreCaseOr(event.getPlayer().getInventory().getItemInMainHand().getType().toString(), "_SWORDS", "BOW", "TRIDENT")) {
            return;  //弓、剑、弩、三叉戟  不处理
        }
        if (event.getClickedBlock() != null) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (StrUtil.containsIgnoreCaseOr(ValueUtil.getOrDefault(event.getItem(), InvUtil.AIR_STACK).getType().toString(), "BOW")) {
                    return;
                }
                if (StrUtil.containsIgnoreCaseOr(event.getClickedBlock().getType().toString(), "GRASS_BLOCK", "DIRT")) {
                    if (StrUtil.containsIgnoreCaseOr(ValueUtil.getOrDefault(event.getItem(), InvUtil.AIR_STACK).getType().toString(), "_SHOVEL", "_HOE"))
                        return;  //玩家已经拿着对应道具了，就不要换
                    int fastestSlot = InvUtil.getFastestToolSlot(event.getPlayer(), event.getClickedBlock(), ToolType.HOE);
                    if (fastestSlot == event.getPlayer().getInventory().getHeldItemSlot()) {
                        return;
                    }
                    InvUtil.swapSlot(event.getPlayer(), EquipmentSlot.HAND, fastestSlot);
                    MsgUtil.sendActionBar(event.getPlayer(), "&a已自动切换为合适的工具. " + SettingsUtil.SETTING_HELP);
                }
            }
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                ItemStack before = new ItemStack(event.getPlayer().getInventory().getItemInMainHand());  //保存主手
                Pair<Float, ToolType> fastest = Pair.of(event.getClickedBlock().getBreakSpeed(event.getPlayer()), ToolType.UNKNOWN);
                for (ToolType toolType : TOOLS.keySet()) {
                    ItemStack item = TOOLS.get(toolType);
                    event.getPlayer().getInventory().setItemInMainHand(item);  //手中设为模板
                    float breakSpeed = event.getClickedBlock().getBreakSpeed(event.getPlayer());
                    if (breakSpeed > fastest.getKey()) {
                        fastest = Pair.of(breakSpeed, toolType);  //计算并排序
                    }
                }
                event.getPlayer().getInventory().setItemInMainHand(before);  //恢复手中道具
                if (fastest.getValue() == ToolType.UNKNOWN) {  //时间都一样，或者根本没算成，无需更换道具
                    return;
                }
                int fastestToolSlot = InvUtil.getFastestToolSlot(event.getPlayer(), event.getClickedBlock(), fastest.getValue());
                if (fastestToolSlot == event.getPlayer().getInventory().getHeldItemSlot()) {
                    return;
                }
                InvUtil.swapSlot(event.getPlayer(), EquipmentSlot.HAND, fastestToolSlot);  //更换这个类别中最快速度的工具
                MsgUtil.sendActionBar(event.getPlayer(), "&a已自动切换为合适的工具. " + SettingsUtil.SETTING_HELP);
            }
        }
    }
}
