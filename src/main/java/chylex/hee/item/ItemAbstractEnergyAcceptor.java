package chylex.hee.item;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockList;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public abstract class ItemAbstractEnergyAcceptor extends Item{
	public abstract boolean canAcceptEnergy(ItemStack is);
	public abstract void onEnergyAccepted(ItemStack is);
	public abstract int getEnergyPerUse(ItemStack is);
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (!canAcceptEnergy(is))return;
		
		NBTTagCompound nbt = ItemUtil.getNBT(is,true);

		if (nbt.hasKey("engDrain") && entity instanceof EntityPlayer){
			boolean stop = false;
			int[] loc = nbt.getIntArray("engDrain");
			byte wait = nbt.getByte("engWait");
			
			if (!world.isRemote && Math.abs(nbt.getFloat("engDist")-MathUtil.distance(loc[0]+0.5D-entity.posX,loc[1]+0.5D-entity.posY,loc[2]+0.5D-entity.posZ)) > 0.05D)stop = true;
			else if (wait > 0)nbt.setByte("engWait",(byte)(wait-1));
			else{
				TileEntity tile = world.getTileEntity(new BlockPos(loc[0],loc[1],loc[2]));
				
				if (tile instanceof TileEntityEnergyCluster){
					TileEntityEnergyCluster cluster = (TileEntityEnergyCluster)tile;
					
					if (cluster.data.drainEnergyUnit()){
						cluster.synchronize();
						
						if (!world.isRemote)onEnergyAccepted(is);
						else{
							Random rand = world.rand;
							
							for(int a = 0; a < 26; a++){
								HardcoreEnderExpansion.fx.energyClusterMoving(world,cluster.xCoord+0.5D+(rand.nextFloat()-0.5D)*0.2D,cluster.yCoord+0.5D+(rand.nextFloat()-0.5D)*0.2D,cluster.zCoord+0.5D+(rand.nextFloat()-0.5D)*0.2D,(rand.nextFloat()-0.5D)*0.4D,(rand.nextFloat()-0.5D)*0.4D,(rand.nextFloat()-0.5D)*0.4D,cluster.getColor(0),cluster.getColor(1),cluster.getColor(2));
							}
						}
					}
					else stop = true;
				}
				else stop = true;
				
				if (!stop)nbt.setByte("engWait",(byte)4);
			}
			
			if (stop){
				nbt.removeTag("engDrain");
				nbt.removeTag("engWait");
				nbt.removeTag("engDist");
			}
		}
		
		if (world.provider.getDimensionId() == 1){
			byte timer = nbt.getByte("engRgnTim");
			
			if (++timer <= 42+world.rand.nextInt(20)){
				nbt.setByte("engRgnTim",timer);
				return;
			}
			else nbt.setByte("engRgnTim",(byte)0);
			
			EnergyChunkData chunk = WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords(world,(int)entity.posX,(int)entity.posZ,true);
			if (chunk.drainEnergyUnit())onEnergyAccepted(is);
		}
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		NBTTagCompound tag = ItemUtil.getNBT(is,true);
		
		if (world.getBlock(x,y,z) == BlockList.energy_cluster && canAcceptEnergy(is)){
			if (tag.hasKey("engDrain")){
				tag.removeTag("engDrain");
				tag.removeTag("engWait");
				tag.removeTag("engDist");
			}
			else if (world.getTileEntity(x,y,z) instanceof TileEntityEnergyCluster){
				tag.setIntArray("engDrain",new int[]{ x, y, z });
				tag.setFloat("engDist",(float)MathUtil.distance(x+0.5D-player.posX,y+0.5D-player.posY,z+0.5D-player.posZ));
			}
			
			return true;
		}
		
		return false;
	}
}
