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
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.system.util.MathUtil;

public abstract class TileEntityAbstractEnergyInventory extends TileEntityAbstractInventory{
	private static final byte[] chunkOffX = new byte[]{ -1, -1, -1, 0, 0, 0, 1, 1, 1 },
								chunkOffZ = new byte[]{ -1, 0, 1, -1, 0, 1, -1, 0, 1 };
	
	private float energyLevel, maxEnergyLevel;
	private byte regenTimer;
	
	public TileEntityAbstractEnergyInventory(){
		maxEnergyLevel = getMaxEnergy();
	}
	
	protected abstract boolean isActive();
	protected abstract void onWork();
	protected abstract float getAbsorbAmountPerTick();
	protected abstract float getDrainAmountPerTick();
	protected abstract float getMaxEnergy();
	
	@Override
	public void updateEntity(){
		float drain = getDrainAmountPerTick();
		
		if (isActive() && energyLevel >= drain){
			energyLevel -= drain;
			onWork();
		}
		
		if (energyLevel < maxEnergyLevel && ++regenTimer >= 15){
			Stopwatch.timeAverage("TileEntityAbstractEnergyInventory - regen",30);
			
			float regenLeft = Math.min(getAbsorbAmountPerTick(),maxEnergyLevel-energyLevel), total = regenLeft;
			
			if (getWorldObj().provider.dimensionId == 1){
				regenLeft = WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords(xCoord,zCoord,true).drainEnergy(regenLeft);
			}
			
			if (regenLeft > 0F){
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
						if ((regenLeft = iter.next().data.drainEnergy(regenLeft)) <= EnergyChunkData.minSignificantEnergy)break;
					}
				}
			}
			
			energyLevel += total-regenLeft;
			regenTimer = 0;
			
			Stopwatch.finish("TileEntityAbstractEnergyInventory - regen");
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setFloat("energyLvl",energyLevel);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		energyLevel = nbt.getFloat("energyLvl");
	}
}
