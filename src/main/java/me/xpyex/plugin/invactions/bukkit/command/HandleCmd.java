package me.xpyex.plugin.invactions.bukkit.command;

import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.inventory.InvBuilder;
import me.xpyex.plugin.xplib.bukkit.inventory.Menu;
import me.xpyex.plugin.xplib.bukkit.inventory.button.UnmodifiableButton;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HandleCmd implements CommandExecutor {

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
                    sender.sendMessage("重载完成");
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
                    .setSign("a", new UnmodifiableButton(menu, (player -> InvActionsServerConfig.getConfig().AutoFarmer ? SettingsUtil.getConfig(player).AutoFarmer ? 1 : 0 : -1))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动收割", "&f当右键农作物时", "&f若成熟则自动收割", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动收割", "&f当右键农作物时", "&f若成熟则自动收割", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a自动收割", "&f当右键农作物时", "&f若成熟则自动收割", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.getConfig(player).AutoFarmer = !SettingsUtil.getConfig(player).AutoFarmer;
                                          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                                      }))
                        , 1)
                    .setSign("b", new UnmodifiableButton(menu, (player -> InvActionsServerConfig.getConfig().AutoTool ? SettingsUtil.getConfig(player).AutoTool ? 1 : 0 : -1))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动更换工具", "&f当尝试破坏方块", "&f或尝试耕地时", "&f自动从背包内换出适合的工具", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动更换工具", "&f当尝试破坏方块", "&f或尝试耕地时", "&f自动从背包内换出适合的工具", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a自动更换工具", "&f当尝试破坏方块", "&f或尝试耕地时", "&f自动从背包内换出适合的工具", "", "&f当前状态: &a启用"))
                                      .setClickEffect((player, clickType, itemStack) -> {
                                          SettingsUtil.getConfig(player).AutoTool = !SettingsUtil.getConfig(player).AutoTool;
                                          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                                      })
                        , 1)
                    .setSign("c", new UnmodifiableButton(menu, player -> InvActionsServerConfig.getConfig().BetterInfinity ? SettingsUtil.getConfig(player).BetterInfinity ? 1 : 0 : -1)
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a优化弓的&5&l无限&a附魔", "&f当使用附魔了“无限”的弓", "&f无需携带箭矢", "&f也可以射箭", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a优化弓的&5&l无限&a附魔", "&f当使用附魔了“无限”的弓", "&f无需携带箭矢", "&f也可以射箭", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a优化弓的&5&l无限&a附魔", "&f当使用附魔了“无限”的弓", "&f无需携带箭矢", "&f也可以射箭", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.getConfig(player).BetterInfinity = !SettingsUtil.getConfig(player).BetterInfinity;
                                          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                                      }))
                        , 1)
                    .setSign("d", new UnmodifiableButton(menu, player -> InvActionsServerConfig.getConfig().BetterLoyalty ? SettingsUtil.getConfig(player).BetterLoyalty ? 1 : 0 : -1)
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a更好的&5&l忠诚&a附魔", "&f当扔出附魔了“忠诚”的三叉戟", "&f超出视距自动收回", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动补充道具", "&f当扔出附魔了“忠诚”的三叉戟", "&f超出视距自动收回", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a自动补充道具", "&f当扔出附魔了“忠诚”的三叉戟", "&f超出视距自动收回", "", "&f当前状态: &a启用"))
                                      .setClickEffect((player, clickType, itemStack) -> {
                                          SettingsUtil.getConfig(player).BetterLoyalty = !SettingsUtil.getConfig(player).BetterLoyalty;
                                          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                                      })
                        , 1)
                    .setSign("e", new UnmodifiableButton(menu, (player -> InvActionsServerConfig.getConfig().CraftDrop ? SettingsUtil.getConfig(player).CraftDrop ? 1 : 0 : -1))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a一键丢出合成结果", "&f当合成时，对着合成结果", "&f按下 &e&lCtrl+Q", "&f丢出所有合成结果", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a一键丢出合成结果", "&f当合成时，对着合成结果", "&f按下 &e&lCtrl+Q", "&f丢出所有合成结果", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a一键丢出合成结果", "&f当合成时，对着合成结果", "&f按下 &e&lCtrl+Q", "&f丢出所有合成结果", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.getConfig(player).CraftDrop = !SettingsUtil.getConfig(player).CraftDrop;
                                          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                                      }))
                        , 1)
                    .setSign("f", new UnmodifiableButton(menu, (player -> InvActionsServerConfig.getConfig().DynamicLight ? SettingsUtil.getConfig(player).DynamicLight ? 1 : 0 : -1))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a动态光源", "&f当手持光源道具时", "&f在玩家位置模拟光源", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a动态光源", "&f当手持光源道具时", "&f在玩家位置模拟光源", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a动态光源", "&f当手持光源道具时", "&f在玩家位置模拟光源", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.getConfig(player).DynamicLight = !SettingsUtil.getConfig(player).DynamicLight;
                                          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                                      }))
                        , 1)
                    .setSign("g", new UnmodifiableButton(menu, (player -> InvActionsServerConfig.getConfig().DefaultF ? SettingsUtil.getConfig(player).DefaultF ? 1 : 0 : -1))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a按下 &e&lF &a整理", "&f当没有打开任何界面时", "&f不论是否潜行且低头", "&f按下 &e&lF &f就整理自身背包", "&5&o该设定不影响 &e&lShift+F &5&o整理容器", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a按下 &e&lF &a整理", "&f当没有打开任何界面时", "&f不论是否潜行且低头", "&f按下 &e&lF &f就整理自身背包", "&5&o该设定不影响 &e&lShift+F &5&o整理容器", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a按下 &e&lF &a整理", "&f当没有打开任何界面时", "&f不论是否潜行且低头", "&f按下 &e&lF &f就整理自身背包", "&5&o该设定不影响 &e&lShift+F &5&o整理容器", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.getConfig(player).DefaultF = !SettingsUtil.getConfig(player).DefaultF;
                                          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                                      }))
                        , 1)
                    .setSign("h", new UnmodifiableButton(menu, (player -> InvActionsServerConfig.getConfig().QuickDrop ? SettingsUtil.getConfig(player).QuickDrop ? 1 : 0 : -1))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a丢出同类道具", "&f当没有打开任何界面时", "&f按下 &e&lShift+Q", "&f扔出背包中所有同类道具", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a丢出同类道具", "&f当没有打开任何界面时", "&f按下 &e&lShift+Q", "&f扔出背包中所有同类道具", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a丢出同类道具", "&f当没有打开任何界面时", "&f按下 &e&lShift+Q", "&f扔出背包中所有同类道具", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.getConfig(player).QuickDrop = !SettingsUtil.getConfig(player).QuickDrop;
                                          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                                      }))
                        , 1)
                    .setSign("i", new UnmodifiableButton(menu, (player -> InvActionsServerConfig.getConfig().QuickMove ? SettingsUtil.getConfig(player).QuickMove ? 1 : 0 : -1))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a快速移动道具",
                                          "&f当打开容器或背包时",
                                          "&f1. 拿起任意道具",
                                          "&f2.1到容器内按下鼠标中键",
                                          "&f  将背包内所有相同道具移入容器",
                                          "",
                                          "&f2.2.到背包内按下鼠标中键",
                                          "&f   将容器内所有相同道具移入背包",
                                          "",
                                          "&f2.3.到界面外按下鼠标中键",
                                          "&f   扔出所有同种道具",
                                          "",
                                          "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a快速移动道具",
                                          "&f当打开容器或背包时",
                                          "&f1. 拿起任意道具",
                                          "&f2.1.到容器内按下鼠标中键",
                                          "&f   将背包内所有相同道具移入容器",
                                          "",
                                          "&f2.2.到背包内按下鼠标中键",
                                          "&f   将容器内所有相同道具移入背包",
                                          "",
                                          "&f2.3.到界面外按下鼠标中键",
                                          "&f   扔出所有同种道具",
                                          "",
                                          "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a快速移动道具",
                                          "&f当打开容器或背包时",
                                          "&f1. 拿起任意道具",
                                          "&f2.1.到容器内按下鼠标中键",
                                          "&f   将背包内所有相同道具移入容器",
                                          "",
                                          "&f2.2.到背包内按下鼠标中键",
                                          "&f   将容器内所有相同道具移入背包",
                                          "",
                                          "&f2.3.到界面外按下鼠标中键",
                                          "&f   扔出所有同种道具",
                                          "",
                                          "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.getConfig(player).QuickMove = !SettingsUtil.getConfig(player).QuickMove;
                                          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                                      }))
                        , 1)
                    .setSign("j", new UnmodifiableButton(menu, player -> InvActionsServerConfig.getConfig().QuickShulkerBox ? SettingsUtil.getConfig(player).QuickShulkerBox ? 1 : 0 : -1)
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a快捷编辑潜影盒", "&f在背包中 &e&lShift+右键 &f潜影盒", "&f或", "&f手持潜影盒 &e&lShift+右键", "&f直接打开该潜影盒", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a快捷编辑潜影盒", "&f在背包中 &e&lShift+右键 &f潜影盒", "&f或", "&f手持潜影盒 &e&lShift+右键", "&f直接打开该潜影盒", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a快捷编辑潜影盒", "&f在背包中 &e&lShift+右键 &f潜影盒", "&f或", "&f手持潜影盒 &e&lShift+右键", "&f直接打开该潜影盒", "", "&f当前状态: &a启用"))
                                      .setClickEffect((player, clickType, itemStack) -> {
                                          SettingsUtil.getConfig(player).QuickShulkerBox = !SettingsUtil.getConfig(player).QuickShulkerBox;
                                          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                                      })
                        , 1)
                    .setSign("k", new UnmodifiableButton(menu, (player -> InvActionsServerConfig.getConfig().ReplaceBrokenArmor ? SettingsUtil.getConfig(player).ReplaceBrokenArmor ? 1 : 0 : -1))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动穿戴盔甲", "&f当穿戴盔甲损坏时", "&f自动从背包补充", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动穿戴盔甲", "&f当穿戴盔甲损坏时", "&f自动从背包补充", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a自动穿戴盔甲", "&f当穿戴盔甲损坏时", "&f自动从背包补充", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.getConfig(player).ReplaceBrokenArmor = !SettingsUtil.getConfig(player).ReplaceBrokenArmor;
                                          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                                      }))
                        , 1)
                    .setSign("l", new UnmodifiableButton(menu, (player -> InvActionsServerConfig.getConfig().ReplaceBrokenTool ? SettingsUtil.getConfig(player).ReplaceBrokenTool ? 1 : 0 : -1))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动补充道具", "&f当手中物品损坏/用尽时", "&f自动从背包补充", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动补充道具", "&f当手中物品损坏/用尽时", "&f自动从背包补充", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a自动补充道具", "&f当手中物品损坏/用尽时", "&f自动从背包补充", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.getConfig(player).ReplaceBrokenTool = !SettingsUtil.getConfig(player).ReplaceBrokenTool;
                                          player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                                      }))
                        , 1)
                    .setSign("A", new UnmodifiableButton(menu, (player -> player.hasPermission("InvActions.admin") ? 1 : 0))
                                      .addMode(1, ItemUtil.getItemStack(Material.ORANGE_WOOL, "&6重载所有配置文件", "&f包括服务端和玩家"))
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
                Bukkit.getScheduler().runTask(InvActions.getInstance(), () -> {
                    menu.open(1);
                });
            }
        });
        return true;
    }
}
