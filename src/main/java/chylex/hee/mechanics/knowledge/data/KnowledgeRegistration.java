package chylex.hee.mechanics.knowledge.data;
import gnu.trove.map.hash.TObjectByteHashMap;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.knowledge.data.renderer.DummyRenderer;
import chylex.hee.mechanics.knowledge.data.renderer.IRegistrationRenderer;
import chylex.hee.mechanics.knowledge.fragment.KnowledgeFragment;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C19KnowledgeRegistrationNotification;
import chylex.hee.system.logging.Log;

public class KnowledgeRegistration{
	public static final HashMap<String,KnowledgeRegistration> lookup = new HashMap<>();
	private static final TObjectByteHashMap<String> compendiumSlotCache = new TObjectByteHashMap<>();
	private static final DummyRenderer dummyRenderer = new DummyRenderer();
	
	public final KnowledgeCategory category;
	public final ObjectFragmentSet fragmentSet;
	private IRegistrationRenderer renderer;
	public final String identifier;
	private int x,y;
	private byte textureIndex;
	
	public KnowledgeRegistration(KnowledgeCategory category, String identifier){
		if (category != null)(this.category = category).registrations.add(this);
		else this.category = null;
		
		lookup.put(identifier,this);
		
		this.fragmentSet = new ObjectFragmentSet(this);
		this.identifier = identifier;
	}
	
	public KnowledgeRegistration setPosition(int x, int y){
		this.x = x;
		this.y = y;
		return this;
	}
	
	public KnowledgeRegistration setRenderer(IRegistrationRenderer renderer){
		this.renderer = renderer;
		return this;
	}
	
	public KnowledgeRegistration setBackgroundTextureIndex(int index){
		this.textureIndex = (byte)index;
		return this;
	}
	
	public KnowledgeRegistration setFragments(KnowledgeFragment[] fragments){
		for(KnowledgeFragment fragment:fragments)this.fragmentSet.addFragment(fragment);
		return this;
	}
	
	public int getX(){
		return category.getTargetOffsetX()+x;
	}

	public int getY(){
		return category.getTargetOffsetY()+y;
	}
	
	public IRegistrationRenderer getRenderer(){
		return renderer == null?dummyRenderer:renderer;
	}
	
	public int getBackgroundTextureIndex(){
		return textureIndex;
	}
	
	public boolean hasSomeFragments(EntityPlayer player){
		byte cache = compendiumSlotCache.get(player.getCommandSenderName());
		if (cache != compendiumSlotCache.getNoEntryValue()){
			ItemStack invIS = player.inventory.mainInventory[cache];
			
			if (invIS != null && invIS.getItem() == ItemList.ender_compendium)return fragmentSet.getUnlockedFragments(invIS).length > 0;
			else compendiumSlotCache.remove(player.getCommandSenderName());
		}
		
		for(int a = 0; a < player.inventory.mainInventory.length; a++){
			ItemStack invIS = player.inventory.mainInventory[a];
			if (invIS == null || invIS.getItem() != ItemList.ender_compendium)continue;
			
			compendiumSlotCache.put(player.getCommandSenderName(),(byte)a);
			return fragmentSet.getUnlockedFragments(invIS).length > 0;
		}
		
		return false;
	}
	
	public UnlockResult tryUnlockFragment(EntityPlayer player, float chance){
		return tryUnlockFragment(player,chance,null);
	}
	
	public UnlockResult tryUnlockFragment(EntityPlayer player, float chance, byte[] unlockableFragments){
		if (player == null){
			Thread.dumpStack();
			Log.error("Player is null when unlocking knowledge fragment!");
			return UnlockResult.BAD_LUCK;
		}
		
		if (player.getRNG().nextFloat() >= chance)return UnlockResult.BAD_LUCK;
		
		int compendiumSlot = -1,paperSlot = -1;
		ItemStack[] inv = player.inventory.mainInventory;
		
		byte cache = compendiumSlotCache.get(player.getCommandSenderName());
		if (cache != compendiumSlotCache.getNoEntryValue()){
			ItemStack invIS = inv[cache];
			
			if (invIS != null && invIS.getItem() == ItemList.ender_compendium)compendiumSlot = cache;
			else compendiumSlotCache.remove(player.getCommandSenderName());
		}
		
		for(int a = 0; a < inv.length && (compendiumSlot == -1 || paperSlot == -1); a++){
			ItemStack invIS = inv[a];
			
			if (invIS == null)continue;
			if (compendiumSlot == -1 && invIS.getItem() == ItemList.ender_compendium)compendiumSlotCache.put(player.getCommandSenderName(),(byte)(compendiumSlot = a));
			if (paperSlot == -1 && invIS.getItem() == Items.paper)paperSlot = a;
		}
		
		if (compendiumSlot == -1)return UnlockResult.NO_COMPENDIUM;
		
		if (paperSlot == -1){
			PacketPipeline.sendToPlayer(player,new C19KnowledgeRegistrationNotification(-1));
			return UnlockResult.NO_PAPER;
		}
		
		if (fragmentSet.unlockRandomFragment(player,inv[compendiumSlot],unlockableFragments)){
			if (--inv[paperSlot].stackSize == 0)inv[paperSlot] = null;
			player.inventoryContainer.detectAndSendChanges();
			return UnlockResult.SUCCESSFUL;
		}
		
		return UnlockResult.NOTHING_TO_UNLOCK;
	}
}
