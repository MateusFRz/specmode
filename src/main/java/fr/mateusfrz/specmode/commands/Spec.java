package fr.mateusfrz.specmode.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class Spec implements CommandExecutor {

    private HashMap<UUID, Location> playerOldLocation;
    private HashMap<UUID, Long> playerCooldown = new HashMap<>();
    private Plugin plugin;


    public Spec(HashMap<UUID, Location> playerOldLocation, Plugin plugin) {
        this.playerOldLocation = playerOldLocation;
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 0) {
                if (args.length > 1 && args[0].equalsIgnoreCase("cube") && (player.hasPermission("specmode.cube") || player.isOp())) {
                    try {
                        double size = Double.parseDouble(args[1]);

                        plugin.getConfig().set("cube.size", size);
                        plugin.saveConfig();

                        player.sendMessage("§6§l[SPECMODE] §aCube size is now §6" + size);
                        return true;
                    } catch (NumberFormatException e) {
                        player.sendMessage("§6§l[SPECMODE] This not a number !");
                        return false;
                    }
                }
            } else if (player.hasPermission("specmode.bypass") || !playerCooldown.containsKey(player.getUniqueId()) || playerCooldown.get(player.getUniqueId()) <= System.currentTimeMillis()) {
                if (player.getGameMode().equals(GameMode.SPECTATOR) && playerOldLocation.containsKey(player.getUniqueId())) {
                    //Switch player from spec to survival and remove OldLocation

                    player.teleport(playerOldLocation.get(player.getUniqueId()));
                    player.setGameMode(GameMode.SURVIVAL);
                    playerOldLocation.remove(player.getUniqueId());
                    playerCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getConfig().getInt("cooldown") * 1000));

                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    Bukkit.getOnlinePlayers().forEach(players -> players.showPlayer(plugin, player));

                    return true;

                } else if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                    // Switch player from survival to spec and register his location

                    playerOldLocation.put(player.getUniqueId(), player.getLocation());
                    player.setGameMode(GameMode.SPECTATOR);
                    Bukkit.getOnlinePlayers().forEach(players -> players.hidePlayer(plugin, player));

                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 1, false, false));

                    return true;

                } else {
                    Location loc = player.getLocation();

                    player.sendMessage("§c§oAn error occur please report to you're administrator !");

                    playerOldLocation.remove(player.getUniqueId());
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleport(new Location(player.getWorld(), loc.getX(), loc.getWorld().getHighestBlockYAt(loc), loc.getZ()));
                    return true;
                }
            } else {
                long time = (playerCooldown.get(player.getUniqueId()) - System.currentTimeMillis()) /1000;
                player.sendMessage("§cPlease wait " + time + " second before use it again");
                return true;
            }
        }
        return false;
    }
}
