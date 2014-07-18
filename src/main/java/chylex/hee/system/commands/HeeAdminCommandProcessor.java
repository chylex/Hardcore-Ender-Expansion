package chylex.hee.system.commands;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.DragonSpecialAttackBase;
import chylex.hee.entity.boss.dragon.managers.DragonAttackManager;

public class HeeAdminCommandProcessor{
	public void onCommand(ICommandSender sender, String[] args){
		String extra = "";
		
		if (args.length == 0){
			sendMessage(sender,
				EnumChatFormatting.GREEN+"Available commands:\n"+EnumChatFormatting.RESET+
				"/heeadmin dragon-attack-creative <true|false>\n"+
				"/heeadmin dragon-set-angry\n"+
				"/heeadmin dragon-set-attack <none|dive|fire|punch|bite|freeze|summon|bats>\n"+
				"/heeadmin kill-bosses");
			return;
		}
		else if (args[0].equalsIgnoreCase("dragon-attack-creative") && args.length >= 2){
			DragonAttackManager.nocreative = args[1].equalsIgnoreCase("true");
		}
		else if (args[0].equalsIgnoreCase("dragon-set-angry")){
			EntityBossDragon dragon = HeeDebugCommandProcessor.getDragon();
			if (dragon == null){
				sendMessage(sender,"Dragon not loaded.");
				return;
			}
			else dragon.setAngry(true);
		}
		else if (args[0].equalsIgnoreCase("dragon-set-attack") && args.length >= 2){
			EntityBossDragon dragon = HeeDebugCommandProcessor.getDragon();
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
			World[] toClean = world == null?DimensionManager.getWorlds():new World[]{ world };
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
		else{
			sendMessage(sender,"Unknown command or wrong parameters.");
			return;
		}
		sendMessage(sender,"Request processed."+extra);
	}
	
	private void sendMessage(ICommandSender receiver, String message){
		receiver.addChatMessage(new ChatComponentText(message));
	}
}
