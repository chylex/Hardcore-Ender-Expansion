package chylex.hee.world.feature.stronghold.rooms.loot;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.tileentity.TileEntityLootChest;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.util.IStructureTileEntity;
import chylex.hee.world.util.Size;

public abstract class StrongholdRoomRelic extends StrongholdRoom{
	private ItemStack relicItem;
	
	public StrongholdRoomRelic(Size size, @Nullable Facing4[] connectWith){
		super(size, connectWith);
	}
	
	public final StrongholdRoomRelic setRelicItem(ItemStack relicItem){
		this.relicItem = relicItem;
		return this;
	}
	
	protected final IStructureTileEntity getRelicGenerator(){
		return (tile, random) -> ((TileEntityLootChest)tile).getSourceInventory().setInventorySlotContents(14, relicItem);
	}
}
