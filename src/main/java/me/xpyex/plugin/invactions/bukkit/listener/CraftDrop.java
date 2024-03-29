package me.xpyex.plugin.invactions.bukkit.listener;

import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.util.InvUtil;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.util.strings.MsgUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftDrop implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        if (!InvActionsServerConfig.getConfig().CraftDrop || !SettingsUtil.getConfig((Player) event.getWhoClicked()).CraftDrop)
            return;

        if (event.getClick() != ClickType.CONTROL_DROP) {
            return;
        }

        ItemStack least = null;
        for (ItemStack matrix : event.getInventory().getMatrix()) {
            if (matrix == null || matrix.getAmount() == 0) continue;

            if (least == null) {
                least = new ItemStack(matrix);
                continue;
            }

            if (matrix.getAmount() < least.getAmount()) {
                least = new ItemStack(matrix);
            }
        }

        if (least == null || least.getAmount() < 2) {
            return;
        }

        event.setCancelled(true);
        ItemStack tool = new ItemStack(event.getWhoClicked().getInventory().getItemInMainHand());

        ItemStack toDrop = new ItemStack(event.getRecipe().getResult());
        toDrop.setAmount(toDrop.getAmount() * least.getAmount());
        event.getWhoClicked().getInventory().setItemInMainHand(toDrop);
        event.getWhoClicked().dropItem(true);

        for (ItemStack matrix : event.getInventory().getMatrix()) {
            if (matrix == null || matrix.getAmount() == 0) continue;

            matrix.setAmount(matrix.getAmount() - least.getAmount());
        }
        event.getInventory().setResult(InvUtil.AIR_STACK);
        event.getWhoClicked().getInventory().setItemInMainHand(tool);
        MsgUtil.sendActionBar((Player) event.getWhoClicked(), "&a已自动合成所有物品，并自动丢出. " + InvActionsServerConfig.SETTING_HELP);
    }
}
