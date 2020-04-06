package fr.fr_phonix.specmode.npc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class NPCManager {

    private Map<UUID, NPC> npcList = new HashMap<>();
    private ArrayList<UUID> observers = new ArrayList<>();

    public NPCManager(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Set<Map.Entry<UUID, NPC>> entrySet = npcList.entrySet();
            for (Map.Entry<UUID, NPC> entry : entrySet) {
                entry.getValue().setLocation(Bukkit.getPlayer(entry.getKey()).getLocation());
            }
        }, 1L, 0L);
    }

    public void createNPC(Player player) {
        NPC npc = new NPC(player.getLocation(), player.getName());
        npcList.put(player.getUniqueId(), npc);
        for (UUID uuid : observers) {
            if (!uuid.equals(player.getUniqueId()))
                npc.attach(Bukkit.getPlayer(uuid));
        }
    }

    public void removeNPC(Player player) {
        npcList.get(player.getUniqueId()).detachAll();
        npcList.remove(player.getUniqueId());
    }

    public void attachToNPC(Player player) {
        NPC tmp = npcList.getOrDefault(player.getUniqueId(), null);
        observers.add(player.getUniqueId());

        for (NPC npc : npcList.values()) {
            if (!npc.equals(tmp))
                npc.attach(player);
        }
    }

    public void detachToNPC(Player player) {
        observers.remove(player.getUniqueId());
        for (NPC npc : npcList.values()) {
            npc.detach(player);
        }
    }

    public void detachAll() {
        ArrayList<UUID> uuids = (ArrayList<UUID>) observers.clone();
        for (UUID uuid : uuids) {
            detachToNPC(Bukkit.getPlayer(uuid));
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
