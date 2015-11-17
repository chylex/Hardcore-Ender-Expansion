package chylex.hee.entity.mob.ai;
import java.util.ArrayList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;

public final class AIUtil{
	public static final byte mutexIncompatible = 0b0111;
	public static final byte mutexOverrideMovement = 0b0001;
	public static final byte mutexOverrideWatching = 0b0010;
	public static final byte mutexOverrideSwimming = 0b0100;
	public static final byte mutexTarget = 0b0001;
	
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
