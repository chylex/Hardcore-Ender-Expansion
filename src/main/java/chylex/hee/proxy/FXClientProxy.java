package chylex.hee.proxy;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.entity.fx.EntityAltarOrbFX;
import chylex.hee.entity.fx.EntityBigPortalFX;
import chylex.hee.entity.fx.EntityCustomBubbleFX;
import chylex.hee.entity.fx.EntityEnderGooFX;
import chylex.hee.entity.fx.EntityEnergyClusterFX;
import chylex.hee.entity.fx.EntityOrbitingPortalFX;
import chylex.hee.entity.fx.EntitySoulCharmFX;
import chylex.hee.entity.fx.behavior.ParticleBehaviorMoveTo;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.entity.item.EntityItemIgneousRock;
import chylex.hee.entity.item.EntityItemInstabilityOrb;
import chylex.hee.entity.mob.EntityMobCorporealMirage;
import chylex.hee.entity.projectile.EntityProjectileCorruptedEnergy;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.curse.CurseType;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.system.logging.Log;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public class FXClientProxy extends FXCommonProxy{
	private static void spawn(EntityFX fx){
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}
	
	/*
	 * GENERIC
	 */
	
	@Override
	public void omnipresent(String particleName, World world, double x, double y, double z, double motionX, double motionY, double motionZ){
		switch(particleName){
			case "smoke": spawn(new EntitySmokeFX(world,x,y,z,motionX,motionY,motionZ)); break;
			case "largesmoke": spawn(new EntitySmokeFX(world,x,y,z,motionX,motionY,motionZ,2.5F)); break;
			case "portal": spawn(new EntityPortalFX(world,x,y,z,motionX,motionY,motionZ)); break;
			default: Log.debug("Particle $0 not found!",particleName);
		}
	}
	
	@Override
	public void item(ItemStack is, World world, double x, double y, double z, double motionX, double motionY, double motionZ){
		spawn(is.getItemSpriteNumber() == 0 ?
			new EntityDiggingFX(world,x,y,z,motionX,motionY,motionZ,Block.getBlockFromItem(is.getItem()),is.getItemDamage()) :
			new EntityBreakingFX(world,x,y,z,motionX,motionY,motionZ,is.getItem(),is.getItemDamage())
		);
	}
	
	@Override
	public void itemTarget(ItemStack is, World world, double startX, double startY, double startZ, final double targetX, final double targetY, final double targetZ, final float speedMultiplier){
		EntityFX fx = (is.getItemSpriteNumber() == 0 ?
			new EntityDiggingFX(world,startX,startY,startZ,0D,0D,0D,Block.getBlockFromItem(is.getItem()),is.getItemDamage()){
				final ParticleBehaviorMoveTo moveBehavior = new ParticleBehaviorMoveTo(this,targetX,targetY,targetZ,speedMultiplier);
				
				@Override
				public void onUpdate(){
					moveBehavior.update(this);
				}
			} :
			new EntityBreakingFX(world,startX,startY,startZ,0D,0D,0D,is.getItem(),is.getItemDamage()){
				final ParticleBehaviorMoveTo moveBehavior = new ParticleBehaviorMoveTo(this,targetX,targetY,targetZ,speedMultiplier);
				
				@Override
				public void onUpdate(){
					moveBehavior.update(this);
				}
			}
		);
		
		fx.noClip = true;
		spawn(fx);
	}

	@Override
	public void bubble(World world, double x, double y, double z, double motionX, double motionY, double motionZ){
		spawn(new EntityCustomBubbleFX(world,x,y,z,motionX,motionY,motionZ));
	}
	
	@Override
	public void flame(World world, double x, double y, double z, final int maxAge){
		flame(world,x,y,z,0D,0D,0D,maxAge);
	}
	
	@Override
	public void flame(World world, double x, double y, double z, double motionX, double motionY, double motionZ, final int maxAge){
		spawn(new EntityFlameFX(world,x,y,z,motionX,motionY,motionZ){{
			particleMaxAge = maxAge;
		}});
	}
	
	@Override
	public void portalBig(World world, double x, double y, double z, double motionX, double motionY, double motionZ){
		spawn(new EntityBigPortalFX(world,x,y,z,motionX,motionY,motionZ));
	}
	
	@Override
	public void portalBig(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float scaleMp){
		spawn(new EntityBigPortalFX(world,x,y,z,motionX,motionY,motionZ,scaleMp));
	}
	
	@Override
	public void portalBig(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float scaleMp, final float red, final float green, final float blue){
		spawn(new EntityBigPortalFX(world,x,y,z,motionX,motionY,motionZ,scaleMp){{
			particleRed = red;
			particleGreen = green;
			particleBlue = blue;
		}});
	}
	
	@Override
	public void portalOrbiting(World world, double x, double y, double z, double motionY){
		spawn(new EntityOrbitingPortalFX(world,x,y,z,motionY));
	}
	
	@Override
	public void portalColor(World world, double x, double y, double z, double motionX, double motionY, double motionZ, final float red, final float green, final float blue){
		spawn(new EntityPortalFX(world,x,y,z,motionX,motionY,motionZ){{
			particleRed = red;
			particleGreen = green;
			particleBlue = blue;
		}});
	}
	
	@Override
	public void magicCrit(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float red, float green, float blue){
		EntityCritFX fx = new EntityCritFX(world,x,y,z,motionX,motionY,motionZ);
		fx.setRBGColorF(red,green,blue);
		fx.nextTextureIndexX();
		spawn(fx);
	}
	
	@Override
	public void spell(World world, double x, double y, double z, float red, float green, float blue){
		EntitySpellParticleFX fx = new EntitySpellParticleFX(world,x,y,z,0D,0D,0D);
		fx.setRBGColorF(red,green,blue);
		spawn(fx);
	}
	
	@Override
	public void aura(World world, double x, double y, double z, final float red, final float green, final float blue, final int maxAge){
		spawn(new EntityAuraFX(world,x,y,z,0D,0D,0D){{
			particleMaxAge = maxAge;
			particleRed = red;
			particleGreen = green;
			particleBlue = blue;
		}});
	}
	
	@Override
	public void curse(World world, double x, double y, double z, CurseType type){
		Random rand = world.rand;
		int color = rand.nextInt(5) <= 2 ? type.getColor(0) : type.getColor(1);
		portalBig(world,x,y,z,(rand.nextDouble()-0.5D)*rand.nextDouble()*0.03D,(rand.nextDouble()-0.5D)*rand.nextDouble()*0.03D,(rand.nextDouble()-0.5D)*rand.nextDouble()*0.03D,0.1F+rand.nextFloat()*0.1F,((color>>16)&255)/255F,((color>>8)&255)/255F,(color&255)/255F);
	}
	
	/*
	 * BLOCKS
	 */
	
	@Override
	public void corruptedEnergy(World world, int x, int y, int z){
		Random rand = world.rand;
		double motX = (rand.nextDouble()-rand.nextDouble())*0.2D, motY = (rand.nextDouble()-rand.nextDouble())*0.2D, motZ = (rand.nextDouble()-rand.nextDouble())*0.2D;
		spawn(new EntityBigPortalFX(world,x+0.5D,y+0.5D,z+0.5D,motX,motY,motZ,rand.nextBoolean() ? 1F : 1.5F+rand.nextFloat()));
	}
	
	@Override
	public void enderGoo(World world, int x, int y, int z){
		Random rand = world.rand;
		spawn(new EntityEnderGooFX(world,x+rand.nextDouble(),y+rand.nextDouble()*0.5D,z+rand.nextDouble(),(rand.nextDouble()-0.5D)*0.1D,0.12D,(rand.nextDouble()-0.5D)*0.1D));
	}
	
	@Override
	public void altarOrb(World world, double startX, double startY, double startZ, double targetX, double targetY, double targetZ, EssenceType essence){
		spawn(new EntityAltarOrbFX(world,startX,startY,startZ,targetX,targetY,targetZ,essence));
	}
	
	@Override
	public void soulCharm(World world, int x, int y, int z){
		spawn(new EntitySoulCharmFX(world,x+0.25D+world.rand.nextDouble()*0.5D,y+0.1D+world.rand.nextDouble(),z+0.25D+world.rand.nextDouble()*0.5D));
	}
	
	@Override
	public void energyCluster(TileEntityEnergyCluster cluster){
		Random rand = cluster.getWorldObj().rand;
		spawn(new EntityEnergyClusterFX(cluster.getWorldObj(),cluster.xCoord+0.5D+(rand.nextDouble()-rand.nextDouble())*0.1D,cluster.yCoord+0.5D+(rand.nextDouble()-rand.nextDouble())*0.1D,cluster.zCoord+0.5D+(rand.nextDouble()-rand.nextDouble())*0.1D,cluster.getColor(0),cluster.getColor(1),cluster.getColor(2),cluster.data));
	}
	
	@Override
	public void energyClusterMoving(World world, double x, double y, double z, double motionX, double motionY, double motionZ, double red, double green, double blue){
		spawn(new EntityEnergyClusterFX(world,x,y,z,red,green,blue,motionX,motionY,motionZ));
	}

	@Override
	public void soulCharmMoving(World world, double startX, double startY, double startZ, double targetX, double targetY, double targetZ){
		spawn(new EntitySoulCharmFX(world,startX,startY,startZ,targetX,targetY,targetZ));
	}
	
	/*
	 * ENTITIES
	 */

	@Override
	public void altarAura(EntityItemAltar item){
		Random rand = item.worldObj.rand;
		
		spawn(new EntityAuraFX(item.worldObj,item.posX+rand.nextDouble()*0.4D-0.2D,item.posY+rand.nextDouble()*0.4D,item.posZ+rand.nextDouble()*0.4D-0.2D,0D,rand.nextDouble()*0.05D+0.05D,0D){{
			setParticleTextureIndex(82);
			particleRed = particleGreen = particleBlue = 1F;
			particleScale = rand.nextFloat()*0.2F+0.5F;
			particleMaxAge = 10+rand.nextInt(3);
			motionX = motionZ = 0D;
		}});
	}

	@Override
	public void igneousRockBreak(EntityItemIgneousRock rock){
		Random rand = rock.worldObj.rand;
		
		spawn(new EntityBreakingFX(rock.worldObj,rock.posX+rand.nextDouble()*0.4D-0.2D,rock.posY+0.2D+rand.nextDouble()*0.2D,rock.posZ+rand.nextDouble()*0.4D-0.2D,0D,-0.015D,0D,ItemList.igneous_rock,0){{
			noClip = true;
			motionX = motionZ = 0D;
			motionY = -0.015D;
		}});
	}
	
	@Override
	public void instability(EntityItemInstabilityOrb orb){
		Random rand = orb.worldObj.rand;
		double motX = (rand.nextDouble()-0.5D)*0.25D, motY = 0.12D+rand.nextGaussian()*0.02D+rand.nextGaussian()*0.02D, motZ = (rand.nextDouble()-0.5D)*0.25D;

		if (rand.nextInt(4) == 0){
			spawn(new EntityFX(orb.worldObj,orb.posX,orb.posY+0.25D,orb.posZ,motX,motY,motZ){{
				setParticleTextureIndex(160+rand.nextInt(8));
				particleRed = particleBlue = 0F;
				particleGreen = 0.75F;
				particleGravity = Blocks.snow.blockParticleGravity/3F;
				particleScale /= 1.75F;
				particleMaxAge = 80+rand.nextInt(30);
			}});
		}
		else{
			spawn(new EntityBreakingFX(orb.worldObj,orb.posX,orb.posY+0.25D,orb.posZ,motX,motY,motZ,ItemList.instability_orb,0){{
				particleGravity /= 3F;
				particleScale /= 2.5F;
				particleMaxAge = 80+rand.nextInt(30);
			}});
		}
	}
	
	@Override
	public void spatialDash(EntityProjectileSpatialDash spatialDash){
		Random rand = spatialDash.worldObj.rand;
		
		spawn(new EntityBigPortalFX(spatialDash.worldObj,spatialDash.posX+(rand.nextDouble()-rand.nextDouble())*0.3D,spatialDash.posY+(rand.nextDouble()-rand.nextDouble())*0.3D,spatialDash.posZ+(rand.nextDouble()-rand.nextDouble())*0.3D,(rand.nextDouble()-0.5D)*0.01D,(rand.nextDouble()-0.5D)*0.01D,(rand.nextDouble()-0.5D)*0.01D,0.1F));
		
		for(int a = 0; a < 3; a++){
			double motX = (rand.nextDouble()-rand.nextDouble())*0.0002D, motY = (rand.nextDouble()-rand.nextDouble())*0.0002D, motZ = (rand.nextDouble()-rand.nextDouble())*0.0002D;
			spawn(new EntityEnergyClusterFX(spatialDash.worldObj,spatialDash.posX,spatialDash.posY,spatialDash.posZ,0.6D,0.2D,1D,motX,motY,motZ,0.015F));
		}
	}

	@Override
	public void spatialDashExplode(EntityProjectileSpatialDash spatialDash){
		Random rand = spatialDash.worldObj.rand;
		spawn(new EntityBigPortalFX(spatialDash.worldObj,spatialDash.posX+(rand.nextDouble()-rand.nextDouble())*0.1D,spatialDash.posY+(rand.nextDouble()-rand.nextDouble())*0.1D,spatialDash.posZ+(rand.nextDouble()-rand.nextDouble())*0.1D,(rand.nextDouble()-0.5D)*0.3D,(rand.nextDouble()-0.5D)*0.3D,(rand.nextDouble()-0.5D)*0.3D,1F));
	}
	
	@Override
	public void mirageHurt(EntityMobCorporealMirage mirage){
		Random rand = mirage.worldObj.rand;
		double realY = mirage.posY+MathHelper.cos((float)mirage.angle)*0.15D;
		spawn(new EntitySoulCharmFX(mirage.worldObj,mirage.posX+(rand.nextDouble()-rand.nextDouble())*mirage.width*0.2D,realY-0.1D+rand.nextDouble()*mirage.height*1.1D,mirage.posZ+(rand.nextDouble()-rand.nextDouble())*mirage.width*0.2D,rand.nextFloat()*0.04D));
	}
	
	@Override
	public void mirageDeath(EntityMobCorporealMirage mirage){
		Random rand = mirage.worldObj.rand;
		double realY = mirage.posY+MathHelper.cos((float)mirage.angle)*0.15D;
		spawn(new EntitySoulCharmFX(mirage.worldObj,mirage.posX+(rand.nextDouble()-rand.nextDouble())*mirage.width*0.8D,realY-0.1D+rand.nextDouble()*mirage.height*1.1D,mirage.posZ+(rand.nextDouble()-rand.nextDouble())*mirage.width*0.8D,rand.nextFloat()*0.05D));
	}
	
	@Override
	public void corruptedEnergy(EntityProjectileCorruptedEnergy energy){
		Random rand = energy.worldObj.rand;
		double motX = (rand.nextDouble()-rand.nextDouble())*0.2D, motY = (rand.nextDouble()-rand.nextDouble())*0.2D, motZ = (rand.nextDouble()-rand.nextDouble())*0.2D;
		spawn(new EntityBigPortalFX(energy.worldObj,energy.posX+(rand.nextDouble()-0.5D)*0.2D,energy.posY+0.5D+(rand.nextDouble()-0.5D)*0.2D,energy.posZ+(rand.nextDouble()-0.5D)*0.2D,motX,motY,motZ,rand.nextBoolean() ? 0.3F : 0.4F+rand.nextFloat()*0.4F));
	}
}
