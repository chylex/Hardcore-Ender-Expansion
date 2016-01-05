package chylex.hee.world.end.tick;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.world.end.EndTerritory;

@FunctionalInterface
public interface ITerritoryBehavior{
	void tick(EndTerritory teritory, Pos centerPos, NBTTagCompound nbt, World world);
}
