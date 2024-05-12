package me.xpyex.plugin.invactions.bukkit.command;

import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.module.RootModule;
import me.xpyex.plugin.xplib.bukkit.inventory.InvBuilder;
import me.xpyex.plugin.xplib.bukkit.inventory.Menu;
import me.xpyex.plugin.xplib.bukkit.inventory.button.UnmodifiableButton;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import me.xpyex.plugin.xplib.bukkit.util.language.LangUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HandleCmd implements CommandExecutor {
    private static final String[] menuID = "abcdefghijklmnopqrstuvwxyz".split("");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(InvActions.getInstance(), () -> {
            if (args.length != 0) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("InvActions.admin")) {
                        sender.sendMessage("你没有权限");
                        return;
                    }
                    ConfigUtil.reload(InvActions.getInstance());
                    InvActions.getInstance().updateServerConfig();
                    sender.sendMessage(LangUtil.getMessage(InvActions.getInstance(), "Reload.reload"));
                    return;
                }
                if (args[0].equalsIgnoreCase("addInvs")) {
                    if (!sender.hasPermission("InvActions.admin")) {
                        sender.sendMessage("你没有权限");
                        return;
                    }
                    if (args.length < 2) {
                        sender.sendMessage("参数不足");
                        return;
                    }
                    if (args.length > 2) {
                        sender.sendMessage("Invs内容不可包含空格，多余的参数已被忽略");
                    }
                    InvActionsServerConfig.getConfig().AllowInvs.add(args[1]);
                    ConfigUtil.saveConfig(InvActions.getInstance(), "config", InvActionsServerConfig.getConfig(), true);
                    ConfigUtil.reload(InvActions.getInstance());
                    sender.sendMessage("已允许整理 " + args[1]);
                    return;
                }
            }
            if (sender instanceof Player) {
                InvBuilder setter = new InvBuilder("InvActions-设定-" + sender.getName(), "#########", "#abcdefg#", "#hijk   #", "########A");
                setter.setSign("#", ItemUtil.getItemStack(Material.BLACK_STAINED_GLASS_PANE, " "));
                setter.setSign(" ", Material.AIR);

                Menu menu = new Menu((Player) sender);
                menu.setPage(1, setter)
                    .setSign("A", new UnmodifiableButton(menu, (player -> player.hasPermission("InvActions.admin") ? 1 : 0))
                                      .addMode(1, ItemUtil.getItemStack(Material.ORANGE_WOOL,
                                          LangUtil.getMessage(InvActions.getInstance(), "Reload.menu.name"),
                                          LangUtil.getMessages(InvActions.getInstance(), "Reload.menu.lore").toArray(new String[0])))
                                      .addMode(0, ItemUtil.getItemStack(Material.BLACK_STAINED_GLASS_PANE, " "))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          if (!player.hasPermission("InvActions.admin")) {  //玩家没权限的时候
                                              return;
                                          }
                                          Bukkit.getScheduler().runTask(InvActions.getInstance(), () -> {
                                              Bukkit.dispatchCommand(player, "InvActions reload");
                                              player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          });
                                      }))
                        , 1);
                for (int i = 0; i < RootModule.modules.size(); i++) {
                    RootModule module = RootModule.modules.get(i);
                    menu.setSign(menuID[i], module.getMenuButton(menu), 1);
                }
                Bukkit.getScheduler().runTask(InvActions.getInstance(), () -> {
                    menu.open(1);
                });
            }
        });
        return true;
    }
}
