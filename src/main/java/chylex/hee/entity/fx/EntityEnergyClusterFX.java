package chylex.hee.entity.fx;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.mechanics.energy.EnergyClusterData;
import chylex.hee.mechanics.energy.EnergyClusterHealth;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityEnergyClusterFX extends EntitySoulCharmFX{
	private EntityEnergyClusterFX(World world, double x, double y, double z, double red, double green, double blue){
		super(world,x,y,z,0D,0D,0D);
		motionX = motionY = motionZ = particleAlpha = 0;motionX = 0.0001;

		particleRed = (float)red;
		particleGreen = (float)green;
		particleBlue = (float)blue;
	}
	
	public EntityEnergyClusterFX(World world, double x, double y, double z, double red, double green, double blue, EnergyClusterData data){
		this(world,x,y,z,red,green,blue);
		
		float energyAmt = data.getEnergyLevel();
		particleScale = 0.05F+rand.nextFloat()*0.14F+0.025F*energyAmt;
		
		if (rand.nextInt(5)+1 < data.getHealthStatus().ordinal()){
			float mp = 1F-0.2F*((float)data.getHealthStatus().ordinal()/EnergyClusterHealth.values.length);
			particleRed *= mp;
			particleGreen *= mp;
			particleBlue *= mp;
		}
	}
	
	public EntityEnergyClusterFX(World world, double x, double y, double z, double red, double green, double blue, double motionX, double motionY, double motionZ){
		this(world,x,y,z,red,green,blue);
		
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		this.particleScale = 0.04F+rand.nextFloat()*0.1F;
	}
	
	public EntityEnergyClusterFX(World world, double x, double y, double z, double red, double green, double blue, double motionX, double motionY, double motionZ, float scale){
		this(world,x,y,z,red,green,blue,motionX,motionY,motionZ);

		this.particleScale = scale;
	}

	@Override
	protected void handleMotion(){
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
	}
	
	@Override
	protected Block getTargetBlock(){
		return BlockList.energy_cluster;
	}
}
