package chylex.hee.tileentity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockList;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.mechanics.misc.PlayerTransportBeacons;
import chylex.hee.proxy.ModCommonProxy.MessageType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTransportBeacon extends TileEntityAbstractEnergyInventory{
	private boolean hasEnergy, noTampering;
	private int actualX, actualY = -1, actualZ;
	private float beamAngle;
	
	@Override
	public void updateEntity(){
		if (!worldObj.isRemote){
			if (actualY == -1){
				actualX = xCoord;
				actualY = yCoord;
				actualZ = zCoord;
			}
			
			if (xCoord == actualX && yCoord == actualY && zCoord == actualZ && worldObj.provider.dimensionId == 1){
				if (!noTampering){
					noTampering = true;
					worldObj.addBlockEvent(xCoord,yCoord,zCoord,BlockList.transport_beacon,0,1);
				}
			}
			else if (noTampering){
				noTampering = false;
				worldObj.addBlockEvent(xCoord,yCoord,zCoord,BlockList.transport_beacon,0,0);
			}
		}
		
		super.updateEntity();
		
		if (worldObj.isRemote){
			beamAngle += 1.5F;
			
			EntityPlayer player = HardcoreEnderExpansion.proxy.getClientSidePlayer();
			
			if (player.getDistance(xCoord,yCoord,zCoord) < 8D && (Math.abs(player.lastTickPosX-player.posX) > 0.0001D || Math.abs(player.lastTickPosY-player.posY) > 0.0001D || Math.abs(player.lastTickPosZ-player.posZ) > 0.0001D)){
				worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);
			}
		}
	}
	
	public boolean teleportPlayer(EntityPlayer player, int x, int z, PlayerTransportBeacons data){
		if (!hasEnergy || !noTampering)return false;
		
		for(int y = 1; y < player.worldObj.getActualHeight(); y++){
			if (player.worldObj.getBlock(x,y,z) == BlockList.transport_beacon){
				// TODO particles
				if (player.isRiding())player.mountEntity(null);
				player.setPositionAndUpdate(x+0.5D,y+1D,z+0.5D);
				player.fallDistance = 0F;
				hasEnergy = false;
				worldObj.addBlockEvent(xCoord,yCoord,zCoord,BlockList.transport_beacon,1,0);
				return true;
			}
		}
		
		data.removeBeacon(x,z); // beacon is fake or removed
		return false;
	}
	
	@Override
	public boolean receiveClientEvent(int eventId, int eventData){
		HardcoreEnderExpansion.proxy.sendMessage(MessageType.TRANSPORT_BEACON_GUI,new int[]{ xCoord, yCoord, zCoord, eventId, eventData });
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
		worldObj.addBlockEvent(xCoord,yCoord,zCoord,BlockList.transport_beacon,1,1);
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
		nbt.setIntArray("actualPos",new int[]{ actualX, actualY, actualZ });
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		hasEnergy = nbt.getBoolean("hasEng");
		
		int[] actualPos = nbt.getIntArray("actualPos");
		
		if (actualPos.length == 3){
			actualX = actualPos[0];
			actualY = actualPos[1];
			actualZ = actualPos[2];
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
	public int[] getAccessibleSlotsFromSide(int side){
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
