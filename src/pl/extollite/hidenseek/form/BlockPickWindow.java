package pl.extollite.hidenseek.form;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.Map;

public class BlockPickWindow extends FormWindowSimple {

    public BlockPickWindow(Game game, Player player) {
        super(HNSUtils.colorize(HNS.getInstance().getLanguage().getBlock_menu_title()), HNSUtils.colorize(HNS.getInstance().getLanguage().getBlock_menu_content()));
        for(Map.Entry<String, Block> entry : game.getMap().getMapBlocks().entrySet()) {
            this.addButton(new ElementButton(entry.getValue().getName(), new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, entry.getKey())));
        }
    }
}
