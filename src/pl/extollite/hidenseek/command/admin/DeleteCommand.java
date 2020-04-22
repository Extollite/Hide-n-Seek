package pl.extollite.hidenseek.command.admin;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.permission.Permission;
import cn.nukkit.utils.Config;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.command.CommandManager;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.game.Status;
import pl.extollite.hidenseek.hnsutils.HNSUtils;


import java.util.HashMap;
import java.util.Map;

public class DeleteCommand extends CommandManager {

    public DeleteCommand() {
        super("hnsadelete", "", "/hnsa delete <arena_name>");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        parameters.put("delete", new CommandParameter[]{
                new CommandParameter("Arena Name", CommandParamType.STRING, false),
        });
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.admin.command.delete");
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length < 1)
            return false;
        if(sender instanceof Player){
            Player p = (Player)sender;
            Game g = HNS.getInstance().getGame(args[0]);
            if(g == null){
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_noexist());
                return false;
            }
            try{
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_attempt().replace("%arena%", g.getName()));
                if (g.getStatus() == Status.BEGINNING || g.getStatus() == Status.RUNNING) {
                    HNSUtils.sendMessage(sender, "  &7- &cGame running! &aStopping..");
                    g.stop(true);
                }
                if(!g.getHiders().isEmpty()){
                    HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_kicking());
                    for(Player player : g.getHiders()){
                        g.leave(player, false);
                    }
                }
                if(!g.getSeekers().isEmpty()){
                    HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_kicking());
                    for(Player player : g.getSeekers()){
                        g.leave(player, false);
                    }
                }
                Config arenas = new Config(HNS.getInstance().getDataFolder()+"/arenas.yml", Config.YAML);
                arenas.set("arenas."+g.getName(), null);
                arenas.save();
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_deleted().replace("%arena%", g.getName()));
                HNS.getInstance().getGames().remove(g);
            }
            catch (Exception e){
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_failed());
            }
        }
        return true;
    }
}
