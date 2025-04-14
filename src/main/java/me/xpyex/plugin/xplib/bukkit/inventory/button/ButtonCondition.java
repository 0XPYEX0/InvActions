package me.xpyex.plugin.xplib.bukkit.inventory.button;

import java.util.function.Function;
import org.bukkit.entity.Player;

/**
 * 判断当前按钮的模式(Mode)，在Menu内显示
 */
public interface ButtonCondition extends Function<Player, Integer> {
}
