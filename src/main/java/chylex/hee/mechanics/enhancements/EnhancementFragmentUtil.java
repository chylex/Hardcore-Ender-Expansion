package chylex.hee.mechanics.enhancements;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.knowledge.fragment.EnhancementKnowledgeFragment;

public final class EnhancementFragmentUtil{
	public static KnowledgeFragment[] getEnhancementFragments(Class<? extends Enum> enhancementEnum){
		Enum[] values = enhancementEnum.getEnumConstants();
		KnowledgeFragment[] fragments = new KnowledgeFragment[values.length];
		for(int a = 0; a < values.length; a++)fragments[a] = new EnhancementKnowledgeFragment(a).setEnhancement((IEnhancementEnum)values[a]);
		return fragments;
	}
	
	private EnhancementFragmentUtil(){}
}
