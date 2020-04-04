package fr.fr_phonix.specmode.npc;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {

    private Location location;
    private Location oldLocation;
    private String name;
    private GameProfile gameProfile;
    private Object entityPlayer;
    /*private String texture;
    private String signature;*/
    private int entityId;

    private List<UUID> observer = new ArrayList<>();

    public NPC(Location location, String name) {//, String texture, String signature) {
        this.oldLocation = location;
        this.location = location;
        this.name = name;
        /*this.texture = null;
        this.signature = null;*/

        spawn();
    }

    public void setLocation(Location location) {
        this.oldLocation = this.location;
        this.location = location;

        move();
    }


    public void attach(Player player) {
        observer.add(player.getUniqueId());
        teleport();
    }

    public void detach(Player player) {
        observer.remove(player.getUniqueId());
        destroy(player);
    }

    public void detachAll() {
        for (UUID uuid : observer)
            destroy(Bukkit.getPlayer(uuid));
    }

    public void teleport() {
        try {
            entityPlayer.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class)
                    .invoke(entityPlayer, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void spawn() {
        try {
            Object minecraftServer = getCraftBukkitClass("CraftServer").getMethod("getServer").invoke(Bukkit.getServer());
            Object worldServer = getCraftBukkitClass("CraftWorld").getMethod("getHandle").invoke(location.getWorld());

            gameProfile = new GameProfile(UUID.randomUUID(), name);
            //gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

            Constructor<?> entityPlayerConstructor = getNMSClass("EntityPlayer").getDeclaredConstructors()[0];
            Constructor<?> playerInteractManagerConstructor = getNMSClass("PlayerInteractManager").getDeclaredConstructors()[0];

            entityPlayer = entityPlayerConstructor.newInstance(minecraftServer, worldServer, gameProfile, playerInteractManagerConstructor.newInstance(worldServer));
            entityPlayer.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class)
                    .invoke(entityPlayer, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            entityId = (int) getNMSClass("Entity").getMethod("getId").invoke(entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void move() {
        try {

            entityPlayer.getClass().getField("yaw").set(entityPlayer, location.getYaw());
            entityPlayer.getClass().getField("pitch").set(entityPlayer, location.getPitch());

            float yaw = (float) entityPlayer.getClass().getField("yaw").get(entityPlayer);
            float pitch = (float) entityPlayer.getClass().getField("pitch").get(entityPlayer);

            // PacketPlayOutEntityMove
            Constructor<?> packetPlayOutEntityMoveConstructor = getNMSClass("PacketPlayOutEntity$PacketPlayOutRelEntityMoveLook").getConstructor(int.class, short.class, short.class, short.class, byte.class, byte.class, boolean.class);
            Object packetPlayOutEntityMove = packetPlayOutEntityMoveConstructor.newInstance(
                    entityId,
                    (short) ((location.getX() - oldLocation.getX()) * 4096),
                    (short) ((location.getY() - oldLocation.getY()) * 4096),
                    (short) ((location.getZ() - oldLocation.getZ()) * 4096),
                    (byte) (yaw * 256 / 360),
                    (byte) pitch,
                    false);
            sendPacket(packetPlayOutEntityMove);


            // PacketPlayOutEntityHeadRotation
            Constructor<?> packetPlayOutEntityHeadRotationConstructor = getNMSClass("PacketPlayOutEntityHeadRotation").getConstructor(getNMSClass("Entity"), byte.class);
            Object packetPlayOutEntityHeadRotation = packetPlayOutEntityHeadRotationConstructor.newInstance(entityPlayer, (byte) (yaw * 256 / 360));
            sendPacket(packetPlayOutEntityHeadRotation);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void show() {
        try {
            // PacketPlayOutPlayerInfo - Add
            Object addPlayerEnum = getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getField("ADD_PLAYER").get(null);
            Constructor<?> packetPlayOutPlayerInfoConstructor = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"), Class.forName("[Lnet.minecraft.server." + getVersion() + ".EntityPlayer;"));

            Object array = Array.newInstance(getNMSClass("EntityPlayer"), 1);
            Array.set(array, 0, entityPlayer);

            Object packetPlayerOutPlayerInfoAdd = packetPlayOutPlayerInfoConstructor.newInstance(addPlayerEnum, array);
            sendPacket(packetPlayerOutPlayerInfoAdd);

            // PacketPlayOutNamedEntitySpawn
            Constructor<?> packetPlayOutNamedEntitySpawnConstructor = getNMSClass("PacketPlayOutNamedEntitySpawn").getConstructor(getNMSClass("EntityHuman"));
            Object packetPlayOutNamedEntitySpawn = packetPlayOutNamedEntitySpawnConstructor.newInstance(entityPlayer);
            sendPacket(packetPlayOutNamedEntitySpawn);

            // PacketPlayOutPlayerInfo - Remove
            Object removePlayerEnum = getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getField("REMOVE_PLAYER").get(null);

            Object packetPlayerOutPlayerInfoRemove = packetPlayOutPlayerInfoConstructor.newInstance(removePlayerEnum, array);
            sendPacket(packetPlayerOutPlayerInfoRemove);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void destroy(Player player) {
        try {
            int[] id = {entityId};
            Constructor<?> packetPlayOutEntityDestroyConstructor = getNMSClass("PacketPlayOutEntityDestroy").getConstructor(int[].class);
            Object packetPlayOutEntityDestroy = packetPlayOutEntityDestroyConstructor.newInstance(id);

            sendPacket(player, packetPlayOutEntityDestroy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPacket(Object packet) {
        try {
            for (UUID uuid : observer) {
                Object handle = Bukkit.getPlayer(uuid).getClass().getMethod("getHandle").invoke(Bukkit.getPlayer(uuid));
                Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

                playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getVersion() {
        // net.minecraft.server.v1_15_1_R1
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}
