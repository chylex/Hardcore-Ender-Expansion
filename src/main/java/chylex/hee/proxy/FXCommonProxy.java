package chylex.hee.proxy;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.entity.item.EntityItemIgneousRock;
import chylex.hee.entity.item.EntityItemInstabilityOrb;
import chylex.hee.entity.mob.EntityMobCorporealMirage;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public class FXCommonProxy{
	// GENERIC
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
	
	// BLOCKS
	public void corruptedEnergy(World world, int x, int y, int z){}
	public void enderGoo(World world, int x, int y, int z){}
	public void altarOrb(World world, double startX, double startY, double startZ, double targetX, double targetY, double targetZ, EssenceType essence){}
	public void soulCharm(World world, int x, int y, int z){}
	public void soulCharmMoving(World world, double startX, double startY, double startZ, double targetX, double targetY, double targetZ){}
	public void energyCluster(TileEntityEnergyCluster cluster){}
	public void energyClusterMoving(World world, double x, double y, double z, double motionX, double motionY, double motionZ, double red, double green, double blue){}
	
	// ENTITIES
	public void altarAura(EntityItemAltar item){}
	public void igneousRockBreak(EntityItemIgneousRock rock){}
	public void instability(EntityItemInstabilityOrb orb){}
	public void spatialDash(EntityProjectileSpatialDash spatialDash){}
	public void spatialDashExplode(EntityProjectileSpatialDash spatialDash){}
	public void mirageHurt(EntityMobCorporealMirage mirage){}
	public void mirageDeath(EntityMobCorporealMirage mirage){}
}
