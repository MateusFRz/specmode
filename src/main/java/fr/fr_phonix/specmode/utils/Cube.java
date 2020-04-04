package fr.fr_phonix.specmode.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Cube {

    private final double x_max;
    private final double z_max;
    private final double x_min;
    private final double z_min;

    private final double size;

    public Cube(double x, double z, double size) {
        x_max = x + size;
        z_max = z + size;
        x_min = x - size;
        z_min = z - size;
        this.size = size;
    }

    public Cube(Location location, double size) {
        this(location.getX(), location.getZ(), size);
    }

    public double getX_max() {
        return x_max;
    }

    public double getZ_max() {
        return z_max;
    }

    public double getX_min() {
        return x_min;
    }

    public double getZ_min() {
        return z_min;
    }

    @Override
    public String toString() {
        return "max:" + x_max + " - min:" + x_min + "   |   min:" + z_max + " - max:" + z_min;
    }


    public void draw(Player player) {
        for (double i = 0; i < (2*size); i++) {
            player.spawnParticle(Particle.BARRIER, new Location(player.getWorld(), x_min + i, player.getLocation().getY() + 1, z_max), 1);
            player.spawnParticle(Particle.BARRIER, new Location(player.getWorld(), x_min + i, player.getLocation().getY() + 1, z_min), 1);
            player.spawnParticle(Particle.BARRIER, new Location(player.getWorld(), x_min, player.getLocation().getY() + 1, z_min + i), 1);
            player.spawnParticle(Particle.BARRIER, new Location(player.getWorld(), x_max, player.getLocation().getY() + 1, z_min + i), 1);
        }

    }
}
