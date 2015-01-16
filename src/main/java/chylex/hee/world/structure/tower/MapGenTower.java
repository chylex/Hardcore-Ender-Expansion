package chylex.hee.world.structure.tower;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.gen.structure.StructureStart;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.MapGenScatteredFeatureCustom;
import chylex.hee.world.structure.island.util.IslandSpawnChecker;
import chylex.hee.world.util.BlockLocation;
import chylex.hee.world.util.WorldGenChance;

public class MapGenTower extends MapGenScatteredFeatureCustom{
	public MapGenTower(){
		super(9,15,350,32);
	}

	@Override
	protected boolean canStructureSpawn(int x, int z, double dist, Random rand){
		if (rand.nextInt(100) > 28 || (dist > 900D && !WorldGenChance.checkChance(0.25D+0.75D*WorldGenChance.cubic2Decr.calculate(dist,900D,3800D),rand)))return false;
		
		long seed1 = worldObj.getWorldInfo().getSeed();
		int seed2 = 1+WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getDragonDeathAmount();

		List<BlockLocation> locs = new ArrayList<>();
		
		for(int xx = x-12; xx <= x+12; xx++){
			for(int zz = z-12; zz <= z+12; zz++){
				if (IslandSpawnChecker.getIslandBiomeAt(xx,zz,seed1,seed2) != -1){
					locs.add(new BlockLocation(xx*16+(IslandSpawnChecker.featureSize>>1),0,zz*16+(IslandSpawnChecker.featureSize>>1)));
				}
			}
		}
		
		int xx = x*16+16, zz = z*16+16, check = MathUtil.ceil(IslandSpawnChecker.featureSize*0.6F);
		
		for(BlockLocation loc:locs){
			if (MathUtil.distance(xx-loc.x,zz-loc.z) < check)return false;
		}
		
		return true;
	}

	@Override
	protected String getStructureName(){
		return "hardcoreenderdragon_EndTower";
	}
	
	@Override
	protected StructureStart getStructureStart(int x, int z){
		return new StructureTower(worldObj,rand,x,z);
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			int x = (int)player.posX/16, z = (int)player.posZ/16;
			
			long seed1 = world.getWorldInfo().getSeed();
			int seed2 = 1+WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getDragonDeathAmount();
			
			List<BlockLocation> locs = new ArrayList<>();
			
			for(int xx = x-12; xx <= x+12; xx++){
				for(int zz = z-12; zz <= z+12; zz++){
					if (IslandSpawnChecker.getIslandBiomeAt(xx,zz,seed1,seed2) != -1){
						locs.add(new BlockLocation(xx*16+(IslandSpawnChecker.featureSize>>1),0,zz*16+(IslandSpawnChecker.featureSize>>1)));
					}
				}
			}
			
			int xx = x*16, zz = z*16, check = MathUtil.ceil(IslandSpawnChecker.featureSize*0.6F);
			
			for(BlockLocation loc:locs){
				if (MathUtil.distance(xx-loc.x,zz-loc.z) < check){
					HardcoreEnderExpansion.notifications.report("cannot "+MathUtil.distance(xx-loc.x,zz-loc.z));
					return;
				}
			}
			
			HardcoreEnderExpansion.notifications.report("can");
		}
	};
}