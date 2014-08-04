package chylex.hee.entity.fx.handler;
import java.util.Random;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.util.MathUtil;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class FXHandler{
	private static final Random rand = new Random();
	
	public static void handle(World world, EntityClientPlayerMP player, FXType fx, double x, double y, double z){
		switch(fx){
			case ESSENCE_ALTAR_SMOKE:
				for(int a = 0; a < 15; a++)world.spawnParticle("largesmoke",x+randCenter(0.5D),y+0.6D+rand.nextDouble()*0.25D,z+randCenter(0.5D),0D,0.15D,0D);
				break;
				
			case ENDERMAN_BLOODLUST_TRANSFORMATION:
				for(int a = 0; a < 20; a++)world.spawnParticle("largesmoke",x+randCenter(0.3D),y+rand.nextDouble()*2.8D,z+randCenter(0.3D),0D,0D,0D);
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
				
			case LASER_BEAM_DESTROY:
				String[] particles = new String[]{ "largesmoke", "portal", "flame" };
				
				for(int a = 0; a < 10; a++){
					for(String pt:particles)player.worldObj.spawnParticle(pt,x+randCenter(0.5D),y+randCenter(0.5D),z+randCenter(0.5D),0D,0D,0D);
				}
				
				break;
				
			case GEM_LINK:
				for(int a = 0; a < 25; a++)HardcoreEnderExpansion.fx.portalOrbiting(world,x+0.5D,y+0.38D+rand.nextDouble()*0.6D,z+0.5D,rand.nextDouble()*0.045D+0.015D);
				world.playSound(x+0.5D,y+1D,z+0.5D,"hardcoreenderexpansion:environment.gem.link",1F,rand.nextFloat()*0.02F+0.64F,false);
				break;
				
			case GEM_TELEPORT_TO:
				for(int a = 0; a < 25; a++)HardcoreEnderExpansion.fx.portalOrbiting(world,x,y+1.63D+rand.nextDouble()*0.6D,z,rand.nextDouble()*0.045D+0.015D);
				world.playSound(x,y+1.63D,z,"mob.endermen.portal",1.2F,player.worldObj.rand.nextFloat()*0.05F+0.85F,false);
				break;
				
			default:
		}
	}
	
	private static double randCenter(double mp){
		return (rand.nextDouble()-rand.nextDouble())*mp;
	}
}
