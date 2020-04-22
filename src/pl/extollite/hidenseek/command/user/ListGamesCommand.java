package pl.extollite.hidenseek.command.user;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.permission.Permission;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.command.CommandManager;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.HashMap;
import java.util.Map;


public class ListGamesCommand extends CommandManager {

    public ListGamesCommand() {
        super("hnslistgames", "", "/hns listgames");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.command.listgames", null, Permission.DEFAULT_TRUE);
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        HNSUtils.sendMessage(sender, "&6&l Games:");
        for(Game g : HNS.getInstance().getGames())
            HNSUtils.sendMessage(sender, " &4 - &6" + g.getName() + "&4:&6" + g.getStatus().getName());
        return true;
    }
}
