package chylex.hee.entity.mob.ai;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;

public final class AIToggle<T extends EntityAIBase>{
	private final EntityAITasks taskList;
	private final int priority;
	private final T task;
	private boolean enabled;
	
	public AIToggle(EntityAITasks taskList, int priority, T task){
		this.taskList = taskList;
		this.priority = priority;
		this.task = task;
	}
	
	public void changeState(boolean enableTask){
		if (enableTask && !enabled)taskList.addTask(priority, task);
		else if (!enableTask && enabled)taskList.removeTask(task);
		
		enabled = enableTask;
	}
	
	public T getTask(){
		return task;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
}
