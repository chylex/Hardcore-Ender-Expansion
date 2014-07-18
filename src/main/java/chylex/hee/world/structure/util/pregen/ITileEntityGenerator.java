package chylex.hee.world.structure.util.pregen;
import net.minecraft.tileentity.TileEntity;

public interface ITileEntityGenerator{
	void onTileEntityRequested(String key, TileEntity tile);
}
