package chylex.hee.system.commands;
import static net.minecraft.util.EnumChatFormatting.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import org.apache.commons.lang3.ArrayUtils;
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
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class HeeAdminCommand extends HeeCommand{
	private static final String pre = DARK_PURPLE+"[HEE] "+RESET;
	
	private List<SubCommand> sub = new ArrayList<>();
	
	public HeeAdminCommand(){
		super("heeadmin");
		
		sub.add(new SubCommand("help","<page>",0,false){
			@Override
			void run(ICommandSender sender, String[] args){}
		});
		
		sub.add(new SubCommand("version",0,false){
			@Override
			void run(ICommandSender sender, String[] args){
				sendMessage(sender,DARK_PURPLE+"Hardcore Ender Expansion");
				sendMessage(sender,LIGHT_PURPLE+"Version: "+RESET+HardcoreEnderExpansion.modVersion+"/"+HardcoreEnderExpansion.buildId);
				sendMessage(sender,LIGHT_PURPLE+"Mod file: "+RESET+(Log.isDeobfEnvironment ? "<deobf>" : HardcoreEnderExpansion.sourceFile.getName()));
				
				if (UpdateNotificationManager.enableNotifications || UpdateNotificationManager.enableBuildCheck){
					sendMessage(sender,LIGHT_PURPLE+"Available for: "+RESET+UpdateNotificationManager.mcVersions);
					sendMessage(sender,LIGHT_PURPLE+"Release date: "+RESET+UpdateNotificationManager.releaseDate);
				}
				else sendMessage(sender,GRAY+"Update information unavailable, notifications are disabled.");
			}
		});
		
		sub.add(new SubCommand("kill-bosses",0,true){
			@Override
			void run(ICommandSender sender, String[] args){
				int counter = 0;
				
				for(Object o:sender.getEntityWorld().loadedEntityList){
					if (o instanceof IBossDisplayData && o instanceof EntityLiving){
						((EntityLiving)o).setHealth(0F);
						++counter;
					}
				}
				
				sendMessage(sender,pre+"Killed "+counter+" entit"+(counter == 1 ? "y" : "ies")+".");
			}
		});
		
		sub.add(new SubCommand("compendium-set-points","<pts>",1,true){
			@Override
			void run(ICommandSender sender, String[] args){
				EntityPlayer player = (EntityPlayer)sender;
				int amount = DragonUtil.tryParse(args[0],-1);
				
				if (amount == -1){
					sendMessage(sender,"Invalid number.");
					return;
				}
				
				PlayerCompendiumData data = CompendiumEvents.getPlayerData(player);
				data.payPoints(data.getPoints());
				data.givePoints(amount);
				PacketPipeline.sendToPlayer(player,new C19CompendiumData(player));
				sendMessage(sender,"Compendium points updated.");
			}
		});
		
		sub.add(new SubCommand("compendium-unlock-all",0,true){
			@Override
			void run(ICommandSender sender, String[] args){
				EntityPlayer player = (EntityPlayer)sender;
				PlayerCompendiumData data = CompendiumEvents.getPlayerData(player);
				
				for(KnowledgeObject<?> object:KnowledgeObject.getAllObjects())data.tryDiscoverObject(object,false);
				for(KnowledgeFragment fragment:KnowledgeFragment.getAllFragments())data.tryUnlockFragment(fragment);
				
				PacketPipeline.sendToPlayer(player,new C19CompendiumData(player));
				sendMessage(sender,pre+"Compendium data unlocked.");
			}
		});
		
		sub.add(new SubCommand("compendium-reset",0,true){
			@Override
			void run(ICommandSender sender, String[] args){
				EntityPlayer player = (EntityPlayer)sender;
				PlayerCompendiumData data = CompendiumEvents.getPlayerData(player);
				data.loadNBTData(new NBTTagCompound());
				
				PacketPipeline.sendToPlayer(player,new C19CompendiumData(player));
				sendMessage(sender,pre+"Compendium data reset.");
			}
		});
		
		sub.add(new SubCommand("spawn-entity","<endercrystal|homelandcache>",1,true){
			@Override
			void run(ICommandSender sender, String[] args){
				EntityPlayer player = (EntityPlayer)sender;
				Entity e = null;
				
				switch(args[0]){
					case "endercrystal": e = new EntityBlockEnderCrystal(player.worldObj); break;
					case "homelandcache": e = new EntityBlockHomelandCache(player.worldObj); break;
				}
				
				if (e == null)sendMessage(sender,pre+"Unknown entity.");
				else{
					e.setPosition(player.posX,player.posY,player.posZ);
					player.worldObj.spawnEntityInWorld(e);
					sendMessage(sender,pre+"Entity spawned.");
				}
			}
		});
		
		sub.add(new SubCommand("dragon-set-angry",0,false){
			@Override
			void run(ICommandSender sender, String[] args){
				EntityBossDragon dragon = HeeDebugCommand.getDragon();
				
				if (dragon == null)sendMessage(sender,pre+"Dragon is not loaded.");
				else{
					dragon.setAngry(true);
					sendMessage(sender,pre+"Dragon state updated.");
				}
			}
		});
		
		sub.add(new SubCommand("dragon-set-attack","<none|divebomb|fireburst|punch|summoning|bloodlust>",1,false){
			@Override
			void run(ICommandSender sender, String[] args){
				EntityBossDragon dragon = HeeDebugCommand.getDragon();
				if (dragon == null){
					sendMessage(sender,"Dragon not loaded.");
					return;
				}
				
				String[] ids = new String[]{ "none", "divebomb", "fireburst", "punch", "summoning", "bloodlust" };
				DragonSpecialAttackBase att = dragon.attacks.getSpecialAttackById(ArrayUtils.indexOf(ids,args[0]));
				
				if (att != null){
					dragon.forceSpecialAttack(att);
					sendMessage(sender,pre+"Dragon attack set.");
				}
				else sendMessage(sender,pre+"Attack not found.");
			}
		});
		
		sub.add(new SubCommand("purify-loaded-curses",0,true){
			@Override
			void run(ICommandSender sender, String[] args){
				int counter = 0;
				
				for(Entity entity:(List<Entity>)sender.getEntityWorld().loadedEntityList){
					if (entity instanceof ICurseCaller){
						entity.setDead();
						((ICurseCaller)entity).onPurify();
						++counter;
					}
				}
				
				sendMessage(sender,pre+"Purified "+counter+" curse"+(counter == 1 ? "." : "es."));
			}
		});
		
		sub.add(new SubCommand("achievement-unlock","<id>",1,true){
			@Override
			void run(ICommandSender sender, String[] args){
				for(Achievement achievement:(List<Achievement>)AchievementList.achievementList){
					if (achievement.statId.equals(args[0])){
						((EntityPlayer)sender).addStat(achievement,1);
						sendMessage(sender,pre+"Attempted to unlock.");
						return;
					}
				}
				
				sendMessage(sender,pre+"Achievement not found.");
			}
		});
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args){
		int helpPage = args.length == 0 ? 0 : args[0].equalsIgnoreCase("help") ? DragonUtil.tryParse(args.length == 2 ? args[1] : "1",1)-1 : -1;
		
		if (helpPage >= 0){
			final int cmdsPerPage = 7;
			int pages = MathUtil.ceil(sub.size()/(float)cmdsPerPage);
			if (helpPage >= pages-1)helpPage = pages-1;
			
			sendMessage(sender,GREEN+"[Hardcore Ender Expansion] page "+(helpPage+1)+"/"+pages);
			
			for(int a = helpPage*cmdsPerPage; a < Math.min((helpPage+1)*cmdsPerPage,sub.size()); a++){
				SubCommand cmd = sub.get(a);
				sendMessage(sender,"/heeadmin "+cmd.name+(cmd.arguments == null ? "" : " "+cmd.arguments));
			}
		}
		else{
			for(SubCommand cmd:sub){
				if (cmd.name.equalsIgnoreCase(args[0])){
					if (args.length <= cmd.argCount)sendMessage(sender,pre+"Invalid amount of parameters.");
					else if (cmd.requiresPlayer && !(sender instanceof EntityPlayer))sendMessage(sender,pre+"You need to be in-game to invoke this command.");
					else cmd.run(sender,ArrayUtils.remove(args,0));
					
					return;
				}
			}
			
			sendMessage(sender,pre+"Unknown command.");
		}
	}
}
