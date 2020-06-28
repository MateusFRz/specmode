package fr.fr_phonix.specmode.npc;

import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class NPCManager {

    private Map<UUID, NPC> npcList = new HashMap<>();
    private ArrayList<UUID> observers = new ArrayList<>();
    private Plugin plugin;

    public NPCManager(Plugin plugin) {
        this.plugin = plugin;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Set<Map.Entry<UUID, NPC>> entrySet = npcList.entrySet();
            for (Map.Entry<UUID, NPC> entry : entrySet) {
                entry.getValue().setLocation(Bukkit.getPlayer(entry.getKey()).getLocation());
            }
        }, 1L, 0L);
    }

    public void createNPC(Player player) {
        Property property = ((CraftPlayer) player).getProfile().getProperties().get("textures").iterator().next();
        NPC npc = new NPC(player.getLocation(), player.getName(), property.getValue(), property.getSignature());
        npcList.put(player.getUniqueId(), npc);
        for (UUID uuid : observers) {
            if (!uuid.equals(player.getUniqueId())) {
                npc.attach(uuid);
                if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline())
                    Bukkit.getPlayer(uuid).hidePlayer(plugin, player);

            }
        }
    }

    public void removeNPC(UUID player) {
        npcList.get(player).detachAll();
        npcList.remove(player);
        observers.forEach(observer -> {
            if (Bukkit.getPlayer(observer) != null && Bukkit.getPlayer(observer).isOnline())
                Bukkit.getPlayer(observer).showPlayer(plugin, Bukkit.getPlayer(player));
        });
    }

    public void attach(UUID player) {
        observers.add(player);
        showAllNPCToPlayer(player);
    }

    public void detach(UUID player) {
        observers.remove(player);
        hideAllNPCToPlayer(player);
    }

    public void detachAll() {
        ArrayList<UUID> uuids = (ArrayList<UUID>) observers.clone();
        for (UUID uuid : uuids) {
            detach(uuid);
        }
    }

    public void showAllNPCToPlayer(UUID player) {
        NPC tmp = npcList.getOrDefault(player, null);
        for (NPC npc : npcList.values()) {
            if (!npc.equals(tmp))
                npc.attach(player);
        }
    }

    public void hideAllNPCToPlayer(UUID player) {
        for (NPC npc : npcList.values()) {
            npc.detach(player);
        }
    }

    public NPC getNPC(Player player) {
        return npcList.get(player.getUniqueId());
    }

    public boolean hasNPC(Player player) {
        return npcList.containsKey(player.getUniqueId());
    }

    public boolean isObserver(Player player) {
        return observers.contains(player.getUniqueId());
    }

    public List<UUID> getObservers() {
        return observers;
    }
}
