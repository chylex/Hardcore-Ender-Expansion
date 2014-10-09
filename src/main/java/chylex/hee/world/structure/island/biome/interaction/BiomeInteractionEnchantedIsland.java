package chylex.hee.world.structure.island.biome.interaction;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.entity.mob.EntityMobHomelandEnderman;
import chylex.hee.mechanics.misc.HomelandEndermen;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.data.AbstractBiomeInteraction;

public class BiomeInteractionEnchantedIsland{
	public static class InteractionOvertake extends AbstractBiomeInteraction{
		public long groupId = -1L;
		
		@Override
		public void init(){
			List<EntityMobHomelandEnderman> endermen = world.getEntitiesWithinAABB(EntityMobHomelandEnderman.class,getIslandBoundingBox());
			
			for(int attempt = 0; attempt < 5 && !endermen.isEmpty(); attempt++){
				EntityMobHomelandEnderman subject = endermen.remove(rand.nextInt(endermen.size()));
				
				if (subject.getGroupId() != -1L){
					List<EntityMobHomelandEnderman> sameGroup = HomelandEndermen.getInSameGroup(subject);
					if (sameGroup.size() < 5)continue;
					
					if (rand.nextInt(666) < MathUtil.square(sameGroup.size())){
						groupId = subject.getGroupId();
						
						System.out.println("STARTING OVERTAKE WITH "+sameGroup.size()+" MEMBERS");
						for(EntityMobHomelandEnderman e:sameGroup)System.out.println(e.getGroupRole());
						break;
					}
				}
			}
			
			if (groupId == -1L)entity.setDead();
		}

		@Override
		public void update(){}

		@Override
		public void saveToNBT(NBTTagCompound nbt){}

		@Override
		public void loadFromNBT(NBTTagCompound nbt){}
	}
}
