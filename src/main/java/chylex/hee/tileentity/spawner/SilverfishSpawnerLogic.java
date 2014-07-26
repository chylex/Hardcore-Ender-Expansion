package chylex.hee.tileentity.spawner;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SilverfishSpawnerLogic extends CustomSpawnerLogic{
	public SilverfishSpawnerLogic(TileEntityCustomSpawner spawnerTile){
		super(spawnerTile);
		this.minSpawnDelay = 65;
		this.maxSpawnDelay = 115;
		this.spawnRange = 5;
		this.spawnCount = 2;
		this.maxNearbyEntities = 6;
		this.activatingRangeFromPlayer = 127;
	}
	
	@Override
	public void updateSpawner(){
		if (!isActivated())return;

		World world = getSpawnerWorld();
		
		if (world.isRemote){
			double particleX = getSpawnerX()+world.rand.nextFloat(),
				   particleY = getSpawnerY()+world.rand.nextFloat(),
				   particleZ = getSpawnerZ()+world.rand.nextFloat();
			
			world.spawnParticle("smoke",particleX,particleY,particleZ,0D,0D,0D);
			world.spawnParticle("flame",particleX,particleY,particleZ,0D,0D,0D);

			if (spawnDelay > 0)--spawnDelay;

			field_98284_d = field_98287_c;
			field_98287_c = (field_98287_c+(1000F/(spawnDelay+200F)))%360D;
		}
		else{
			if (spawnDelay == -1)resetTimer();
			if (spawnDelay > 0){
				--spawnDelay;
				return;
			}

			boolean flag = false;

			for(int attempt = 0,i = 0,spx = getSpawnerX(),spy = getSpawnerY(),spz = getSpawnerZ(); attempt < 10 && i < spawnCount; ++attempt){
				EntityLiving entity = createMob(world);
				if (entity == null)return;

				int nearbyEntityAmount = world.getEntitiesWithinAABB(entity.getClass(),AxisAlignedBB.getBoundingBox(spx,spy,spz,spx+1,spy+6,spz+1).expand(spawnRange*2D,0.5D,spawnRange*2D)).size();
				if (nearbyEntityAmount >= maxNearbyEntities || world.getEntitiesWithinAABB(entity.getClass(),AxisAlignedBB.getBoundingBox(spx,spy,spz,spx+1,spy+6,spz+1).expand(40D,30D,40D)).size() > 35){
					resetTimer();
					return;
				}

				double xx = spx+(world.rand.nextDouble()-world.rand.nextDouble())*spawnRange,
					   zz = spz+(world.rand.nextDouble()-world.rand.nextDouble())*spawnRange;
				
				for(int yy = 0; yy <= 6; yy++){
					entity.setLocationAndAngles(xx,spy+yy,zz,world.rand.nextFloat()*360F,0F);
	
					if (entity.worldObj.checkNoEntityCollision(entity.boundingBox) && entity.worldObj.getCollidingBoundingBoxes(entity,entity.boundingBox).isEmpty()){
						func_98265_a(entity); // OBFUSCATED spawn entity

						world.playAuxSFX(2004,spx,spy,spz,0);
						entity.spawnExplosionParticle();
						
						++i;
						flag = true;
						break;
					}
				}
			}

			if (flag)resetTimer();
		}
	}
	
	@Override
	public void onBlockBreak(){
		World world = getSpawnerWorld();
		Random rand = world.rand;

		for(int attempt = 0,found = 0,targ = 4+rand.nextInt(4),xx,yy,zz; attempt < 400 && found < targ; attempt++){
			xx = getSpawnerX()+rand.nextInt(10)-5;
			yy = getSpawnerY()+1-rand.nextInt(5);
			zz = getSpawnerZ()+rand.nextInt(10)-5;
			
			if (world.getBlock(xx,yy,zz) == Blocks.stonebrick){
				world.setBlockToAir(xx,yy,zz);
				world.playAuxSFX(xx,yy,zz,2001,Block.getIdFromBlock(Blocks.stonebrick));
				
				EntitySilverfish silverfish = new EntitySilverfish(world);
				silverfish.setLocationAndAngles(xx+0.5D,yy+0.5D,zz+0.5D,rand.nextFloat()*360F,0F);
				world.spawnEntityInWorld(silverfish);
				++found;
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Entity func_98281_h(){
		if (entityCache == null)entityCache = func_98265_a(createMob(null)); // OBFUSCATED spawn entity
		return entityCache;
	}
	
	private EntitySilverfish createMob(World world){
		return new EntitySilverfish(world);
	}
}
