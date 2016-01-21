package chylex.hee.world.end.tick;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.nbt.NBTCompound;

@FunctionalInterface
public interface ITerritoryBehavior{
	void tick(World world, NBTCompound nbt);
}
