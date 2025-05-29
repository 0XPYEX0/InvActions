package me.xpyex.plugin.invactions.bukkit.message.module;

import lombok.Data;
import me.xpyex.plugin.invactions.bukkit.message.MenuInfo;

@Data
public class EggCatcherMessage {
    private String caught = "&a你成功用鸡蛋捕获了一个 &r{0} &a！";
    private String failed = "&c布嚎！捕捉 &r{0} &c失败了，它跑了！";
    private MenuInfo menu = MenuInfo.of("&a捕捉生物", "&f使用鸡蛋命中实体", "&f能够将其捕捉进刷怪蛋", "&f可以拾取");
}
