package stickoroch.playerfriskrp;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class FriskManager implements Listener {

    public List<FriskSession> sessions = new ArrayList<>();

    public FriskManager(){
        Bukkit.getScheduler().runTaskTimer(PlayerFriskRP.getInstance(), () -> {
            for (FriskSession s : sessions)
            {
                if(s.timer == 0 && !s.friskInProcess){
                    s.friskInProcess = true;
                    s.startFrisk();
                }
                if(!s.friskInProcess) {
                    s.updateTimer();
                    s.timer--;
                }
                if(s.friskInProcess){
                    String a =PlayerFriskRP.getInstance().getConfig().getString("friskProcessSus")
                            .replaceAll("&", "\u00a7");
                    s.suspect.sendActionBar(a);
                }
            }
        }, 20, 20);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        FriskSession x = null;
        for (FriskSession s : sessions) {
            if(s.frisker.equals(e.getPlayer()) || s.suspect.equals(e.getPlayer())){
                x = s;
                break;
            }
        }
        if(x == null) return;
        sessions.remove(x);
        x.friskZalupa();
        x.friskCanceled();
        x.frisker.closeInventory();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        FriskSession x = null;
        for (FriskSession s : sessions) {
            if(s.frisker.equals(e.getPlayer())){
                x = s;
                break;
            }
        }
        if(x == null) return;

        sessions.remove(x);
        x.friskCanceled();
    }

    @EventHandler
    public void onSusOpenInventory(InventoryOpenEvent e){
        for (FriskSession s : sessions) {
            if(s.suspect.equals(e.getPlayer())){
                e.setCancelled(true);
                break;
            }
        }
    }
    @EventHandler
    public void onSusPickupInventory(InventoryPickupItemEvent e){
        for (FriskSession s : sessions) {
            if(s.suspect.getInventory().equals(e.getInventory())){
                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onSusInteractInventory(InventoryClickEvent e){
        for (FriskSession s : sessions) {
            if(s.suspect.equals(e.getWhoClicked())){
                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onSusDropInventory(PlayerDropItemEvent e){
        for (FriskSession s : sessions) {
            if(s.suspect.equals(e.getPlayer())){
                e.setCancelled(true);
                break;
            }
        }
    }

}
