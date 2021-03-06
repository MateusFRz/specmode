package fr.fr_phonix.specmode.commands;

import fr.fr_phonix.specmode.npc.NPCManager;
import fr.fr_phonix.specmode.utils.PlayerUtils;
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

    private HashMap<UUID, Long> playerCooldown = new HashMap<>();
    private Plugin plugin;
    private NPCManager npcManager;


    public Spec(Plugin plugin, NPCManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
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

                        player.sendMessage("§6§l[SpecMode] §aCube size is now §6" + size);
                        return true;
                    } catch (NumberFormatException e) {
                        player.sendMessage("§6[SpecMode] This not a number !");
                        return false;
                    }
                }
                if (args.length > 1 && args[0].equalsIgnoreCase("tp")  && (player.hasPermission("specmode.teleport") || player.isOp())) {
                    if (npcManager.hasNPC(player)) {
                        Player teleportPlayer = Bukkit.getPlayer(args[1]);
                        if (teleportPlayer != null && player.isOnline()) {
                            player.teleport(teleportPlayer);
                            player.setGameMode(GameMode.SPECTATOR);

                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 0, false, false));
                            npcManager.removeNPC(player.getUniqueId());
                            npcManager.createNPC(player);

                            return true;
                        }
                    } else {
                        player.sendMessage("§6[SpecMode] You have to be in spectator's mode to teleport to a player");
                    }
                }
                if (args[0].equalsIgnoreCase("option") && (player.hasPermission("specmode.option"))) {
                    PlayerUtils.openGUI(player, npcManager.isObserver(player));
                    return true;
                }
                return false;
            } else if (player.hasPermission("specmode.bypass") || !playerCooldown.containsKey(player.getUniqueId()) || playerCooldown.get(player.getUniqueId()) <= System.currentTimeMillis()) {
                if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                    if (!PlayerUtils.playerOldLocation.containsKey(player.getUniqueId())) {
                        player.sendMessage("§6[SpecMode] Error, you are going to be teleport to the highest block at you're location");

                        player.teleport(player.getLocation().getWorld().getHighestBlockAt(player.getLocation()).getLocation());
                        player.setGameMode(GameMode.SURVIVAL);
                        playerCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getConfig().getInt("cooldown") * 1000));

                        if (npcManager.hasNPC(player)) npcManager.removeNPC(player.getUniqueId());
                        return true;
                    }
                    //Switch player from spec to survival and remove OldLocation

                    player.teleport(PlayerUtils.playerOldLocation.get(player.getUniqueId()));
                    player.setGameMode(GameMode.SURVIVAL);

                    PlayerUtils.playerOldLocation.remove(player.getUniqueId());
                    playerCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (plugin.getConfig().getInt("cooldown") * 1000));

                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    if (npcManager.hasNPC(player)) npcManager.removeNPC(player.getUniqueId());

                    return true;

                } else if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                    // Switch player from survival to spec and register his location

                    player.setGameMode(GameMode.SPECTATOR);
                    PlayerUtils.playerOldLocation.put(player.getUniqueId(), player.getLocation());

                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 0, false, false));
                    npcManager.createNPC(player);

                    return true;

                } else {
                    player.sendMessage("§6[SpecMode] §cYou must be in SURVIVAL to access spectator mode !");
                    return true;
                }
            } else {
                long time = (playerCooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                player.sendMessage("§6[SpecMode] §cPlease wait " + time + " second before use it again");
                return true;
            }
        }
        return false;
    }
}
