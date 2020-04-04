package fr.fr_phonix.specmode;

import fr.fr_phonix.specmode.commands.Spec;
import fr.fr_phonix.specmode.listeners.GUIListener;
import fr.fr_phonix.specmode.listeners.ListernerManager;
import fr.fr_phonix.specmode.metrics.Metrics;
import fr.fr_phonix.specmode.npc.NPCManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Main extends JavaPlugin {

    private HashMap<UUID, Location> playerOldLocation = new HashMap<>();
    private File file;
    private NPCManager npcManager;

    @Override
    public void onEnable() {

        Metrics metrics = new Metrics(this, 6953);
        npcManager = new NPCManager(this);
        ListernerManager listernerManager = new ListernerManager(this, npcManager);

        if (!getDataFolder().exists()) getDataFolder().mkdir();

        file = new File(getDataFolder() + File.separator + "players.dat");
        if (!file.exists()) {
            try {
                file.createNewFile();
                getConfig().options().copyDefaults(true);
                saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            Bukkit.getConsoleSender().sendMessage("§6§l[SPECMODE] §aGetting players of players.dat...");

            //PLAYER.DAT LOADING
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("players");

            for (String playerUUID : Objects.requireNonNull(configurationSection, "Section null (players)").getKeys(false)) {

                ConfigurationSection section = configurationSection.getConfigurationSection(playerUUID);

                double x = Objects.requireNonNull(section).getDouble("x");
                double y = section.getDouble("y");
                double z = section.getDouble("z");

                World world = Bukkit.getWorld(Objects.requireNonNull(section.getString("world")));

                Location loc = new Location(world, x, y, z);

                playerOldLocation.put(UUID.fromString(playerUUID), loc);
            }
            //END LOADING
        }


        Bukkit.getConsoleSender().sendMessage("§6§l[SPECMODE] §aPlayers locations loaded");

        Objects.requireNonNull(getCommand("spec")).setExecutor(new Spec(playerOldLocation, this, npcManager));
        listernerManager.registerEvents();

        Bukkit.getScheduler().runTaskTimer(this, new SpecTask(playerOldLocation, this, npcManager), 0L, 5L);
    }

    @Override
    public void onDisable() {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

        Bukkit.getConsoleSender().sendMessage("§6§l[SPECMODE] §aSaving player location");

        fileConfiguration.set("players", null);


        if (!fileConfiguration.isConfigurationSection("players")) {
            fileConfiguration.createSection("players");
        }

        if (!playerOldLocation.isEmpty()) {
            Set<Map.Entry<UUID, Location>> playerEntry = playerOldLocation.entrySet();


            for (Map.Entry<UUID, Location> entry : playerEntry) {
                HashMap<String, Object> coordinates = new HashMap<>();
                coordinates.put("x", entry.getValue().getX());
                coordinates.put("y", entry.getValue().getY());
                coordinates.put("z", entry.getValue().getZ());
                coordinates.put("world", Objects.requireNonNull(entry.getValue().getWorld()).getName());

                fileConfiguration.set("players." + entry.getKey().toString(), coordinates);
            }
        }

        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage("§6§l[SPECMODE] §aPlayer location saved");
        npcManager.detachAll();

    }
}
