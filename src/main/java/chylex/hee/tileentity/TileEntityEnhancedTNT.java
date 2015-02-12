package chylex.hee.tileentity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.block.BlockList;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.mechanics.enhancements.types.TNTEnhancements;

public class TileEntityEnhancedTNT extends TileEntity implements IEnhanceableTile{
	private List<Enum> tntEnhancements = new ArrayList<>();
	
	@Override
	public void loadEnhancementsFromItem(ItemStack is){
		for(Enum enhancement:EnhancementHandler.getEnhancements(is))tntEnhancements.add(enhancement);
	}
	
	@Override
	public List<Enum> getEnhancements(){
		return tntEnhancements;
	}
	
	@Override
	public ItemStack createEnhancementDisplay(){
		return EnhancementHandler.addEnhancements(new ItemStack(BlockList.enhanced_tnt),tntEnhancements);
	}
	
	@Override
	public boolean canUpdate(){
		return false;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setString("enhancements",EnhancementEnumHelper.serialize(tntEnhancements));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		tntEnhancements = EnhancementEnumHelper.deserialize(nbt.getString("enhancements"),TNTEnhancements.class);
	}
}
