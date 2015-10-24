package chylex.hee.mechanics.compendium.util;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.content.objects.ObjectDummy;
import chylex.hee.mechanics.compendium.content.objects.ObjectItem;
import chylex.hee.mechanics.compendium.content.objects.ObjectMob;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.GameRegistryUtil;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public final class KnowledgeSerialization{
	private static final BiMap<Character,Class<? extends IObjectHolder<?>>> types = HashBiMap.create(4);
	private static final Map<Class<? extends IObjectHolder<?>>,IObjectSerializer<?,?>> handlers = new HashMap<>(4,1F);
	
	static{
		types.put('b',ObjectBlock.class);
		types.put('i',ObjectItem.class);
		types.put('m',ObjectMob.class);
		types.put('d',ObjectDummy.class);
		
		handlers.put(ObjectBlock.class,new IObjectSerializer<ObjectBlock,BlockInfo>(){
			@Override
			public String serialize(BlockInfo obj){
				UniqueIdentifier id = GameRegistryUtil.findIdentifier(obj.block);
				return id.modId+":"+id.name+"/"+obj.meta;
			}

			@Override
			public BlockInfo deserialize(String line){
				String[] data = StringUtils.split(line,'/');
				return data.length == 2 ? new BlockInfo(GameData.getBlockRegistry().getObject(data[0]),DragonUtil.tryParse(data[1],ObjectBlock.wildcard)) : null;
			}
		});
		
		handlers.put(ObjectItem.class,new IObjectSerializer<ObjectItem,ItemStack>(){
			@Override
			public String serialize(ItemStack obj){
				UniqueIdentifier id = GameRegistryUtil.findIdentifier(obj.getItem());
				return id.modId+":"+id.name+"/"+obj.getItemDamage();
			}

			@Override
			public ItemStack deserialize(String line){
				String[] data = StringUtils.split(line,'/');
				return data.length == 2 ? new ItemStack(GameData.getItemRegistry().getObject(data[0]),1,DragonUtil.tryParse(data[1],ObjectItem.wildcard)) : null;
			}
		});
		
		handlers.put(ObjectMob.class,new IObjectSerializer<ObjectMob,Class<? extends EntityLiving>>(){
			@Override
			public String serialize(Class<? extends EntityLiving> obj){
				return (String)EntityList.classToStringMapping.get(obj);
			}

			@Override
			public Class<? extends EntityLiving> deserialize(String line){
				return (Class<? extends EntityLiving>)EntityList.stringToClassMapping.get(line);
			}
		});
		
		handlers.put(ObjectDummy.class,new IObjectSerializer<ObjectDummy,String>(){
			@Override
			public String serialize(String obj){
				return obj;
			}

			@Override
			public String deserialize(String line){
				return line;
			}
		});
	}
	
	public static <T extends IObjectHolder<R>,R> String serialize(KnowledgeObject<T> obj){
		char chr = types.inverse().get(obj.holder.getClass()).charValue();
		return chr+((IObjectSerializer<T,R>)handlers.get(obj.holder.getClass())).serialize(obj.holder.getUnderlyingObject());
	}
	
	public static <T extends IObjectHolder<R>,R> KnowledgeObject<T> deserialize(String line){
		return KnowledgeObject.fromObject(handlers.get(types.get(line.charAt(0))).deserialize(line.substring(1)));
	}
	
	private static interface IObjectSerializer<T extends IObjectHolder<R>,R>{
		String serialize(R obj);
		R deserialize(String line);
	}
	
	private KnowledgeSerialization(){}
}
