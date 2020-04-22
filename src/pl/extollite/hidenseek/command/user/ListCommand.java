package pl.extollite.hidenseek.command.user;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.permission.Permission;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.command.CommandManager;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ListCommand extends CommandManager {

    public ListCommand() {
        super("hnslist", "", "/hns list");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.command.list", null, Permission.DEFAULT_TRUE);
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!HNS.getInstance().getPlayerManager().hasPlayerData(p)) {
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_leave_not_in_game());
                return true;
            } else {
                Game g = HNS.getInstance().getPlayerManager().getData(p).getGame();
                if (g != null) {
                    StringBuilder playerNames = new StringBuilder();
                    for (Player player : g.getHiders()) {
                        playerNames.append("&6, &c").append(player.getName());
                    }
                    for (Player player : g.getSeekers()) {
                        playerNames.append("&6, &c").append(player.getName());
                    }
                    HNSUtils.sendMessage(p, "&6Players:" + playerNames.substring(3));
                }
            }
        }
        return true;
    }
}
