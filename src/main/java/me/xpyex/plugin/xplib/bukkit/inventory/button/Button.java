package me.xpyex.plugin.xplib.bukkit.inventory.button;

import java.util.HashMap;
import me.xpyex.plugin.xplib.bukkit.inventory.Menu;
import me.xpyex.plugin.xplib.util.value.ValueUtil;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Button {
    private final HashMap<Integer, ItemStack> MODES = new HashMap<>();  //各种状态是什么按钮
    private final ButtonCondition condition;
    private final Menu menu;
    private ButtonClickEffect clickEffect;

    public Button(Menu menu, ButtonCondition condition) {
        ValueUtil.notNull("menu或condition不应为null", menu, condition);
        this.condition = condition;
        this.menu = menu;
        //
    }

    /**
     * 获取该Button所在的Menu
     *
     * @return Button所在的Menu
     */
    @NotNull
    public final Menu getMenu() {
        return menu;
        //
    }

    /**
     * 为该Button新增模式
     *
     * @param mode   模式数
     * @param button 该模式展示的ItemStack
     * @return 返回自身，创建链式代码
     */
    @NotNull
    public final Button addMode(int mode, ItemStack button) {
        MODES.put(mode, button);
        return this;
        //
    }

    /**
     * 获取该Button实例在当前状态对应的ItemStack
     *
     * @return 该Button实例在当前状态对应的ItemStack
     */
    @Deprecated
    public final ItemStack getButton() {
        return getStack();
        //
    }

    /**
     * 获取该Button实例在当前状态对应的ItemStack
     *
     * @return 该Button实例在当前状态对应的ItemStack
     */
    @NotNull
    public ItemStack getStack() {
        int i = condition.apply(menu.getPlayer());
        ValueUtil.mustTrue("按钮不存在该状态: " + i, MODES.containsKey(i));
        return MODES.get(i);
    }

    /**
     * 获取该Button在点击后会执行的方法体，或暂未被设定
     *
     * @return 方法体
     */
    @Nullable
    public ButtonClickEffect getClickEffect() {
        return clickEffect;
        //
    }

    /**
     * 设定该Button在点击后会执行什么
     *
     * @param effect 待执行的方法体
     * @return 返回自身，制造链式代码
     */
    @NotNull
    public Button setClickEffect(ButtonClickEffect effect) {
        this.clickEffect = effect;
        return this;
        //
    }
}
