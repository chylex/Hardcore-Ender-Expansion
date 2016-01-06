package chylex.hee.world.end.tick;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.world.end.EndTerritory;

public class TerritoryBehaviorTest implements ITerritoryBehavior{
	@Override
	public void tick(EndTerritory teritory, Pos centerPos, NBTTagCompound nbt, World world){
		nbt.setLong("pos",centerPos.toLong());
	}
}
