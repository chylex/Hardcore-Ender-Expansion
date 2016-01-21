package chylex.hee.world.end.gen;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.end.TerritoryGenerator;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.util.BoundingBox;

public class TerritoryArcaneConjunctions extends TerritoryGenerator{
	public TerritoryArcaneConjunctions(EndTerritory territory, EnumSet variations, StructureWorld world, Random rand){
		super(territory,variations,world,rand);
	}

	@Override
	public void generate(){
		final BoundingBox area = world.getArea();
		
		final List<Pos> generatedBlobs = new ArrayList<>();
		final int blobAmount = 70+rand.nextInt(20);
		
		int minDist = 8;
		
		for(int blob = 0; blob < blobAmount;){
			final float rad = 5F+2F*rand.nextFloat();
			final int radOffset = MathUtil.floor(rad*0.725F);
			
			Pos pos = Pos.at(
				area.x1+minDist+rand.nextInt(area.x2-area.x1-minDist*2),
				area.y1+minDist+rand.nextInt((area.y2-area.y1)/2-minDist*2)+radOffset,
				area.z1+minDist+rand.nextInt(area.z2-area.z1-minDist*2)
			);
			
			if (!generatedBlobs.contains(pos)){
				generateBlob(pos.getX(),pos.getY()-radOffset,pos.getZ(),rad,0.85F,0F);
				generatedBlobs.add(pos);
				blob++;
			}
		}
		
		final List<Pos> required = new ArrayList<>(generatedBlobs);
		final int connectionAmount = MathUtil.floor(blobAmount*(1.5D+0.5D*rand.nextDouble()));
		
		for(int connection = 0; connection < connectionAmount; connection++){
			Pos pos = required.isEmpty() ? generatedBlobs.get(rand.nextInt(generatedBlobs.size())) : required.remove(rand.nextInt(required.size()));
			
			Pos targetPos = null;
			double closest = Double.MAX_VALUE;
			
			for(int attempt = 0; attempt < 10; attempt++){
				Pos testPos = generatedBlobs.get(rand.nextInt(generatedBlobs.size()));
				double dist = testPos.distance(pos);
				
				if (dist < closest){
					targetPos = testPos;
					closest = dist;
					
					if (dist < 32D)break;
				}
			}
			
			if (targetPos != null && closest < 64D)generateConnection(pos,targetPos);
		}
	}
	
	private final void generateBlob(final int x, final int y, final int z, final double rad, final float cutOffTop, final float cutOffBottom){ // TODO fix cutOffBottom
		double radSq = MathUtil.square(rad+0.5D);
		int dist = MathUtil.ceil(radSq);
		Pos center = Pos.at(x,y,z);
		
		Pos.forEachBlock(center.offset(-dist,-dist,-dist),center.offset(dist,dist,dist),pos -> {
			if (MathUtil.distanceSquared(pos.x-x,pos.y-y,pos.z-z) <= radSq && pos.y < y+rad*cutOffTop && pos.y > y-rad*(1F-cutOffBottom)){
				world.setBlock(pos,Blocks.end_stone);
			}
		});
	}
	
	private final void generateConnection(Pos startPos, Pos endPos){
		Vec pos = Vec.xyz(startPos.getX()+0.5D,startPos.getY()+0.5D,startPos.getZ()+0.5D);
		Vec dir = Vec.xyz(endPos.getX()-startPos.getX(),endPos.getY()-startPos.getY(),endPos.getZ()-startPos.getZ());
		
		int length = MathUtil.floor(dir.length())*2;
		dir = dir.normalized().multiplied(0.5D);
		
		for(int a = 0; a < length; a++){
			generateBlob(MathUtil.floor(pos.x),MathUtil.floor(pos.y),MathUtil.floor(pos.z),0.35D+0.75D*rand.nextDouble(),0.95F,0.95F);
			pos.moveBy(dir);
		}
	}
}
