package me.xpyex.plugin.invactions.bukkit.listener;

import java.util.HashMap;
import java.util.TreeMap;
import me.xpyex.plugin.invactions.bukkit.enums.ItemType;
import me.xpyex.plugin.invactions.bukkit.util.InvUtil;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.StrUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AutoTool implements Listener {
    private static final HashMap<ItemType.ToolType, ItemStack> TOOLS = new HashMap<>();
    static {
        TOOLS.put(ItemType.ToolType.AXE, new ItemStack(Material.DIAMOND_AXE));
        TOOLS.put(ItemType.ToolType.PICKAXE, new ItemStack(Material.DIAMOND_PICKAXE));
        TOOLS.put(ItemType.ToolType.SHOVEL, new ItemStack(Material.DIAMOND_SHOVEL));
        TOOLS.put(ItemType.ToolType.HOE, new ItemStack(Material.DIAMOND_HOE));  //割稻草块
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!SettingsUtil.getServerSetting("AutoTool")) return;

        if (!SettingsUtil.getSetting(event.getPlayer(), "AutoTool")) return;

        if (event.getClickedBlock() != null) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (ItemUtil.typeIsOr(event.getClickedBlock(), Material.GRASS_BLOCK, Material.DIRT, Material.COARSE_DIRT)) {
                    if (StrUtil.containsIgnoreCaseOr(event.getPlayer().getInventory().getItemInMainHand().getType().toString(), "_SHOVEL", "_HOE"))
                        return;  //玩家已经拿着对应道具了，就不要换
                    InvUtil.swapSlotToMainHand(event.getPlayer(), InvUtil.getFastestToolSlot(event.getPlayer(), event.getClickedBlock(), ItemType.ToolType.HOE));
                }
            }
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                ItemStack before = new ItemStack(event.getPlayer().getInventory().getItemInMainHand());  //保存主手
                TreeMap<Float, ItemType.ToolType> timeCost = new TreeMap<>();
                for (ItemType.ToolType toolType : TOOLS.keySet()) {
                    ItemStack item = TOOLS.get(toolType);
                    event.getPlayer().getInventory().setItemInMainHand(item);  //手中设为模板
                    timeCost.put(event.getClickedBlock().getBreakSpeed(event.getPlayer()), toolType);  //计算并排序
                }
                event.getPlayer().getInventory().setItemInMainHand(before);  //恢复手中道具
                if (timeCost.size() <= 1) {  //时间都一样，或者根本没算成，无需更换道具
                    return;
                }
                InvUtil.swapSlotToMainHand(event.getPlayer(), InvUtil.getFastestToolSlot(event.getPlayer(), event.getClickedBlock(), timeCost.get(timeCost.lastKey())));  //更换这个类别中最快速度的工具
            }
        }
    }
}
