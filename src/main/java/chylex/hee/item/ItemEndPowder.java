package chylex.hee.item;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.fx.FXHelper;
import chylex.hee.entity.item.EntityItemEndPowder;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.logging.Log;

public class ItemEndPowder extends ItemAbstractCustomEntity{
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (isHeld && world.isRemote && entity == HardcoreEnderExpansion.proxy.getClientSidePlayer() && ((EntityPlayer)entity).openContainer == null){
			final byte maxDist = 16;
			Random rand = world.rand;
			PosMutable mpos = new PosMutable();
			
			for(int attempt = 0; attempt < 200; attempt++){
				mpos.set(entity).move(rand.nextInt(maxDist*2+1-maxDist),rand.nextInt(maxDist*2+1-maxDist),rand.nextInt(maxDist*2+1-maxDist));
				
				if (EnhancementRegistry.canEnhanceBlock(mpos.getBlock(world))){
					FXHelper.create("portal").pos(mpos).fluctuatePos(0.65D).fluctuateMotion(0.1D).spawn(world.rand,3); // TODO look at the beauty
				}
			}
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		player.openGui(HardcoreEnderExpansion.instance,4,world,0,-1,0);
		return is;
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		Pos pos = Pos.at(x,y,z);
		
		if (EnhancementRegistry.canEnhanceBlock(pos.getBlock(world))){
			if (!(pos.getTileEntity(world) instanceof IEnhanceableTile)){
				Item prevItem = Item.getItemFromBlock(pos.getBlock(world));
				Item newItem = EnhancementRegistry.getItemTransformation(prevItem);
				
				if (newItem instanceof ItemBlock){
					pos.setBlock(world,((ItemBlock)newItem).field_150939_a);
					
					if (!(pos.getTileEntity(world) instanceof IEnhanceableTile)){
						Log.reportedError("Failed converting $0 to enhanceable tile ($1 <-> $2)!",prevItem,newItem,pos.getTileEntity(world));
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
