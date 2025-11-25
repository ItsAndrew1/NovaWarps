//Developed by _ItsAndrew_
package org.andrew.novaWarps;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WarpsGUI implements Listener {
    NovaWarps plugin;
    BukkitRunnable task;

    public WarpsGUI(NovaWarps plugin){
        this.plugin = plugin;
    }

    //Shows the GUI to the player
    public void showGUI(Player player){
        FileConfiguration warps = plugin.getWarps().getConfig();
        String chatPrefix = plugin.getConfig().getString("prefix");
        String stringOpenSound = plugin.getConfig().getString("open-warps-gui-sound").toLowerCase();
        String guiTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("gui-title")));
        boolean exitItemToggle = plugin.getConfig().getBoolean("exit-item.toggle");

        //If there aren't any warps configured, sends a message to the player
        if(!warps.isConfigurationSection("warps")){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix+" &cThere are no warps configured. Contact the server administrators about this!"));
            return;
        }

        Inventory gui = Bukkit.createInventory(null, plugin.getGuiSize(), guiTitle);

        //Shows the exitItem if exit-item.toggle is true
        if(exitItemToggle){
            String stringExitItemMaterial = plugin.getConfig().getString("exit-item.material");
            Material exitItemMaterial = Material.matchMaterial(stringExitItemMaterial.toUpperCase());
            ItemStack exitItem = new ItemStack(exitItemMaterial);
            ItemMeta exitItemMeta = exitItem.getItemMeta();

            exitItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("exit-item.display-name"))));
            exitItem.setItemMeta(exitItemMeta);

            int exitItemSlot = plugin.getConfig().getInt("exit-item.slot");
            gui.setItem(exitItemSlot, exitItem);
        }

        try{
            boolean togglePermissions = plugin.getConfig().getBoolean("toggle-permissions");

            NamespacedKey soundName = NamespacedKey.minecraft(stringOpenSound.toLowerCase());
            Sound trueOpenSound = Registry.SOUNDS.get(soundName);
            float openSoundVolume = plugin.getConfig().getInt("open-warps-gui-sound-volume");
            float openSoundPitch = plugin.getConfig().getInt("open-warps-gui-sound-pitch");

            player.playSound(player.getLocation(), trueOpenSound, openSoundVolume, openSoundPitch);

            for(String warp : warps.getConfigurationSection("warps").getKeys(false)){
                String guiWarpItem = "warps."+warp+".gui-item";
                int guiWarpSlot = Integer.parseInt(Objects.requireNonNull(warps.getString("warps."+warp+ ".gui-slot")));

                Material guiWarpMaterial = Material.matchMaterial(warps.getString(guiWarpItem).toUpperCase());
                ItemStack guiWarp = new ItemStack(guiWarpMaterial);
                ItemMeta guiWarpMeta = guiWarp.getItemMeta();

                //If the enchant-glint of a warp is true, it shows it on the item
                String toggleEnchantGlint = warps.getString("warps."+warp+".enchant-glint");
                if(toggleEnchantGlint.equalsIgnoreCase("true")){
                    guiWarpMeta.addEnchant(Enchantment.LURE, 1, true);
                    guiWarpMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                else{
                    guiWarpMeta.removeEnchant(Enchantment.LURE);
                    guiWarpMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                String guiWarpTitle = ChatColor.translateAlternateColorCodes('&', warps.getString("warps."+warp+".gui-title"));
                guiWarpMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', guiWarpTitle));

                if(togglePermissions){
                    //Sets the lore for players with and without permission to use a specific warp
                    if(!player.hasPermission("warps."+warp+".permission")){
                        List<String> coloredLore = new ArrayList<>();
                        for(String rawLoreLine : warps.getStringList("warps."+warp+".lore")){
                            coloredLore.add(ChatColor.translateAlternateColorCodes('&', rawLoreLine));
                        }
                        coloredLore.add("");
                        coloredLore.add(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("no-permission-lore"))));
                        guiWarpMeta.setLore(coloredLore);
                    }
                    else{
                        List<String> coloredLore = new ArrayList<>();
                        for(String rawLoreLine : warps.getStringList("warps."+warp+".lore")){
                            coloredLore.add(ChatColor.translateAlternateColorCodes('&', rawLoreLine));
                        }
                        guiWarpMeta.setLore(coloredLore);
                    }
                }

                guiWarp.setItemMeta(guiWarpMeta);
                gui.setItem(guiWarpSlot, guiWarp);
            }
          //Displays any errors in the server console
        } catch (Exception e){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', chatPrefix+" &cError! Contact the server administrators about this."));
            Bukkit.getLogger().warning(e.getMessage());
            return;
        }
        player.openInventory(gui);
    }

    //Handles any click in the GUI made by a player
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        //Checking certain conditions
        if(!(event.getWhoClicked() instanceof Player player)) return;

        String title = plugin.getConfig().getString("gui-title");
        if(!event.getView().getTitle().equalsIgnoreCase(title)) return;

        ItemStack clicked = event.getCurrentItem();
        if(clicked == null || clicked.getType() == Material.AIR) return;

        //Exit button
        String exitItemString = plugin.getConfig().getString("exit-item.material").toUpperCase();
        Material exitItemMaterial = Material.matchMaterial(exitItemString);
        if(clicked.getType() == exitItemMaterial){
            String exitButtonSoundString = plugin.getConfig().getString("exit-button-sound");
            NamespacedKey checkSound = NamespacedKey.minecraft(exitButtonSoundString.toLowerCase());
            Sound exitButtonSound = Registry.SOUNDS.get(checkSound);
            float exitButtonSoundVolume = plugin.getConfig().getInt("exit-button-sound-volume");
            float exitButtonSoundPitch = plugin.getConfig().getInt("exit-button-sound-pitch");
            player.playSound(player.getLocation(), exitButtonSound, exitButtonSoundVolume, exitButtonSoundPitch);

            player.closeInventory();
        }

        FileConfiguration warps = plugin.getWarps().getConfig();
        String prefix = plugin.getConfig().getString("prefix");

        for(String warp : warps.getConfigurationSection("warps").getKeys(false)){
            String mainPath = "warps."+warp;
            double locationX = warps.getInt(mainPath+".location.x");
            double locationY = warps.getInt(mainPath+".location.y");
            double locationZ = warps.getInt(mainPath+".location.z");

            String warpMaterialString = warps.getString(mainPath+".gui-item").toUpperCase();
            Material warpMaterial = Material.matchMaterial(warpMaterialString);

            if(clicked.getType() == warpMaterial){
                player.closeInventory();

                double playerX = player.getLocation().getX();
                double playerY = player.getLocation().getY();
                double playerZ = player.getLocation().getZ();

                String playerPermission = warps.getString(mainPath+".permission");
                boolean togglePermissions = plugin.getConfig().getBoolean("toggle-permissions");
                boolean toggleCooldowns = plugin.getConfig().getBoolean("toggle-cooldowns");

                //Check for player's permission if togglePermissions is true
                if(togglePermissions){
                    if(!player.hasPermission(playerPermission)){
                        //Setting the values and strings for the sound
                        String noPermissionSoundString = plugin.getConfig().getString("no-permission-sound");
                        String noPermissionChatMessage = plugin.getConfig().getString("no-permission-chat-message");
                        float noPermissionSoundVolume = plugin.getConfig().getInt("no-permission-sound-volume");
                        float noPermissionSoundPitch = plugin.getConfig().getInt("no-permission-sound-volume");
                        NamespacedKey checkNoPermissionSound = NamespacedKey.minecraft(noPermissionSoundString);
                        Sound noPermissionSound = Registry.SOUNDS.get(checkNoPermissionSound);

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissionChatMessage));
                        player.playSound(player.getLocation(), noPermissionSound, noPermissionSoundVolume, noPermissionSoundPitch);
                        player.closeInventory();
                        return;
                    }
                }

                //Check if the player has cooldown if toggleCooldowns is true
                if(toggleCooldowns){
                    if(plugin.getCooldownManager().playerHasCooldown(player)){
                        long remainingCooldown = plugin.getCooldownManager().getRemainingCooldown(player);
                        String remainingCooldownString = formatCooldown(remainingCooldown);

                        String playerHasCooldownSoundString = plugin.getConfig().getString("player-has-cooldown-sound");
                        float playerHasCooldownSoundVolume = plugin.getConfig().getInt("player-has-cooldown-sound-volume");
                        float playerHasCooldownSoundPitch = plugin.getConfig().getInt("player-has-cooldown-sound-pitch");
                        NamespacedKey checkPlayerHasCooldownSound = NamespacedKey.minecraft(playerHasCooldownSoundString.toLowerCase());
                        Sound playerHasCooldownSound = Registry.SOUNDS.get(checkPlayerHasCooldownSound);

                        player.playSound(player.getLocation(), playerHasCooldownSound, playerHasCooldownSoundVolume, playerHasCooldownSoundPitch);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou still have a cooldown of &l"+remainingCooldownString+"&c!"));
                        player.closeInventory();
                        return;
                    }
                }

                final int[] second = {plugin.getConfig().getInt("task-timer")};
                task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(isMoving(player, playerX, playerY, playerZ)){
                            String playerMovedTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("player-moved-title")));
                            String playerMovedSubtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("player-moved-subtitle")));
                            String playerMovedChatMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("player-moved-message")));

                            float PlayerMovedSoundVolume = plugin.getConfig().getInt("player-moved-sound-volume");
                            float PlayerMovedSoundPitch = plugin.getConfig().getInt("player-moved-sound-pitch");
                            String playerMovedSoundString = plugin.getConfig().getString("player-moved-sound");
                            NamespacedKey checkPlayerMovedSound = NamespacedKey.minecraft(playerMovedSoundString.toLowerCase());
                            Sound truePlayerMovedSound = Registry.SOUNDS.get(checkPlayerMovedSound);

                            player.sendTitle(playerMovedTitle, playerMovedSubtitle);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', playerMovedChatMessage));
                            player.playSound(player.getLocation(), truePlayerMovedSound, PlayerMovedSoundVolume, PlayerMovedSoundPitch);

                            task.cancel();
                            return;
                        }

                        String titleStyle = plugin.getConfig().getString("task-active-title-style");
                        String subtitle = plugin.getConfig().getString("task-active-subtitle");

                        String taskActiveSound = plugin.getConfig().getString("task-in-progress-sound").toLowerCase();
                        float taskActiveSoundVolume = plugin.getConfig().getInt("task-in-progress-sound-volume");
                        float taskActiveSoundPitch = plugin.getConfig().getInt("task-in-progress-sound-pitch");
                        NamespacedKey checkTaskActiveSound = NamespacedKey.minecraft(taskActiveSound);
                        Sound trueTaskActiveSound = Registry.SOUNDS.get(checkTaskActiveSound);

                        //If the second is 0
                        if(second[0] == 0){
                            World world = Bukkit.getWorld(Objects.requireNonNull(warps.getString(mainPath + ".world")));
                            if(world == null){
                                Bukkit.getLogger().info("[NW] World invalid for warp "+warp+"!");
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere is a problem. Contact the server administrators about this"));
                                return;
                            }

                            //Starts the cooldown if 'toggle-cooldowns' is true
                            if(toggleCooldowns){
                                plugin.getCooldownManager().startCooldown(player);
                            }

                            //Information for the sound of playerWarped
                            String playerWarpedSoundString = plugin.getConfig().getString("player-warped-sound");
                            float playerWarpedSoundVolume = plugin.getConfig().getInt("player-warped-sound-volume");
                            float playerWarpedSoundPitch = plugin.getConfig().getInt("player-warped-sound-pitch");
                            NamespacedKey checkPlayerWarpedSound = NamespacedKey.minecraft(playerWarpedSoundString.toLowerCase());
                            Sound playerWarpedSound = Registry.SOUNDS.get(checkPlayerWarpedSound);

                            Location teleportLocation = new Location(world, locationX, locationY, locationZ);
                            player.playSound(player.getLocation(), playerWarpedSound, playerWarpedSoundVolume, playerWarpedSoundPitch);
                            player.sendTitle(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("player-warped-title"))),"");
                            player.teleport(teleportLocation);
                            task.cancel();
                            return;
                        }
                        player.sendTitle(ChatColor.translateAlternateColorCodes('&', titleStyle+ second[0]), ChatColor.translateAlternateColorCodes('&', subtitle));
                        player.playSound(player.getLocation(), trueTaskActiveSound, taskActiveSoundVolume, taskActiveSoundPitch);
                        second[0]--;
                    }
                };
                task.runTaskTimer(plugin, 0L, 20L);
            }
        }
    }

    //Check if the player is standing still
    public boolean isMoving(Player player, double x, double y, double z){
        double playerX = player.getLocation().getX();
        double playerY = player.getLocation().getY();
        double playerZ = player.getLocation().getZ();

        return playerX != x || playerY != y || playerZ != z;
    }

    //Creates the string of characters needed for displaying the remaining cooldown
    public String formatCooldown(long seconds){
        long days = seconds/86400;
        long hours = seconds/3600;
        long minutes = seconds/60;
        long secs = seconds%60;

        StringBuilder displayCooldown = new StringBuilder();
        if(days > 0) displayCooldown.append(days).append("d ");
        if(days > 0 || hours > 0) displayCooldown.append(hours).append("h ");
        if(days > 0 || hours > 0 || minutes > 0) displayCooldown.append(minutes).append("m ");
        displayCooldown.append(secs).append("s ");

        return displayCooldown.toString().trim();
    }
}
