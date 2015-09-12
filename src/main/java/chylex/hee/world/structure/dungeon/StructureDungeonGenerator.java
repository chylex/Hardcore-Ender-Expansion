package chylex.hee.world.structure.dungeon;
import chylex.hee.world.structure.IStructureGenerator;

public abstract class StructureDungeonGenerator implements IStructureGenerator{
	protected final StructureDungeon dungeon;
	
	public StructureDungeonGenerator(StructureDungeon dungeon){
		this.dungeon = dungeon;
	}
	
	@FunctionalInterface
	public static interface Constructor<T extends StructureDungeonGenerator>{
		T construct(StructureDungeon dungeon);
	}
}
