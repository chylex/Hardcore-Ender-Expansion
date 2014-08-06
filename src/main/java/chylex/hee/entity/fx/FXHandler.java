package chylex.hee.entity.fx;
import java.util.Random;
import net.minecraft.client.entity.EntityClientPlayerMP;
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
				
			case GEM_TELEPORT_FROM:
				for(int a = 0; a < 20; a++)world.spawnParticle("largesmoke",x+randCenter(width*1.2D),y+rand.nextDouble()*height*0.9D,z+randCenter(width*1.2D),0D,0.04D,0D);
				world.playSound(x,y+1D,z,"mob.endermen.portal",1.2F,rand.nextFloat()*0.05F+0.85F,false);
				break;
				
			case ORB_TRANSFORMATION:
				for(int a = 0; a < 18; a++)world.spawnParticle("largesmoke",x+randCenter(width),y+0.1D+rand.nextDouble()*height,z+randCenter(width),0D,0D,0D);
				world.playSound(x,y,z,"hardcoreenderexpansion:block.random.transform",1.4F,1F+rand.nextFloat()*0.2F,false);
				break;
		}
	}
	
	private static double randCenter(double mp){
		return (rand.nextDouble()-rand.nextDouble())*mp;
	}
}
