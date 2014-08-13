package chylex.hee.mechanics.infestation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.InfestationSavefile;
import chylex.hee.system.weight.WeightedList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

public final class InfestationEvents{
	private static InfestationEvents instance;
	
	public static void register(){
		FMLCommonHandler.instance().bus().register(instance = new InfestationEvents());
	}
	
	private static WeightedList<InfestationEffect> basicEffects = new WeightedList<>(
		new InfestationEffect(Potion.digSlowdown,1F,10),
		new InfestationEffect(Potion.moveSlowdown,0.86F,9),
		new InfestationEffect(Potion.weakness,0.925F,7)
	);
	
	private static WeightedList<InfestationEffect> criticalEffects = new WeightedList<>(
		new InfestationEffect(Potion.blindness,0.95F,10),
		new InfestationEffect(Potion.confusion,0.6F,7),
		new InfestationEffect(Potion.poison,0.68F,5)
	);
	
	public static boolean isValidPotionEffect(int potionId){
		for(InfestationEffect eff:basicEffects){
			if (eff.getPotionId() == potionId)return true;
		}
		for(InfestationEffect eff:criticalEffects){
			if (eff.getPotionId() == potionId)return true;
		}
		
		return false;
	}
	
	private short tickTimer;
	
	private InfestationEvents(){}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e){
		for(Object o:e.player.getActivePotionEffects()){
			PotionEffect eff = (PotionEffect)o;
			if (eff.getIsAmbient() && isValidPotionEffect(eff.getPotionID())){
				InfestationEffect.setCurativeItems(eff);
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent e){
		if (e.phase != Phase.START || ++tickTimer < 600)return;
		tickTimer = 0;
		
		World world = e.world;
		InfestationSavefile file = WorldDataHandler.get(InfestationSavefile.class);
		
		for(Object o:world.playerEntities){
			if (world.rand.nextInt(3) == 0 || o == null)continue;
			
			EntityPlayer player = (EntityPlayer)o;
			if (file.decreaseInfestationStartTimer(player) == 1){
				int power = file.getInfestationPower(player);
				
				if (power >= 1700-world.rand.nextInt(500)){
					player.addChatMessage(new ChatComponentText("You start to feel a bit "+new String[]{ "funny","strange","weird","dizzy" }[world.rand.nextInt(4)]+"..."));
					
					int amount = 1;
					if (power >= 2400+world.rand.nextInt(500))++amount;
					if (power >= 3800+world.rand.nextInt(1000))++amount;
					
					for(int a = 0; a < Math.min(amount,1+world.rand.nextInt(1+amount)); a++){
						player.addPotionEffect(basicEffects.getRandomItem(world.rand).createPotionEffect(power >= 2300 ? world.rand.nextInt(2) : 0,6000+(power-1000)+(world.rand.nextInt(power)>>2)));
					}
					
					if (world.rand.nextInt(10000) < power-2500){
						amount = 1;
						if (power >= 3500+world.rand.nextInt(800))++amount;
						if (power >= 5600+world.rand.nextInt(1200))++amount;
						
						for(int a = 0; a < Math.min(amount,1+world.rand.nextInt(2+amount)); a++){
							player.addPotionEffect(criticalEffects.getRandomItem(world.rand).createPotionEffect(power >= 4500 ? world.rand.nextInt(2) : 0,6000+(power-1000)+(world.rand.nextInt(power)>>2)));
						}
					}
				}
				
				file.resetInfestation(player);
			}
		}
	}
}