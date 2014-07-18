package chylex.hee.mechanics.knowledge.fragment;
import net.minecraft.client.Minecraft;

public class TriggerKnowledgeFragment extends KnowledgeFragment{
	public TriggerKnowledgeFragment(int id){
		super(id);
	}
	
	@Override
	public boolean canShow(int[] unlockedFragments){
		return false;
	}

	@Override
	public int getHeight(Minecraft mc, boolean isUnlocked){
		return 0;
	}

	@Override
	public void render(int x, int y, Minecraft mc, boolean isUnlocked){}
}
