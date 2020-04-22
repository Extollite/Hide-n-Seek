package pl.extollite.hidenseek.command.admin;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.permission.Permission;
import cn.nukkit.utils.TextFormat;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.command.CommandManager;
import pl.extollite.hidenseek.data.KitEntry;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.game.Status;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.HashMap;
import java.util.Map;

public class KitCommand extends CommandManager {

    public KitCommand() {
        super("hnskit", "", "/hns kit <Player> <name>");
        String[] kits = new String[HNS.getInstance().getKits().size()];
        int index = 0;
        for(KitEntry entry : HNS.getInstance().getKits()){
            kits[index++] = TextFormat.clean(entry.getName());
        }
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        parameters.put("kits", new CommandParameter[]{
                new CommandParameter("Player", CommandParamType.TARGET, false),
                new CommandParameter("Kit", false, kits)
        });
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.admin.command.kit", null);
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length < 2){
            return false;
        }
        Player p = HNS.getInstance().getServer().getPlayerExact(args[0]);
        if(p == null){
            return false;
        }
        if (!HNS.getInstance().getPlayerManager().hasPlayerData(p)) {
            HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_leave_not_in_game());
            return true;
        } else {
            Game g = HNS.getInstance().getPlayerManager().getData(p).getGame();
            if (g != null && g.getStatus() == Status.BEGINNING && g.getSeekers().contains(p)) {
                for(KitEntry entry : HNS.getInstance().getKits()){
                    if(TextFormat.clean(entry.getName()).equals(args[1]) ){
                        if(entry.getPermission() != null && p.hasPermission(entry.getPermission())){
                            entry.giveKit(p);
                        }
                    }
                }
            }
            else
                return false;
        }
        return true;
    }
}
