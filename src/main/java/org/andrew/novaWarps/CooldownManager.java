//Developed by _ItsAndrew_
package org.andrew.novaWarps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CooldownManager {
    NovaWarps plugin;

    public CooldownManager(NovaWarps plugin){
        this.plugin = plugin;
    }

    //Starts the cooldown after a player warps
    public void startCooldown(Player player){
        FileConfiguration playerData = plugin.getPlayerData().getConfig();
        String warpCooldown = plugin.getConfig().getString("cooldown");

        boolean toggleCooldowns = plugin.getConfig().getBoolean("toggle-cooldowns");
        if(toggleCooldowns){
            long cooldownFromConfig;
            try{
                cooldownFromConfig = parseCooldown(warpCooldown);
            } catch (Exception e){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " &cThere was an error. Contact the server administrators about this."));
                Bukkit.getLogger().warning("[NOVAWARPS] There is something wrong with the cooldown in config.yml!");
                Bukkit.getLogger().warning(e.getMessage());
                return;
            }

            long rawCooldown =System.currentTimeMillis() / 1000 + cooldownFromConfig;
            String pathToSet = "players."+player.getName()+".cooldown";
            playerData.set(pathToSet, rawCooldown);
            plugin.getPlayerData().saveConfig();
        }
        else{
            playerData.set("players."+player.getName(), null);
            plugin.getPlayerData().saveConfig();
        }
    }

    //Checks if the player has cooldown or not
    public boolean playerHasCooldown(Player player){
        plugin.getPlayerData().reloadConfig();
        FileConfiguration playerData = plugin.getPlayerData().getConfig();

        long cooldownNow = System.currentTimeMillis() / 1000;
        long cooldownExpiresAt = playerData.getLong("players."+player.getName()+".cooldown");

        return cooldownNow < cooldownExpiresAt;
    }

    //Gets the cooldown a player has
    public long getRemainingCooldown(Player player){
        plugin.getPlayerData().reloadConfig();
        FileConfiguration playerData = plugin.getPlayerData().getConfig();

        long cooldownNow = System.currentTimeMillis() / 1000;
        long cooldownExpiresAt = playerData.getLong("players."+player.getName()+".cooldown");

        return Math.max(0, cooldownExpiresAt-cooldownNow);
    }

    //Parses the cooldown from config.yml
    public long parseCooldown(String cooldown){
        long seconds = 0;
        Matcher m = Pattern.compile("(\\d+)([dhms])").matcher(cooldown.toLowerCase());

        //10m => "10" = group(1); "m" = group(2)
        while(m.find()){
            int value = Integer.parseInt(m.group(1));
            switch(m.group(2)){
                case "d" -> seconds += value*86400L;
                case "h" -> seconds += value*3600L;
                case "m" -> seconds += value*60L;
                case "s" -> seconds += value;
            }
        }
        return seconds;
    }
}
