package chylex.hee.tileentity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockList;
import chylex.hee.entity.fx.FXType;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.mechanics.misc.PlayerTransportBeacons;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.proxy.ModCommonProxy.MessageType;
import chylex.hee.system.util.BlockPosM;

public class TileEntityTransportBeacon extends TileEntityAbstractEnergyInventory{
	private boolean hasEnergy, noTampering;
	private float beamAngle;
	private long cachedPos = Long.MIN_VALUE;
	
	@Override
	public void update(){
		if (!worldObj.isRemote){
			if (cachedPos == Long.MIN_VALUE)cachedPos = getPos().toLong();
			
			if (cachedPos == getPos().toLong() && worldObj.provider.getDimensionId() == 1){
				if (!noTampering){
					noTampering = true;
					worldObj.addBlockEvent(getPos(),BlockList.transport_beacon,0,1);
				}
			}
			else if (noTampering){
				noTampering = false;
				worldObj.addBlockEvent(getPos(),BlockList.transport_beacon,0,0);
			}
		}
		
		super.update();
		
		if (worldObj.isRemote){
			beamAngle += 1.5F;
			
			EntityPlayer player = HardcoreEnderExpansion.proxy.getClientSidePlayer();
			BlockPos pos = getPos();
			
			if (player.getDistance(pos.getX(),pos.getY(),pos.getZ()) < 8D && (Math.abs(player.lastTickPosX-player.posX) > 0.0001D || Math.abs(player.lastTickPosY-player.posY) > 0.0001D || Math.abs(player.lastTickPosZ-player.posZ) > 0.0001D)){
				worldObj.markBlockRangeForRenderUpdate(pos,pos);
			}
		}
	}
	
	public boolean teleportPlayer(EntityPlayer player, int x, int z, PlayerTransportBeacons data){
		if (!hasEnergy || !noTampering)return false;
		
		BlockPosM pos = new BlockPosM(x,0,z);
		
		while(++pos.y <= player.worldObj.getActualHeight()){
			if (pos.getBlock(player.worldObj) == BlockList.transport_beacon){
				if (player.isRiding())player.mountEntity(null);
				player.fallDistance = 0F;
				
				PacketPipeline.sendToAllAround(player,64D,new C21EffectEntity(FXType.Entity.SIMPLE_TELEPORT,player));
				player.setPositionAndUpdate(x+0.5D,pos.y+1D,z+0.5D);
				PacketPipeline.sendToAllAround(player,64D,new C21EffectEntity(FXType.Entity.SIMPLE_TELEPORT,player));
				
				hasEnergy = false;
				worldObj.addBlockEvent(pos,BlockList.transport_beacon,1,0);
				return true;
			}
		}
		
		data.removeBeacon(x,z); // beacon is fake or removed
		return false;
	}
	
	@Override
	public boolean receiveClientEvent(int eventId, int eventData){
		HardcoreEnderExpansion.proxy.sendMessage(MessageType.TRANSPORT_BEACON_GUI,new int[]{ getPos().getX(), getPos().getY(), getPos().getZ(), eventId, eventData });
		return true;
	}
	
	@Override
	protected byte getDrainTimer(){
		return 1;
	}

	@Override
	protected float getDrainAmount(){
		return EnergyChunkData.energyDrainUnit*4F;
	}

	@Override
	protected boolean isWorking(){
		return !hasEnergy;
	}

	@Override
	protected void onWork(){
		hasEnergy = true;
		worldObj.addBlockEvent(getPos(),BlockList.transport_beacon,1,1);
	}

	public float getBeamAngle(){
		return beamAngle;
	}
	
	public boolean hasEnergy(){
		return hasEnergy;
	}
	
	public boolean hasNotBeenTampered(){
		return noTampering;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("hasEng",hasEnergy);
		nbt.setLong("actualPosL",cachedPos);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		hasEnergy = nbt.getBoolean("hasEng");
		
		if (nbt.hasKey("actualPosL"))cachedPos = nbt.getLong("actualPosL");
		else{
			int[] cached = nbt.getIntArray("actualPos");
			if (cached.length == 3)cachedPos = new BlockPos(cached[0],cached[1],cached[2]).toLong();
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared(){
		return 16384D;
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return ArrayUtils.EMPTY_INT_ARRAY;
	}

	@Override
	public int getSizeInventory(){
		return 0;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return false;
	}

	@Override
	protected String getContainerDefaultName(){
		return "";
	}
}
