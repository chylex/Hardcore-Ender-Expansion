package chylex.hee.system.commands;
import java.util.List;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.block.EntityBlockEnderCrystal;
import chylex.hee.entity.block.EntityBlockHomelandCache;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.DragonSpecialAttackBase;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.mechanics.curse.ICurseCaller;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C19CompendiumData;
import chylex.hee.system.logging.Log;
import chylex.hee.system.update.UpdateNotificationManager;

public class HeeAdminCommand extends HeeCommand{
	public HeeAdminCommand(){
		super("heeadmin");
	}
	
	@Override
	public void execute(ICommandSender sender, String[] args){
		EntityPlayer player = sender instanceof EntityPlayer ? (EntityPlayer)sender : null;
		String extra = "";
		
		if (args.length == 0){
			for(String s:(
				EnumChatFormatting.GREEN+"Available commands:\n"+
				"/heeadmin version\n"+
				"/heeadmin dragon-set-angry\n"+
				"/heeadmin dragon-set-attack <none|divebomb|fireburst|punch|summoning|bloodlust>\n"+
				"/heeadmin kill-bosses\n"+
				"/heeadmin spawn-entity <endercrystal|homelandcache>\n"+
				"/heeadmin compendium-reset\n"+
				"/heeadmin compendium-set-points <pts>\n"+
				"/heeadmin compendium-unlock-all\n"+
				"/heeadmin achievement-unlock <id>\n"+
				"/heeadmin purify-all-curses"
				).split("\n")){
				sendMessage(sender,s);
			}
			
			return;
		}
		else if (args[0].equalsIgnoreCase("version")){
			sendMessage(sender,EnumChatFormatting.DARK_PURPLE+"Hardcore Ender Expansion");
			sendMessage(sender,EnumChatFormatting.LIGHT_PURPLE+"Version: "+EnumChatFormatting.RESET+HardcoreEnderExpansion.modVersion+"/"+HardcoreEnderExpansion.buildId);
			sendMessage(sender,EnumChatFormatting.LIGHT_PURPLE+"Mod file: "+EnumChatFormatting.RESET+(Log.isDeobfEnvironment ? "<deobf>" : HardcoreEnderExpansion.sourceFile.getName()));
			
			if (UpdateNotificationManager.enableNotifications || UpdateNotificationManager.enableBuildCheck){
				sendMessage(sender,EnumChatFormatting.LIGHT_PURPLE+"Available for: "+EnumChatFormatting.RESET+UpdateNotificationManager.mcVersions);
				sendMessage(sender,EnumChatFormatting.LIGHT_PURPLE+"Release date: "+EnumChatFormatting.RESET+UpdateNotificationManager.releaseDate);
			}
			else sendMessage(sender,EnumChatFormatting.GRAY+"Update information unavailable, notifications are disabled.");
			
			return;
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
			String[] ids = new String[]{ "none", "divebomb", "fireburst", "punch", "summoning", "bloodlust" };
			
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
		else if (args[0].equalsIgnoreCase("spawn-entity") && args.length >= 2 && player != null){
			Entity e = null;
			
			switch(args[1]){
				case "endercrystal": e = new EntityBlockEnderCrystal(player.worldObj); break;
				case "homelandcache": e = new EntityBlockHomelandCache(player.worldObj); break;
			}
			
			if (e == null)sendMessage(sender,"Unknown entity.");
			else{
				e.setPosition(player.posX,player.posY,player.posZ);
				player.worldObj.spawnEntityInWorld(e);
			}
			
			return;
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
			for(KnowledgeFragment fragment:KnowledgeFragment.getAllFragments())data.tryUnlockFragment(fragment);
			
			PacketPipeline.sendToPlayer(player,new C19CompendiumData(player));
		}
		else if (args[0].equalsIgnoreCase("achievement-unlock") && player != null && args.length == 2){
			for(Achievement achievement:(List<Achievement>)AchievementList.achievementList){
				if (achievement.statId.equals(args[1])){
					player.addStat(achievement,1);
					return;
				}
			}
			
			sendMessage(sender,"Achievement not found.");
			return;
		}
		else if (args[0].equalsIgnoreCase("purify-all-curses") && player != null){
			for(Entity entity:(List<Entity>)player.worldObj.loadedEntityList){
				if (entity instanceof ICurseCaller){
					entity.setDead();
					((ICurseCaller)entity).onPurify();
				}
			}
		}
		else{
			sendMessage(sender,"Unknown command or wrong parameters.");
			return;
		}
		
		sendMessage(sender,"Request processed."+extra);
	}
}
