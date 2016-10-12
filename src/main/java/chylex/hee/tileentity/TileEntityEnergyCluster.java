package chylex.hee.tileentity;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockEnergyCluster;
import chylex.hee.mechanics.energy.EnergyClusterData;
import chylex.hee.mechanics.energy.EnergyClusterGenerator;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C10ParticleEnergyTransfer;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.ColorUtil;
import chylex.hee.tileentity.base.TileEntityAbstractEnergyInventory;
import chylex.hee.tileentity.base.TileEntityAbstractSynchronized;

public class TileEntityEnergyCluster extends TileEntityAbstractSynchronized{
	private EnergyClusterData data;
	private Pos cachedCoords;
	private byte[] colRgb;
	private boolean firstTick;
	public boolean shouldNotExplode = false;
	
	public void generate(EnergyClusterGenerator generator, Random rand){
		data = generator.generate(rand);
		cachedCoords = Pos.at(xCoord, yCoord, zCoord);
		setColor(ColorUtil.hsvToRgb(rand.nextFloat(), 0.5F, 0.65F));
		synchronize();
	}
	
	public void setColor(float[] rgb){
		colRgb = ArrayUtils.toPrimitive(Arrays.stream(ArrayUtils.toObject(rgb)).map(color -> (byte)(Math.floor(color*255F)-128)).toArray(Byte[]::new));
	}

	@Override
	public void updateEntity(){
		if (data == null || cachedCoords == null){
			shouldNotExplode = true;
			Pos.at(xCoord, yCoord, zCoord).setAir(worldObj);
			return;
		}
		
		if (!worldObj.isRemote){
			if (cachedCoords.getX() != xCoord || cachedCoords.getY() != yCoord || cachedCoords.getZ() != zCoord)BlockEnergyCluster.destroyCluster(this);
			else data.update(this);
		}
		else{
			if (!firstTick){
				for(int a = 0; a < 12; a++)HardcoreEnderExpansion.fx.energyCluster(this);
				firstTick = true;
			}
			
			if (worldObj.rand.nextInt(4) == 0)HardcoreEnderExpansion.fx.energyCluster(this);
		}
		
		shouldNotExplode = false;
	}
	
	public Optional<EnergyClusterData> getData(){
		return Optional.ofNullable(data);
	}
	
	public int tryDrainEnergy(final int units, TileEntityAbstractEnergyInventory tile){
		int left = units;
		
		while(left > 0){
			if (data.drainUnit())--left;
			else break;
		}
		
		if (left != units)PacketPipeline.sendToAllAround(this, 64D, new C10ParticleEnergyTransfer(tile, this));
		return left;
	}
	
	public float getColor(int index){
		return (colRgb[index]+128F)/255F;
	}
	
	public byte getColorRaw(int index){
		return colRgb[index];
	}
	
	@Override
	public NBTTagCompound writeTileToNBT(NBTTagCompound nbt){
		nbt.setByteArray("col", colRgb);
		if (cachedCoords != null)nbt.setLong("loc", cachedCoords.toLong());
		if (data != null)data.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readTileFromNBT(NBTTagCompound nbt){
		if ((colRgb = nbt.getByteArray("col")).length != 3)colRgb = new byte[]{ 0, 0, 0 };
		cachedCoords = nbt.hasKey("loc") ? Pos.at(nbt.getLong("loc")) : null;
		(data = new EnergyClusterData()).readFromNBT(nbt);
	}
}
