package chylex.hee.tileentity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C10ParticleEnergyTransfer;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.system.util.MathUtil;

public abstract class TileEntityAbstractEnergyInventory extends TileEntityAbstractInventory{
	private static final byte[] chunkOffX = new byte[]{ -1, -1, -1, 0, 0, 0, 1, 1, 1 },
								chunkOffZ = new byte[]{ -1, 0, 1, -1, 0, 1, -1, 0, 1 };
	
	private byte drainTimer;
	private float energyLeft;
	private boolean draining;
	
	protected abstract byte getDrainTimer();
	protected abstract float getDrainAmount();
	protected abstract boolean isWorking();
	protected abstract void onWork();
	
	@Override
	public void updateEntity(){
		if (isWorking()){
			draining = true;
			if (MathUtil.floatEquals(energyLeft,0F))onWork();
		}
		else draining = false;
		
		if (draining && (drainTimer == 0 || --drainTimer == 0)){
			Stopwatch.timeAverage("TileEntityAbstractEnergyInventory - drain",30);
			
			float drain = MathUtil.floatEquals(energyLeft,0F) ? getDrainAmount() : energyLeft;
			
			if (getWorldObj().provider.dimensionId == 1){
				float newDrain = WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords(worldObj,xCoord,zCoord,true).drainEnergy(drain);
				if (!MathUtil.floatEquals(newDrain,drain))PacketPipeline.sendToAllAround(this,64D,new C10ParticleEnergyTransfer(this,xCoord+0.5D,yCoord+68D,zCoord+0.5D,(byte)80,(byte)80,(byte)80));
				drain = newDrain;
			}
			
			if (drain > EnergyChunkData.minSignificantEnergy){
				List<TileEntityEnergyCluster> clusters = new ArrayList<>();
				int chunkX = xCoord>>4, chunkZ = zCoord>>4;
				
				for(int a = 0; a < 9; a++){
					Map<ChunkPosition,TileEntity> tiles = worldObj.getChunkFromBlockCoords(chunkX+chunkOffX[a],chunkZ+chunkOffZ[a]).chunkTileEntityMap;
					
					for(Entry<ChunkPosition,TileEntity> entry:tiles.entrySet()){
						ChunkPosition pos = entry.getKey();
						
						if (entry.getValue().getClass() == TileEntityEnergyCluster.class && MathUtil.distance(pos.chunkPosX-xCoord,pos.chunkPosY-yCoord,pos.chunkPosZ-zCoord) <= 16D){
							clusters.add((TileEntityEnergyCluster)entry.getValue());
						}
					}
				}
				
				if (!clusters.isEmpty()){
					Collections.shuffle(clusters);
					
					for(Iterator<TileEntityEnergyCluster> iter = clusters.iterator(); iter.hasNext();){
						if ((drain = iter.next().drainEnergy(drain,this)) <= EnergyChunkData.minSignificantEnergy)break;
					}
				}
			}
			
			drainTimer = getDrainTimer();
			if (drain > EnergyChunkData.minSignificantEnergy)energyLeft = drain;
			
			Stopwatch.finish("TileEntityAbstractEnergyInventory - drain");
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setByte("drainTim",drainTimer);
		if (!MathUtil.floatEquals(energyLeft,0F))nbt.setFloat("engLeft",energyLeft);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		drainTimer = nbt.getByte("drainTim");
		energyLeft = nbt.getFloat("engLeft");
	}
}
