package chylex.hee.world.structure.tower;
import java.util.Random;
import net.minecraft.world.gen.structure.StructureStart;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.WorldGenSavefile;
import chylex.hee.system.savedata.types.WorldGenSavefile.WorldGenElement;
import chylex.hee.world.structure.MapGenScatteredFeatureCustom;

public class MapGenTower extends MapGenScatteredFeatureCustom{
	public MapGenTower(){
		super(10,24,350,32);
	}

	@Override
	protected boolean canStructureSpawn(int x, int z, Random rand){
		return !WorldDataHandler.<WorldGenSavefile>get(WorldGenSavefile.class).isAreaBlocked(x>>4,z>>4,WorldGenElement.DUNGEON_TOWER);
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