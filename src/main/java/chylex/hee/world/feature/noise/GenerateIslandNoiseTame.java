package chylex.hee.world.feature.noise;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.StructureWorldFeature;
import chylex.hee.world.util.BoundingBox;
import com.google.common.base.Objects;

/**
 * A special version of island noise generation which uses {@link StructureWorldFeature} and allows
 * conditional spawning with multiple attempts to have more control over the generator.
 */
public class GenerateIslandNoiseTame{
	private final GenerateIslandNoise generator;
	
	private BoundingBox worldArea;
	private boolean isCenteredXZ;
	
	private int minBlocks = -1, maxBlocks = -1;
	private int minSizeX = -1, maxSizeX = -1;
	private int minSizeZ = -1, maxSizeZ = -1;
	
	public GenerateIslandNoiseTame(GenerateIslandNoise generator){
		this.generator = generator;
	}
	
	public void setWorldArea(int radX, int sizeY, int radZ){
		this.worldArea = new BoundingBox(Pos.at(-radX,0,-radZ),Pos.at(radX,sizeY,radZ));
	}
	
	public void setCenterXZ(){
		this.isCenteredXZ = true;
	}
	
	public void setMinBlocks(int minBlocks){
		this.minBlocks = minBlocks;
	}
	
	public void setMaxBlocks(int maxBlocks){
		this.maxBlocks = maxBlocks;
	}
	
	public void setMinSize(int minSizeX, int minSizeZ){
		this.minSizeX = minSizeX;
		this.minSizeZ = minSizeZ;
	}
	
	public void setMaxSize(int maxSizeX, int maxSizeZ){
		this.maxSizeX = maxSizeX;
		this.maxSizeZ = maxSizeZ;
	}
	
	public boolean generate(StructureWorld world, int offsetX, int offsetY, int offsetZ){
		BoundingBox area = Objects.firstNonNull(worldArea,world.getArea());
		StructureWorldFeature part = new StructureWorldFeature(world.getParentWorld(),area.x2,area.y2,area.z2);
		
		generator.generate(part);
		
		if (isCenteredXZ){
			offsetX += part.getCenterOffsetX();
			offsetZ += part.getCenterOffsetZ();
		}
		
		if (minBlocks != -1 && part.getGeneratedBlocks() < minBlocks)return false;
		if (maxBlocks != -1 && part.getGeneratedBlocks() > maxBlocks)return false;
		
		if (minSizeX != -1 && part.getGeneratedSizeX() < minSizeX)return false;
		if (maxSizeX != -1 && part.getGeneratedSizeX() > maxSizeX)return false;
		
		if (minSizeZ != -1 && part.getGeneratedSizeZ() < minSizeZ)return false;
		if (maxSizeZ != -1 && part.getGeneratedSizeZ() > maxSizeZ)return false;
		
		part.insertInto(world,offsetX,offsetY,offsetZ);
		return true;
	}
}
