package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class QuickMoveMessage {
    private String drop = "&a已丢出所有相同道具.";
    private String move_to_container = "&a已将所有相同道具移至容器内.";
    private String move_to_player = "&a已将所有相同道具移至你背包.";
    private MenuInfo menu = MenuInfo.of("&a快速移动道具",
        "&f当打开容器或背包时",
        "&f1. 拿起任意道具",
        "&f2.1到容器内按下鼠标中键",
        "&f  将背包内所有相同道具移入容器",
        "",
        "&f2.2.到背包内按下鼠标中键",
        "&f   将容器内所有相同道具移入背包",
        "",
        "&f2.3.到界面外按下鼠标中键",
        "&f   扔出所有同种道具");
}
