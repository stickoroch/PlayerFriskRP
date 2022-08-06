package stickoroch.playerfriskrp;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandFrisk implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command,String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player frisker = (Player) sender;
        double d = PlayerFriskRP.getInstance().getConfig().getDouble("friskDistance");
        List<String> nearPlayers = frisker.getNearbyEntities(d, 0.7, d)
                .stream().filter(x -> x instanceof Player && !x.getName().equals(frisker.getName()))
                .map(x -> x.getName()).collect(Collectors.toList());

        if (args.length == 0) {
            if (nearPlayers.size() == 0) {
                String[] ms = PlayerFriskRP.getInstance().getConfig().getString("friskRadiusClear").
                        replaceAll("&", "\u00a7").split("!n");
                for (String m : ms) {
                    frisker.sendMessage(m);
                }
            } else if(nearPlayers.size() == 1){
                Player  suspect = Bukkit.getPlayer(nearPlayers.get(0));
                boolean a = isPlayerInFrisk(suspect);

                if(!a){
                    PlayerFriskRP.getFriskManager().sessions.add(new FriskSession(frisker, suspect));
                }else{
                    frisker.sendActionBar(PlayerFriskRP.getInstance().getConfig().getString("suspectInProcess").replace('&', '\u00a7'));
                }
            } else{


                String[] a = PlayerFriskRP.getInstance().getConfig().getString("playerForFriskTitle")
                        .replaceAll("&", "\u00a7").split("!n");
                for(String b : a){
                    frisker.sendMessage(b);
                }
            for (String np : nearPlayers) {
                //if (isPlayerInFrisk(Bukkit.getPlayer(np))) continue;
                TextComponent cc = new TextComponent(PlayerFriskRP.getInstance().getConfig().getString("friskMenu")
                        .replaceAll("&", "\u00a7").replaceAll("%player%", np));
                cc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/frisk " + np));
                cc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(PlayerFriskRP.getInstance().getConfig()
                        .getString("hoverMessages").replaceAll("&", "\u00a7").replaceAll("%player%", np)).create()));

                frisker.sendMessage(cc);
                }
            }
            return true;
        }

        Player suspect = Bukkit.getPlayer(args[0]);

        if(suspect == null){
            frisker.sendMessage(PlayerFriskRP.getInstance().getConfig()
                    .getString("susIsNotOnline").replaceAll("&", "\u00a7"));
            return  true;
        }

        if(suspect.equals(frisker)){
            frisker.sendMessage(PlayerFriskRP.getInstance().getConfig().getString("uContFriskUs")
                    .replaceAll("&", "\u00a7"));
            return  true;
        }

        boolean a = isPlayerInFrisk(suspect);

        if(!a){
            PlayerFriskRP.getFriskManager().sessions.add(new FriskSession(frisker, suspect));
        }else{
           frisker.sendActionBar(PlayerFriskRP.getInstance().getConfig().getString("suspectInProcess").replace('&', '\u00a7'));
        }


        return true;
    }

    public boolean isPlayerInFrisk(Player p){
        for (FriskSession s : PlayerFriskRP.getFriskManager().sessions) {
            if(s.suspect.equals(p) || s.frisker.equals(p)) return true;
        }
            return false;
    }
}
