package fr.mateusfrz.specmode.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerUtils {

    @NotNull
    public static Boolean isOnline(Player player) {
        if (player == null) return false;
        return Bukkit.getOnlinePlayers().contains(player);
    }

    @NotNull
    public static Boolean isInTheCube(@NotNull Location location, @NotNull Cube cube) {
        return (location.getX() > cube.getX_min() && location.getX() < cube.getX_max()
                && location.getZ() > cube.getZ_min() && location.getZ() < cube.getZ_max());
    }
}
