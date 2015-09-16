package chylex.hee.item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.entity.fx.FXHelper;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.energy.IEnergyItem;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public abstract class ItemAbstractEnergyAcceptor extends Item implements IEnergyItem{
	// TODO
	/*public static void enhanceCapacity(ItemStack is){
		int prev = is.getItem().getMaxDamage(), now = is.getMaxDamage();
		is.setItemDamage(is.getItemDamage()+(now-prev));
	}

	// TODO
	public final int calculateMaxDamage(ItemStack is, Enum capacityEnhancement){
		return EnhancementHandler.hasEnhancement(is,capacityEnhancement) ? MathUtil.ceil(1.5F*super.getMaxDamage(is)) : super.getMaxDamage(is);
	}*/
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		NBTTagCompound nbt = ItemUtil.getTagRoot(is,true);
		if (!nbt.hasKey("engLoc") || !(entity instanceof EntityPlayer) || world.getTotalWorldTime()%4 != 0)return;
		
		Pos pos = Pos.at(nbt.getLong("engLoc"));
		TileEntityEnergyCluster cluster = pos.<TileEntityEnergyCluster>getTileEntity(world);
		
		if (cluster != null && cluster.getData().isPresent()){
			if (!world.isRemote){
				double dist = pos.distance(entity);
				
				if (isHeld && Math.abs(nbt.getFloat("engDst")-dist) <= 0.05D && dist <= 5D && canAcceptEnergy(is) && cluster.getData().get().drainUnit()){
					acceptEnergy(is);
					nbt.setFloat("engDst",(float)dist);
				}
				else clearEnergyData(nbt);
			}
			else{
				FXHelper.create("energy")
				.pos(cluster.xCoord,cluster.yCoord,cluster.zCoord)
				.fluctuatePos(0.1D)
				.fluctuateMotion(0.2D)
				.paramColor(cluster.getColor(0),cluster.getColor(1),cluster.getColor(2))
				.spawn(world.rand,26);
			}
		}
		else clearEnergyData(nbt);
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		Pos pos = Pos.at(x,y,z);
		
		if (pos.getBlock(world) == BlockList.energy_cluster && canAcceptEnergy(is)){
			NBTTagCompound nbt = ItemUtil.getTagRoot(is,true);
			
			if (nbt.hasKey("engLoc"))clearEnergyData(nbt);
			else{
				nbt.setLong("engLoc",pos.toLong());
				nbt.setFloat("engDst",(float)pos.distance(player));
			}
			
			return true;
		}
		else return false;
	}
	
	private static final void clearEnergyData(NBTTagCompound nbt){
		nbt.removeTag("engLoc");
		nbt.removeTag("engDst");
	}
}
