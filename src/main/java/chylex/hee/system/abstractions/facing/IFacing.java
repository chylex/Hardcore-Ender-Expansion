package chylex.hee.system.abstractions.facing;
import net.minecraft.util.EnumFacing;

public interface IFacing{
	EnumFacing toEnumFacing();
	int getX();
	int getY();
	int getZ();
}
