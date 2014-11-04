package chylex.hee.item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public abstract class ItemAbstractEnergyAcceptor extends Item{
	protected abstract boolean canAcceptEnergy(ItemStack is);
	protected abstract void onEnergyAccepted(ItemStack is);
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (world.provider.dimensionId != 1 || !canAcceptEnergy(is))return;
		
		if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
		
		byte timer = is.stackTagCompound.getByte("energyRgnTim");
		
		if (++timer > 7)is.stackTagCompound.setByte("energyRgnTim",timer);
		else return;
		
		EnergyChunkData chunk = WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords((int)entity.posX,(int)entity.posZ,true);
		if (chunk.drainEnergyUnit())onEnergyAccepted(is);
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (world.getBlock(x,y,z) == BlockList.energy_cluster && canAcceptEnergy(is)){
			TileEntityEnergyCluster tile = (TileEntityEnergyCluster)world.getTileEntity(x,y,z);

			if (tile != null && tile.data.drainEnergyUnit()){ // TODO click once and drain in update
				tile.onAbsorbed(player,is);
				if (!world.isRemote)onEnergyAccepted(is);
			}
			
			return true;
		}
		else return false;
	}
}
