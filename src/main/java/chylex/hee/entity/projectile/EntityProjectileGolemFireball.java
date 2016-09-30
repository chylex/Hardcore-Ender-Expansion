package chylex.hee.entity.projectile;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.abstractions.Explosion;
import chylex.hee.system.abstractions.Pos;

public class EntityProjectileGolemFireball extends EntityLargeFireball{
	public EntityProjectileGolemFireball(World world){
		super(world);
		setSize(0.2F, 0.2F);
	}
	
	public EntityProjectileGolemFireball(World world, EntityLivingBase shooter, double x, double y, double z, double xDiff, double yDiff, double zDiff){
		super(world, shooter, xDiff, yDiff, zDiff);
		setPosition(x, y, z);
		setSize(0.2F, 0.2F);
	}

	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			if (mop.entityHit != null)mop.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, shootingEntity), ModCommonProxy.opMobs ? 8F : 4F);
			
			new FieryExplosion(worldObj, posX, posY, posZ, ModCommonProxy.opMobs ? 3F : 2.35F, this, shootingEntity).trigger();
			setDead();
		}
	}
	
	static class FieryExplosion extends Explosion{
		public FieryExplosion(World world, double x, double y, double z, float radius, Entity explodingEntity, Entity cause){
			super(world, x, y, z, radius, explodingEntity, cause);
			honorMobGriefingRule = true;
			spawnFire = true;
		}
		
		@Override
		protected void onSpawnFire(Map<Pos, Block> affected){
			for(Entry<Pos, Block> entry:affected.entrySet()){
				if (entry.getValue().func_149730_j() && entry.getKey().getUp().isAir(world) && calcRand.nextInt(9) == 0){ // OBFUSCATED isOpaque
					entry.getKey().getUp().setBlock(world, Blocks.fire);
				}
			}
		}
	}
}
