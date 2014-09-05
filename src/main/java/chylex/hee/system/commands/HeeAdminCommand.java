package chylex.hee.system.commands;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.DragonSpecialAttackBase;
import chylex.hee.entity.boss.dragon.managers.DragonAttackManager;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C19CompendiumData;

public class HeeAdminCommand extends HeeCommand{
	public HeeAdminCommand(){
		super("heeadmin");
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args){
		EntityPlayer player = sender instanceof EntityPlayer ? (EntityPlayer)sender : null;
		String extra = "";
		
		if (args.length == 0){
			for(String s:(
				EnumChatFormatting.GREEN+"Available commands:\n"+
				"/heeadmin dragon-attack-creative <true|false>\n"+
				"/heeadmin dragon-set-angry\n"+
				"/heeadmin dragon-set-attack <none|dive|fire|punch|bite|freeze|summon|bats>\n"+
				"/heeadmin kill-bosses\n"+
				"/heeadmin compendium-reset\n"+
				"/heeadmin compendium-set-points <pts>\n"+
				"/heeadmin compendium-unlock-all"
				).split("\n")){
				sendMessage(sender,s);
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("dragon-attack-creative") && args.length >= 2){
			DragonAttackManager.nocreative = args[1].equalsIgnoreCase("true");
		}
		else if (args[0].equalsIgnoreCase("dragon-set-angry")){
			EntityBossDragon dragon = HeeDebugCommand.getDragon();
			if (dragon == null){
				sendMessage(sender,"Dragon not loaded.");
				return;
			}
			else dragon.setAngry(true);
		}
		else if (args[0].equalsIgnoreCase("dragon-set-attack") && args.length >= 2){
			EntityBossDragon dragon = HeeDebugCommand.getDragon();
			if (dragon == null){
				sendMessage(sender,"Dragon not loaded.");
				return;
			}
			
			DragonSpecialAttackBase att = null;
			String[] ids = new String[]{ "none","","","bats","fire","bite","punch","freeze","","summon","dive" };
			for(int a = 0; a < ids.length; a++){
				if (args[1].equalsIgnoreCase(ids[a])){
					att = dragon.attacks.getSpecialAttackById(a);
					break;
				}
			}
			
			if (att != null)dragon.forceSpecialAttack(att);
			else{
				sendMessage(sender,"Attack not found.");
				return;
			}
		}
		else if (args[0].equalsIgnoreCase("kill-bosses")){
			World world = sender.getEntityWorld();
			World[] toClean = world == null ? DimensionManager.getWorlds() : new World[]{ world };
			int counter = 0;
			
			for(World w:toClean){
				for(Object o:w.loadedEntityList){
					if (o instanceof IBossDisplayData && o instanceof EntityLiving){
						((EntityLiving)o).setHealth(0F);
						++counter;
					}
				}
			}
			
			extra = " "+counter+" entit"+(counter == 1?"y":"ies")+" killed.";
		}
		else if (args[0].equalsIgnoreCase("compendium-reset") && player != null){
			PlayerCompendiumData data = CompendiumEvents.getPlayerData(player);
			data.loadNBTData(new NBTTagCompound());
			PacketPipeline.sendToPlayer(player,new C19CompendiumData(player));
		}
		else if (args[0].equalsIgnoreCase("compendium-set-points") && player != null && args.length == 2){
			int amount = 0;
			
			try{
				amount = Integer.parseInt(args[1]);
			}catch(NumberFormatException e){
				sendMessage(sender,"Invalid parameters.");
				return;
			}
			
			PlayerCompendiumData data = CompendiumEvents.getPlayerData(player);
			data.payPoints(data.getPoints());
			data.givePoints(amount);
			PacketPipeline.sendToPlayer(player,new C19CompendiumData(player));
		}
		else if (args[0].equalsIgnoreCase("compendium-unlock-all") && player != null){
			PlayerCompendiumData data = CompendiumEvents.getPlayerData(player);
			
			for(KnowledgeObject<?> object:KnowledgeObject.getAllObjects())data.tryDiscoverObject(object,false);
			for(KnowledgeFragment fragment:KnowledgeFragment.getAllFragments())data.unlockFragment(fragment);
			
			PacketPipeline.sendToPlayer(player,new C19CompendiumData(player));
		}
		else{
			sendMessage(sender,"Unknown command or wrong parameters.");
			return;
		}
		
		sendMessage(sender,"Request processed."+extra);
	}
}
