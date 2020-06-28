package fr.fr_phonix.specmode.listeners;

import fr.fr_phonix.specmode.npc.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class ListernerManager {

    private Plugin plugin;
    private NPCManager npcManager;

    public ListernerManager(Plugin plugin, NPCManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    public void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new GUIListener(npcManager), plugin);
        pluginManager.registerEvents(new PlayerListener(plugin, npcManager), plugin);
    }
}
