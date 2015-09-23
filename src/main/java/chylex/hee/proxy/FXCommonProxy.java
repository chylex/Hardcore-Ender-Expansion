package chylex.hee.proxy;
import net.minecraft.item.ItemStack;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.entity.item.EntityItemIgneousRock;
import chylex.hee.entity.item.EntityItemInstabilityOrb;
import chylex.hee.entity.projectile.EntityProjectileCorruptedEnergy;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;
import chylex.hee.mechanics.curse.CurseType;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public class FXCommonProxy{
	// SETTINGS
	public FXCommonProxy reset(){ return this; }
	public FXCommonProxy setRenderDist(double dist){ return this; }
	public FXCommonProxy setOmnipresent(){ return this; }
	public FXCommonProxy setLimiter(){ return this; }
	public FXCommonProxy setNoClip(){ return this; }
	
	// GENERIC
	public void global(String particleName, double x, double y, double z, double motionX, double motionY, double motionZ){}
	public void global(String particleName, double x, double y, double z, double motionX, double motionY, double motionZ, float parameter){}
	public void global(String particleName, double x, double y, double z, double motionX, double motionY, double motionZ, final float red, final float green, final float blue){}
	public void item(ItemStack is, double x, double y, double z, double motionX, double motionY, double motionZ){}
	public void itemTarget(ItemStack is, double startX, double startY, double startZ, double targetX, double targetY, double targetZ, float speedMultiplier){}
	public void flame(double x, double y, double z, int maxAge){}
	public void flame(double x, double y, double z, double motionX, double motionY, double motionZ, int maxAge){}
	public void portalBig(double x, double y, double z, double motionX, double motionY, double motionZ, float scaleMp, float red, float green, float blue){}
	public void portalOrbiting(double x, double y, double z, double motionY){}
	public void aura(double x, double y, double z, float red, float green, float blue, int maxAge){}
	public void curse(double x, double y, double z, CurseType type){}
	public void spatialDash(double x, double y, double z){}
	
	// BLOCKS
	public void corruptedEnergy(int x, int y, int z){}
	public void enderGoo(int x, int y, int z){}
	public void energyCluster(TileEntityEnergyCluster cluster){}
	
	// ENTITIES
	public void altarAura(EntityItemAltar item){}
	public void igneousRockBreak(EntityItemIgneousRock rock){}
	public void instability(EntityItemInstabilityOrb orb){}
	public void spatialDashExplode(EntityProjectileSpatialDash spatialDash){}
	public void corruptedEnergy(EntityProjectileCorruptedEnergy energy){}
}
