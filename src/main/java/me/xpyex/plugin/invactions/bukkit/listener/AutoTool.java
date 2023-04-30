package me.xpyex.plugin.invactions.bukkit.listener;

import java.util.HashMap;
import me.xpyex.plugin.invactions.bukkit.enums.ItemType;
import me.xpyex.plugin.invactions.bukkit.util.InvUtil;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.api.Pair;
import me.xpyex.plugin.xplib.bukkit.util.strings.StrUtil;
import org.bukkit.GameMode;
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
        TOOLS.put(ItemType.ToolType.SWORD, new ItemStack(Material.DIAMOND_SWORD));  //蜘蛛丝、树叶
        TOOLS.put(ItemType.ToolType.SHEARS, new ItemStack(Material.SHEARS));  //剪刀: 羊毛 蜘蛛丝 树叶
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!SettingsUtil.getServerSetting("AutoTool")) return;

        if (!SettingsUtil.getSetting(event.getPlayer(), "AutoTool")) return;

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR)
            return;

        if (event.getClickedBlock() != null) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (StrUtil.containsIgnoreCaseOr(event.getClickedBlock().getType().toString(), "GRASS_BLOCK", "DIRT")) {
                    if (StrUtil.containsIgnoreCaseOr(event.getPlayer().getInventory().getItemInMainHand().getType().toString(), "_SHOVEL", "_HOE"))
                        return;  //玩家已经拿着对应道具了，就不要换
                    InvUtil.swapSlotToMainHand(event.getPlayer(), InvUtil.getFastestToolSlot(event.getPlayer(), event.getClickedBlock(), ItemType.ToolType.HOE));
                } else if (event.getClickedBlock().toString().contains("_LOG")) {  //原木
                    if (StrUtil.containsIgnoreCaseOr(event.getPlayer().getInventory().getItemInMainHand().getType().toString(), "_AXE"))
                        return;  //玩家已经拿着对应道具了，就不要换
                    InvUtil.swapSlotToMainHand(event.getPlayer(), InvUtil.getFastestToolSlot(event.getPlayer(), event.getClickedBlock(), ItemType.ToolType.AXE));
                }
            }
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                ItemStack before = new ItemStack(event.getPlayer().getInventory().getItemInMainHand());  //保存主手
                Pair<Float, ItemType.ToolType> fastest = new Pair<>(event.getClickedBlock().getBreakSpeed(event.getPlayer()), ItemType.ToolType.UNKNOWN);
                for (ItemType.ToolType toolType : TOOLS.keySet()) {
                    ItemStack item = TOOLS.get(toolType);
                    event.getPlayer().getInventory().setItemInMainHand(item);  //手中设为模板
                    float breakSpeed = event.getClickedBlock().getBreakSpeed(event.getPlayer());
                    if (breakSpeed > fastest.getKey()) {
                        fastest = new Pair<>(breakSpeed, toolType);  //计算并排序
                    }
                }
                event.getPlayer().getInventory().setItemInMainHand(before);  //恢复手中道具
                if (fastest.getValue() == ItemType.ToolType.UNKNOWN) {  //时间都一样，或者根本没算成，无需更换道具
                    return;
                }
                InvUtil.swapSlotToMainHand(event.getPlayer(), InvUtil.getFastestToolSlot(event.getPlayer(), event.getClickedBlock(), fastest.getValue()));  //更换这个类别中最快速度的工具
            }
        }
    }
}
