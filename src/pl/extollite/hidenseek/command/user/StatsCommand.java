package pl.extollite.hidenseek.command.user;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.permission.Permission;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.command.CommandManager;
import pl.extollite.hidenseek.form.StatsWindow;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.HashMap;
import java.util.Map;

public class StatsCommand extends CommandManager {

    public StatsCommand() {
        super("hnsstats", "", "/hns stats");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.command.stats", null, Permission.DEFAULT_TRUE);
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.showFormWindow(new StatsWindow(p));
        }
        return true;
    }
}
