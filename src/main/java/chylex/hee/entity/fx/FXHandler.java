package chylex.hee.entity.fx;
import java.util.Random;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockSpookyLeaves;
import chylex.hee.block.BlockSpookyLog;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class FXHandler{
	private static final Random rand = new Random();
	
	public static void handleBasic(World world, EntityClientPlayerMP player, FXType.Basic fx, double x, double y, double z){
		switch(fx){
			case ESSENCE_ALTAR_SMOKE:
				for(int a = 0; a < 15; a++)world.spawnParticle("largesmoke",x+randCenter(0.5D),y+0.6D+rand.nextDouble()*0.25D,z+randCenter(0.5D),0D,0.15D,0D);
				break;
				
			case LASER_BEAM_DESTROY:
				String[] particles = new String[]{ "largesmoke", "portal", "flame" };
				
				for(int a = 0; a < 10; a++){
					for(String pt:particles)world.spawnParticle(pt,x+randCenter(0.5D),y+randCenter(0.5D),z+randCenter(0.5D),0D,0D,0D);
				}
				
				break;
				
			case SPOOKY_LOG_DECAY:
				((BlockSpookyLog)BlockList.spooky_log).addDestroyEffectsCustom(world,(int)x,(int)y,(int)z);
				break;
				
			case SPOOKY_LEAVES_DECAY:
				((BlockSpookyLeaves)BlockList.spooky_leaves).addDestroyEffectsCustom(world,(int)x,(int)y,(int)z);
				break;
				
			case DUNGEON_PUZZLE_BURN:
				for(int a = 0; a < 6; a++)world.spawnParticle("flame",x+randCenter(0.3D),y+0.6D*rand.nextDouble(),z+randCenter(0.3D),0D,0.04D,0D);
				world.playSoundEffect(x,y,z,"random.fizz",0.6F,2.6F+(rand.nextFloat()-rand.nextFloat())*0.8F);
				break;
				
			case DRAGON_EGG_RESET:
				for(int a = 0; a < 40; a++){
					world.spawnParticle("smoke",x+randCenter(0.8D),y+randCenter(0.8D),z+randCenter(0.8D),randCenter(0.01D),randCenter(0.01D),randCenter(0.01D));
					world.spawnParticle("portal",x+randCenter(0.8D),y+randCenter(0.8D)-0.5D,z+randCenter(0.8D),randCenter(2D),randCenter(2D),randCenter(2D));
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
							for(int i = 0; i < 2; ++i)world.spawnParticle("snowballpoof",xx+rand.nextDouble()-0.5D,yy,zz+rand.nextDouble()-0.5D,0D,0D,0D);
						}
					}
				}
				
				world.playSound(x,y,z,"hardcoreenderexpansion:environment.random.freeze",1F,rand.nextFloat()*0.1F+0.9F,false);
				break;
				
			case IGNEOUS_ROCK_MELT:
				for(int a = 0; a < 40; a++)HardcoreEnderExpansion.fx.flame(player.worldObj,x+randCenter(1.25D),y+randCenter(1.25D),z+randCenter(1.25D),12+rand.nextInt(9));
				break;
				
			case ENDERMAN_BLOODLUST_TRANSFORMATION:
				for(int a = 0; a < 20; a++)world.spawnParticle("largesmoke",x+randCenter(0.3D),y+rand.nextDouble()*2.8D,z+randCenter(0.3D),0D,0D,0D);
				break;
				
			case ENDER_GUARDIAN_TELEPORT:
				for(int a = 0; a < 80; a++)HardcoreEnderExpansion.fx.portalBig(world,x+randCenter(0.6D),y+rand.nextDouble()*3.2D,z+randCenter(0.6D),+randCenter(0.125D),rand.nextDouble()*0.2D-0.1D,+randCenter(0.125D));
				break;
				
			case LOUSE_ARMOR_HIT:
				for(int a = 0; a < 10; a++)world.spawnParticle("magicCrit",x+randCenter(0.4D),y+rand.nextDouble()*0.45D,z+randCenter(0.4D),randCenter(0.2D),randCenter(0.2D),randCenter(0.2D));
				break;
				
			default:
		}
	}
	
	public static void handleEntity(World world, EntityClientPlayerMP player, FXType.Entity fx, double x, double y, double z, float width, float height){
		switch(fx){
			case CHARM_CRITICAL:
				for(int a = 0; a < 12; a++)world.spawnParticle("crit",x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.2D),randCenter(0.2D),randCenter(0.2D));
				break;
				
			case CHARM_WITCH:
				for(int a = 0; a < 18; a++)world.spawnParticle("witchMagic",x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D));
				break;
				
			case CHARM_BLOCK_EFFECT:
				for(int a = 0; a < 10; a++)world.spawnParticle("magicCrit",x+randCenter(width),y+rand.nextDouble()*height*1.4D,z+randCenter(width),randCenter(0.2D),randCenter(0.2D),randCenter(0.2D));
				break;
				
			case CHARM_LAST_RESORT:
				for(int a = 0; a < 35; a++)world.spawnParticle("smoke",x+randCenter(width*1.25D),y+rand.nextDouble()*height,z+randCenter(width*1.25D),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D));
				for(int a = 0; a < 75; a++)HardcoreEnderExpansion.fx.portalBig(world,x+randCenter(width*1.25D),y+rand.nextDouble()*height,z+randCenter(width*1.25D),randCenter(0.1D),randCenter(0.1D),randCenter(0.1D),0.72F+rand.nextFloat()*0.2F);
				world.playSound(x,y+0.1D,z,"mob.endermen.portal",1.1F,1F+rand.nextFloat()*0.1F,false);
				break;
				
			case GEM_TELEPORT_FROM:
				for(int a = 0; a < 20; a++)world.spawnParticle("largesmoke",x+randCenter(width*1.2D),y+rand.nextDouble()*height*0.9D,z+randCenter(width*1.2D),0D,0.04D,0D);
				world.playSound(x,y+1D,z,"mob.endermen.portal",1.2F,rand.nextFloat()*0.05F+0.85F,false);
				break;
				
			case ORB_TRANSFORMATION:
				for(int a = 0; a < 18; a++)world.spawnParticle("largesmoke",x+randCenter(width),y+0.1D+rand.nextDouble()*height,z+randCenter(width),0D,0D,0D);
				world.playSound(x,y,z,"hardcoreenderexpansion:block.random.transform",1.4F,1F+rand.nextFloat()*0.2F,false);
				break;
				
			case LOUSE_REGEN:
				for(int a = 0; a < 6; a++)HardcoreEnderExpansion.fx.aura(world,x+randCenter(width),y+rand.nextDouble()*height,z+randCenter(width),0F,0.7F,0F,14+rand.nextInt(10));
				break;
		}
	}
	
	public static void handleLine(World world, EntityClientPlayerMP player, FXType.Line fx, double x1, double y1, double z1, double x2, double y2, double z2){
		Vec3 lineVec = Vec3.createVectorHelper(x2-x1,y2-y1,z2-z1);
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
				
				for(int a = 0; a < 35; a++)world.spawnParticle("smoke",x1+randCenter(0.8D),y1+randCenter(0.8D),z1+randCenter(0.8D),randCenter(0.01D),randCenter(0.01D),randCenter(0.01D));
				
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
		}
	}
	
	private static double randCenter(double mp){
		return (rand.nextDouble()-rand.nextDouble())*mp;
	}
}
