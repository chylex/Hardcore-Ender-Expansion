package chylex.hee.mechanics.knowledge.fragment;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class KnowledgeFragment{
	public final byte id;
	private int[] requirements = new int[0];
	private IFragmentUnlockAction unlockAction;
	
	protected KnowledgeFragment(int id){
		this.id = (byte)id;
	}
	
	public KnowledgeFragment setUnlockRequirements(int...unlockRequirementIds){
		requirements = unlockRequirementIds;
		return this;
	}
	
	public KnowledgeFragment setUnlockAction(IFragmentUnlockAction action){
		this.unlockAction = action;
		return this;
	}
	
	public int[] getUnlockRequirements(){
		return requirements;
	}
	
	public boolean canShow(int[] unlockedFragments){
		return true;
	}
	
	public void onUnlocked(int[] unlockedFragments, EntityPlayer player){
		if (unlockAction != null)unlockAction.trigger(player);
	}
	
	@SideOnly(Side.CLIENT)
	public abstract int getHeight(Minecraft mc, boolean isUnlocked);
	
	@SideOnly(Side.CLIENT)
	public abstract void render(int x, int y, Minecraft mc, boolean isUnlocked);
	
	@Override
	public final boolean equals(Object o){
		return (o instanceof KnowledgeFragment)?((KnowledgeFragment)o).id == id:false;
	}
	
	@Override
	public final int hashCode(){
		return id;
	}
}
