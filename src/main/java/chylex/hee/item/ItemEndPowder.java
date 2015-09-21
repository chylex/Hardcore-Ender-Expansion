package chylex.hee.item;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.item.EntityItemEndPowder;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.logging.Log;

public class ItemEndPowder extends ItemAbstractCustomEntity{
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		player.openGui(HardcoreEnderExpansion.instance,4,world,0,-1,0);
		return is;
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		Pos pos = Pos.at(x,y,z);
		
		if (EnhancementHandler.canEnhanceBlock(pos.getBlock(world))){
			if (!(pos.getTileEntity(world) instanceof IEnhanceableTile)){
				ItemStack prevIS = new ItemStack(pos.getBlock(world));
				Item newItem = EnhancementHandler.getEnhancementTransformation(prevIS);
				
				if (newItem instanceof ItemBlock){
					pos.setBlock(world,((ItemBlock)newItem).field_150939_a);
					
					if (!(pos.getTileEntity(world) instanceof IEnhanceableTile)){
						Log.error("Failed converting $0 to enhanceable tile ($1 <-> $2)!",prevIS,newItem,pos.getTileEntity(world));
						return false;
					}
				}
			}
			
			player.openGui(HardcoreEnderExpansion.instance,4,world,x,y,z);
			return true;
		}
		
		return false;
	}
	
	@Override
	protected EntityItem createEntityItem(World world, double x, double y, double z, ItemStack is){
		return new EntityItemEndPowder(world,x,y,z,is);
	}
}
