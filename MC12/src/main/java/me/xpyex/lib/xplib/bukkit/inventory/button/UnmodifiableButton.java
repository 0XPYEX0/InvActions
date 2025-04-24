package me.xpyex.lib.xplib.bukkit.inventory.button;

import me.xpyex.lib.xplib.bukkit.inventory.Menu;

/**
 * 不允许被修改的按钮格 (仅展示功能，但可点击切换模式[Mode] )
 */
public class UnmodifiableButton extends Button {

    public UnmodifiableButton(Menu menu, ButtonCondition condition) {
        super(menu, condition);
        //
    }
}
