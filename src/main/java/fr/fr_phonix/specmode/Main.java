package fr.fr_phonix.specmode;

import fr.fr_phonix.specmode.commands.Spec;
import fr.fr_phonix.specmode.listeners.ListernerManager;
import fr.fr_phonix.specmode.metrics.Metrics;
import fr.fr_phonix.specmode.npc.NPCManager;
import fr.fr_phonix.specmode.utils.PlayerUtils;
import fr.fr_phonix.specmode.utils.Update;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;


public class Main extends JavaPlugin {

    private File file;
    private NPCManager npcManager;

    @Override
    public void onEnable() {

        Metrics metrics = new Metrics(this, 6953);
        npcManager = new NPCManager(this);
        ListernerManager listernerManager = new ListernerManager(this, npcManager);



        if (!getDataFolder().exists()) getDataFolder().mkdir();

        file = new File(getDataFolder() + File.separator + "players.dat");
        try {
            if (file.createNewFile()) {
                getConfig().options().copyDefaults(true);
                saveConfig();
            } else {
                Bukkit.getLogger().log(Level.INFO, getDescription().getPrefix() + " §aGetting players of players.dat...");

                //PLAYER.DAT LOADING
                FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

                ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("players");

                for (String playerUUID : Objects.requireNonNull(configurationSection, "Section null (players)").getKeys(false)) {

                    ConfigurationSection section = configurationSection.getConfigurationSection(playerUUID);

                    if (section.isConfigurationSection("location")) {
                        double x = Objects.requireNonNull(section).getDouble("location.x");
                        double y = section.getDouble("location.y");
                        double z = section.getDouble("location.z");

                        World world = Bukkit.getWorld(Objects.requireNonNull(section.getString("location.world")));

                        Location loc = new Location(world, x, y, z);

                        PlayerUtils.playerOldLocation.put(UUID.fromString(playerUUID), loc);
                        if(Bukkit.getPlayer(UUID.fromString(playerUUID)) != null) {
                            npcManager.createNPC(Bukkit.getPlayer(UUID.fromString(playerUUID)));
                        }
                    }

                    if (section.getBoolean("observer"))
                        npcManager.attach(UUID.fromString(playerUUID));
                }
                //END LOADING
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        Bukkit.getLogger().log(Level.INFO, getDescription().getPrefix()  + " §aPlayers locations loaded");

        Objects.requireNonNull(getCommand("spec")).setExecutor(new Spec(this, npcManager));
        listernerManager.registerEvents();

        Bukkit.getScheduler().runTaskTimer(this, new SpecTask(this, npcManager), 0L, 5L);

        if (!Update.isUpToDate(this))
            Bukkit.getLogger().log(Level.INFO, getDescription().getPrefix()  + " §cThis plugin got an update please check the new update !");

    }

    @Override
    public void onDisable() {
        try {
            file.delete();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

        Bukkit.getLogger().log(Level.INFO, getDescription().getPrefix()  + " Saving player location");
        if (!fileConfiguration.isConfigurationSection("players"))
            fileConfiguration.createSection("players");

        if (!PlayerUtils.playerOldLocation.isEmpty()) {
            Set<Map.Entry<UUID, Location>> playerEntry = PlayerUtils.playerOldLocation.entrySet();


            for (Map.Entry<UUID, Location> entry : playerEntry) {
                HashMap<String, Object> coordinates = new HashMap<>();
                coordinates.put("x", entry.getValue().getX());
                coordinates.put("y", entry.getValue().getY());
                coordinates.put("z", entry.getValue().getZ());
                coordinates.put("world", Objects.requireNonNull(entry.getValue().getWorld()).getName());

                fileConfiguration.set("players." + entry.getKey().toString() + ".location", coordinates);
            }

        }

        if (!npcManager.getObservers().isEmpty()) {
            for (UUID uuid : npcManager.getObservers())
                fileConfiguration.set("players." + uuid.toString() + ".observer", true);
        }


        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getLogger().log(Level.INFO, getDescription().getPrefix() + " §aPlayer location saved");
        npcManager.detachAll();

    }
}
