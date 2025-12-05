//Developed by _ItsAndrew_
package org.andrew.novaWarps;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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

        Sound good = Registry.SOUNDS.get(NamespacedKey.minecraft("entity.player.levelup"));
        Sound bad = Registry.SOUNDS.get(NamespacedKey.minecraft("entity.enderman.teleport"));

        if(command.getName().equalsIgnoreCase("warpconfig")){
            if(!sender.hasPermission("warps.admin")){
                player.playSound(player.getLocation(), bad, 1f, 1f);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cYou don't have permission to run this command."));
                return true;
            }

            switch(args[0]){
                case "create":
                    if(args.length < 2){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig create <name>"));
                        player.playSound(player.getLocation(), bad, 1f, 1f);
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
                    if(plugin.getConfig().getBoolean("toggle-permissions")){ //Creating the permission section if it is toggled
                        warps.set(pathToSet+".permission", "");
                    }
                    plugin.getWarps().saveConfig();

                    player.playSound(player.getLocation(), good, 1f, 1.4f);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aWarp &l"+ warpToAdd +" &asaved to &lwarps.yml&a!"));
                    break;

                case "delete":
                    if(args.length < 2){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig delete <name>"));
                        player.playSound(player.getLocation(), bad, 1f, 1f);
                        return true;
                    }

                    ConfigurationSection warpsSection = warps.getConfigurationSection("warps");
                    if(warpsSection == null || warpsSection.getKeys(false).isEmpty()){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cThere aren't any warps configured!"));
                        player.playSound(player.getLocation(), bad, 1f, 1f);
                        return true;
                    }
                    String warpToDelete = args[1];
                    String pathToDelete = "warps."+warpToDelete;

                    if(Objects.requireNonNull(warps.getConfigurationSection("warps")).getKeys(false).contains(warpToDelete)){
                        warps.set(pathToDelete, null);
                        plugin.getWarps().saveConfig();

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aWarp &l"+warpToDelete+" &adeleted from &lwarps.yml&a!"));
                        player.playSound(player.getLocation(), good, 1f, 1.4f);
                        Bukkit.getLogger().info("[NW] Warp "+warpToDelete+" deleted from warps.yml!");
                    }
                    else{
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cWarp &l"+warpToDelete+" &cdoesn't exist!"));
                        player.playSound(player.getLocation(), bad, 1f, 1f);
                        return true;
                    }
                    break;

                case "reload":
                    plugin.getWarps().reloadConfig();
                    plugin.reloadConfig();

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &a&lNovaWarps &areloaded successfully!"));
                    player.playSound(player.getLocation(), good, 1f, 1.4f);
                    Bukkit.getLogger().info("[NW] NovaWarps reloaded successfully!");
                    break;

                case "help":
                    List<String> helpMessageLines = plugin.getConfig().getStringList("help-message");

                    for(String rawLine : helpMessageLines){
                        String coloredLine = ChatColor.translateAlternateColorCodes('&', rawLine);
                        sender.sendMessage(coloredLine);
                    }
                    player.playSound(player.getLocation(), good, 1f, 1.4f);
                    break;

                case "manage":
                    if(args.length < 3){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> ..."));
                        player.playSound(player.getLocation(), bad, 1f, 1f);
                        return true;
                    }

                    warpsSection = warps.getConfigurationSection("warps");
                    if(warpsSection == null || warpsSection.getKeys(false).isEmpty()){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cThere aren't any warps configured!"));
                        player.playSound(player.getLocation(), bad, 1f, 1f);
                        return true;
                    }
                    String mainPath = "warps."+args[1];

                    switch(args[2]){
                        case "setguislot":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setguislot <nr>"));
                                player.playSound(player.getLocation(), bad, 1f, 1f);
                                return true;
                            }

                            int guiSlot;
                            try{
                                guiSlot = Integer.parseInt(args[3]);
                                if(guiSlot < 1 || guiSlot > plugin.getGuiSize()){
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cThe slot must be between &l1 &cand &l"+plugin.getGuiSize()+"&c!"));
                                    player.playSound(player.getLocation(), bad, 1f, 1f);
                                    return true;
                                }
                            } catch (NumberFormatException e){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cThe slot must be a number!"));
                                player.playSound(player.getLocation(), bad, 1f, 1f);
                                return true;
                            }
                            warps.set(mainPath+".gui-slot", guiSlot);
                            plugin.getWarps().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aSlot &l"+guiSlot+" &asaved for warp &l"+args[1]+"&a!"));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        case "setguiitem":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setguiitem <item>"));
                                player.playSound(player.getLocation(), bad, 1f, 1f);
                                return true;
                            }

                            String item = args[3];
                            Material realItem = Material.matchMaterial(item.toUpperCase());

                            if(realItem == null){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cItem &l"+item+" &cdoes not exist!"));
                                player.playSound(player.getLocation(), bad, 1f, 1f);
                                return true;
                            }
                            warps.set(mainPath+".gui-item", item.toUpperCase());
                            plugin.getWarps().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aGUI item &l"+item+" &asaved for warp &l"+args[1]+"&a!"));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        case "setlocation":
                            if(args.length < 6){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setlocation <x> <y> <z>"));
                                player.playSound(player.getLocation(), bad, 1f, 1f);
                                return true;
                            }

                            int x, y, z;
                            try{
                                x = Integer.parseInt(args[3]);
                                y = Integer.parseInt(args[4]);
                                z = Integer.parseInt(args[5]);
                            } catch (NumberFormatException e){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cThe coordonates must be numbers!"));
                                player.playSound(player.getLocation(), bad, 1f, 1f);
                                return true;
                            }
                            warps.set(mainPath+".location.x", x);
                            warps.set(mainPath+".location.y", y);
                            warps.set(mainPath+".location.z", z);
                            plugin.getWarps().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aLocation &l"+x+" "+y+" "+z+" &asaved for warp &l"+args[1]+"&a!"));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        case "setworld":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setworld <world>"));
                                player.playSound(player.getLocation(), bad, 1f, 1f);
                                return true;
                            }
                            String world = args[3];
                            warps.set(mainPath+".world", world);
                            plugin.getWarps().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aWorld &l"+world+" &asaved for warp &l"+args[1]+"&a!"));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        case "setperm":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setperm <perm>"));
                                player.playSound(player.getLocation(), bad, 1f, 1f);
                                return true;
                            }
                            String permission = args[3];

                            //Check if the permission is not 'warps.admin' or 'warps.use' (It will override with the permissions in 'plugin.yml')
                            if(permission.equalsIgnoreCase("warps.admin") || permission.equalsIgnoreCase("warps.use")){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cYou cannot assign this permission."));
                                player.playSound(player.getLocation(), bad, 1f, 1f);
                                return true;
                            }
                            warps.set(mainPath+".permission", permission);
                            plugin.getWarps().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aPermission &l"+permission+" &asaved for warp &l"+args[1]+"&a!"));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        case "setguititle":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setguititle <title>"));
                                player.playSound(player.getLocation(), bad, 1f, 1f);
                                return true;
                            }
                            String title = args[3];
                            warps.set(mainPath+".gui-title", title);
                            plugin.getWarps().saveConfig();

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aGUI title &r"+title+" &asaved for warp &l"+args[1]+"&a!"));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        case "setenchantglint":
                            if(args.length < 4){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUsage: &l/warpconfig manage <name> setenchantglint <true/false>"));
                                player.playSound(player.getLocation(), bad, 1f, 1f);
                                return true;
                            }

                            String value = args[3];
                            if(!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")){
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cThe value must be &ltrue &cor &lfalse&c!"));
                                player.playSound(player.getLocation(), bad, 1f, 1f);
                                return true;
                            }

                            warps.set(mainPath+".enchant-glint", value);
                            plugin.getWarps().saveConfig();
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &aValue &l"+value+" &asaved for warp &l"+args[1]+"&a!"));
                            player.playSound(player.getLocation(), good, 1f, 1.4f);
                            break;

                        default:
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUnknown command. Use &l/warpconfig help"));
                            player.playSound(player.getLocation(), bad, 1f, 1f);
                            break;
                    }
                    break;

                default:
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix+" &cUnknown command. use &l/warpconfig help"));
                    player.playSound(player.getLocation(), bad, 1f, 1f);
                    break;
            }
        }

        if(command.getName().equalsIgnoreCase("warps")){
            if(!sender.hasPermission("warps.use")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to run this command."));
                player.playSound(player.getLocation(), bad, 1f, 1f);
                return true;
            }
            plugin.getGuiManager().showGUI(player);
        }

        return false;
    }
}
