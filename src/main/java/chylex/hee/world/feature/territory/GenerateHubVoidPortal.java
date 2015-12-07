package chylex.hee.world.feature.territory;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.entity.technical.EntityTechnicalVoidPortal;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.structure.StructureWorld;

public class GenerateHubVoidPortal implements ITerritoryFeature{
	private int attempts;
	
	public void setAttempts(int attempts){
		this.attempts = attempts;
	}
	
	@Override
	public boolean generate(EndTerritory territory, StructureWorld world, Random rand){
		for(int forced = 0; forced < 2; forced++){
			for(int attempt = 0; attempt < attempts; attempt++){
				if (generateVoidPortal(world,rand,forced == 1))return true;
			}
		}
		
		return false;
	}
	
	private boolean generateVoidPortal(StructureWorld world, Random rand, boolean ignoreHeightChecks){
		final double angle = rand.nextDouble()*Math.PI*2D;
		final double dist = 24D+rand.nextDouble()*8D;
		
		final int x = MathUtil.floor(Math.cos(angle)*dist);
		final int z = MathUtil.floor(Math.sin(angle)*dist);
		final int testY = world.getTopY(x,z);
		
		if (testY <= 0)return false;
		
		int y = testY, topY = testY;
		
		for(int offX = -8; offX <= 8; offX++){
			for(int offZ = -8; offZ <= 8; offZ++){
				double offDist = MathUtil.distance(offX,offZ);
				
				if (offDist < 8D && !(offX == 0 && offZ == 0)){
					int top = world.getTopY(x+offX,z+offZ);
					if (world.getBlock(x+offX,top,z+offZ) == BlockList.ravish_brick)return false;
					
					if (offDist < 7D)y = Math.min(y,top);
					topY = Math.max(topY,top);
				}
			}
		}
		
		if (!ignoreHeightChecks && topY-y > 1)return false;
		
		final int height = 10;
		
		for(int offX = -8; offX <= 8; offX++){
			for(int offZ = -8; offZ <= 8; offZ++){
				double offDist = MathUtil.distance(offX,offZ);
				
				if (offDist < 7D){
					for(int offY = 0; offY <= topY-y; offY++){
						world.setAir(x+offX,y+offY,z+offZ);
					}
					
					world.setBlock(x+offX,y-2,z+offZ,Blocks.end_stone); // End Powder Ore might be under it, which makes random particles
				}
				
				if (offDist < 3D)world.setBlock(x+offX,y-1,z+offZ,BlockList.void_portal,Meta.voidPortalTravel);
				else if (offDist < 4D)world.setBlock(x+offX,y-1,z+offZ,BlockList.void_portal_frame);
				else if (offDist < 6D)world.setBlock(x+offX,y-1,z+offZ,BlockList.dark_loam);
				else if (offDist < 7D){
					world.setBlock(x+offX,y-1,z+offZ,Blocks.end_stone); // same reason as above
					world.setBlock(x+offX,y,z+offZ,BlockList.dark_loam_slab);
				}
				else if (offDist < 8D){
					world.setBlock(x+offX,y,z+offZ,Blocks.end_stone);
					world.setAir(x+offX,y+1,z+offZ);
				}
				
				if (offDist < 6D)world.setBlock(x+offX,y+height,z+offZ,BlockList.dark_loam_slab,Meta.slabTop);
				else if (offDist < 7D)world.setBlock(x+offX,y+height,z+offZ,BlockList.dark_loam);
			}
		}
		
		EntityTechnicalVoidPortal portalEntity = new EntityTechnicalVoidPortal(world.getParentWorld());
		portalEntity.setPosition(x+0.5D,y-1D,z+0.5D);
		world.addEntity(portalEntity);
		
		for(int cornerX = -1; cornerX <= 1; cornerX += 2){
			for(int cornerZ = -1; cornerZ <= 1; cornerZ += 2){
				for(int offY = 0; offY <= height; offY++){
					world.setBlock(x+cornerX*5,y+offY,z+cornerZ*5,BlockList.dark_loam);
				}
			}
		}
		
		return true;
	}
}
