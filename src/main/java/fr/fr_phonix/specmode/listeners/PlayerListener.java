package fr.fr_phonix.specmode.listeners;

import fr.fr_phonix.specmode.npc.NPCManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private NPCManager npcManager;

    public PlayerListener(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onPlayerQuitEvent (PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (npcManager.hasNPC(player)) {
            npcManager.removeNPC(player);
        }
    }

    @EventHandler
    public void onPlayerJoinEvent (PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            npcManager.createNPC(player);
        }
    }
}
