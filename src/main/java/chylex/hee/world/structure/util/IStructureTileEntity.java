package chylex.hee.world.structure.util;
import java.util.Random;
import net.minecraft.tileentity.TileEntity;

@FunctionalInterface
public interface IStructureTileEntity{
	void generateTile(TileEntity tile, Random rand);
}
