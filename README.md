**NovaWarps v1.0** is a lightweight warps plugin designed to help server owners create and customize warps.  
The plugin is **plug-and-play** style, meaning that you put the `.jar` in `plugins` folder and runs perfectly.

---

## ⚙️ Features
 - Customizable GUI for players
 - Customizable sounds
 - Cooldowns for warping
 - Permissions for each warp  
**And much more!** 
#### 🔴 About PERMISSIONS:
- It is advised to have a *permission plugin* on your server, such as **LuckPerms**. Otherwise, the plugin will still work **WITHOUT** permissions.
- If you use **LuckPerms**, you can use this command:
 ```
 /lp group <group> permission set <permission> true
 ```
---

## 🪄 Commands
**Commands** let the staff manage the warps as they like.  

| Command                         | Description                              | Permission    |
|---------------------------------|------------------------------------------|---------------|
| `/warpconfig create <name>`     | *Creates* a warp in `warps.yml`          | `warps.admin` |
| `/warpconfig delete <name>`     | *Deletes* a warp from `warps.yml`        | `warps.admin` |
| `/warpconfig reload`            | *Reloads* all the `.yml` files.          | `warps.admin` |
| `/warpconfig help`              | Gives you all the *available commands*   | `warps.admin` |
| `/warpconfig manage <name> ...` | Helps *managing* all the warps available | `warps.admin` |
| `/warps`                        | Opens the warps GUI                      | `warps.use`   |

**🔴Quick Tip:** You can use */wc* instead of */warpconfig* for faster inputs.

---

## 📁Configuration Files
NovaWarps uses **3 .yml files**, each with it's own use. Here is what each file is for:
- **config.yml** contains all the configurable settings about the plugin, such as:
  - Configuring the Warps GUI
  - Configuring all the sounds that the plugin uses
  - Configure the *warp iteself*  
  And **more**.
- **playerData.yml** contains each player's cooldowns. It is best *not to modify anything*!
- **warps.yml** contains each warp you create and configure.

**🔴 Quick Tip:** Always run */warpconfig reload* after making a change in any `.yml file`.

---

## ❤️Credits
NovaWarps is being developed by **\_ItsAndrew_**.  
Special thanks to anyone who *contributes*, *helps* and *gives feedback*!  
If you encounter bugs, please open up an [issue](https://github.com/ItsAndrew1/NovaWarps/issues) or DM me on discord: **\_ItsAndrew_**