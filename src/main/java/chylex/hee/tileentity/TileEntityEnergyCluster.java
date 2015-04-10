package chylex.hee.tileentity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockEnergyCluster;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.mechanics.energy.EnergyClusterData;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C10ParticleEnergyTransfer;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.ColorUtil;
import chylex.hee.system.util.MathUtil;

public class TileEntityEnergyCluster extends TileEntityAbstractSynchronized{
	public final EnergyClusterData data;
	public boolean shouldNotExplode = false;
	private boolean shouldBeDestroyedSilently = false;
	private byte[] colRgb;
	private BlockPosM cachedCoords = new BlockPosM(0,-1,0);
	
	public TileEntityEnergyCluster(){
		data = new EnergyClusterData();
	}
	
	public TileEntityEnergyCluster(World world){
		this();
		float[] rgb = ColorUtil.hsvToRgb(world.rand.nextFloat(),0.5F,0.65F);
		colRgb = new byte[]{ (byte)(Math.floor(rgb[0]*255F)-128), (byte)(Math.floor(rgb[1]*255F)-128), (byte)(Math.floor(rgb[2]*255F)-128) };
	}

	@Override
	public void updateEntity(){
		if (shouldBeDestroyedSilently){
			shouldNotExplode = true;
			BlockPosM.tmp(xCoord,yCoord,zCoord).setAir(worldObj);
			return;
		}
		
		if (!worldObj.isRemote){
			if (cachedCoords.y == -1){
				data.generate(worldObj,xCoord,zCoord);
				cachedCoords = new BlockPosM(xCoord,yCoord,zCoord);
				synchronize();
			}
			else if (cachedCoords.x != xCoord || cachedCoords.y != yCoord || cachedCoords.z != zCoord){
				BlockEnergyCluster.destroyCluster(this);
				return;
			}
			
			data.update(this);
		}
		else{
			if (worldObj.rand.nextInt(5) == 0)HardcoreEnderExpansion.fx.energyCluster(this);
		}
		
		shouldNotExplode = false;
	}
	
	public float addEnergy(float amount, TileEntityAbstractEnergyInventory tile){
		if (data.getEnergyLevel() < data.getMaxEnergyLevel())PacketPipeline.sendToAllAround(this,64D,new C10ParticleEnergyTransfer(tile,this));
		
		float left = data.addEnergy(amount);
		if (!MathUtil.floatEquals(left,amount))synchronize();
		return left;
	}
	
	public float drainEnergy(float amount, TileEntityAbstractEnergyInventory tile){
		if (data.getEnergyLevel() >= EnergyChunkData.minSignificantEnergy)PacketPipeline.sendToAllAround(this,64D,new C10ParticleEnergyTransfer(tile,this));
		
		float left = data.drainEnergy(amount);
		if (!MathUtil.floatEquals(left,amount))synchronize();
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
		nbt.setByteArray("col",colRgb);
		nbt.setLong("loc",cachedCoords.toLong());
		data.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readTileFromNBT(NBTTagCompound nbt){
		colRgb = nbt.getByteArray("col");
		cachedCoords = BlockPosM.fromNBT(nbt,"loc");
		data.readFromNBT(nbt);
		
		if (colRgb.length == 0)shouldBeDestroyedSilently = true;
	}
}
