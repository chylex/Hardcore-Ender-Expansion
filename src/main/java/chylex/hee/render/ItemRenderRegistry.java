package chylex.hee.render;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockList.BlockData;
import chylex.hee.block.state.IAbstractState;
import chylex.hee.system.logging.Log;

@SideOnly(Side.CLIENT)
public class ItemRenderRegistry{
	public static void registerAll(RenderItem renderItem){
		ItemModelMesher mesher = renderItem.getItemModelMesher();
		
		loadBlockModels();
	}
	
	private static void loadBlockModels(){
		Set<String> models = new HashSet<>();
		
		for(Entry<String,BlockData> data:BlockList.getBlockEntries()){
			Block block = data.getValue().block;
			
			if (block instanceof IAbstractState){
				IAbstractState state = (IAbstractState)block;
				
				models.clear();
				Collection<?> values = state.getPropertyArray()[0].getAllowedValues();
				String propName = state.getPropertyArray()[0].getName();
				
				for(Object value:values)models.add(new StringBuilder().append(data.getKey()).append('#').append(propName).append('=').append(value).toString());
				Log.debug("Registering $0 models: $1",data.getKey(),models);
				
				// TODO ModelBakery.addVariantName(Item.getItemFromBlock(block),models.toArray(new String[models.size()]));
			}
		}
	}
}
