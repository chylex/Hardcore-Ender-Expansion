package chylex.hee.tileentity;
import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockEnergyCluster;
import chylex.hee.mechanics.energy.EnergyClusterData;
import chylex.hee.system.util.ColorUtil;

public class TileEntityEnergyCluster extends TileEntityAbstractSynchronized{
	public final EnergyClusterData data;
	public boolean shouldNotExplode = false;
	private boolean shouldBeDestroyedSilently = false;
	private byte[] colRgb;
	private int[] cachedCoords = ArrayUtils.EMPTY_INT_ARRAY;
	
	public TileEntityEnergyCluster(){
		data = new EnergyClusterData();
	}
	
	public TileEntityEnergyCluster(World world){
		this();
		float[] rgb = ColorUtil.hsvToRgb(world.rand.nextFloat(),0.5F,0.65F);
		colRgb = new byte[]{ (byte)(Math.floor(rgb[0])-128), (byte)(Math.floor(rgb[1])-128), (byte)(Math.floor(rgb[2])-128) };
	}

	@Override
	public void updateEntity(){
		if (!worldObj.isRemote){
			if (shouldBeDestroyedSilently){
				shouldNotExplode = true;
				worldObj.setBlockToAir(xCoord,yCoord,zCoord);
			}
			
			if (cachedCoords.length == 0){
				data.generate(worldObj.rand,xCoord,zCoord);
				cachedCoords = new int[]{ xCoord, yCoord, zCoord };
			}
			else if (cachedCoords[0] != xCoord || cachedCoords[1] != yCoord || cachedCoords[2] != zCoord){
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
	
	public void onAbsorbed(EntityPlayer player, ItemStack is){
		if (!worldObj.isRemote)return;
		
		for(int a = 0; a < 26; a++){
			HardcoreEnderExpansion.fx.energyClusterMoving(worldObj,xCoord+0.5D+rand(0.1D),yCoord+0.5D+rand(0.1D),zCoord+0.5D+rand(0.1D),rand(0.5D),rand(0.25D),rand(0.5D),getColor(0),getColor(1),getColor(2));
		}
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
		nbt.setIntArray("loc",cachedCoords);
		data.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readTileFromNBT(NBTTagCompound nbt){
		if (!nbt.hasKey("col"))shouldBeDestroyedSilently = true;
		colRgb = nbt.getByteArray("col");
		cachedCoords = nbt.getIntArray("loc");
		data.readFromNBT(nbt);
	}
	
	/**
	 * Helper method that returns random number between -1 and 1 multiplied by number provided.
	 */
	private double rand(double mp){
		return (worldObj.rand.nextDouble()-worldObj.rand.nextDouble())*mp;
	}
}
