package chylex.hee.proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.system.update.UpdateNotificationManager;

public class NotificationClientProxy extends NotificationCommonProxy{
	@Override
	protected void tryDeliverNotifications(){
		if (Minecraft.getMinecraft().thePlayer != null){
			deliverNotificationsToPlayer(Minecraft.getMinecraft().thePlayer);
			clearNotifications();
		}
	}
	
	@Override
	public void onPlayerLogin(EntityPlayer player){
		deliverNotificationsToPlayer(player);
		clearNotifications();
		UpdateNotificationManager.tryRunUpdateCheck();
	}
}
