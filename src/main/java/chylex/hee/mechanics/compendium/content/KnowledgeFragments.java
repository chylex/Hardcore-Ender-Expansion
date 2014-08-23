package chylex.hee.mechanics.compendium.content;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragment;

public final class KnowledgeFragments{
	private static final TIntObjectMap<KnowledgeFragment> fragmentMap = new TIntObjectHashMap<>();
	
	public static KnowledgeFragment getById(int id){
		return fragmentMap.get(id);
	}
	
	public static void initialize(){
		//
		
		for(KnowledgeFragment fragment:KnowledgeFragment.getAllFragments())fragmentMap.put(fragment.globalID,fragment);
	}
	
	private KnowledgeFragments(){}
}
