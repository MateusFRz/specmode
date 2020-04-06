package fr.fr_phonix.specmode.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class PlayerUtils {

    private static Inventory GUI;
    public static final String GUI_TITLE = ChatColor.RED + "" + ChatColor.BOLD + "Specmode options";
    public static HashMap<UUID, Location> playerOldLocation = new HashMap<>();

    @NotNull
    public static boolean isOnline(Player player) {
        if (player == null) return false;
        return Bukkit.getOnlinePlayers().contains(player);
    }

    @NotNull
    public static boolean isInTheCube(@NotNull Location location, @NotNull Cube cube) {
        return (location.getX() > cube.getX_min() && location.getX() < cube.getX_max()
                && location.getZ() > cube.getZ_min() && location.getZ() < cube.getZ_max());
    }

    public static void openGUI(Player player, boolean observer) {
        if (GUI == null) createInventory();

        if (observer)
            GUI.setItem(11, ItemUtils.item(ChatColor.RED + "Hide spectators", Material.ENDER_PEARL, 1));
        else
            GUI.setItem(11, ItemUtils.item(ChatColor.GREEN + "Show spectators", Material.ENDER_EYE, 1));

        player.openInventory(GUI);
    }

    private static void createInventory() {
        GUI = Bukkit.createInventory(null, 27, GUI_TITLE);
        GUI.setItem(0, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(1, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(2, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(3, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(4, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(5, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(6, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(7, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(8, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(9, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(10, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));

        GUI.setItem(12, ItemUtils.item(ChatColor.RED + "Not yet", Material.BARRIER, 1));
        GUI.setItem(13, ItemUtils.item(ChatColor.RED + "Not yet", Material.BARRIER, 1));
        GUI.setItem(14, ItemUtils.item(ChatColor.RED + "Not yet", Material.BARRIER, 1));
        GUI.setItem(15, ItemUtils.item(ChatColor.RED + "Not yet", Material.BARRIER, 1));

        GUI.setItem(16, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(17, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(18, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(19, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(20, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(21, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(22, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(23, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(24, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(25, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));
        GUI.setItem(26, ItemUtils.item(" ", Material.GRAY_STAINED_GLASS_PANE, 1));

    }
}
