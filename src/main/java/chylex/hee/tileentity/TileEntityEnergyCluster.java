package chylex.hee.tileentity;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.mechanics.misc.EnergyClusterData;
import chylex.hee.system.util.ColorUtil;

public class TileEntityEnergyCluster extends TileEntityAbstractSynchronized{
	private static final Random rand = new Random();

	public final EnergyClusterData data;
	private float[] colRgb;
	public boolean shouldNotExplode = false;
	private byte observationTimer = 0;
	
	public TileEntityEnergyCluster(){
		data = new EnergyClusterData(rand);
		colRgb = ColorUtil.hsvToRgb(rand.nextFloat(),0.5F,0.65F);
	}

	@Override
	public void updateEntity(){
		if (!worldObj.isRemote){
			data.update(this);
			
			if (++observationTimer > 120){
				observationTimer = 0;
				for(EntityPlayer observer:ObservationUtil.getAllObservers(worldObj,xCoord+0.5D,yCoord+0.5D,zCoord+0.5D,6D)){
					if (rand.nextBoolean() && rand.nextBoolean())KnowledgeRegistrations.SPECTRAL_WAND.tryUnlockFragment(observer,0.3F,new short[]{ 1,2 });
					else{
						boolean hasWand = observer.inventory.getCurrentItem() != null && observer.inventory.getCurrentItem().getItem() == ItemList.spectral_wand;
						KnowledgeRegistrations.ENERGY_CLUSTER.tryUnlockFragment(observer,hasWand ? 0.34F : 0.22F,new short[]{ 0,1,2,5,6 });
					}
				}
			}
		}
		else{
			if (rand.nextInt(5) == 0)HardcoreEnderExpansion.fx.energyCluster(this);
		}
		
		shouldNotExplode = false;
	}
	
	public void onAbsorbed(EntityPlayer player, ItemStack is){
		if (!worldObj.isRemote)return;
		for(int a = 0; a < 26; a++){
			HardcoreEnderExpansion.fx.energyClusterMoving(worldObj,xCoord+0.5D+rand(0.1D),yCoord+0.5D+rand(0.1D),zCoord+0.5D+rand(0.1D),rand(0.5D),rand(0.25D),rand(0.5D),colRgb[0],colRgb[1],colRgb[2]);
		}
	}
	
	/**
	 * Helper method that returns random number between -1 and 1 multiplied by number provided.
	 */
	private double rand(double mp){
		return (rand.nextDouble()-rand.nextDouble())*mp;
	}
	
	public boolean tryDecreaseClusterSize(){
		if (data.removeEnergyUnit()){
			synchronize();
			return true;
		}
		else return false;
	}
	
	public float[] getColor(){
		return colRgb;
	}
	
	@Override
	public NBTTagCompound writeTileToNBT(NBTTagCompound nbt){
		nbt.setFloat("colRed",colRgb[0]);
		nbt.setFloat("colGreen",colRgb[1]);
		nbt.setFloat("colBlue",colRgb[2]);
		data.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readTileFromNBT(NBTTagCompound nbt){
		colRgb = new float[]{ nbt.getFloat("colRed"), nbt.getFloat("colGreen"), nbt.getFloat("colBlue") };
		data.readFromNBT(nbt);
	}
}
