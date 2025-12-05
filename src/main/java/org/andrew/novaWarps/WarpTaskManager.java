//Developed by _ItsAndrew_
package org.andrew.novaWarps;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class WarpTaskManager{
    private final NovaWarps plugin;
    private BukkitRunnable task;

    public WarpTaskManager(NovaWarps plugin){
        this.plugin = plugin;
    }

    public void startTask(Player player, ItemStack clickedItem){
        FileConfiguration warps = plugin.getWarps().getConfig();

        for(String warp : warps.getConfigurationSection("warps").getKeys(false)){
            String mainPath = "warps."+warp;
            double locationX = warps.getInt(mainPath+".location.x");
            double locationY = warps.getInt(mainPath+".location.y");
            double locationZ = warps.getInt(mainPath+".location.z");

            String warpMaterialString = warps.getString(mainPath+".gui-item").toUpperCase();
            Material warpMaterial = Material.matchMaterial(warpMaterialString);

            if(clickedItem.getType() == warpMaterial){
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
                if(toggleCooldowns) {
                    if (plugin.getCooldownManager().playerHasCooldown(player)) {
                        long remainingCooldown = plugin.getCooldownManager().getRemainingCooldown(player);
                        String remainingCooldownString = formatCooldown(remainingCooldown);

                        String playerHasCooldownSoundString = plugin.getConfig().getString("player-has-cooldown-sound");
                        float playerHasCooldownSoundVolume = plugin.getConfig().getInt("player-has-cooldown-sound-volume");
                        float playerHasCooldownSoundPitch = plugin.getConfig().getInt("player-has-cooldown-sound-pitch");
                        NamespacedKey checkPlayerHasCooldownSound = NamespacedKey.minecraft(playerHasCooldownSoundString.toLowerCase());
                        Sound playerHasCooldownSound = Registry.SOUNDS.get(checkPlayerHasCooldownSound);

                        player.playSound(player.getLocation(), playerHasCooldownSound, playerHasCooldownSoundVolume, playerHasCooldownSoundPitch);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou still have a cooldown of &l" + remainingCooldownString + "&c!"));
                        player.closeInventory();
                        return;
                    }
                }

                //Starts the actual warp task :)
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
                            player.sendTitle(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("player-warped-title"))),"");
                            player.teleport(teleportLocation);
                            player.playSound(player.getLocation(), playerWarpedSound, playerWarpedSoundVolume, playerWarpedSoundPitch);

                            //Spawns the firework if it is toggled
                            spawnFirework(player, warp);

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

    private void spawnFirework(Player player, String warp){
        FileConfiguration config = plugin.getConfig();
        FileConfiguration warps = plugin.getWarps().getConfig();

        boolean toggleFireWorks = config.getBoolean("toggle-firework");
        String mainColor = config.getString("firework-main-color");
        String secondColor = config.getString("firework-secondary-color");

        if(!toggleFireWorks) return;

        Location playerLocation = new Location(Bukkit.getWorld(warps.getString("warps."+warp+".world")), player.getX(), player.getY() + 0.5, player.getZ());
        Firework firework = (Firework) player.getWorld().spawnEntity(playerLocation, EntityType.FIREWORK_ROCKET);
        FireworkMeta fwMeta = firework.getFireworkMeta();

        fwMeta.addEffect(FireworkEffect.builder()
                .withColor(getColorFromConfig(mainColor), getColorFromConfig(secondColor))
                .with(FireworkEffect.Type.BALL)
                .trail(true)
                .flicker(true)
                .build()
        );
        fwMeta.setPower(1);
        firework.setFireworkMeta(fwMeta);

        //Detonating the firework
        Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 1L);
    }

    private Color getColorFromConfig(String hexValue){
        return Color.fromRGB(
                Integer.valueOf(hexValue.substring(1, 3), 16),
                Integer.valueOf(hexValue.substring(3, 5), 16),
                Integer.valueOf(hexValue.substring(5,7), 16)
        );
    }

    //Check if the player is standing still
    private boolean isMoving(Player player, double x, double y, double z){
        double playerX = player.getLocation().getX();
        double playerY = player.getLocation().getY();
        double playerZ = player.getLocation().getZ();

        return playerX != x || playerY != y || playerZ != z;
    }

    //Creates the string of characters needed for displaying the remaining cooldown
    private String formatCooldown(long seconds){
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
