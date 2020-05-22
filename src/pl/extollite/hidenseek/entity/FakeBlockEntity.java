package pl.extollite.hidenseek.entity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.UpdateBlockPacket;

public class FakeBlockEntity extends EntityFallingBlock {

    private final Player linked;
    private final Block block;
    private final Vector3 position;
    private EntityBlock falling;
    boolean first = true;

    public FakeBlockEntity(FullChunk chunk, CompoundTag nbt, Player player, Block block) {
        super(chunk, nbt);
        this.onGround = true;
        this.setImmobile(true);
        this.linked = player;
        this.block = block;
        this.position = new Vector3(player.getFloorX(), player.getFloorY(), player.getFloorZ());
        this.health = 20;
        this.closed = false;
    }

    @Override
    public void spawnTo(Player player) {
        if(player.equals(linked)){
            CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<DoubleTag>("Pos")
                            .add(new DoubleTag("", linked.getFloorX()+0.5))
                            .add(new DoubleTag("", linked.getFloorY()))
                            .add(new DoubleTag("", linked.getFloorZ()+0.5)))
                    .putList(new ListTag<DoubleTag>("Motion")
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0)))

                    .putList(new ListTag<FloatTag>("Rotation")
                            .add(new FloatTag("", 0))
                            .add(new FloatTag("", 0)))
                    .putInt("TileID", block.getId())
                    .putByte("Data", block.getDamage());

            falling = new EntityBlock(linked.getLevel().getChunk((int) linked.x >> 4, (int) linked.z >> 4), nbt, linked);
            falling.spawnTo(linked);
            return;
        }
        super.spawnTo(player);
        UpdateBlockPacket updateBlock = new UpdateBlockPacket();
        updateBlock.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(block.getId(), block.getDamage());
        updateBlock.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
        updateBlock.x = (int)position.x;
        updateBlock.y = (int)position.y;
        updateBlock.z = (int)position.z;
        player.dataPacket(updateBlock);
    }

    @Override
    protected float getGravity() {
        return 0.00f;
    }

    @Override
    protected float getDrag() {
        return 0.00f;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        return true;
    }

    @Override
    public void despawnFrom(Player player) {
        if(first){
            falling.close();
            first = false;
            return;
        }
        super.despawnFrom(player);
        UpdateBlockPacket updateBlock = new UpdateBlockPacket();
        updateBlock.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(0);
        updateBlock.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
        updateBlock.x = position.getFloorX();
        updateBlock.y = position.getFloorY();
        updateBlock.z = position.getFloorZ();
        player.dataPacket(updateBlock);
    }

}
