package chylex.hee.world.structure.sanctuary;
import java.util.Random;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
import chylex.hee.world.structure.MapGenScatteredFeatureCustom;
import chylex.hee.world.util.WorldGenChance;

public class MapGenSanctuary extends MapGenScatteredFeatureCustom{
	public MapGenSanctuary(){
		super(12,20,4200,112);
	}

	@Override
	protected boolean canStructureSpawn(int x, int z, double dist, Random rand){
		return rand.nextInt(5) <= 1 && WorldGenChance.checkChance(0.2D+0.8D*WorldGenChance.linear3IncrDecr.calculate(dist,3200D,8500D,36000D),rand);
	}

	@Override
	protected String getStructureName(){
		return "hee_EndSanctuary";
	}
	
	@Override
	protected StructureStart getStructureStart(int x, int z){
		return new StructureSanctuary(worldObj,rand,x,z);
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			ComponentSanctuary sanctuary = new ComponentSanctuary(world,world.rand,(int)player.posX,(int)player.posZ);
			sanctuary.addComponentParts(world,world.rand,new StructureBoundingBox(-9999999,0,-9999999,9999999,256,9999999));
			HardcoreEnderExpansion.notifications.report("Generated sanctuary.");
		}
	};
}