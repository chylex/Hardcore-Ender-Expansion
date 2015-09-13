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
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.ColorUtil;

public class TileEntityEnergyCluster extends TileEntityAbstractSynchronized{
	private EnergyClusterData data;
	private Pos cachedCoords;
	private byte[] colRgb;
	public boolean shouldNotExplode = false;
	
	public void generate(EnergyClusterGenerator generator, Random rand){
		data = generator.generate(rand);
		cachedCoords = Pos.at(xCoord,yCoord,zCoord);
		colRgb = ArrayUtils.toPrimitive(Arrays.stream(ArrayUtils.toObject(ColorUtil.hsvToRgb(rand.nextFloat(),0.5F,0.65F))).map(color -> (byte)(Math.floor(color*255F)-128)).toArray(Byte[]::new));
		synchronize();
	}

	@Override
	public void updateEntity(){
		if (data == null || cachedCoords == null){
			shouldNotExplode = true;
			Pos.at(xCoord,yCoord,zCoord).setAir(worldObj);
			return;
		}
		
		if (!worldObj.isRemote){
			if (cachedCoords.getX() != xCoord || cachedCoords.getY() != yCoord || cachedCoords.getZ() != zCoord)BlockEnergyCluster.destroyCluster(this);
			else if (data.update(this))synchronize();
		}
		else if (worldObj.rand.nextInt(5) == 0)HardcoreEnderExpansion.fx.energyCluster(this);
		
		shouldNotExplode = false;
	}
	
	public Optional<EnergyClusterData> getData(){
		return Optional.ofNullable(data);
	}
	
	/*public float addEnergy(float amount, TileEntityAbstractEnergyInventory tile){
		if (data.getEnergyLevel() < data.getMaxEnergyLevel())PacketPipeline.sendToAllAround(this,64D,new C10ParticleEnergyTransfer(tile,this));
		
		float left = data.addEnergy(amount);
		if (!MathUtil.floatEquals(left,amount))synchronize();
		return left;
	}
	
	public float drainEnergy(float amount, TileEntityAbstractEnergyInventory tile){
		if (data.getEnergyLevel() >= EnergyValues.min)PacketPipeline.sendToAllAround(this,64D,new C10ParticleEnergyTransfer(tile,this));
		
		float left = data.drainEnergy(amount);
		if (!MathUtil.floatEquals(left,amount))synchronize();
		return left;
	}*/
	
	public float getColor(int index){
		return (colRgb[index]+128F)/255F;
	}
	
	public byte getColorRaw(int index){
		return colRgb[index];
	}
	
	@Override
	public NBTTagCompound writeTileToNBT(NBTTagCompound nbt){
		nbt.setByteArray("col",colRgb);
		nbt.setLong("loc",cachedCoords.toLong());
		data.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readTileFromNBT(NBTTagCompound nbt){
		if ((colRgb = nbt.getByteArray("col")).length != 3)colRgb = new byte[]{ 0, 0, 0 };
		cachedCoords = Pos.at(nbt.getLong("loc"));
		data.readFromNBT(nbt);
	}
}
