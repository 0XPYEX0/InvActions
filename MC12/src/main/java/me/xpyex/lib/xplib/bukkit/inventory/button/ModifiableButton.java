package me.xpyex.lib.xplib.bukkit.inventory.button;

import me.xpyex.lib.xplib.bukkit.inventory.Menu;
import me.xpyex.lib.xplib.util.value.ValueUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 可被修改的按钮格
 */
public class ModifiableButton extends Button {
    private ButtonReturnItem returnItemEffect;
    private @NotNull ItemStack stack = new ItemStack(Material.AIR);

    public ModifiableButton(Menu menu, ButtonCondition condition) {
        super(menu, condition);
        //
    }

    @Nullable
    public ButtonReturnItem getReturnItem() {
        return returnItemEffect;
        //
    }

    public void setReturnItem(ButtonReturnItem f) {
        this.returnItemEffect = f;
        //
    }

    @Override
    @NotNull
    public ItemStack getStack() {
        return stack;
        //
    }

    public void setStack(ItemStack stack) {
        ValueUtil.notNull("stack不应为null", stack);
        this.stack = stack;
    }
}
