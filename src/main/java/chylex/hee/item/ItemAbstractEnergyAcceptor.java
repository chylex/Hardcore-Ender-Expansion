package chylex.hee.item;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.causatum.CausatumMeters;
import chylex.hee.mechanics.causatum.CausatumUtils;
import chylex.hee.mechanics.energy.EnergyValues;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public abstract class ItemAbstractEnergyAcceptor extends Item{
	public abstract boolean canAcceptEnergy(ItemStack is);
	public abstract void onEnergyAccepted(ItemStack is);
	public abstract int getEnergyPerUse(ItemStack is);
	protected abstract float getRegenSpeedMultiplier();
	
	public static void enhanceCapacity(ItemStack is){
		int prev = is.getItem().getMaxDamage(), now = is.getMaxDamage();
		is.setItemDamage(is.getItemDamage()+(now-prev));
	}
	
	public final int calculateMaxDamage(ItemStack is, Enum capacityEnhancement){
		return EnhancementHandler.hasEnhancement(is,capacityEnhancement) ? MathUtil.ceil(1.5F*super.getMaxDamage(is)) : super.getMaxDamage(is);
	}
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (!canAcceptEnergy(is))return;
		
		NBTTagCompound nbt = ItemUtil.getTagRoot(is,true);
		
		if (nbt.hasKey("engDrain") && entity instanceof EntityPlayer){
			boolean stop = false;
			Pos loc = Pos.at(nbt.getLong("engDrain"));
			byte wait = nbt.getByte("engWait");
			
			if (!world.isRemote && Math.abs(nbt.getFloat("engDist")-MathUtil.distance(loc.getX()+0.5D-entity.posX,loc.getY()+0.5D-entity.posY,loc.getZ()+0.5D-entity.posZ)) > 0.05D)stop = true;
			else if (wait > 0)nbt.setByte("engWait",(byte)(wait-1));
			else{
				TileEntity tile = loc.getTileEntity(world);
				
				if (tile instanceof TileEntityEnergyCluster){
					TileEntityEnergyCluster cluster = (TileEntityEnergyCluster)tile;
					
					if (cluster.getData().get().drainUnit()){
						cluster.synchronize();
						
						if (!world.isRemote){
							onEnergyAccepted(is);
							CausatumUtils.increase((EntityPlayer)entity,CausatumMeters.END_ENERGY,EnergyValues.unit);
						}
						else{
							Random rand = world.rand;
							
							for(int a = 0; a < 26; a++){
								HardcoreEnderExpansion.fx.global("energy",cluster.xCoord+0.5D+(rand.nextFloat()-0.5D)*0.2D,cluster.yCoord+0.5D+(rand.nextFloat()-0.5D)*0.2D,cluster.zCoord+0.5D+(rand.nextFloat()-0.5D)*0.2D,(rand.nextFloat()-0.5D)*0.4D,(rand.nextFloat()-0.5D)*0.4D,(rand.nextFloat()-0.5D)*0.4D,cluster.getColor(0),cluster.getColor(1),cluster.getColor(2));
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
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		NBTTagCompound nbt = ItemUtil.getTagRoot(is,true);
		
		if (Pos.at(x,y,z).getBlock(world) == BlockList.energy_cluster && canAcceptEnergy(is)){
			if (nbt.hasKey("engDrain")){
				nbt.removeTag("engDrain");
				nbt.removeTag("engWait");
				nbt.removeTag("engDist");
			}
			else if (Pos.at(x,y,z).getTileEntity(world) instanceof TileEntityEnergyCluster){
				nbt.setLong("engDrain",Pos.at(x,y,z).toLong());
				nbt.setFloat("engDist",(float)MathUtil.distance(x+0.5D-player.posX,y+0.5D-player.posY,z+0.5D-player.posZ));
			}
			
			return true;
		}
		
		return false;
	}
	
	public void damageItem(ItemStack is, EntityLivingBase owner){
		is.damageItem(getEnergyPerUse(is),owner);
	}
}
