package chylex.hee.system.commands;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.DragonSpecialAttackBase;
import chylex.hee.system.logging.Log;
import chylex.hee.world.structure.island.ComponentIsland;

public class HeeDebugCommand extends HeeCommand{
	public static float overrideWingSpeed = 1F;
	
	public HeeDebugCommand(){
		super("heedebug");
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args){
		if (!Log.isDebugEnabled()){
			sendMessage(sender,"Debug mode is not enabled.");
			return;
		}
		
		if (args.length == 0){
			for(String s:(
				EnumChatFormatting.GREEN+"Available commands:\n"+
				"/heedebug dragon-set-target <newtarget>\n"+
				"/heedebug dragon-wing-speed <newspeed>\n"+
				"/heedebug dragon-attack-end\n"+
				"/heedebug dragon-freeze\n"+
				"/heedebug dragon-debug-start\n"+
				"/heedebug dragon-debug-stop\n"+
				"/heedebug viewitems\n"+
				"/heedebug speedup\n"+
				"/heedebug noweather\n"+
				"/heedebug test <testid>"
				).split("\n")){
				sendMessage(sender,s);
			}
		
			return;
		}
		
		if (args[0].equalsIgnoreCase("dragon-set-target") && args.length == 2){
			EntityBossDragon dragon = getDragon();
			if (dragon == null)return;
			
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
		else if (args[0].equalsIgnoreCase("dragon-wing-speed") && args.length == 2){
			try{
				overrideWingSpeed = Float.parseFloat(args[1]);
			}catch(NumberFormatException e){
				sendMessage(sender,"Invalid number.");
			}
		}
		else if (args[0].equalsIgnoreCase("dragon-attack-eff")){
			EntityBossDragon dragon = getDragon();
			if (dragon == null)return;
			
			for(DragonSpecialAttackBase attack:dragon.attacks.getSpecialAttackList()){
				sendMessage(sender,attack.id+" | "+attack.previousEffectivness+" / "+attack.effectivness+" / "+attack.newEffectivness);
			}
		}
		else if (args[0].equalsIgnoreCase("dragon-attack-end")){
			EntityBossDragon dragon = getDragon();
			if (dragon != null)dragon.forceAttackEnd = true;
		}
		else if (args[0].equalsIgnoreCase("dragon-freeze")){
			EntityBossDragon dragon = getDragon();
			if (dragon != null)dragon.frozen = !dragon.frozen;
		}
		else if (args[0].equalsIgnoreCase("dragon-debug-start")){
			DebugBoard.startDebug(DimensionManager.getWorld(1));
		}
		else if (args[0].equalsIgnoreCase("dragon-debug-stop")){
			DebugBoard.stopDebug();
		}
		else if (args[0].equalsIgnoreCase("viewitems")){
			HardcoreEnderExpansion.proxy.openGui("itemviewer");
		}
		else if (args[0].equalsIgnoreCase("speedup")){
			HardcoreEnderExpansion.proxy.openGui("speedup");
		}
		else if (args[0].equalsIgnoreCase("noweather")){
			for(WorldServer world:DimensionManager.getWorlds()){
				WorldInfo info = world.getWorldInfo();
				info.setRaining(false);
				info.setThundering(false);
				info.setRainTime(Integer.MAX_VALUE);
				info.setThunderTime(Integer.MAX_VALUE);
			}
		}
		else if (args[0].equalsIgnoreCase("test") && args.length == 2){
			try{
				((Runnable)Class.forName("chylex.hee."+args[1]).getField("$debugTest").get(null)).run();
				sendMessage(sender,"Test done.");
			}catch(Throwable t){
				sendMessage(sender,"Test not found.");
			}
		}
		else if (args[0].equalsIgnoreCase(".tmp")){ // TODO remove
			World world = Minecraft.getMinecraft().theWorld;
			EntityPlayer plr = Minecraft.getMinecraft().thePlayer;
			//new WorldGenBlob().generate(world,world.rand,(int)plr.posX+10,(int)plr.posY,(int)plr.posZ);
			
			ComponentIsland island = new ComponentIsland(world.rand,(int)plr.posX-104,(int)plr.posZ-104);
			island.addComponentParts(world,world.rand,new StructureBoundingBox(-999999,0,-999999,999999,128,999999));
		}
		else{
			sendMessage(sender,"Unknown command.");
			return;
		}
		
		sendMessage(sender,"Request processed.");
	}
	
	public static final EntityBossDragon getDragon(){
		WorldServer world = DimensionManager.getWorld(1);
		
		for(Object o:world.loadedEntityList){
			if (o instanceof EntityBossDragon)return (EntityBossDragon)o;
		}
		
		return null;
	}
}
