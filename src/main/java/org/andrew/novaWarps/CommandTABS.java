//Developed by _ItsAndrew_
package org.andrew.novaWarps;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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
        FileConfiguration warps = plugin.getWarps().getConfig();

        if(command.getName().equalsIgnoreCase("warpconfig")){
            if(args.length == 1){
                return Arrays.asList("create", "delete", "manage", "reload", "help");
            }
            if(args.length == 2 && args[0].equalsIgnoreCase("manage")){
                if(!warps.isConfigurationSection("warps")){
                    return Collections.emptyList();
                }
                Set<String> keys = warps.getConfigurationSection("warps").getKeys(false);
                return new ArrayList<>(keys);
            }
            if(args.length == 3 && args[0].equalsIgnoreCase("manage")){
                return Arrays.asList("setguislot", "setguiitem", "setguititle","setperm", "setenchantglint", "setlocation", "setworld");
            }
            if(args.length == 4 && args[2].equalsIgnoreCase("setenchantglint")){
                return Arrays.asList("true", "false");
            }
        }

        return new ArrayList<>();
    }
}
