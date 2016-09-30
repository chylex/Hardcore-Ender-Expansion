package chylex.hee.tileentity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.enhancements.EnhancementList;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.mechanics.enhancements.types.TNTEnhancements;

public class TileEntityEnhancedTNT extends TileEntity implements IEnhanceableTile<TNTEnhancements>{
	private final EnhancementList<TNTEnhancements> enhancements = new EnhancementList<>(TNTEnhancements.class);
	
	@Override
	public Item getEnhancementItem(){
		return Item.getItemFromBlock(BlockList.enhanced_tnt);
	}
	
	@Override
	public EnhancementList<TNTEnhancements> getEnhancements(){
		return enhancements;
	}
	
	@Override
	public boolean canUpdate(){
		return false;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setString("enhancements2", enhancements.serialize());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		enhancements.deserialize(nbt.getString("enhancements2"));
	}
}
