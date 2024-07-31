package me.xpyex.plugin.invactions.bukkit.module;

import me.xpyex.plugin.invactions.bukkit.util.InvUtil;
import me.xpyex.plugin.xplib.bukkit.strings.MsgUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftDrop extends RootModule {
    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!serverEnabled()) return;

        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!playerEnabled(((Player) event.getWhoClicked()))) return;

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
        MsgUtil.sendActionBar((Player) event.getWhoClicked(), getMessageWithSuffix("drop"));
    }
}
