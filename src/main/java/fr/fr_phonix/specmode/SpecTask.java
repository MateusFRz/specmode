package fr.fr_phonix.specmode;


import fr.fr_phonix.specmode.npc.NPCManager;
import fr.fr_phonix.specmode.utils.Cube;
import fr.fr_phonix.specmode.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.UUID;

class SpecTask implements Runnable {


    private Plugin plugin;
    private NPCManager npcManager;

    public SpecTask(Plugin plugin, NPCManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    @Override
    public void run() {
        if (!PlayerUtils.playerOldLocation.isEmpty()) {
            for (UUID uuids : PlayerUtils.playerOldLocation.keySet()) {

                Cube cube = new Cube(PlayerUtils.playerOldLocation.get(uuids), plugin.getConfig().getDouble("cube.size")); // Leak memory here
                Player player = Bukkit.getPlayer(uuids);

                if (PlayerUtils.isOnline(player)) {
                    Location location = Objects.requireNonNull(player).getLocation();
                    cube.draw(player);
                    if (!PlayerUtils.isInTheCube(location, cube) && player.getGameMode().equals(GameMode.SPECTATOR)) {
                        player.teleport(PlayerUtils.playerOldLocation.get(uuids));
                        npcManager.getNPC(player).teleport(player.getLocation());
                        player.sendMessage("§6§l[SPECMODE] §cYou can't escape too far !");
                    }
                }
            }
        }

    }

}
