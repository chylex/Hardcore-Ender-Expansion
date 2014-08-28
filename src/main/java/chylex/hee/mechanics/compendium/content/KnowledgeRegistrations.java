package chylex.hee.mechanics.compendium.content;
import net.minecraft.init.Blocks;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.content.objects.ObjectDummy;
import chylex.hee.mechanics.compendium.content.type.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.type.KnowledgeFragmentText;
import chylex.hee.mechanics.compendium.content.type.KnowledgeObject;

public final class KnowledgeRegistrations{
	public static final KnowledgeObject<ObjectDummy>
		HELP = new KnowledgeObject<>(new ObjectDummy());
	
	public static final KnowledgeObject<ObjectBlock>
		TEST_OBJECT = new KnowledgeObject<>(new ObjectBlock(Blocks.bedrock));
	
	public static void initialize(){
		HELP.setFragments(new KnowledgeFragment[]{
			new KnowledgeFragmentText(0).setContents("Welcome to the Ender Compendium, the source of all knowledge about the End!")
		});
		
		KnowledgeCategories.OVERWORLD.addKnowledgeObjects(new KnowledgeObject[]{
			TEST_OBJECT.setPos(0,0).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(1000).setContents("Test fragment").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(1001).setContents("Test fragment 2 Test fragment 2 Test fragment 2 Test fragment 2 Test fragment 2").setPrice(10),
				new KnowledgeFragmentText(1002).setContents("Test fragment 2 Test fragment 2 Test fragment 2 Test fragment 2 Test fragment 2").setPrice(100),
				new KnowledgeFragmentText(1003).setContents("Test fragment 2 Test fragment 2 Test fragment 2 Test fragment 2 Test fragment 2").setPrice(400),
				new KnowledgeFragmentText(1004).setContents("Test fragment 2 Test fragment 2 Test fragment 2 Test fragment 2 Test fragment 2").setPrice(5000)
			})
		});
	}
	
	private KnowledgeRegistrations(){}
}
