package fr.fr_phonix.specmode.listeners;

import fr.fr_phonix.specmode.npc.NPCManager;
import fr.fr_phonix.specmode.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

    private NPCManager npcManager;

    public GUIListener(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(PlayerUtils.GUI_TITLE)) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            if (event.getCurrentItem().hasItemMeta()) {
               if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Show spectators")) {
                   npcManager.attachToNPC(player);
                   event.getWhoClicked().sendMessage("§6[SpecMode] §aSpectator are now visible");

                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Hide spectators")) {
                   npcManager.detachToNPC(player);
                   event.getWhoClicked().sendMessage("§6[SpecMode] §cSpectator are now hide");
                }
            }
            PlayerUtils.openGUI(player, npcManager.isObserver(player));
        }
    }
}
