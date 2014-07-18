package chylex.hee.item;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.item.EntityItemEndPowder;

public class ItemEndPowder extends ItemAbstractCustomEntity{
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (player.isSneaking())player.openGui(HardcoreEnderExpansion.instance,4,world,0,0,0);
		return is;
	}
	
	@Override
	protected EntityItem createEntityItem(World world, double x, double y, double z, ItemStack is){
		return new EntityItemEndPowder(world,x,y,z,is);
	}
}
