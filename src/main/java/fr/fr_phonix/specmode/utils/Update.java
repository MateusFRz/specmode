package fr.fr_phonix.specmode.utils;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class Update {

    public static boolean isUpToDate(Plugin plugin) {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=67643").openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext())
                    return plugin.getDescription().getVersion().equals(scanner.next());

            } catch (IOException exception) {
                plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }

            return false;
    }
}
