package chylex.hee.proxy;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.fx.EntityBigPortalFX;
import chylex.hee.entity.fx.EntityCustomBubbleFX;
import chylex.hee.entity.fx.EntityEnderGooFX;
import chylex.hee.entity.fx.EntityEnergyClusterFX;
import chylex.hee.entity.fx.EntityGlitterFX;
import chylex.hee.entity.fx.EntityOrbitingPortalFX;
import chylex.hee.entity.fx.behavior.ParticleBehaviorMoveTo;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.entity.item.EntityItemIgneousRock;
import chylex.hee.entity.item.EntityItemInstabilityOrb;
import chylex.hee.entity.projectile.EntityProjectileCorruptedEnergy;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.curse.CurseType;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public class FXClientProxy extends FXCommonProxy{
	private static double renderDist = 64D;
	private static boolean noClip = false;
	private static boolean enableLimiter = false;
	private static byte amountCheck = 0;
	private static int particleAmount = 0, particleLimiter = 0;
	
	private static World world(){
		return Minecraft.getMinecraft().theWorld;
	}
	
	public static void spawn(EntityFX fx){
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.renderViewEntity == null)return;
		
		if (renderDist == -1 || MathUtil.distanceSquared(mc.renderViewEntity.posX-fx.posX,mc.renderViewEntity.posY-fx.posY,mc.renderViewEntity.posZ-fx.posZ) <= renderDist*renderDist){
			if (enableLimiter){
				if (++amountCheck >= 100){
					amountCheck = 0;
					particleAmount = Integer.parseInt(mc.effectRenderer.getStatistics());
				}
				
				if (particleAmount > 600){
					if (++particleLimiter < (particleAmount>>5))return;
					particleLimiter = 0;
				}
			}
			
			if (noClip)fx.noClip = true;
			mc.effectRenderer.addEffect(fx);
		}
	}
	
	/*
	 * SETTINGS
	 */
	
	@Override
	public FXCommonProxy reset(){
		renderDist = 64D;
		enableLimiter = false;
		noClip = false;
		return this;
	}
	
	@Override
	public FXCommonProxy setRenderDist(double dist){
		renderDist = dist;
		return this;
	};
	
	@Override
	public FXCommonProxy setOmnipresent(){
		renderDist = -1;
		return this;
	}
	
	@Override
	public FXCommonProxy setLimiter(){
		enableLimiter = true;
		return this;
	}
	
	@Override
	public FXCommonProxy setNoClip(){
		noClip = true;
		return super.setNoClip();
	}
	
	/*
	 * GENERIC
	 */
	
	@Override
	public void global(String particleName, double x, double y, double z, double motionX, double motionY, double motionZ){
		switch(particleName){
			case "smoke": spawn(new EntitySmokeFX(world(),x,y,z,motionX,motionY,motionZ)); break;
			case "largesmoke": spawn(new EntitySmokeFX(world(),x,y,z,motionX,motionY,motionZ,2.5F)); break;
			case "portal": spawn(new EntityPortalFX(world(),x,y,z,motionX,motionY,motionZ)); break;
			case "portalbig": spawn(new EntityBigPortalFX(world(),x,y,z,motionX,motionY,motionZ)); break;
			case "bubble": spawn(new EntityCustomBubbleFX(world(),x,y,z,motionX,motionY,motionZ)); break;
			case "flame": spawn(new EntityFlameFX(world(),x,y,z,motionX,motionY,motionZ)); break;
			case "explosion": spawn(new EntityExplodeFX(world(),x,y,z,motionX,motionY,motionZ)); break;
			case "largeexplosion": spawn(new EntityLargeExplodeFX(Minecraft.getMinecraft().renderEngine,world(),x,y,z,motionX,motionY,motionZ)); break;
			case "hugeexplosion": spawn(new EntityHugeExplodeFX(world(),x,y,z,motionX,motionY,motionZ)); break;
			case "lava": spawn(new EntityLavaFX(world(),x,y,z)); break;
			
			case "magiccrit":
				EntityCritFX fxCrit = new EntityCritFX(world(),x,y,z,motionX,motionY,motionZ);
				fxCrit.setRBGColorF(fxCrit.getRedColorF()*0.3F,fxCrit.getGreenColorF()*0.8F,fxCrit.getBlueColorF());
				fxCrit.nextTextureIndexX();
				spawn(fxCrit);
				break;
				
			case "witchmagic":
				EntitySpellParticleFX fxSpell = new EntitySpellParticleFX(world(),x,y,z,motionX,motionY,motionZ);
				float color = world().rand.nextFloat()*0.5F+0.35F;
				fxSpell.setRBGColorF(color,0F,color);
				fxSpell.setBaseSpellTextureIndex(144);
				spawn(fxSpell);
				
			default: throw new IllegalArgumentException("Unknown particle effect "+particleName);
		}
	}
	
	@Override
	public void global(String particleName, double x, double y, double z, double motionX, double motionY, double motionZ, float parameter){
		switch(particleName){
			case "portalbig": spawn(new EntityBigPortalFX(world(),x,y,z,motionX,motionY,motionZ,parameter)); break;
			case "smoke": spawn(new EntitySmokeFX(world(),x,y,z,motionX,motionY,motionZ,parameter)); break;
			default: throw new IllegalArgumentException("Unknown particle effect "+particleName);
		}
	}
	
	@Override
	public void global(String particleName, double x, double y, double z, double motionX, double motionY, double motionZ, final float red, final float green, final float blue){
		switch(particleName){
			case "portal": spawn(new EntityPortalFX(world(),x,y,z,motionX,motionY,motionZ){{ particleRed = red; particleGreen = green; particleBlue = blue; }}); break;
			case "energy": spawn(new EntityEnergyClusterFX(world(),x,y,z,red,green,blue,motionX,motionY,motionZ)); break;
			case "glitter": spawn(new EntityGlitterFX(world(),x,y,z,motionX,motionY,motionZ,red,green,blue)); break;
			
			case "magiccrit":
				EntityCritFX fxCrit = new EntityCritFX(world(),x,y,z,motionX,motionY,motionZ);
				fxCrit.setRBGColorF(red,green,blue);
				fxCrit.nextTextureIndexX();
				spawn(fxCrit);
				break;
				
			case "spell":
				EntitySpellParticleFX fxSpell = new EntitySpellParticleFX(world(),x,y,z,motionX,motionY,motionZ);
				fxSpell.setRBGColorF(red,green,blue);
				spawn(fxSpell);
				break;
				
			default: throw new IllegalArgumentException("Unknown particle effect "+particleName);
		}
	}
	
	@Override
	public void item(ItemStack is, double x, double y, double z, double motionX, double motionY, double motionZ){
		spawn(is.getItemSpriteNumber() == 0 ?
			new EntityDiggingFX(world(),x,y,z,motionX,motionY,motionZ,Block.getBlockFromItem(is.getItem()),is.getItemDamage()) :
			new EntityBreakingFX(world(),x,y,z,motionX,motionY,motionZ,is.getItem(),is.getItemDamage())
		);
	}
	
	@Override
	public void itemTarget(ItemStack is, double startX, double startY, double startZ, final double targetX, final double targetY, final double targetZ, final float speedMultiplier){
		EntityFX fx = (is.getItemSpriteNumber() == 0 ?
			new EntityDiggingFX(world(),startX,startY,startZ,0D,0D,0D,Block.getBlockFromItem(is.getItem()),is.getItemDamage()){
				final ParticleBehaviorMoveTo moveBehavior = new ParticleBehaviorMoveTo(this,targetX,targetY,targetZ,speedMultiplier);
				
				@Override
				public void onUpdate(){
					moveBehavior.update(this);
				}
			} :
			new EntityBreakingFX(world(),startX,startY,startZ,0D,0D,0D,is.getItem(),is.getItemDamage()){
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
	public void flame(double x, double y, double z, final int maxAge){
		flame(x,y,z,0D,0D,0D,maxAge);
	}
	
	@Override
	public void flame(double x, double y, double z, double motionX, double motionY, double motionZ, final int maxAge){
		spawn(new EntityFlameFX(world(),x,y,z,motionX,motionY,motionZ){{
			particleMaxAge = maxAge;
		}});
	}
	
	@Override
	public void portalBig(double x, double y, double z, double motionX, double motionY, double motionZ, float scaleMp, final float red, final float green, final float blue){
		spawn(new EntityBigPortalFX(world(),x,y,z,motionX,motionY,motionZ,scaleMp){{
			particleRed = red;
			particleGreen = green;
			particleBlue = blue;
		}});
	}
	
	@Override
	public void portalOrbiting(double x, double y, double z, double motionY){
		spawn(new EntityOrbitingPortalFX(world(),x,y,z,motionY));
	}
	
	@Override
	public void aura(double x, double y, double z, final float red, final float green, final float blue, final int maxAge){
		spawn(new EntityAuraFX(world(),x,y,z,0D,0D,0D){{
			particleMaxAge = maxAge;
			particleRed = red;
			particleGreen = green;
			particleBlue = blue;
		}});
	}
	
	@Override
	public void curse(double x, double y, double z, CurseType type){
		Random rand = world().rand;
		int color = rand.nextInt(5) <= 2 ? type.getColor(0) : type.getColor(1);
		portalBig(x,y,z,(rand.nextDouble()-0.5D)*rand.nextDouble()*0.03D,(rand.nextDouble()-0.5D)*rand.nextDouble()*0.03D,(rand.nextDouble()-0.5D)*rand.nextDouble()*0.03D,0.1F+rand.nextFloat()*0.1F,((color>>16)&255)/255F,((color>>8)&255)/255F,(color&255)/255F);
	}
	
	@Override
	public void spatialDash(double x, double y, double z){
		Random rand = world().rand;
		
		spawn(new EntityBigPortalFX(world(),x+(rand.nextDouble()-rand.nextDouble())*0.3D,y+(rand.nextDouble()-rand.nextDouble())*0.3D,z+(rand.nextDouble()-rand.nextDouble())*0.3D,(rand.nextDouble()-0.5D)*0.01D,(rand.nextDouble()-0.5D)*0.01D,(rand.nextDouble()-0.5D)*0.01D,0.1F));
		
		for(int a = 0; a < 3; a++){
			double motX = (rand.nextDouble()-rand.nextDouble())*0.0002D, motY = (rand.nextDouble()-rand.nextDouble())*0.0002D, motZ = (rand.nextDouble()-rand.nextDouble())*0.0002D;
			spawn(new EntityEnergyClusterFX(world(),x,y,z,0.6D,0.2D,1D,motX,motY,motZ,0.015F));
		}
	}
	
	/*
	 * BLOCKS
	 */
	
	@Override
	public void corruptedEnergy(int x, int y, int z){
		Random rand = world().rand;
		double motX = (rand.nextDouble()-rand.nextDouble())*0.2D, motY = (rand.nextDouble()-rand.nextDouble())*0.2D, motZ = (rand.nextDouble()-rand.nextDouble())*0.2D;
		spawn(new EntityBigPortalFX(world(),x+0.5D,y+0.5D,z+0.5D,motX,motY,motZ,rand.nextBoolean() ? 1F : 1.5F+rand.nextFloat()));
	}
	
	@Override
	public void enderGoo(int x, int y, int z){
		Random rand = world().rand;
		spawn(new EntityEnderGooFX(world(),x+rand.nextDouble(),y+rand.nextDouble()*0.5D,z+rand.nextDouble(),(rand.nextDouble()-0.5D)*0.1D,0.12D,(rand.nextDouble()-0.5D)*0.1D));
	}
	
	@Override
	public void energyCluster(TileEntityEnergyCluster cluster){
		Random rand = cluster.getWorldObj().rand;
		spawn(new EntityEnergyClusterFX(cluster.getWorldObj(),cluster.xCoord+0.5D+(rand.nextDouble()-rand.nextDouble())*0.1D,cluster.yCoord+0.5D+(rand.nextDouble()-rand.nextDouble())*0.1D,cluster.zCoord+0.5D+(rand.nextDouble()-rand.nextDouble())*0.1D,cluster.getColor(0),cluster.getColor(1),cluster.getColor(2),cluster.getData().get()));
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
	public void spatialDashExplode(EntityProjectileSpatialDash spatialDash){
		Random rand = spatialDash.worldObj.rand;
		spawn(new EntityBigPortalFX(spatialDash.worldObj,spatialDash.posX+(rand.nextDouble()-rand.nextDouble())*0.1D,spatialDash.posY+(rand.nextDouble()-rand.nextDouble())*0.1D,spatialDash.posZ+(rand.nextDouble()-rand.nextDouble())*0.1D,(rand.nextDouble()-0.5D)*0.3D,(rand.nextDouble()-0.5D)*0.3D,(rand.nextDouble()-0.5D)*0.3D,1F));
	}
	
	@Override
	public void corruptedEnergy(EntityProjectileCorruptedEnergy energy){
		Random rand = energy.worldObj.rand;
		double motX = (rand.nextDouble()-rand.nextDouble())*0.2D, motY = (rand.nextDouble()-rand.nextDouble())*0.2D, motZ = (rand.nextDouble()-rand.nextDouble())*0.2D;
		spawn(new EntityBigPortalFX(energy.worldObj,energy.posX+(rand.nextDouble()-0.5D)*0.2D,energy.posY+0.5D+(rand.nextDouble()-0.5D)*0.2D,energy.posZ+(rand.nextDouble()-0.5D)*0.2D,motX,motY,motZ,rand.nextBoolean() ? 0.3F : 0.4F+rand.nextFloat()*0.4F));
	}
}
