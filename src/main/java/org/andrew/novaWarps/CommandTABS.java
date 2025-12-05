//Developed by _ItsAndrew_
package org.andrew.novaWarps;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//This is for setting the tabs of each command
public class CommandTABS implements TabCompleter {
    NovaWarps plugin;

    public CommandTABS(NovaWarps plugin){
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String labels, @NotNull String[] args){
        FileConfiguration warpsFile = plugin.getWarps().getConfig();

        if(command.getName().equalsIgnoreCase("warpconfig")){
            if(args.length == 1){
                return Arrays.asList("create", "delete", "manage", "reload", "help");
            }

            //Displays the warps for /wc delete command
            if(args.length == 2 && args[1].equalsIgnoreCase("delete")){
                //Checking if there are any warps
                ConfigurationSection warps = warpsFile.getConfigurationSection("warps");
                if(warps == null || warps.getKeys(false).isEmpty()){
                    return Collections.emptyList();
                }

                Set<String> Warps = warps.getKeys(false);
                return new ArrayList<>(Warps);
            }

            //Displays the warps for /wc manage command
            if(args.length == 2 && args[0].equalsIgnoreCase("manage")){
                //Checking if there are any warps configured
                ConfigurationSection warps = warpsFile.getConfigurationSection("warps");
                if(warps == null || warps.getKeys(false).isEmpty()){
                    return Collections.emptyList();
                }

                Set<String> Warps = warpsFile.getConfigurationSection("warps").getKeys(false);
                return new ArrayList<>(Warps);
            }

            if(args.length == 3 && args[0].equalsIgnoreCase("manage")){
                boolean togglePermissions = plugin.getConfig().getBoolean("toggle-permissions");
                if(!togglePermissions){ //Returns tabs without 'setperm' if the permissions are not toggled
                    return Arrays.asList("setguislot", "setguiitem", "setguititle", "setenchantglint", "setlocation", "setworld");
                }
                return Arrays.asList("setguislot", "setguiitem", "setguititle", "setperm", "setenchantglint", "setlocation", "setworld");
            }
            if(args.length == 4 && args[2].equalsIgnoreCase("setenchantglint")){
                return Arrays.asList("true", "false");
            }
        }

        return new ArrayList<>();
    }
}
