package chylex.hee.item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public abstract class ItemAbstractEnergyAcceptor extends Item{
	protected abstract boolean canAcceptEnergy(ItemStack is);
	protected abstract void onEnergyAccepted(ItemStack is);
	protected abstract int getEnergyPerUse(ItemStack is);
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (!canAcceptEnergy(is))return;
		
		if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
		
		if (is.stackTagCompound.getBoolean("engDrain") && entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)entity;
			
			int[] loc = is.stackTagCompound.getIntArray("engDrain");
			TileEntity tile = world.getTileEntity(loc[0],loc[1],loc[2]);
			
			if (tile instanceof TileEntityEnergyCluster && !(Math.abs(player.lastTickPosX-player.posX) > 0.0001D || Math.abs(player.lastTickPosZ-player.posZ) > 0.0001D)){
				TileEntityEnergyCluster cluster = (TileEntityEnergyCluster)tile;
				
				if (cluster.data.drainEnergyUnit()){
					cluster.onAbsorbed(player,is);
					if (!world.isRemote)onEnergyAccepted(is);
				}
				else is.stackTagCompound.removeTag("engDrain");
			}
			else is.stackTagCompound.removeTag("engDrain");
		}
		
		if (world.provider.dimensionId == 1){ 
			byte timer = is.stackTagCompound.getByte("engRgnTim");
			
			if (++timer > 7)is.stackTagCompound.setByte("engRgnTim",timer);
			else return;
			
			EnergyChunkData chunk = WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords((int)entity.posX,(int)entity.posZ,true);
			if (chunk.drainEnergyUnit())onEnergyAccepted(is);
		}
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
		
		if (world.getBlock(x,y,z) == BlockList.energy_cluster && canAcceptEnergy(is)){
			if (is.stackTagCompound.hasKey("engDrain"))is.stackTagCompound.removeTag("engDrain");
			else if (world.getTileEntity(x,y,z) instanceof TileEntityEnergyCluster)is.stackTagCompound.setIntArray("engDrain",new int[]{ x, y, z });
			
			return true;
		}
		
		return false;
	}
}
