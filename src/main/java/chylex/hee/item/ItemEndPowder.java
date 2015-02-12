package chylex.hee.item;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.item.EntityItemEndPowder;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;

public class ItemEndPowder extends ItemAbstractCustomEntity{
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		player.openGui(HardcoreEnderExpansion.instance,4,world,0,-1,0);
		return is;
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (EnhancementHandler.canEnhanceBlock(world.getBlock(x,y,z)) && world.getTileEntity(x,y,z) instanceof IEnhanceableTile){
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
