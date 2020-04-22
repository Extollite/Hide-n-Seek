package pl.extollite.hidenseek.form;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.data.MapEntry;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.Map;

public class MapPickWindow extends FormWindowSimple {

    public MapPickWindow(Game game, Player player) {
        super(HNSUtils.colorize(HNS.getInstance().getLanguage().getMap_menu_title()), HNSUtils.colorize(HNS.getInstance().getLanguage().getMap_menu_content()));
        for(MapEntry entry : game.getMaps()) {
            Integer votes = game.getVotes().get(entry.getName());
            votes = (votes == null ? 0 : votes);
            this.addButton(new ElementButton(HNSUtils.colorize(entry.getName())+"\n"+HNSUtils.colorize(HNS.getInstance().getLanguage().getMap_menu_votes())+votes));
        }
    }
}
