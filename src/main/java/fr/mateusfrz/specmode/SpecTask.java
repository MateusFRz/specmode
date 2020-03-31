package fr.mateusfrz.specmode;


import fr.mateusfrz.specmode.utils.Cube;
import fr.mateusfrz.specmode.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

class SpecTask implements Runnable {


    private HashMap<UUID, Location> playerOldLocation;
    private Plugin plugin;

    SpecTask(HashMap<UUID, Location> playerOldLocation, Plugin plugin) {
        this.playerOldLocation = playerOldLocation;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!playerOldLocation.isEmpty()) {
            for (UUID uuids : playerOldLocation.keySet()) {

                Cube cube = new Cube(playerOldLocation.get(uuids), plugin.getConfig().getDouble("cube.size")); // Leak memory here
                Player player = Bukkit.getPlayer(uuids);

                if (PlayerUtils.isOnline(player)) {
                    Location location = Objects.requireNonNull(player).getLocation();
                    cube.draw(player);
                    if (!PlayerUtils.isInTheCube(location, cube) && player.getGameMode().equals(GameMode.SPECTATOR)) {
                        player.teleport(playerOldLocation.get(uuids));
                        player.sendMessage("§6§l[SPECMODE] §cYou can't escape too far !");
                    }
                }
            }
        }

    }

}
