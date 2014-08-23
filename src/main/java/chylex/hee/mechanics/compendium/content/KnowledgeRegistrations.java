package chylex.hee.mechanics.compendium.content;
import net.minecraft.init.Blocks;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.content.type.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.type.KnowledgeFragmentText;
import chylex.hee.mechanics.compendium.content.type.KnowledgeObject;

public final class KnowledgeRegistrations{
	private static final TIntObjectMap<KnowledgeFragment> fragmentMap = new TIntObjectHashMap<>();
	
	public static KnowledgeFragment getById(int id){
		return fragmentMap.get(id);
	}
	
	public static KnowledgeObject getObject(Object obj){ // TODO
		return null;
	}
	
	public static final KnowledgeObject<ObjectBlock> TEST_OBJECT = new KnowledgeObject<>(new ObjectBlock(Blocks.bedrock));
	
	public static void initialize(){
		KnowledgeCategories.OVERWORLD.addKnowledgeObjects(new KnowledgeObject[]{
			TEST_OBJECT.setPos(0,0).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(1000).setContents("Test fragment").setPrice(5)
			})
		});
		
		for(KnowledgeFragment fragment:KnowledgeFragment.getAllFragments())fragmentMap.put(fragment.globalID,fragment);
	}
	
	private KnowledgeRegistrations(){}
}
