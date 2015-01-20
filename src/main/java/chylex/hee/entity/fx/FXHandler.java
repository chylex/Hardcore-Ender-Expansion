package chylex.hee.entity.fx;
import java.util.Random;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockSpookyLeaves;
import chylex.hee.block.BlockSpookyLog;
import chylex.hee.mechanics.misc.HomelandEndermen.HomelandRole;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;

@SideOnly(Side.CLIENT)
public final class FXHandler{
	private static final Random rand = new Random();
	
	public static void handleBasic(World world, EntityClientPlayerMP player, FXType.Basic fx, double x, double y, double z){
		switch(fx){
			case ESSENCE_ALTAR_SMOKE:
				for(int a = 0; a < 15; a++)world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,x+randCenter(0.5D),y+0.6D+rand.nextDouble()*0.25D,z+randCenter(0.5D),0D,0.15D,0D);
				break;
				
			case LASER_BEAM_DESTROY:
				EnumParticleTypes[] particles = new EnumParticleTypes[]{ EnumParticleTypes.SMOKE_LARGE, EnumParticleTypes.PORTAL, EnumParticleTypes.FLAME };
				
				for(int a = 0; a < 10; a++){
					for(EnumParticleTypes pt:particles)world.spawnParticle(pt,x+randCenter(0.5D),y+randCenter(0.5D),z+randCenter(0.5D),0D,0D,0D);
				}
				
				break;
				
			case SPOOKY_LOG_DECAY:
				((BlockSpookyLog)BlockList.spooky_log).addDestroyEffectsCustom(world,new BlockPosM(x,y,z));
				break;
				
			case SPOOKY_LEAVES_DECAY:
				((BlockSpookyLeaves)BlockList.spooky_leaves).addDestroyEffectsCustom(world,new BlockPosM(x,y,z));
				break;
				
			case DUNGEON_PUZZLE_BURN:
				for(int a = 0; a < 6; a++)world.spawnParticle(EnumParticleTypes.FLAME,x+randCenter(0.3D),y+0.6D*rand.nextDouble(),z+randCenter(0.3D),0D,0.04D,0D);
				world.playSoundEffect(x,y,z,"random.fizz",0.6F,2.6F+(rand.nextFloat()-rand.nextFloat())*0.8F);
				break;
				
			case DRAGON_EGG_RESET:
				for(int a = 0; a < 40; a++){
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,x+randCenter(0.8D),y+randCenter(0.8D),z+randCenter(0.8D),randCenter(0.01D),randCenter(0.01D),randCenter(0.01D));
					world.spawnParticle(EnumParticleTypes.PORTAL,x+randCenter(0.8D),y+randCenter(0.8D)-0.5D,z+randCenter(0.8D),randCenter(2D),randCenter(2D),randCenter(2D));
				}
				
				world.playSound(x,y,z,"mob.endermen.portal",1.2F,world.rand.nextFloat()*0.05F+0.85F,false);
				break;
				
			case GEM_LINK:
				for(int a = 0; a < 25; a++)HardcoreEnderExpansion.fx.portalOrbiting(world,x+0.5D,y+0.38D+rand.nextDouble()*0.6D,z+0.5D,rand.nextDouble()*0.045D+0.015D);
				world.playSound(x+0.5D,y+1D,z+0.5D,"hardcoreenderexpansion:environment.gem.link",1F,rand.nextFloat()*0.02F+0.64F,false);
				break;
				
			case GEM_TELEPORT_TO:
				for(int a = 0; a < 25; a++)HardcoreEnderExpansion.fx.portalOrbiting(world,x,y+1.63D+rand.nextDouble()*0.6D,z,rand.nextDouble()*0.045D+0.015D);
				world.playSound(x,y+1.63D,z,"mob.endermen.portal",1.2F,world.rand.nextFloat()*0.05F+0.85F,false);
				break;
				
			case ENDER_PEARL_FREEZE:
				for(double xx = x-5; xx <= x+5; xx++){
					for(double yy = y-5; yy <= y+5; yy++){
						for(double zz = z-5; zz <= z+5; zz++){
							if (MathUtil.distance(xx-x,yy-y,zz-z) > 5D)continue;
							for(int i = 0; i < 2; ++i)world.spawnParticle(EnumParticleTypes.SNOWBALL,xx+rand.nextDouble()-0.5D,yy,zz+rand.nextDouble()-0.5D,0D,0D,0D);
						}
					}
				}
				
