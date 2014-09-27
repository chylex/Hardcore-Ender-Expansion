package chylex.hee.world.structure.island.biome.interaction;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.entity.mob.EntityMobHomelandEnderman;
import chylex.hee.world.structure.island.biome.data.AbstractBiomeInteraction;

public class BiomeInteractionEnchantedIsland{
	public static class InteractionOvertake extends AbstractBiomeInteraction{
		public long groupId;
		
		@Override
		public void init(){
			List<EntityMobHomelandEnderman> endermen = world.getEntitiesWithinAABB(EntityMobHomelandEnderman.class,getIslandBoundingBox());
		}

		@Override
		public void update(){}

		@Override
		public void saveToNBT(NBTTagCompound nbt){}

		@Override
		public void loadFromNBT(NBTTagCompound nbt){}
	}
}
