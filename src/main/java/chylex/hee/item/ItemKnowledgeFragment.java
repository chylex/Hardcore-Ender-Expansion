package chylex.hee.item;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.mechanics.knowledge.data.KnowledgeCategory;
import chylex.hee.mechanics.knowledge.data.KnowledgeRegistration;
import chylex.hee.mechanics.knowledge.util.FragmentWeightLists.FragmentWeightList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C03KnowledgeRegistrationNotification;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemKnowledgeFragment extends Item{
	public ItemKnowledgeFragment(){
		setHasSubtypes(true);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (world.isRemote)return is;
		
		/*for(int a = 0; a < player.inventory.mainInventory.length; a++){
			ItemStack invIS = player.inventory.mainInventory[a];
			if (invIS == null || invIS.getItem() != ItemList.ender_compendium)continue;
			
			String registration = is.stackTagCompound != null?is.stackTagCompound.getString("knowledgeRegistration"):null;
			boolean succeeded = false;
			
			if (registration == null){
				List<KnowledgeRegistration> registrations = new ArrayList<>();
				for(KnowledgeCategory cat:KnowledgeCategory.categories)registrations.addAll(cat.registrations);
				
				Collections.shuffle(registrations);
				
				for(KnowledgeRegistration reg:registrations){
					if (reg.fragmentSet.unlockRandomFragment(player,invIS,null)){
						succeeded = true;
						break;
					}
				}
			}
			else{
				KnowledgeRegistration reg = KnowledgeRegistration.lookup.get(registration);
				
				if (reg != null){
					for(int attempt = 0; attempt < 20 && !succeeded; attempt++){
						if (reg.fragmentSet.unlockRandomFragment(player,invIS,null))succeeded = true;
					}
				}
			}
			
			if (succeeded){
				world.playSoundAtEntity(player,"hardcoreenderexpansion:player.random.pageflip",1.5F,0.5F*((player.getRNG().nextFloat()-player.getRNG().nextFloat())*0.7F+1.8F));
				--is.stackSize;
			}
			else if (registration != null){
				PacketPipeline.sendToPlayer(player,new C03KnowledgeRegistrationNotification(-3));
			}
			
			return is;
		}

		PacketPipeline.sendToPlayer(player,new C03KnowledgeRegistrationNotification(-2));*/
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		/*String registration = is.stackTagCompound != null ? is.stackTagCompound.getString("knowledgeRegistration") : null;
		
		if (registration != null){
			KnowledgeRegistration reg = KnowledgeRegistration.lookup.get(registration);
			if (reg != null)textLines.add(reg.getRenderer().getTooltip());
		}*/
	}
}
