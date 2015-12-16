package chylex.hee.entity.fx;
import java.util.Random;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockSpookyLeaves;
import chylex.hee.block.BlockSpookyLog;
import chylex.hee.init.BlockList;
import chylex.hee.proxy.FXClientProxy;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.util.FastRandom;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class FXHandler{
	private static final FastRandom rand = new FastRandom();
	private static final Random slowRand = new Random();
	private static final FXClientProxy fx = (FXClientProxy)HardcoreEnderExpansion.fx;
	
	public static void handleBasic(World world, EntityClientPlayerMP player, FXType.Basic fxType, double x, double y, double z){
		switch(fxType){
			case ESSENCE_ALTAR_SMOKE:
				for(int a = 0; a < 15; a++)fx.global("largesmoke",x+randCenter(0.5D),y+0.6D+rand.nextDouble()*0.25D,z+randCenter(0.5D),0D,0.15D,0D);
				break;
				
			case LASER_BEAM_DESTROY:
				String[] particles = new String[]{ "largesmoke", "portal", "flame" };
				
				for(int a = 0; a < 10; a++){
					for(String pt:particles)fx.global(pt,x+randCenter(0.5D),y+randCenter(0.5D),z+randCenter(0.5D),0D,0D,0D);
				}
				
				break;
				
			case SPOOKY_LOG_DECAY:
				((BlockSpookyLog)BlockList.spooky_log).addDestroyEffectsCustom(world,(int)x,(int)y,(int)z);
				break;
				
			case SPOOKY_LEAVES_DECAY:
				((BlockSpookyLeaves)BlockList.spooky_leaves).addDestroyEffectsCustom(world,(int)x,(int)y,(int)z);
				break;
				
			case DUNGEON_PUZZLE_BURN:
				for(int a = 0; a < 6; a++)fx.global("flame",x+randCenter(0.3D),y+0.6D*rand.nextDouble(),z+randCenter(0.3D),0D,0.04D,0D);
				world.playSoundEffect(x,y,z,"random.fizz",0.6F,2.6F+(rand.nextFloat()-rand.nextFloat())*0.8F);
				break;
				
			case DRAGON_EGG_RESET:
				for(int a = 0; a < 40; a++){
					fx.global("smoke",x+randCenter(0.8D),y+randCenter(0.8D),z+randCenter(0.8D),randCenter(0.01D),randCenter(0.01D),randCenter(0.01D));
					fx.global("portal",x+randCenter(0.8D),y+randCenter(0.8D)-0.5D,z+randCenter(0.8D),randCenter(2D),randCenter(2D),randCenter(2D));
				}
				
				world.playSound(x,y,z,"mob.endermen.portal",1.2F,world.rand.nextFloat()*0.05F+0.85F,false);
				break;
				
			case GEM_LINK:
				for(int a = 0; a < 25; a++)fx.portalOrbiting(x+0.5D,y+0.38D+rand.nextDouble()*0.6D,z+0.5D,rand.nextDouble()*0.045D+0.015D);
				world.playSound(x+0.5D,y+1D,z+0.5D,"hardcoreenderexpansion:environment.gem.link",1F,rand.nextFloat()*0.02F+0.64F,false);
				break;
				
			case GEM_TELEPORT_TO:
				for(int a = 0; a < 25; a++)fx.portalOrbiting(x,y+0.1D+rand.nextDouble()*1.2D,z,rand.nextDouble()*0.045D+0.015D);
				world.playSound(x,y+1.63D,z,"mob.endermen.portal",1.2F,world.rand.nextFloat()*0.05F+0.85F,false);
				break;
				
			case ENDER_PEARL_FREEZE:
				ItemStack snowball = new ItemStack(Items.snowball);
				
				for(double xx = x-5; xx <= x+5; xx++){
					for(double yy = y-5; yy <= y+5; yy++){
						for(double zz = z-5; zz <= z+5; zz++){
							if (MathUtil.distance(xx-x,yy-y,zz-z) > 5D)continue;
							for(int i = 0; i < 2; ++i)fx.item(snowball,xx+rand.nextDouble()-0.5D,yy,zz+rand.nextDouble()-0.5D,0D,0D,0D);
						}
					}
				}
				
				world.playSound(x,y,z,"hardcoreenderexpansion:environment.random.freeze",1F,rand.nextFloat()*0.1F+0.9F,false);
				break;
				
			case IGNEOUS_ROCK_MELT:
				for(int a = 0; a < 40; a++)fx.flame(x+randCenter(1.25D),y+randCenter(1.25D),z+randCenter(1.25D),12+rand.nextInt(9));
				break;
				
			case ENDERMAN_BLOODLUST_TRANSFORMATION:
				for(int a = 0; a < 20; a++)fx.global("largesmoke",x+randCenter(0.3D),y+rand.nextDouble()*2.8D,z+randCenter(0.3D),0D,0D,0D);
				break;
				
			case LOUSE_ARMOR_HIT:
				for(int a = 0; a < 10; a++)fx.global("magiccrit",x+randCenter(0.4D),y+rand.nextDouble()*0.45D,z+randCenter(0.4D),randCenter(0.2D),randCenter(0.2D),randCenter(0.2D));
				break;
				
			case HOMELAND_ENDERMAN_TP_OVERWORLD:
				for(int a = 0; a < 50; a++){
					fx.global("portal",x+randCenter(1D),y+rand.nextDouble()*2.9,z+randCenter(1D),randCenter(0.1D),randCenter(0.05D),randCenter(0.1D));
				}
				
				for(int a = 120+rand.nextInt(30); a > 0; a--){
					fx.global("portal",x+randCenter(1D),y+rand.nextDouble()*rand.nextDouble()*60D,z+randCenter(1D),randCenter(0.1D),randCenter(0.05D),randCenter(0.1D));
				}
				
				world.playSound(x,y+1D,z,"mob.endermen.portal",1F,1F,false);
				break;
				
			case FIRE_FIEND_FLAME_ATTACK:
				for(int a = 0; a < 20; a++)fx.flame(x+randCenter(0.9D),y+rand.nextDouble()*1.8D,z+randCenter(0.9D),10);
				world.playSound(x,y,z,"random.fizz",2F,2.2F+(rand.nextFloat()-rand.nextFloat())*0.8F,true);
				break;
				
			case SHRINE_GLITTER:
				double mot = (rand.nextDouble()-0.5D)*0.1D;
				fx.global("glitter",x,y,z,rand.nextInt(6) == 0 ? mot : 0D,rand.nextInt(6) == 0 ? mot : 0D,rand.nextInt(6) == 0 ? mot : 0D,0.7F,0.5F,1F);
				break;
				
			default:
		}
	}
	
	public static void handleEntity(World world, EntityClientPlayerMP player, FXType.Entity fxType, double x, double y, double z, float width, float height){
		width *= 0.75F; // compensate for incorrect rand calculation
		
		switch(fxType){
			case CHARM_CRITICAL:
				for(int a = 0; a < 12; a++)fx.global("crit",x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.2D),randCenter(0.2D),randCenter(0.2D));
				break;
				
			case CHARM_WITCH:
				for(int a = 0; a < 18; a++)fx.global("witchmagic",x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D));
				break;
				
			case CHARM_BLOCK_EFFECT:
				for(int a = 0; a < 10; a++)fx.global("magiccrit",x+randCenter(width),y+rand.nextDouble()*height*1.4D,z+randCenter(width),randCenter(0.2D),randCenter(0.2D),randCenter(0.2D));
				break;
				
			case CHARM_LAST_RESORT:
				for(int a = 0; a < 35; a++)fx.global("smoke",x+randCenter(width*1.25D),y+rand.nextDouble()*height,z+randCenter(width*1.25D),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D));
				for(int a = 0; a < 75; a++)fx.global("portalbig",x+randCenter(width*1.25D),y+rand.nextDouble()*height,z+randCenter(width*1.25D),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D),0.72F+rand.nextFloat()*0.2F);
				world.playSound(x,y+0.1D,z,"mob.endermen.portal",1.1F,1F+rand.nextFloat()*0.1F,false);
				break;
				
			case GEM_TELEPORT_FROM:
				for(int a = 0; a < 20; a++)fx.global("largesmoke",x+randCenter(width*1.2D),y+rand.nextDouble()*height*0.9D,z+randCenter(width*1.2D),0D,0.04D,0D);
				world.playSound(x,y+1D,z,"mob.endermen.portal",1.2F,rand.nextFloat()*0.05F+0.85F,false);
				break;
				
			case ORB_TRANSFORMATION:
				for(int a = 0; a < 18; a++)fx.global("largesmoke",x+randCenter(width),y+0.1D+rand.nextDouble()*height,z+randCenter(width),0D,0D,0D);
				world.playSound(x,y,z,"hardcoreenderexpansion:block.random.transform",1.4F,1F+rand.nextFloat()*0.2F,false);
				break;
				
			case LOUSE_REGEN:
				for(int a = 0; a < 6; a++)fx.aura(x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),0F,0.7F,0F,14+rand.nextInt(10));
				break;
				
			case HOMELAND_ENDERMAN_RECRUIT:
				for(int a = 0; a < 25; a++)fx.global("smoke",x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.05D),randCenter(0.05D),randCenter(0.05D));
				for(int a = 0; a < 8; a++)fx.global("largesmoke",x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.05D),randCenter(0.05D),randCenter(0.05D));
				break;
				
			case BABY_ENDERMAN_GROW:
				for(int a = 0; a < 20; a++)fx.global("smoke",x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.05D),randCenter(0.05D),randCenter(0.05D));
				for(int a = 0; a < 20; a++)fx.global("portal",x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D));
				break;
				
			case ENDER_GUARDIAN_DASH:
				for(int a = 0; a < 50; a++)fx.global("portalbig",x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.3D),randCenter(0.3D),randCenter(0.3D),0.5F+rand.nextFloat()*0.4F);
				break;
				
			case SIMPLE_TELEPORT:
			case SIMPLE_TELEPORT_NOSOUND:
				for(int a = 0; a < 30; a++)fx.global("portal",x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.15D),randCenter(0.15D),randCenter(0.15D));
				if (fxType != FXType.Entity.SIMPLE_TELEPORT_NOSOUND)world.playSound(x,y,z,"mob.endermen.portal",1F,1F,false);
				break;
				
			case SANCTUARY_OVERSEER_SINGLE:
				for(int a = 0; a < 20; a++)fx.global("portalbig",x+randCenter(width*2D),y+rand.nextDouble()*height,z+randCenter(width*2D),randCenter(0.5D),randCenter(0.5D),randCenter(0.5D));
				break;
				
			case ENTITY_EXPLOSION_PARTICLE:
				for(int a = 0; a < 20; a++){
					double offX = slowRand.nextGaussian()*0.02D, offY = slowRand.nextGaussian()*0.02D, offZ = slowRand.nextGaussian()*0.02D;
					fx.global("explosion",x+randCenter(width)-offX*10D,y+rand.nextFloat()*height-offY*10D,z+randCenter(width)-offZ*10D,offX,offY,offZ);
				}
				
				break;
				
			case ENDER_EYE_BREAK:
				FXHelper.create("smoke").pos(x,y,z).fluctuatePos(0.2D).motion(0D,0D,0D).fluctuateMotion(0.08D).spawn(slowRand,18);
				FXHelper.create("glitter").pos(x,y,z).fluctuatePos(0.1D).motion(0D,0D,0D).fluctuateMotion(0.03D).paramColor(0.35F+rand.nextFloat()*0.1F,0.3F+rand.nextFloat()*0.4F,0.4F+rand.nextFloat()*0.1F).spawn(slowRand,40);
				FXHelper.create("glitter").pos(x,y,z).fluctuatePos(0.1D).motion(0D,0D,0D).fluctuateMotion(0.03D).paramColor(0.4F+rand.nextFloat()*0.2F,0.25F+rand.nextFloat()*0.05F,0.6F+rand.nextFloat()*0.25F).spawn(slowRand,20);
				world.playSound(x,y,z,"dig.glass",1.25F,1.1F,false);
				break;
				
			case ENDERMAN_DESPAWN:
				for(int a = 0; a < 12; a++)fx.portalFlyOff(x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),0.2F+rand.nextFloat()*0.1F,0.15D+rand.nextDouble()*0.05D);
				world.playSound(x,y,z,"mob.endermen.portal",2F,0.5F,false);
				break;
				
			case ENDERMAN_TP_FAIL:
				for(int a = 0; a < 8; a++)fx.global("smoke",x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.05D),randCenter(0.05D),randCenter(0.05D));
				// TODO sound
				break;
		}
	}
	
	public static void handleLine(World world, EntityClientPlayerMP player, FXType.Line fxType, double x1, double y1, double z1, double x2, double y2, double z2){
		Vec lineVec = Vec.xyz(x2-x1,y2-y1,z2-z1);
		double len = lineVec.length();
		lineVec = lineVec.normalized();
		
		double addX, addY, addZ;
		
		switch(fxType){
			case CHARM_SLAUGHTER_IMPACT:
			case CHARM_DAMAGE_REDIRECTION:
				addX = lineVec.x*0.5D;
				addY = lineVec.y*0.5D;
				addZ = lineVec.z*0.5D;
				
				float red = 0F, green = 0F, blue = 0F;
				
				if (fxType == FXType.Line.CHARM_SLAUGHTER_IMPACT){
					red = 1F;
					green = 0.25F;
					blue = 0.4F;
				}
				else if (fxType == FXType.Line.CHARM_DAMAGE_REDIRECTION){
					red = 0.3F;
					green = 0.5F;
					blue = 1F;
				}

				for(int a = 0; a < len*2D; a++){
					for(int b = 0; b < 3; b++)fx.global("magiccrit",x1+randCenter(0.4D),y1+0.2D+randCenter(0.4D),z1+randCenter(0.4D),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D),red,green,blue);
					
					x1 += addX;
					y1 += addY;
					z1 += addZ;
				}
				
				break;
			
			case DRAGON_EGG_TELEPORT:
				addX = lineVec.x*0.25D;
				addY = lineVec.y*0.25D;
				addZ = lineVec.z*0.25D;
				
				for(int a = 0; a < 35; a++)fx.global("smoke",x1+randCenter(0.8D),y1+randCenter(0.8D),z1+randCenter(0.8D),randCenter(0.01D),randCenter(0.01D),randCenter(0.01D));
				
				for(int a = 0; a < len*4D; a++){
					for(int b = 0; b < 4; b++)fx.global("portalbig",x1,y1,z1,randCenter(0.01D),randCenter(0.01D),randCenter(0.01D),0.15F);
					
					x1 += addX;
					y1 += addY;
					z1 += addZ;
				}
				
				world.playSound(x1,y1,z1,"mob.endermen.portal",1.2F,1F,false);
				break;
				
			case SPATIAL_DASH_MOVE:
				addX = lineVec.x*0.2D;
				addY = lineVec.y*0.2D;
				addZ = lineVec.z*0.2D;
				
				for(int a = 0; a < len*5D; a++){
					double dist = player.getDistanceSq(x1,y1,z1);
					if (dist > 600D && slowRand.nextBoolean())continue;
					if (dist < 180D)fx.spatialDash(x1,y1,z1);
					fx.spatialDash(x1,y1,z1);
					
					x1 += addX;
					y1 += addY;
					z1 += addZ;
				}
				
				break;
				
			case LOUSE_HEAL_ENTITY:
				addX = lineVec.x*0.125D;
				addY = lineVec.y*0.125D;
				addZ = lineVec.z*0.125D;
				
				for(int a = 0; a < len*8D; a++){
					fx.aura(x1+randCenter(0.1D),y1+randCenter(0.1D),z1+randCenter(0.1D),0F,0.7F,0F,20+rand.nextInt(20));
					x1 += addX;
					y1 += addY;
					z1 += addZ;
				}
				
				break;
				
			case ENDERMAN_TELEPORT:
			case DUNGEON_PUZZLE_TELEPORT:
				double mp = fxType == FXType.Line.DUNGEON_PUZZLE_TELEPORT ? 1.6D : 1.2D;
				double height = fxType == FXType.Line.DUNGEON_PUZZLE_TELEPORT ? 1.8D: 2.9D;
				
				for(int a = 0, particleAmt = fxType == FXType.Line.DUNGEON_PUZZLE_TELEPORT ? 256 : 128; a < particleAmt; a++){
					double linePosition = a/(particleAmt-1D);
					double particleX = x1+(x2-x1)*linePosition+(rand.nextDouble()-0.5D)*mp;
					double particleY = y1+(y2-y1)*linePosition+rand.nextDouble()*height;
					double particleZ = z1+(z2-z1)*linePosition+(rand.nextDouble()-0.5D)*mp;
					fx.global("portal",particleX,particleY,particleZ,(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F);
				}
				
				world.playSound(x1,y1,z1,"mob.endermen.portal",1F,1F,false);
				world.playSound(x2,y2,z2,"mob.endermen.portal",1F,1F,false);
				break;
				
			case ENDERMAN_TELEPORT_SEPARATE:
				for(int a = 0; a < 30; a++){
					fx.global("portal",x1+randCenter(0.6F),y1+rand.nextDouble()*2.9F,z1+randCenter(0.6F),randCenter(0.15D),randCenter(0.15D),randCenter(0.15D));
					fx.global("portal",x2+randCenter(0.6F),y2+rand.nextDouble()*2.9F,z2+randCenter(0.6F),randCenter(0.15D),randCenter(0.15D),randCenter(0.15D));
				}
				
				world.playSound(x1,y1,z1,"mob.endermen.portal",1F,1F,false);
				world.playSound(x2,y2,z2,"mob.endermen.portal",1F,1F,false);
				break;
				
			case FIRE_FIEND_GOLEM_CALL:
				addX = lineVec.x*0.5D;
				addY = lineVec.y*0.5D;
				addZ = lineVec.z*0.5D;
				
				for(int a = 0; a < len*2D; a++){
					if (slowRand.nextBoolean())fx.flame(x1+randCenter(0.1D),y1+randCenter(0.1D),z1+randCenter(0.1D),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D),12);
					x1 += addX;
					y1 += addY;
					z1 += addZ;
				}
				
				break;
				
			case SANCTUARY_OVERSEER_FULL:
				for(int x = (int)Math.min(x1,x2); x <= Math.max(x1,x2); x++){
					for(int y = (int)Math.min(y1,y2); y <= Math.max(y1,y2); y++){
						for(int z = (int)Math.min(z1,z2); z <= Math.max(z1,z2); z++){
							fx.corruptedEnergy(x,y,z);
						}
					}
				}
				
				break;
		}
	}
	
	private static double randCenter(double mp){
		return (rand.nextDouble()-0.5D)*2D*mp;
	}
}
