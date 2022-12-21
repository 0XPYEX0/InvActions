package me.xpyex.plugin.invactions.bukkit.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.plugin.xplib.bukkit.inventory.InvSetter;
import me.xpyex.plugin.xplib.bukkit.inventory.Menu;
import me.xpyex.plugin.xplib.bukkit.inventory.button.UnmodifiableButton;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.GsonUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
                    JsonObject config = ConfigUtil.getConfig(InvActions.getInstance());
                    JsonArray invs = config.get("AllowInvs").getAsJsonArray();
                    invs.add(new JsonPrimitive(args[1]));
                    config.add("AllowInvs", invs);
                    ConfigUtil.saveConfig(InvActions.getInstance(), "config", GsonUtil.parseStr(config), true);
                    ConfigUtil.reload(InvActions.getInstance());
                    sender.sendMessage("已允许整理 " + args[1]);
                    return;
                }
            }
            if (sender instanceof Player) {
                ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + ((Player) sender).getUniqueId(), GsonUtil.parseStr(SettingsUtil.DEFAULT_SETTINGS), false);
                InvSetter setter = new InvSetter("F键整理-设定-" + sender.getName(), "#########", "#1234567#", "#A      #", "#########");
                setter.setSign("#", ItemUtil.getItemStack(Material.BLACK_STAINED_GLASS_PANE, " "));
                setter.setSign(" ", Material.AIR);

                Menu menu = new Menu((Player) sender);
                menu.setPage(1, setter)
                    .setSign("1", new UnmodifiableButton(menu, ((player, clickType) -> {
                            if (!SettingsUtil.getServerSetting("ReplaceBrokenTool")) return -1;

                            return SettingsUtil.getSetting(player, "ReplaceBrokenTool") ? 1 : 0;
                        }))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动补充道具", "&f当手中物品损坏/用尽时", "&f自动从背包补充", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动补充道具", "&f当手中物品损坏/用尽时", "&f自动从背包补充", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a自动补充道具", "&f当手中物品损坏/用尽时", "&f自动从背包补充", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.turnSetting(player, "ReplaceBrokenTool");
                                      }))
                        , 1)

                    .setSign("2", new UnmodifiableButton(menu, ((player, clickType) -> {
                            if (!SettingsUtil.getServerSetting("ReplaceBrokenArmor")) return -1;

                            return SettingsUtil.getSetting(player, "ReplaceBrokenArmor") ? 1 : 0;
                        }))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动穿戴盔甲", "&f当穿戴盔甲损坏时", "&f自动从背包补充", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动穿戴盔甲", "&f当穿戴盔甲损坏时", "&f自动从背包补充", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a自动穿戴盔甲", "&f当穿戴盔甲损坏时", "&f自动从背包补充", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.turnSetting(player, "ReplaceBrokenArmor");
                                      }))
                        , 1)

                    .setSign("3", new UnmodifiableButton(menu, ((player, clickType) -> {
                            if (!SettingsUtil.getServerSetting("AutoFarmer")) return -1;

                            return SettingsUtil.getSetting(player, "AutoFarmer") ? 1 : 0;
                        }))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动收割", "&f当右键农作物时", "&f若成熟则自动收割", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动收割", "&f当右键农作物时", "&f若成熟则自动收割", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a自动收割", "&f当右键农作物时", "&f若成熟则自动收割", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.turnSetting(player, "AutoFarmer");
                                      }))
                        , 1)
                    .setSign("4", new UnmodifiableButton(menu, ((player, clickType) -> {
                            if (!SettingsUtil.getServerSetting("DefaultF")) return -1;

                            return SettingsUtil.getSetting(player, "DefaultF") ? 1 : 0;
                        }))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a按下 &e&lF &a整理", "&f当没有打开任何界面时", "&f不论是否潜行且低头", "&f按下 &e&lF &f就整理自身背包", "&5&o该设定不影响 &e&lShift+F &5&o整理容器", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a按下 &e&lF &a整理", "&f当没有打开任何界面时", "&f不论是否潜行且低头", "&f按下 &e&lF &f就整理自身背包", "&5&o该设定不影响 &e&lShift+F &5&o整理容器", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a按下 &e&lF &a整理", "&f当没有打开任何界面时", "&f不论是否潜行且低头", "&f按下 &e&lF &f就整理自身背包", "&5&o该设定不影响 &e&lShift+F &5&o整理容器", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.turnSetting(player, "DefaultF");
                                      }))
                        , 1)
                    .setSign("5", new UnmodifiableButton(menu, ((player, clickType) -> {
                            if (!SettingsUtil.getServerSetting("QuickDrop")) return -1;

                            return SettingsUtil.getSetting(player, "QuickDrop") ? 1 : 0;
                        }))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a丢出同类道具", "&f当没有打开任何界面时", "&f按下 &e&lShift+Q", "&f扔出背包中所有同类道具", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a丢出同类道具", "&f当没有打开任何界面时", "&f按下 &e&lShift+Q", "&f扔出背包中所有同类道具", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a丢出同类道具", "&f当没有打开任何界面时", "&f按下 &e&lShift+Q", "&f扔出背包中所有同类道具", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.turnSetting(player, "QuickDrop");
                                      }))
                        , 1)
                    .setSign("6", new UnmodifiableButton(menu, ((player, clickType) -> {
                            if (!SettingsUtil.getServerSetting("QuickMove")) return -1;

                            return SettingsUtil.getSetting(player, "QuickMove") ? 1 : 0;
                    }))
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
                                          SettingsUtil.turnSetting(player, "QuickMove");
                                      }))
                        , 1)
                    .setSign("7", new UnmodifiableButton(menu, ((player, clickType) -> {
                            if (!SettingsUtil.getServerSetting("CraftDrop")) return -1;

                            return SettingsUtil.getSetting(player, "CraftDrop") ? 1 : 0;
                        }))
                                      .addMode(-1, ItemUtil.getItemStack(Material.RED_WOOL, "&a一键丢出合成结果", "&f当合成时，对着合成结果", "&f按下 &e&lCtrl+Q", "&f丢出所有合成结果", "", "&f当前状态: &4服务端禁用"))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a一键丢出合成结果", "&f当合成时，对着合成结果", "&f按下 &e&lCtrl+Q", "&f丢出所有合成结果", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a一键丢出合成结果", "&f当合成时，对着合成结果", "&f按下 &e&lCtrl+Q", "&f丢出所有合成结果", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          SettingsUtil.turnSetting(player, "CraftDrop");
                                      }))
                        , 1)
                    .setSign("A", new UnmodifiableButton(menu, ((player, clickType) -> {
                            return player.hasPermission("InvActions.admin") ? 1 : 0;
                        }))
                                      .addMode(1, ItemUtil.getItemStack(Material.ORANGE_WOOL, "&6重载所有配置文件", "&f包括服务端和玩家"))
                                      .addMode(0, new ItemStack(Material.AIR))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          if (itemStack.getAmount() == 0) {
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
