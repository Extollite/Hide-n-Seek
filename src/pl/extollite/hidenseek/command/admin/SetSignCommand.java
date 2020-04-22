package pl.extollite.hidenseek.command.admin;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.permission.Permission;
import cn.nukkit.utils.Config;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.command.CommandManager;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;


import java.util.HashMap;
import java.util.Map;

public class SetSignCommand extends CommandManager {

    public SetSignCommand() {
        super("hnsasetsign", "", "/hnsa setsign <arena_name>");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        parameters.put("setsign", new CommandParameter[]{
                new CommandParameter("Arena Name", CommandParamType.STRING, false),
        });
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.admin.command.setsign");
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
            Block b = p.getTargetBlock(6);
            if(!(b instanceof BlockSignPost))
                return false;
            g.setLobbyBlock((BlockEntitySign) p.getLevel().getBlockEntity(b));
            Config arenas = new Config(HNS.getInstance().getDataFolder()+"/arenas.yml", Config.YAML);
            arenas.set("arenas."+g.getName()+".lobbysign.level", b.getLevel().getName());
            arenas.set("arenas."+g.getName()+".lobbysign.x", b.getX());
            arenas.set("arenas."+g.getName()+".lobbysign.y", b.getY());
            arenas.set("arenas."+g.getName()+".lobbysign.z", b.getZ());
            arenas.save();
            HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_sign_set());
            HNS.checkGame(g, p);
        }
        return true;
    }
}
