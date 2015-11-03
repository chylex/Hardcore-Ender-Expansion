package chylex.hee.entity.mob.ai;
import java.util.ArrayList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;

public final class AIUtil{
	public static void clearEntityTasks(EntityLiving entity){
		clearTasks(entity.tasks);
		clearTasks(entity.targetTasks);
	}
	
	public static void clearTasks(EntityAITasks taskList){
		for(EntityAITaskEntry entry:new ArrayList<EntityAITaskEntry>(taskList.taskEntries)){
			taskList.removeTask(entry.action);
		}
	}
	
	private AIUtil(){}
}
