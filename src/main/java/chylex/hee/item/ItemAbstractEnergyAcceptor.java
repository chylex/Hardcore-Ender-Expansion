package chylex.hee.item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.fx.FXHelper;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.energy.IEnergyItem;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.nbt.NBT;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public abstract class ItemAbstractEnergyAcceptor extends Item implements IEnergyItem{
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		NBTCompound tag = NBT.item(is, true);
		if (!tag.hasKey("engLoc") || !(entity instanceof EntityPlayer) || world.getTotalWorldTime()%4 != 0)return;
		
		Pos pos = Pos.at(tag.getLong("engLoc"));
		TileEntityEnergyCluster cluster = pos.<TileEntityEnergyCluster>getTileEntity(world);
		
		if (cluster != null && cluster.getData().isPresent()){
			if (!world.isRemote){
				double dist = pos.distance(entity);
				
				if (isHeld && Math.abs(tag.getFloat("engDst")-dist) <= 0.05D && dist <= 5D && canAcceptEnergy(is) && cluster.getData().get().drainUnit()){
					acceptEnergy(is);
					tag.setFloat("engDst", (float)dist);
				}
				else clearEnergyData(tag);
			}
			else{
				FXHelper.create("energy")
				.pos(cluster.xCoord, cluster.yCoord, cluster.zCoord)
				.fluctuatePos(0.1D)
				.fluctuateMotion(0.2D)
				.paramColor(cluster.getColor(0), cluster.getColor(1), cluster.getColor(2))
				.spawn(world.rand, 26);
			}
		}
		else clearEnergyData(tag);
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		Pos pos = Pos.at(x, y, z);
		
		if (pos.getBlock(world) == BlockList.energy_cluster && canAcceptEnergy(is)){
			NBTCompound tag = NBT.item(is, true);
			
			if (tag.hasKey("engLoc"))clearEnergyData(tag);
			else{
				tag.setLong("engLoc", pos.toLong());
				tag.setFloat("engDst", (float)pos.distance(player));
			}
			
			return true;
		}
		else return false;
	}
	
	private static final void clearEnergyData(NBTCompound tag){
		tag.removeTag("engLoc");
		tag.removeTag("engDst");
	}
}
