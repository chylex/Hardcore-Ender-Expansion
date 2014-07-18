package chylex.hee.mechanics.knowledge.data;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.knowledge.util.IGuiItemStackRenderer;

public class KnowledgeCategory implements IGuiItemStackRenderer{
	public static final KnowledgeCategory ALTARS_AND_ESSENCES, ENHANCEMENTS, CREATURES, ENVIRONMENT_STRUCTURES,
										  ETHEREAL_COGNITION, BLOCKS_AND_ORES, ENDER_DEXTERITY, MISCELLANEOUS;
	
	public static final KnowledgeCategory[] categories = new KnowledgeCategory[]{
		ALTARS_AND_ESSENCES = new KnowledgeCategory("essences", 0, -60, "Altars and Tables", new ItemStack(BlockList.essence_altar, 1, EssenceType.DRAGON.id)),
		ENHANCEMENTS = new KnowledgeCategory("enhancements", 45, -45, "Enhancements", new ItemStack(Items.ender_pearl)),
		ENDER_DEXTERITY = new KnowledgeCategory("enderpowers", 60, 0, "Ender Dexterity", new ItemStack(ItemList.transference_gem)),
		BLOCKS_AND_ORES = new KnowledgeCategory("ores", 45, 45, "Blocks and Ores", new ItemStack(BlockList.end_powder_ore)),
		CREATURES = new KnowledgeCategory("creatures", 0, 60, "Creatures", new ItemStack(ItemList.enderman_head)),
		ETHEREAL_COGNITION = new KnowledgeCategory("ghoststuff", -45, 45, "Ethereal cognition", new ItemStack(ItemList.ectoplasm)),
		ENVIRONMENT_STRUCTURES = new KnowledgeCategory("worldstructures", -60, 0, "Environment structures", new ItemStack(BlockList.end_terrain, 1, BlockEndstoneTerrain.metaEnchanted)),
		MISCELLANEOUS = new KnowledgeCategory("miscellaneous", -45, -45, "Miscellaneous", new ItemStack(ItemList.music_disk)),
	};
	
	public final String identifier;
	private final int x,y;
	private final int targetOffsetX,targetOffsetY;
	private final String tooltip;
	private final ItemStack is;
	public final List<KnowledgeRegistration> registrations;
	
	KnowledgeCategory(String identifier, int x, int y, String tooltip, ItemStack is){
		this.identifier = identifier;
		this.x = x;
		this.y = y;
		this.targetOffsetX = (int)Math.round(x*5.8333D);
		this.targetOffsetY = (int)Math.round(y*5.8333D);
		this.tooltip = tooltip;
		this.is = is;
		this.registrations = new ArrayList<KnowledgeRegistration>();
	}

	@Override
	public int getX(){
		return x;
	}

	@Override
	public int getY(){
		return y;
	}
	
	public int getTargetOffsetX(){
		return targetOffsetX;
	}
	
	public int getTargetOffsetY(){
		return targetOffsetY;
	}
	
	@Override
	public String getTooltip(){
		return tooltip;
	}

	@Override
	public ItemStack getItemStack(){
		return is;
	}
}
