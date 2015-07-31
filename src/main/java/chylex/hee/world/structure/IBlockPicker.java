package chylex.hee.world.structure;
import java.util.Random;
import chylex.hee.system.abstractions.BlockInfo;

@FunctionalInterface
public interface IBlockPicker{
	BlockInfo pick(Random rand);
}
