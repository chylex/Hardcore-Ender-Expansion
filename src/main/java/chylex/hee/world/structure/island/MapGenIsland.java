package chylex.hee.world.structure.island;
import java.util.Random;
import net.minecraft.world.gen.structure.StructureStart;
import chylex.hee.world.structure.MapGenScatteredFeatureCustom;

public class MapGenIsland extends MapGenScatteredFeatureCustom{
	public MapGenIsland(){
		super(13,28,1600,208);
	}

	@Override
	protected boolean canStructureSpawn(int x, int z, Random rand){
		return rand.nextInt(7) <= 4;
	}

	@Override
	protected String getStructureName(){
		return "hardcoreenderdragon_EndIsland";
	}
	
	@Override
	protected StructureStart getStructureStart(int x, int z){
		return new StructureIsland(worldObj,rand,x,z);
	}
}
