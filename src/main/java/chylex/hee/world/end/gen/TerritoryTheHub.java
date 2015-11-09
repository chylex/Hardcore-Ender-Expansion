package chylex.hee.world.end.gen;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.world.end.TerritoryGenerator;
import chylex.hee.world.structure.StructureWorld;

public class TerritoryTheHub extends TerritoryGenerator{
	public TerritoryTheHub(StructureWorld world, Random rand){
		super(world,rand);
	}
	
	@Override
	public void generate(){
		Pos.forEachBlock(Pos.at(-size,3,-size),Pos.at(size,3,size),pos -> world.setBlock(pos.x,pos.y,pos.z,Blocks.lapis_block));
	}
}
