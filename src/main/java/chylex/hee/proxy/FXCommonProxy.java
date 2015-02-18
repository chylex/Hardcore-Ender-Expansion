package chylex.hee.proxy;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.entity.item.EntityItemIgneousRock;
import chylex.hee.entity.item.EntityItemInstabilityOrb;
import chylex.hee.entity.projectile.EntityProjectileCorruptedEnergy;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;
import chylex.hee.mechanics.curse.CurseType;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public class FXCommonProxy{
	// GENERIC
	public void omnipresent(EnumParticleTypes particleType, World world, double x, double y, double z, double motionX, double motionY, double motionZ){}
	public void item(ItemStack is, World world, double x, double y, double z, double motionX, double motionY, double motionZ){}
	public void itemTarget(ItemStack is, World world, double startX, double startY, double startZ, double targetX, double targetY, double targetZ, float speedMultiplier){}
	public void bubble(World world, double x, double y, double z, double motionX, double motionY, double motionZ){}
	public void flame(World world, double x, double y, double z, int maxAge){}
	public void flame(World world, double x, double y, double z, double motionX, double motionY, double motionZ, int maxAge){}
	public void portalBig(World world, double x, double y, double z, double motionX, double motionY, double motionZ){}
	public void portalBig(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float scaleMp){}
	public void portalBig(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float scaleMp, float red, float green, float blue){}
	public void portalOrbiting(World world, double x, double y, double z, double motionY){}
	public void portalColor(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float red, float green, float blue){}
	public void magicCrit(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float red, float green, float blue){}
	public void spell(World world, double x, double y, double z, float red, float green, float blue){}
	public void aura(World world, double x, double y, double z, float red, float green, float blue, int maxAge){}
	public void curse(World world, double x, double y, double z, CurseType type){}
	public void spatialDash(World world, double x, double y, double z){}
	
	// BLOCKS
	public void corruptedEnergy(World world, int x, int y, int z){}
	public void enderGoo(World world, int x, int y, int z){}
	public void altarOrb(World world, double startX, double startY, double startZ, double targetX, double targetY, double targetZ, EssenceType essence){}
	public void energyCluster(TileEntityEnergyCluster cluster){}
	public void energyClusterMoving(World world, double x, double y, double z, double motionX, double motionY, double motionZ, double red, double green, double blue){}
	
	// ENTITIES
	public void altarAura(EntityItemAltar item){}
	public void igneousRockBreak(EntityItemIgneousRock rock){}
	public void instability(EntityItemInstabilityOrb orb){}
	public void spatialDashExplode(EntityProjectileSpatialDash spatialDash){}
	public void corruptedEnergy(EntityProjectileCorruptedEnergy energy){}
}
