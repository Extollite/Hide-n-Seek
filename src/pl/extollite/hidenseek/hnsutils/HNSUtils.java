package pl.extollite.hidenseek.hnsutils;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import pl.extollite.hidenseek.HNS;

import java.util.*;

public class HNSUtils {

    public static void sendMessage(CommandSender sender, String message){
        sender.sendMessage(TextFormat.colorize('&', HNS.getInstance().getLanguage().getPrefix()+message));
    }

    public static void sendTip(Player player, String message){
       player.sendTip(TextFormat.colorize('&', message));
    }

    public static void sendActionBar(Player player, String message){
        player.sendActionBar(TextFormat.colorize('&', message));
    }

    public static void sendTitle(Player player, String message){
        player.sendTitle(TextFormat.colorize('&', message), "", 5, 10, 5);
    }

    public static String colorize(String message){
        return TextFormat.colorize('&', message);
    }

    public static void broadcast(String message, Collection<Player> recipants){
        HNS.getInstance().getServer().broadcastMessage(TextFormat.colorize('&', HNS.getInstance().getLanguage().getPrefix()+message), recipants);
    }

    public static String translateStop(List<String> win) {
        StringBuilder bc = null;
        int count = 0;
        for (String s : win) {
            count++;
            if (count == 1) bc = new StringBuilder(s);
            else if (count == win.size()) {
                if(bc == null)
                    continue;
                bc.append(", and ").append(s);
            } else {
                if(bc == null)
                    continue;
                bc.append(", ").append(s);
            }
        }
        if (bc != null)
            return bc.toString();
        else
            return "No one";
    }

    public static List<String> convertUUIDListToStringList(List<UUID> uuids) {
        List<String> winners = new ArrayList<>();
        for (UUID uuid : uuids) {
            Optional<Player> playerOptional = HNS.getInstance().getServer().getPlayer(uuid);
            playerOptional.ifPresent(player -> winners.add(player.getName()));
        }
        return winners;
    }
}
