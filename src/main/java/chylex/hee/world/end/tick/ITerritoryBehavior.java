package chylex.hee.world.end.tick;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@FunctionalInterface
public interface ITerritoryBehavior{
	void tick(World world, NBTTagCompound nbt);
}
