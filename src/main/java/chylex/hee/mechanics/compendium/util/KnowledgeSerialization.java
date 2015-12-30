package chylex.hee.mechanics.compendium.util;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
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
	private static final Map<Class<? extends IObjectHolder<?>>,IObjectSerializer<?>> handlers = new HashMap<>(4,1F);
	
	static{
		register('b',ObjectBlock.class,
			obj -> {
				UniqueIdentifier id = GameRegistryUtil.findIdentifier(obj.block);
				return id.modId+":"+id.name+"/"+obj.meta;
			},
			line -> {
				String[] data = StringUtils.split(line,'/');
				return data.length == 2 ? new BlockInfo(GameData.getBlockRegistry().getObject(data[0]),DragonUtil.tryParse(data[1],ObjectBlock.wildcard)) : null;
			}
		);
		
		register('i',ObjectItem.class,
			obj -> {
				UniqueIdentifier id = GameRegistryUtil.findIdentifier(obj.getItem());
				return id.modId+":"+id.name+"/"+obj.getItemDamage();
			},
			line -> {
				String[] data = StringUtils.split(line,'/');
				return data.length == 2 ? new ItemStack(GameData.getItemRegistry().getObject(data[0]),1,DragonUtil.tryParse(data[1],ObjectItem.wildcard)) : null;
			}
		);
		
		register('m',ObjectMob.class,
			obj -> (String)EntityList.classToStringMapping.get(obj),
			line -> (Class<? extends EntityLiving>)EntityList.stringToClassMapping.get(line)
		);
		
		register('d',ObjectDummy.class,
			obj -> obj,
			line -> line
		);
	}
	
	public static <T extends IObjectHolder<R>,R> String serialize(KnowledgeObject obj){ // fucking hell, javac is stupid
		char chr = types.inverse().get(obj.holder.getClass()).charValue();
		return chr+((IObjectSerializer)handlers.get(obj.holder.getClass())).serialize(obj.holder.getUnderlyingObject());
	}
	
	public static <T extends IObjectHolder<R>,R> KnowledgeObject<T> deserialize(String line){
		return KnowledgeObject.fromObject(handlers.get(types.get(line.charAt(0))).deserialize(line.substring(1)));
	}
	
	private static <T extends IObjectHolder<R>,R> void register(char chr, Class<T> cls, final Function<R,String> serializer, final Function<String,R> deserializer){
		types.put(Character.valueOf(chr),cls);
		
		handlers.put(cls,new IObjectSerializer<R>(){
			@Override
			public String serialize(R obj){
				return serializer.apply(obj);
			}

			@Override
			public R deserialize(String line){
				return deserializer.apply(line);
			}
		});
	}
	
	private static interface IObjectSerializer<R>{
		String serialize(R obj);
		R deserialize(String line);
	}
	
	private KnowledgeSerialization(){}
}
