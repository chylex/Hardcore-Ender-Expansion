package chylex.hee.game.commands;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.compendium.KnowledgeRegistrations;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.proxy.ModCommonProxy.MessageType;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.test.UnitTest.RunTime;
import chylex.hee.system.test.UnitTester;
import chylex.hee.system.util.ItemUtil;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class HeeDebugCommand extends BaseCommand{
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
				"/heedebug compendium-reload\n"+
				"/heedebug viewitems\n"+
				"/heedebug speedup\n"+
				"/heedebug noweather\n"+
				"/heedebug stopwatch\n"+
				"/heedebug stick <type>\n"+
				"/heedebug test <testid>\n"+
				"/heedebug unit [trigger]"
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
			
			EntityPlayer target = EntitySelector.players(dragon.worldObj).stream().filter(player -> player.getCommandSenderName().equalsIgnoreCase(args[1])).findAny().orElse(null);
			
			if (target != null){
				dragon.trySetTarget(target);
			}
			else{
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
		else if (args[0].equalsIgnoreCase("compendium-reload")){
			for(KnowledgeObject<?> obj:KnowledgeObject.getAllObjects())obj.reset();
			KnowledgeRegistrations.initialize();
		}
		else if (args[0].equalsIgnoreCase("viewitems")){
			HardcoreEnderExpansion.proxy.sendMessage(MessageType.VIEW_MOD_CONTENT);
		}
		else if (args[0].equalsIgnoreCase("speedup")){
			HardcoreEnderExpansion.proxy.sendMessage(MessageType.SPEED_UP_PLAYER);
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
		else if (args[0].equalsIgnoreCase("stick") && args.length >= 2 && sender instanceof EntityPlayer && ItemList.debug_stick != null){
			ItemStack stick = new ItemStack(ItemList.debug_stick);
			ItemUtil.getTagRoot(stick,true).setString("type",args[1]);
			
			EntityPlayer player = (EntityPlayer)sender;
			player.inventory.mainInventory[player.inventory.currentItem] = stick;
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
		else if (args[0].equalsIgnoreCase("unit") && sender instanceof EntityPlayer){
			StringBuilder build = new StringBuilder();
			for(int a = 1; a < args.length; a++)build.append(args[a]).append(' ');
			
			UnitTester.trigger(RunTime.INGAME,args.length > 1 ? build.deleteCharAt(build.length()-1).toString() : "");
		}
		else if (args[0].equalsIgnoreCase("tmp") && sender instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)sender;
			// tmp command
			SaveData.player(player,CompendiumFile.class).tryUnlockHintFragment(player,KnowledgeFragment.fromID(60));
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
