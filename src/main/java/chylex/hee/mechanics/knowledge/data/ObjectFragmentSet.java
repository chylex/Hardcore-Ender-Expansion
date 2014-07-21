package chylex.hee.mechanics.knowledge.data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.mechanics.knowledge.fragment.KnowledgeFragment;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C28KnowledgeRegistrationNotification;

public class ObjectFragmentSet{
	private final KnowledgeRegistration registration;
	private final Set<KnowledgeFragment> fragments;
	
	public ObjectFragmentSet(KnowledgeRegistration registration){
		this.registration = registration;
		this.fragments = new LinkedHashSet<KnowledgeFragment>();
	}
	
	public void addFragment(KnowledgeFragment fragment){
		fragments.add(fragment);
	}
	
	public KnowledgeFragment getFragmentById(int id){
		for(KnowledgeFragment fragment:fragments){
			if (fragment.id == id)return fragment;
		}
		return null;
	}
	
	public Set<KnowledgeFragment> getAllFragments(){
		return Collections.unmodifiableSet(fragments);
	}
	
	// ITEMSTACK METHODS
	
	public int[] getUnlockedFragments(ItemStack is){
		if (is.stackTagCompound == null)return ArrayUtils.EMPTY_INT_ARRAY;
		return is.stackTagCompound.getCompoundTag("knowledge").getCompoundTag(registration.category.identifier).getIntArray(registration.identifier);
	}
	
	public boolean checkUnlockRequirements(ItemStack is, int fragmentId){
		int[] unlocked = getUnlockedFragments(is);
		
		for(int requirement:getFragmentById(fragmentId).getUnlockRequirements()){
			if (!ArrayUtils.contains(unlocked,requirement))return false;
		}
		
		return true;
	}
	
	public void unlockAllFragments(ItemStack is){
		if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
		
		NBTTagCompound knowledgeData = is.stackTagCompound.getCompoundTag("knowledge");
		NBTTagCompound category = knowledgeData.getCompoundTag(registration.category.identifier);
		
		int[] fragments = new int[this.fragments.size()];
		int a = 0;
		for(KnowledgeFragment fragment:this.fragments)fragments[a++] = fragment.id;
		
		category.setIntArray(registration.identifier,fragments);
		knowledgeData.setTag(registration.category.identifier,category);
		is.stackTagCompound.setTag("knowledge",knowledgeData);
	}
	
	public boolean unlockFragment(EntityPlayer player, ItemStack is, short fragmentId){
		if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
		
		NBTTagCompound knowledgeData = is.stackTagCompound.getCompoundTag("knowledge");
		NBTTagCompound category = knowledgeData.getCompoundTag(registration.category.identifier);
		
		if (category.hasKey(registration.identifier)){
			int[] unlocked = category.getIntArray(registration.identifier);
			if (ArrayUtils.contains(unlocked,fragmentId))return false;
			
			registration.fragmentSet.getFragmentById(fragmentId).onUnlocked(unlocked,player);
			
			int[] newUnlocked = Arrays.copyOf(unlocked,unlocked.length+1);
			newUnlocked[unlocked.length] = fragmentId;
			Arrays.sort(newUnlocked);
			
			category.setIntArray(registration.identifier,newUnlocked);
		}
		else category.setIntArray(registration.identifier,new int[]{ fragmentId });
		
		knowledgeData.setTag(registration.category.identifier,category);
		is.stackTagCompound.setTag("knowledge",knowledgeData);
		
		NBTTagCompound lastUnlock = new NBTTagCompound();
		lastUnlock.setShort("time",(short)200);
		lastUnlock.setString("cat",registration.category.identifier);
		lastUnlock.setString("reg",registration.identifier);
		lastUnlock.setShort("fid",fragmentId);
		is.stackTagCompound.setTag("knowledgeLast",lastUnlock);
		
		if (player != null){
			PacketPipeline.sendToPlayer(player,new C28KnowledgeRegistrationNotification(registration.identifier));
		}
	
		return true;
	}
	
	public boolean unlockRandomFragment(EntityPlayer player, ItemStack is, short[] unlockableFragments){
		List<Short> canUnlock = new ArrayList<>();
		
		int[] unlocked = is.stackTagCompound != null?is.stackTagCompound.getCompoundTag("knowledge").getCompoundTag(registration.category.identifier).getIntArray(registration.identifier):null;
		
		for(KnowledgeFragment fragment:fragments){
			if (checkUnlockRequirements(is,fragment.id) && (unlocked == null || !ArrayUtils.contains(unlocked,fragment.id))){
				if (unlockableFragments == null || ArrayUtils.contains(unlockableFragments,fragment.id))canUnlock.add(fragment.id);
			}
		}

		return canUnlock.isEmpty()?false:unlockFragment(player,is,canUnlock.get(player.getRNG().nextInt(canUnlock.size())));
	}
}