				world.playSound(x,y,z,"hardcoreenderexpansion:environment.random.freeze",1F,rand.nextFloat()*0.1F+0.9F,false);
				break;
				
			case IGNEOUS_ROCK_MELT:
				for(int a = 0; a < 40; a++)HardcoreEnderExpansion.fx.flame(player.worldObj,x+randCenter(1.25D),y+randCenter(1.25D),z+randCenter(1.25D),12+rand.nextInt(9));
				break;
				
			case ENDERMAN_BLOODLUST_TRANSFORMATION:
				for(int a = 0; a < 20; a++)world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,x+randCenter(0.3D),y+rand.nextDouble()*2.8D,z+randCenter(0.3D),0D,0D,0D);
				break;
				
			case LOUSE_ARMOR_HIT:
				for(int a = 0; a < 10; a++)world.spawnParticle(EnumParticleTypes.CRIT_MAGIC,x+randCenter(0.4D),y+rand.nextDouble()*0.45D,z+randCenter(0.4D),randCenter(0.2D),randCenter(0.2D),randCenter(0.2D));
				break;
				
			case HOMELAND_ENDERMAN_TP_OVERWORLD:
				for(int a = 0; a < 50; a++){
					HardcoreEnderExpansion.fx.omnipresent(EnumParticleTypes.PORTAL,world,x+randCenter(1D),y+rand.nextDouble()*2.9,z+randCenter(1D),randCenter(0.1D),randCenter(0.05D),randCenter(0.1D));
				}
				
				for(int a = 120+rand.nextInt(30); a > 0; a--){
					HardcoreEnderExpansion.fx.omnipresent(EnumParticleTypes.PORTAL,world,x+randCenter(1D),y+rand.nextDouble()*rand.nextDouble()*60D,z+randCenter(1D),randCenter(0.1D),randCenter(0.05D),randCenter(0.1D));
				}
				
				world.playSound(x,y+1D,z,"mob.endermen.portal",1F,1F,false);
				break;
				
			case FIRE_FIEND_FLAME_ATTACK:
				for(int a = 0; a < 20; a++)HardcoreEnderExpansion.fx.flame(world,x+randCenter(0.9D),y+rand.nextDouble()*1.8D,z+randCenter(0.9D),10);
				world.playSound(x,y,z,"random.fizz",2F,2.2F+(rand.nextFloat()-rand.nextFloat())*0.8F,true);
				break;
				
			default:
		}
	}
	
	public static void handleEntity(World world, EntityClientPlayerMP player, FXType.Entity fx, double x, double y, double z, float width, float height){
		switch(fx){
			case CHARM_CRITICAL:
				for(int a = 0; a < 12; a++)world.spawnParticle(EnumParticleTypes.CRIT,x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.2D),randCenter(0.2D),randCenter(0.2D));
				break;
				
			case CHARM_WITCH:
				for(int a = 0; a < 18; a++)world.spawnParticle(EnumParticleTypes.SPELL_WITCH,x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D));
				break;
				
			case CHARM_BLOCK_EFFECT:
				for(int a = 0; a < 10; a++)world.spawnParticle(EnumParticleTypes.CRIT_MAGIC,x+randCenter(width),y+rand.nextDouble()*height*1.4D,z+randCenter(width),randCenter(0.2D),randCenter(0.2D),randCenter(0.2D));
				break;
				
			case CHARM_LAST_RESORT:
				for(int a = 0; a < 35; a++)world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,x+randCenter(width*1.25D),y+rand.nextDouble()*height,z+randCenter(width*1.25D),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D));
				for(int a = 0; a < 75; a++)HardcoreEnderExpansion.fx.portalBig(world,x+randCenter(width*1.25D),y+rand.nextDouble()*height,z+randCenter(width*1.25D),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D),0.72F+rand.nextFloat()*0.2F);
				world.playSound(x,y+0.1D,z,"mob.endermen.portal",1.1F,1F+rand.nextFloat()*0.1F,false);
				break;
				
			case GEM_TELEPORT_FROM:
				for(int a = 0; a < 20; a++)world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,x+randCenter(width*1.2D),y+rand.nextDouble()*height*0.9D,z+randCenter(width*1.2D),0D,0.04D,0D);
				world.playSound(x,y+1D,z,"mob.endermen.portal",1.2F,rand.nextFloat()*0.05F+0.85F,false);
				break;
				
			case ORB_TRANSFORMATION:
				for(int a = 0; a < 18; a++)world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,x+randCenter(width),y+0.1D+rand.nextDouble()*height,z+randCenter(width),0D,0D,0D);
				world.playSound(x,y,z,"hardcoreenderexpansion:block.random.transform",1.4F,1F+rand.nextFloat()*0.2F,false);
				break;
				
			case LOUSE_REGEN:
				for(int a = 0; a < 6; a++)HardcoreEnderExpansion.fx.aura(world,x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),0F,0.7F,0F,14+rand.nextInt(10));
				break;
				
			case HOMELAND_ENDERMAN_RECRUIT:
				for(int a = 0; a < 25; a++)HardcoreEnderExpansion.fx.omnipresent(EnumParticleTypes.SMOKE_NORMAL,world,x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.05D),randCenter(0.05D),randCenter(0.05D));
				for(int a = 0; a < 8; a++)HardcoreEnderExpansion.fx.omnipresent(EnumParticleTypes.SMOKE_LARGE,world,x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.05D),randCenter(0.05D),randCenter(0.05D));
				break;
				
			case BABY_ENDERMAN_GROW:
				for(int a = 0; a < 20; a++)HardcoreEnderExpansion.fx.omnipresent(EnumParticleTypes.SMOKE_NORMAL,world,x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.05D),randCenter(0.05D),randCenter(0.05D));
				for(int a = 0; a < 20; a++)HardcoreEnderExpansion.fx.omnipresent(EnumParticleTypes.PORTAL,world,x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D));
				break;
				
			case ENDER_GUARDIAN_DASH:
				for(int a = 0; a < 50; a++)HardcoreEnderExpansion.fx.portalBig(world,x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.3D),randCenter(0.3D),randCenter(0.3D),0.5F+rand.nextFloat()*0.4F);
				break;
				
			case SIMPLE_TELEPORT:
				for(int a = 0; a < 30; a++)HardcoreEnderExpansion.fx.omnipresent(EnumParticleTypes.PORTAL,world,x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.15D),randCenter(0.15D),randCenter(0.15D));
				world.playSound(x,y,z,"mob.endermen.portal",1F,1F,false);
				break;
		}
	}
	
	public static void handleLine(World world, EntityClientPlayerMP player, FXType.Line fx, double x1, double y1, double z1, double x2, double y2, double z2){
		Vec3 lineVec = new Vec3(x2-x1,y2-y1,z2-z1);
		double len = lineVec.lengthVector();
		lineVec = lineVec.normalize();
		
		double addX, addY, addZ;
		
		switch(fx){
			case CHARM_SLAUGHTER_IMPACT:
			case CHARM_DAMAGE_REDIRECTION:
				addX = lineVec.xCoord*0.5D;
				addY = lineVec.yCoord*0.5D;
				addZ = lineVec.zCoord*0.5D;
				
				float red = 0F, green = 0F, blue = 0F;
				
				if (fx == FXType.Line.CHARM_SLAUGHTER_IMPACT){
					red = 1F;
					green = 0.25F;
					blue = 0.4F;
				}
				else if (fx == FXType.Line.CHARM_DAMAGE_REDIRECTION){
					red = 0.3F;
					green = 0.5F;
					blue = 1F;
				}

				for(int a = 0; a < len*2D; a++){
					for(int b = 0; b < 3; b++)HardcoreEnderExpansion.fx.magicCrit(world,x1+randCenter(0.4D),y1+0.2D+randCenter(0.4D),z1+randCenter(0.4D),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D),red,green,blue);
					
					x1 += addX;
					y1 += addY;
					z1 += addZ;
				}
				
				break;
			
			case DRAGON_EGG_TELEPORT:
				addX = lineVec.xCoord*0.25D;
				addY = lineVec.yCoord*0.25D;
				addZ = lineVec.zCoord*0.25D;
				
				for(int a = 0; a < 35; a++)world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,x1+randCenter(0.8D),y1+randCenter(0.8D),z1+randCenter(0.8D),randCenter(0.01D),randCenter(0.01D),randCenter(0.01D));
				
				for(int a = 0; a < len*4D; a++){
					for(int b = 0; b < 4; b++)HardcoreEnderExpansion.fx.portalBig(world,x1,y1,z1,randCenter(0.01D),randCenter(0.01D),randCenter(0.01D),0.15F);
					
					x1 += addX;
					y1 += addY;
					z1 += addZ;
				}
				
				world.playSound(x1,y1,z1,"mob.endermen.portal",1.2F,1F,false);
				break;
				
			case LOUSE_HEAL_ENTITY:
				addX = lineVec.xCoord*0.125D;
				addY = lineVec.yCoord*0.125D;
				addZ = lineVec.zCoord*0.125D;
				
				for(int a = 0; a < len*8D; a++){
					HardcoreEnderExpansion.fx.aura(world,x1+randCenter(0.1D),y1+randCenter(0.1D),z1+randCenter(0.1D),0F,0.7F,0F,20+rand.nextInt(20));
					x1 += addX;
					y1 += addY;
					z1 += addZ;
				}
				
				break;
				
			case ENDERMAN_TELEPORT:
			case DUNGEON_PUZZLE_TELEPORT:
				double mp = fx == FXType.Line.DUNGEON_PUZZLE_TELEPORT ? 1.6D : 1.2D;
				double height = fx == FXType.Line.DUNGEON_PUZZLE_TELEPORT ? 1.8D: 2.9D;
				
				for(int a = 0, particleAmt = fx == FXType.Line.DUNGEON_PUZZLE_TELEPORT ? 256 : 128; a < particleAmt; a++){
					double linePosition = a/(particleAmt-1D);
					double particleX = x1+(x2-x1)*linePosition+(rand.nextDouble()-0.5D)*mp;
					double particleY = y1+(y2-y1)*linePosition+rand.nextDouble()*height;
					double particleZ = z1+(z2-z1)*linePosition+(rand.nextDouble()-0.5D)*mp;
					world.spawnParticle(EnumParticleTypes.PORTAL,particleX,particleY,particleZ,(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F);
				}
				
				world.playSound(x1,y1,z1,"mob.endermen.portal",1F,1F,false);
				world.playSound(x2,y2,z2,"mob.endermen.portal",1F,1F,false);
				break;
				
			case HOMELAND_ENDERMAN_GUARD_CALL:
				addX = lineVec.xCoord*0.25D;
				addY = lineVec.yCoord*0.25D;
				addZ = lineVec.zCoord*0.25D;
				
				HomelandRole role = HomelandRole.GUARD;
				
				for(int a = 0; a < len*4D; a++){
					if (rand.nextBoolean())HardcoreEnderExpansion.fx.portalColor(world,x1+randCenter(0.25D),y1+randCenter(0.25D),z1+randCenter(0.25D),randCenter(1D),-rand.nextDouble(),randCenter(1D),role.red,role.green,role.blue);
					x1 += addX;
					y1 += addY;
					z1 += addZ;
				}
				
				break;
				
			case FIRE_FIEND_GOLEM_CALL:
				addX = lineVec.xCoord*0.5D;
				addY = lineVec.yCoord*0.5D;
				addZ = lineVec.zCoord*0.5D;
				
				for(int a = 0; a < len*2D; a++){
					if (rand.nextBoolean())HardcoreEnderExpansion.fx.flame(world,x1+randCenter(0.1D),y1+randCenter(0.1D),z1+randCenter(0.1D),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D),12);
					x1 += addX;
					y1 += addY;
					z1 += addZ;
				}
				
				break;
		}
	}
	
	private static double randCenter(double mp){ // TODO review
		return (rand.nextDouble()-0.5D)*2D*mp;
	}
}
