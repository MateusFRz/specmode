package fr.fr_phonix.specmode.listeners;

import fr.fr_phonix.specmode.npc.NPCManager;
import fr.fr_phonix.specmode.utils.Update;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerListener implements Listener {

    private NPCManager npcManager;
    private Plugin plugin;

    public PlayerListener(Plugin plugin, NPCManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onPlayerQuitEvent (PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (npcManager.hasNPC(player))
            npcManager.removeNPC(player.getUniqueId());

        if (npcManager.isObserver(player)) {
            npcManager.hideAllNPCToPlayer(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoinEvent (PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() && !Update.isUpToDate(plugin))
            player.sendMessage("§6[" + plugin.getDescription().getPrefix() + "] §cThis plugin got an update please check the new update !");

        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            Bukkit.getOnlinePlayers().forEach(pls -> pls.hidePlayer(plugin, player));
            npcManager.createNPC(player);
        }

        if (npcManager.isObserver(player)) {
            npcManager.showAllNPCToPlayer(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(pls -> {
                if (npcManager.hasNPC(pls))
                    player.hidePlayer(plugin, pls);
            });
        }
    }


}
