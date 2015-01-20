package chylex.hee.tileentity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockEnergyCluster;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.mechanics.energy.EnergyClusterData;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C10ParticleEnergyTransfer;
import chylex.hee.system.util.ColorUtil;
import chylex.hee.system.util.MathUtil;

public class TileEntityEnergyCluster extends TileEntityAbstractSynchronized implements IUpdatePlayerListBox{
	public final EnergyClusterData data;
	public boolean shouldNotExplode = false;
	private boolean shouldBeDestroyedSilently = false;
	private byte[] colRgb;
	private long cachedPos = Long.MIN_VALUE;
	
	public TileEntityEnergyCluster(){
		data = new EnergyClusterData();
	}
	
	public TileEntityEnergyCluster(World world){
		this();
		float[] rgb = ColorUtil.hsvToRgb(world.rand.nextFloat(),0.5F,0.65F);
		colRgb = new byte[]{ (byte)(Math.floor(rgb[0]*255F)-128), (byte)(Math.floor(rgb[1]*255F)-128), (byte)(Math.floor(rgb[2]*255F)-128) };
	}

	@Override
	public void update(){
		if (shouldBeDestroyedSilently){
			shouldNotExplode = true;
			worldObj.setBlockToAir(getPos());
			return;
		}
		
		if (!worldObj.isRemote){
			if (cachedPos == Long.MIN_VALUE){
				data.generate(worldObj,getPos().getX(),getPos().getZ());
				cachedPos = getPos().toLong();
				synchronize();
			}
			else if (cachedPos != getPos().toLong()){
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
		nbt.setLong("cpos",cachedPos);
		data.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readTileFromNBT(NBTTagCompound nbt){
		colRgb = nbt.getByteArray("col");
		
		if (nbt.hasKey("cpos"))cachedPos = nbt.getLong("cpos");
		else{
			int[] cached = nbt.getIntArray("loc");
			if (cached.length == 3)cachedPos = new BlockPos(cached[0],cached[1],cached[2]).toLong();
		}
		
		data.readFromNBT(nbt);
		
		if (colRgb.length == 0)shouldBeDestroyedSilently = true;
	}
}
