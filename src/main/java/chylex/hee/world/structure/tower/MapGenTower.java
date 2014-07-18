package chylex.hee.world.structure.tower;
import java.util.Random;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.gen.structure.StructureStart;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.MapGenScatteredFeatureCustom;

public class MapGenTower extends MapGenScatteredFeatureCustom{
	public MapGenTower(){
		super(10,24,350,32);
	}

	@Override
	protected boolean canStructureSpawn(int x, int z, Random rand){
		ChunkPosition island = worldObj.getChunkProvider().func_147416_a(worldObj,"hardcoreenderdragon_Island",x*16,50,z*16); // OBFUSCATED get nearby structure
		return island == null || MathUtil.distance(x*16-island.chunkPosX,z*16-island.chunkPosZ) > 170;
	}

	@Override
	protected String getStructureName(){
		return "hardcoreenderdragon_EndTower";
	}
	
	@Override
	protected StructureStart getStructureStart(int x, int z){
		return new StructureTower(worldObj,rand,x,z);
	}
}