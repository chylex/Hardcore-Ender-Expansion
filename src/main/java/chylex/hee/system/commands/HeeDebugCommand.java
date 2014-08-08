package chylex.hee.system.commands;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.DragonSpecialAttackBase;

public class HeeDebugCommand extends HeeCommand{
	public static float overrideWingSpeed = 1F;
	public static boolean isDebugEnabled = false;
	
	public HeeDebugCommand(){
		super("heedebug");
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args){
		if (!isDebugEnabled){
			if (args.length == 1 && (args[0].equals("init") || args[0].equals("uninit"))){
				isDebugEnabled = args[0].equals("init");
				if (isDebugEnabled)args = new String[0];
				else{
					sendMessage(sender,"Debug mode has been disabled.");
					return;
				}
			}
			else{
				sendMessage(sender,"Debug mode is not enabled!");
				return;
			}
		}
		
		EntityBossDragon dragon = getDragon();
		if (dragon == null)sendMessage(sender,"Warning: Dragon is unloaded.");
		
		if (args.length == 0){
			sendMessage(sender,
				EnumChatFormatting.GREEN+"Available commands: "+EnumChatFormatting.RESET+
				"setangry, settarget <username>, animspeed <speed>, attackeff, attack <id>, endattack, freeze, kill, startdebug, stopdebug, viewitems, speedup");
			return;
		}
		else if (args[0].equalsIgnoreCase("setangry") && dragon != null){
			dragon.setAngry(true);
		}
		else if (args[0].equalsIgnoreCase("settarget") && args.length == 2 && dragon != null){
			if (args[1].equals("null")){
				dragon.target = null;
				sendMessage(sender,"Target cancelled.");
				return;
			}
			
			boolean found = false;
			for(Object o:dragon.worldObj.playerEntities){
				if (((EntityPlayer)o).getCommandSenderName().equalsIgnoreCase(args[1])){
					dragon.trySetTarget((EntityPlayer)o);
					found = true;
					break;
				}
			}
			if (!found){
				sendMessage(sender,"No such player.");
				return;
			}
		}
		else if (args[0].equalsIgnoreCase("animspeed") && args.length == 2){
			try{
				overrideWingSpeed = Float.parseFloat(args[1]);
			}catch(NumberFormatException e){
				sendMessage(sender,"Invalid number.");
			}
		}
		else if (args[0].equalsIgnoreCase("attackeff") && dragon != null){
			for(DragonSpecialAttackBase attack:dragon.attacks.getSpecialAttackList()){
				sendMessage(sender,attack.id+" | "+attack.previousEffectivness+" / "+attack.effectivness+" / "+attack.newEffectivness);
			}
		}
		else if (args[0].equalsIgnoreCase("attack") && args.length == 2 && dragon != null){
			try{
				int id = Integer.parseInt(args[1]);
				DragonSpecialAttackBase attack = dragon.attacks.getSpecialAttackById(id);
				if (attack == null)sendMessage(sender,"Invalid attack.");
				else dragon.forceSpecialAttack(attack);
			}catch(NumberFormatException e){
				sendMessage(sender,"Invalid attack.");
			}
		}
		else if (args[0].equalsIgnoreCase("endattack") && dragon != null){
			dragon.forceAttackEnd = true;
		}
		else if (args[0].equalsIgnoreCase("freeze") && dragon != null){
			dragon.frozen = !dragon.frozen;
		}
		else if (args[0].equalsIgnoreCase("kill") && dragon != null){
			dragon.setHealth(0);
		}
		else if (args[0].equalsIgnoreCase("startdebug") && dragon != null){
			DebugBoard.startDebug(dragon.worldObj);
		}
		else if (args[0].equalsIgnoreCase("stopdebug")){
			DebugBoard.stopDebug();
		}
		else if (args[0].equalsIgnoreCase("viewitems")){
			HardcoreEnderExpansion.proxy.openGui("itemviewer");
		}
		else if (args[0].equalsIgnoreCase("speedup")){
			HardcoreEnderExpansion.proxy.openGui("speedup");
		}
		else{
			sendMessage(sender,"Unknown command.");
			return;
		}
		sendMessage(sender,"Request processed.");
	}
	
	public static final EntityBossDragon getDragon(){
		for(WorldServer world:DimensionManager.getWorlds()){
			if (world.provider.dimensionId == 1){
				for(Object o:world.loadedEntityList){
					if (o instanceof EntityBossDragon)return (EntityBossDragon)o;
				}
			}
		}
		return null;
	}
}
