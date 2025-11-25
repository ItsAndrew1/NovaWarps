//Developed by _ItsAndrew_
package org.andrew.novaWarps;

import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;

public final class NovaWarps extends JavaPlugin {
    private YMLfiles warps;
    private YMLfiles playerData;
    private int guiSize;
    private WarpsGUI guiManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        //Setting the commands and the TABs
        getCommand("warpconfig").setExecutor(new CommandManager(this));
        getCommand("warpconfig").setTabCompleter(new CommandTABS(this));
        getCommand("warps").setExecutor(new CommandManager(this));

        //Setting the .yml file(s)
        warps = new YMLfiles(this, "warps.yml");
        playerData = new YMLfiles(this, "playerData.yml");
        guiManager = new WarpsGUI(this);
        cooldownManager = new CooldownManager(this);

        //Setting the events of the plugin
        getServer().getPluginManager().registerEvents(new WarpsGUI(this), this);

        //Checking several conditions
        guiSize = getConfig().getInt("gui-rows") * 9;
        if(getConfig().getInt("gui-rows") < 1 || getConfig().getInt("gui-rows") > 6){
            Bukkit.getLogger().warning("[NW] The gui-rows in config.yml is invalid! The value must be between 1 and 6!");
        }

        //Checking if everything is ok for EXIT-ITEM
        String stringExitButtonToggle = getConfig().getString("exit-item.toggle");
        if(!stringExitButtonToggle.equalsIgnoreCase("true") && !stringExitButtonToggle.equalsIgnoreCase("false")){
            Bukkit.getLogger().warning("[NW] Invalid value for exit-item.toggle in config.yml! The value must be true/false!");
        }
        else{
            boolean exitButtonToggle = getConfig().getBoolean("exit-item.toggle");
            if(exitButtonToggle){
                //Check if the slot is valid
                int exitButtonSlot = getConfig().getInt("exit-item.slot");
                if(exitButtonSlot < 1 || exitButtonSlot > getGuiSize()){
                    Bukkit.getLogger().warning("[NW] The exit button's slot must be between 1 and "+getGuiSize());
                }

                //Check if the material is valid
                String stringExitButtonMaterial = getConfig().getString("exit-item.material");
                Material exitButtonMaterial = Material.matchMaterial(stringExitButtonMaterial.toUpperCase());
                if(exitButtonMaterial == null){
                    Bukkit.getLogger().warning("[NW] The material for exit button does not exist!");
                }

                //Check if the sound is valid
                if(!getConfig().getString("exit-button-sound").equalsIgnoreCase("")){
                    String exitButtonSoundString = getConfig().getString("exit-button-sound");
                    NamespacedKey checkSound = NamespacedKey.minecraft(exitButtonSoundString.toLowerCase());
                    Sound trueExitButtonSound = Registry.SOUNDS.get(checkSound);
                    if(trueExitButtonSound == null){
                        Bukkit.getLogger().warning("[NW] The value for exit-button-sound in config.yml is invalid!");
                    }
                }
            }

        }
    }

    @Override
    public void onDisable() {
        saveConfig();
        getWarps().saveConfig();

        Bukkit.getLogger().info("[NW] NovaWarps disabled successfully!");
    }

    //Getters
    public YMLfiles getWarps(){
        return warps;
    }
    public YMLfiles getPlayerData(){
        return playerData;
    }
    public int getGuiSize(){
        return guiSize;
    }
    public WarpsGUI getGuiManager(){
        return guiManager;
    }
    public CooldownManager getCooldownManager(){
        return cooldownManager;
    }
}
