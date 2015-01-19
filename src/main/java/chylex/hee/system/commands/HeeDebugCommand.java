package chylex.hee.system.commands;
import java.util.List;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class HeeDebugCommand extends HeeCommand{
	public static float overrideWingSpeed = 1F;
	
	public HeeDebugCommand(){
		super("heedebug");
	}
	
	@Override
	public void execute(ICommandSender sender, String[] args){
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
				"/heedebug stopwatch\n"+
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
			
			for(EntityPlayer player:(List<EntityPlayer>)dragon.worldObj.playerEntities){
				if (player.getName().equalsIgnoreCase(args[1])){
					dragon.trySetTarget(player);
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
		else if (args[0].equalsIgnoreCase("stopwatch")){
			Stopwatch.isEnabled = !Stopwatch.isEnabled;
		}
		else if (args[0].equalsIgnoreCase("test") && args.length >= 2 && sender instanceof EntityPlayer){
			try{
				Stopwatch.time("HeeDebugCommand - test");
				
				boolean found = false;
				ClassPath path = ClassPath.from(HeeDebugCommand.class.getClassLoader());
				
				for(ClassInfo clsInfo:path.getAllClasses()){
					if (clsInfo.getSimpleName().equals(args[1]) && clsInfo.getPackageName().startsWith("chylex.hee")){	
						HeeTest test = (HeeTest)clsInfo.load().getField("$debugTest").get(null);
						test.player = (EntityPlayer)sender;
						test.world = test.player.worldObj;
						
						try{
							test.run(ArrayUtils.subarray(args,2,args.length));
							sendMessage(sender,"Test completed.");
						}
						catch(Throwable t){
							t.printStackTrace();
							sendMessage(sender,"Test failed.");
						}
						
						found = true;
						break;
					}
				}
				
				if (!found)sendMessage(sender,"Test not found.");
				
				Stopwatch.finish("HeeDebugCommand - test");
			}catch(Throwable t){
				sendMessage(sender,"Test not found.");
			}
			
			return;
		}
		else if (args[0].equalsIgnoreCase("tmp") && sender instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)sender;
			// tmp command
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
	
	public static abstract class HeeTest{
		protected World world;
		protected EntityPlayer player;
		
		public abstract void run(String...args);
	}
}
