//Developed by _ItsAndrew_
package org.andrew.novaWarps;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class WarpsGUI implements Listener {
    private final NovaWarps plugin;

    public WarpsGUI(NovaWarps plugin){
        this.plugin = plugin;
    }

    //Shows the GUI to the player
    public void showGUI(Player player){
        FileConfiguration warps = plugin.getWarps().getConfig();
        String chatPrefix = plugin.getConfig().getString("prefix");
        String stringOpenSound = plugin.getConfig().getString("open-warps-gui-sound").toLowerCase();
        String guiTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("gui-title")));
        boolean exitItemToggle = plugin.getConfig().getBoolean("gui-exit-item.toggle");

        //If there aren't any warps configured, sends a message to the player
        ConfigurationSection warpSection = warps.getConfigurationSection("warps");
        if(warpSection == null || warpSection.getKeys(false).isEmpty()){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere are no warps configured. Contact the server administrators about this!"));

            Sound noWarps = Registry.SOUNDS.get(NamespacedKey.minecraft(plugin.getConfig().getString("no-warps-sound").toLowerCase()));
            float nwsVolume = plugin.getConfig().getInt("nws-volume");
            float nwsPitch = plugin.getConfig().getInt("nws-pitch");
            player.playSound(player.getLocation(), noWarps, nwsVolume, nwsPitch);
            return;
        }

        Inventory hintsGui = Bukkit.createInventory(null, plugin.getGuiSize(), guiTitle); //Creates the GUI

        //Displays decorations (if they are toggled)
        boolean toggleDecorations = plugin.getConfig().getBoolean("gui-toggle-decorations");
        boolean toggleInfoItem = plugin.getConfig().getBoolean("gui-info-item.toggle");
        if(toggleDecorations){
            String stringDecorationItem = plugin.getConfig().getString("gui-decoration-item.material").toUpperCase();
            String diDisplayName = plugin.getConfig().getString("gui-decoration-item.display-name");
            ItemStack decorationItem = new ItemStack(Material.matchMaterial(stringDecorationItem));
            ItemMeta diMeta = decorationItem.getItemMeta();

            for(int i = 0; i<=8; i++){
                if(toggleInfoItem){ //Skips the slot 4 if the info item is toggled
                    if(i == 4) continue;
                }

                if(diDisplayName != null) diMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', diDisplayName));
                decorationItem.setItemMeta(diMeta);
                hintsGui.setItem(i, decorationItem);
            }

            for(int i = 45; i<=53; i++){
                if(diDisplayName != null) diMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', diDisplayName));
                decorationItem.setItemMeta(diMeta);
                hintsGui.setItem(i, decorationItem);
            }
        }

        //Shows the info item if it is toggled
        if(toggleInfoItem){
            String iiDisplayName = plugin.getConfig().getString("gui-info-item.display-name");
            String iiStringMaterial = plugin.getConfig().getString("gui-info-item.material").toUpperCase();
            ItemStack infoItem = new ItemStack(Material.matchMaterial(iiStringMaterial));
            ItemMeta iiMeta = infoItem.getItemMeta();

            //Sets the lore
            if(plugin.getConfig().getStringList("gui-info-item.lore").isEmpty()) iiMeta.setLore(Collections.emptyList()); //Checks if there is any lore
            List<String> coloredLore = new ArrayList<>();
            for(String loreLine : plugin.getConfig().getStringList("gui-info-item.lore")){
                String coloredLoreLine = ChatColor.translateAlternateColorCodes('&', loreLine);
                coloredLore.add(coloredLoreLine);
            }
            iiMeta.setLore(coloredLore);

            iiMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', iiDisplayName));
            infoItem.setItemMeta(iiMeta);
            hintsGui.setItem(4, infoItem);
        }

        //Shows the exitItem if it is toggled
        if(exitItemToggle){
            String stringExitItemMaterial = plugin.getConfig().getString("gui-exit-item.material");
            Material exitItemMaterial = Material.matchMaterial(stringExitItemMaterial.toUpperCase());
            ItemStack exitItem = new ItemStack(exitItemMaterial);
            ItemMeta exitItemMeta = exitItem.getItemMeta();

            exitItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("gui-exit-item.display-name"))));
            exitItem.setItemMeta(exitItemMeta);

            int exitItemSlot = plugin.getConfig().getInt("gui-exit-item.slot");
            hintsGui.setItem(exitItemSlot, exitItem);
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
                    //Sets the lore of each warp
                    List<String> coloredLore = new ArrayList<>();
                    for(String rawLoreLine : warps.getStringList("warps."+warp+".lore")){
                        coloredLore.add(ChatColor.translateAlternateColorCodes('&', rawLoreLine));
                    }
                    guiWarpMeta.setLore(coloredLore);
                }

                guiWarp.setItemMeta(guiWarpMeta);
                hintsGui.setItem(guiWarpSlot, guiWarp);
            }
        } catch (Exception e){ //Displays any errors in the server console
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cError! Contact the server administrators about this."));
            Bukkit.getLogger().warning(e.getMessage());
            return;
        }
        player.openInventory(hintsGui);
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

        event.setCancelled(true); //Doesn't let the player take any items

        //If the player clicks on info item
        Material infoItemMaterial = Material.matchMaterial(plugin.getConfig().getString("gui-info-item.material").toUpperCase());
        if(clicked.getType() == infoItemMaterial) return;

        //If the player clicks on a decoration item
        Material decorationItemMaterial = Material.matchMaterial(plugin.getConfig().getString("gui-decoration-item.material").toUpperCase());
        if(clicked.getType() == decorationItemMaterial) return;

        //If the player clicks on exit button
        String exitItemString = plugin.getConfig().getString("gui-exit-item.material").toUpperCase();
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

        plugin.getWarpTask().startTask(player, clicked); //Starts the task
    }
}
