//Developed by _ItsAndrew_
package org.andrew.novaWarps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class CommandManager implements CommandExecutor {
    private final NovaWarps plugin;

    public CommandManager(NovaWarps plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String labels, @NotNull String[] args){
        FileConfiguration warps = plugin.getWarps().getConfig();
        String prefix = plugin.getConfig().getString("prefix");
        Player player = (Player) sender;

        if(command.getName().equalsIgnoreCase("warpconfig")){
            if(!sender.hasPermission("warps.admin")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cYou don't have permission to run this command."));
                return true;
            }

            switch(args[0]){
                case "create":
                    if(args.length < 2){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig create <name>"));
                        return true;
                    }
                    String warpToAdd = args[1];
                    String pathToSet = "warps."+ warpToAdd;

                    warps.set(pathToSet+".location.x", "");
                    warps.set(pathToSet+".location.y", "");
                    warps.set(pathToSet+".location.z", "");
                    warps.set(pathToSet+".gui-item", "");
                    warps.set(pathToSet+".gui-slot", "");
                    warps.set(pathToSet+".gui-title", "");
                    warps.set(pathToSet+".enchant-glint", "");
                    warps.set(pathToSet+".lore", "");
                    warps.set(pathToSet+".world", "");
                    if(plugin.getConfig().getBoolean("toggle-permissions")){
                        warps.set(pathToSet+".permission", "");
                    }
                    plugin.getWarps().saveConfig();

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aWarp &l"+ warpToAdd +" &asaved to &lwarps.yml&a!"));
                    Bukkit.getLogger().info("[NW] Warp "+ warpToAdd +" saved to warps.yml!");
                    break;

                case "delete":
                    if(args.length < 2){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig delete <name>"));
                        return true;
                    }
                    if(!warps.isConfigurationSection("warps")){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cThere aren't any warps configured!"));
                        return true;
                    }
                    String warpToDelete = args[1];
                    String pathToDelete = "warps."+warpToDelete;

                    if(Objects.requireNonNull(warps.getConfigurationSection("warps")).getKeys(false).contains(warpToDelete)){
                        warps.set(pathToDelete, null);
                        plugin.getWarps().saveConfig();

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aWarp &l"+warpToDelete+" &adeleted from &lwarps.yml &a!"));
                        Bukkit.getLogger().info("[NW] Warp "+warpToDelete+" deleted from warps.yml!");
                    }
                    else{
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cWarp &l"+warpToDelete+" &cdoesn't exist!"));
                        return true;
                    }
                    break;

                case "reload":
                    plugin.getWarps().reloadConfig();
                    plugin.reloadConfig();

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &a&lNovaWarps &areloaded successfully!"));
                    Bukkit.getLogger().info("[NW] NovaWarps reloaded successfully!");
                    break;

                case "help":
                    List<String> helpMessageLines = plugin.getConfig().getStringList("help-message");

                    for(String rawLine : helpMessageLines){
                        String coloredLine = ChatColor.translateAlternateColorCodes('&', rawLine);
                        sender.sendMessage(coloredLine);
                    }

                    break;

                case "manage":
                    if(args.length < 3){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> ..."));
                        return true;
                    }
                    if(!warps.isConfigurationSection("warps")){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cThere aren't any warps configured!"));
                        return true;
                    }
                    String mainPath = "warps."+args[1];

                    switch(args[2]){
                        case "setguislot":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setguislot <nr>"));
                                return true;
                            }

                            int guiSlot;
                            try{
                                guiSlot = Integer.parseInt(args[3]);
                                if(guiSlot < 1 || guiSlot > plugin.getGuiSize()){
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cThe slot must be between &l1 &cand &l"+plugin.getGuiSize()+"&c!"));
                                    return true;
                                }
                            } catch (NumberFormatException e){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cThe slot must be a number!"));
                                return true;
                            }
                            warps.set(mainPath+".gui-slot", guiSlot);
                            plugin.getWarps().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aSlot &l"+guiSlot+" &asaved for warp &l"+args[1]+"&a!"));
                            Bukkit.getLogger().info("[NW] Slot "+guiSlot+" saved for warp "+args[1]+"!");
                            break;

                        case "setguiitem":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setguiitem <item>"));
                                return true;
                            }

                            String item = args[3];
                            Material realItem = Material.matchMaterial(item.toUpperCase());

                            if(realItem == null){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cItem &l"+item+" &cdoes not exist!"));
                                return true;
                            }
                            warps.set(mainPath+".gui-item", item.toUpperCase());
                            plugin.getWarps().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aGUI item &l"+item+" &asaved for warp &l"+args[1]+"&a!"));
                            Bukkit.getLogger().info("[NW] GUI item "+item+" saved for warp "+args[1]);
                            break;

                        case "setlocation":
                            if(args.length < 6){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setlocation <x> <y> <z>"));
                                return true;
                            }

                            int x, y, z;
                            try{
                                x = Integer.parseInt(args[3]);
                                y = Integer.parseInt(args[4]);
                                z = Integer.parseInt(args[5]);
                            } catch (NumberFormatException e){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cThe coordonates must be numbers!"));
                                return true;
                            }
                            warps.set(mainPath+".location.x", x);
                            warps.set(mainPath+".location.y", y);
                            warps.set(mainPath+".location.z", z);
                            plugin.getWarps().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aLocation &l"+x+" "+y+" "+z+" &asaved for warp &l"+args[1]+"&a!"));
                            Bukkit.getLogger().info("[NW] Location "+x+" "+y+" "+z+" saved for warp "+args[1]);
                            break;

                        case "setworld":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setworld <world>"));
                                return true;
                            }
                            String world = args[3];
                            warps.set(mainPath+".world", world);
                            plugin.getWarps().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aWorld &l"+world+" &asaved for warp &l"+args[1]+"&a!"));
                            Bukkit.getLogger().info("[NW] World "+world+" saved for warp "+args[1]+"!");
                            break;

                        case "setperm":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setperm <perm>"));
                                return true;
                            }
                            String permission = args[3];
                            warps.set(mainPath+".permission", permission);
                            plugin.getWarps().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aPermission &l"+permission+" &asaved for warp &l"+args[1]+"&a!"));
                            Bukkit.getLogger().info("[NW] Permission "+permission+" saved for warp "+args[1]+"!");
                            break;

                        case "setguititle":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setguititle <title>"));
                                return true;
                            }
                            String title = args[3];
                            warps.set(mainPath+".gui-title", title);
                            plugin.getWarps().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aGUI title &r"+title+" &asaved for warp &l"+args[1]+"&a!"));
                            Bukkit.getLogger().info("[NW] GUI title "+title+" saved for warp "+args[1]);
                            break;

                        case "setenchantglint":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setenchantglint <true/false>"));
                                return true;
                            }

                            String value = args[3];
                            if(!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cThe value must be &ltrue &cor &lfalse&c!"));
                                return true;
                            }

                            warps.set(mainPath+".enchant-glint", value);
                            plugin.getWarps().saveConfig();
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aValue &l"+value+" &asaved for warp &l"+args[1]+"&a!"));
                            Bukkit.getLogger().info("[NW] Value "+value+" saved for warp "+args[1]);
                            break;

                        default:
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUnknown command. Use &l/warpconfig help"));
                            break;
                    }
                    break;

                default:
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUnknown command. use &l/warpconfig help"));
                    break;
            }
        }

        if(command.getName().equalsIgnoreCase("warps")){
            if(!sender.hasPermission("warps.use")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cYou don't have permission to run this command."));
                return true;
            }
            plugin.getGuiManager().showGUI(player);
        }

        return false;
    }
}
