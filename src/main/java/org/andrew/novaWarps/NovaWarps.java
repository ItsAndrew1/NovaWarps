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
    private WarpTaskManager warpTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        //Setting the commands and the TABs
        getCommand("warpconfig").setExecutor(new CommandManager(this));
        getCommand("warpconfig").setTabCompleter(new CommandTABS(this));
        getCommand("warps").setExecutor(new CommandManager(this));

        //Setting the .yml files and other classes
        warps = new YMLfiles(this, "warps.yml");
        playerData = new YMLfiles(this, "playerData.yml");
        guiManager = new WarpsGUI(this);
        warpTask = new WarpTaskManager(this);
        cooldownManager = new CooldownManager(this);

        //Setting the events of the plugin
        getServer().getPluginManager().registerEvents(new WarpsGUI(this), this);

        //Checking for GUI size
        guiSize = getConfig().getInt("gui-rows") * 9;
        if(getConfig().getInt("gui-rows") < 1 || getConfig().getInt("gui-rows") > 6){
            Bukkit.getLogger().warning("[NOVAWARPS] The gui-rows in config.yml is invalid! The value must be between 1 and 6!");
        }

        //Checking EXIT-ITEM if it is toggled
        try{
            boolean toggleExitItem = getConfig().getBoolean("gui-exit-item.toggle");
            if(toggleExitItem){
                //Check if the slot is valid
                int exitButtonSlot = getConfig().getInt("gui-exit-item.slot");
                if(exitButtonSlot < 1 || exitButtonSlot > getGuiSize()){
                    Bukkit.getLogger().warning("[NOVAWARPS] The exit button's slot must be between 1 and "+getGuiSize());
                }

                //Check if the material is valid
                String stringExitButtonMaterial = getConfig().getString("gui-exit-item.material");
                Material exitButtonMaterial = Material.matchMaterial(stringExitButtonMaterial.toUpperCase());
                if(exitButtonMaterial == null){
                    Bukkit.getLogger().warning("[NOVAWARPS] The material for exit button does not exist!");
                }

                //Check if the sound is valid
                if(!getConfig().getString("exit-button-sound").equalsIgnoreCase("")){
                    String exitButtonSoundString = getConfig().getString("exit-button-sound");
                    NamespacedKey checkSound = NamespacedKey.minecraft(exitButtonSoundString.toLowerCase());
                    Sound trueExitButtonSound = Registry.SOUNDS.get(checkSound);
                    if(trueExitButtonSound == null){
                        Bukkit.getLogger().warning("[NOVAWARPS] The value for exit-button-sound in config.yml is invalid!");
                    }
                }
            }
        }  catch (Exception e){
            Bukkit.getLogger().warning("[NOVAWARPS] There is something wrong with gui-exit-item.toggle value!");
        }

        //Checking INFO-ITEM if it is toggled
        try{
            boolean toggleInfoitem = getConfig().getBoolean("gui-info-item.toggle");
            if(toggleInfoitem){
                //Check if material is valid
                Material infoItem = Material.matchMaterial(getConfig().getString("gui-info-item.material").toUpperCase());
                if(infoItem == null){
                    Bukkit.getLogger().warning("[NOVAWARPS] The material for info item does not exist!");
                }
            }
        } catch (Exception e){
            Bukkit.getLogger().warning("[NOVAWARPS] The value of gui-info-item.toggle is invalid!");
        }

        //Checking DECORATION-ITEM if it is toggled
        try{
            boolean toggleDecoration = getConfig().getBoolean("gui-toggle-decorations");
            if(toggleDecoration){
                //Check if material is valid
                Material decoItem = Material.matchMaterial(getConfig().getString("gui-decoration-item.material").toUpperCase());
                if(decoItem == null){
                    Bukkit.getLogger().warning("[NOVAWARPS] The material for deco item does not exist!");
                }
            }
        } catch (Exception e){
            Bukkit.getLogger().warning("[NOVAWARPS] The value of gui-toggle-decorations is invalid!");
        }

        //Checking boolean for firework
        try{
            boolean toggleFirework = getConfig().getBoolean("toggle-firework");
        } catch (Exception e){
            Bukkit.getLogger().warning("[NOVAWARPS] The value of toggle-firework is invalid!");
        }
    }

    @Override
    public void onDisable() {
        //Saves the config files
        saveConfig();
        getWarps().saveConfig();
        playerData.saveConfig();

        Bukkit.getLogger().info("[NOVAWARPS] NovaWarps disabled successfully!");
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
    public WarpTaskManager getWarpTask(){
        return warpTask;
    }
}
