package chylex.hee.api;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.api.message.MessageRegistry;
import chylex.hee.api.message.utils.MessageLogger;
import chylex.hee.api.message.utils.RunEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

public class HeeIMC{
	private static final Pattern patternKey = Pattern.compile("[^A-Za-z:]");
	private static final Pattern patternFixJson = Pattern.compile("(?:\"(\\w+)?\":)");
	private static final List<HeeMessage> cachedMessages = new ArrayList<>();
	
	/**
	 * Runs IMC messages in the cache. Only handles messages that need to be ran in PostInit. Called internally from HEE.
	 */
	public static void runPostInit(){
		if (!Loader.instance().isInState(LoaderState.POSTINITIALIZATION))throw new IllegalStateException("Invalid loader state, tried running PostInit IMC messages outside PostInit.");
		
		MessageLogger.logState("Running PostInit IMC messages.");
		runMessagesForEvent(RunEvent.POSTINIT);
	}
	
	/**
	 * Runs IMC messages in the cache. Called internally from HEE.
	 */
	public static void runLoadComplete(){
		if (!Loader.instance().isInState(LoaderState.AVAILABLE))throw new IllegalStateException("Invalid loader state, tried running LoadComplete IMC messages outside LoadComplete.");
		
		MessageLogger.logState("Running LoadComplete IMC messages.");
		runMessagesForEvent(RunEvent.LOADCOMPLETE);
	}
	
	/**
	 * Runs IMC messages in the cache unsafely. Has to be called in LoadComplete or later.
	 */
	public static void runUnsafe(){
		if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE))throw new IllegalStateException("Invalid loader state, tried manually running IMC messages before LoadComplete.");
		
		MessageLogger.logState("Running IMC messages unsafely from mod $0.",Loader.instance().activeModContainer().getModId()); // TODO try if this works
		runMessagesForEvent(null);
	}
	
	/* === MESSAGE RUNNING === */
	
	private static void runMessagesForEvent(RunEvent event){
		for(Iterator<HeeMessage> iter = cachedMessages.iterator(); iter.hasNext();){
			HeeMessage msg = iter.next();
			
			if (MessageRegistry.canRunInEvent(msg.key,event)){
				iter.remove();
				MessageLogger.logRun("$0 || $1 $2",msg.modid,msg.key,msg.data);
				MessageRegistry.runMessage(msg.key,msg.nbt);
			}
		}
	}
	
	/* === MESSAGE ACCEPTING === */
	
	public static void acceptString(String sender, String fullNotation){
		int space = fullNotation.indexOf(' ');
		
		if (space == -1)MessageLogger.logError("Received incorrect IMC String message from $0. Cannot identify message key and contents. || $1",sender,fullNotation);
		else acceptString(sender,fullNotation.substring(0,space),fullNotation.substring(space+1));
	}
	
	public static void acceptString(String sender, String key, String message){
		NBTTagCompound nbt = jsonToNBT(sender,key,message);
		if (nbt != null && checkKey(sender,key))cachedMessages.add(new HeeMessage(sender,key,message,nbt));
	}
	
	public static void acceptIMC(IMCMessage message){
		NBTTagCompound nbt = null;
		String data = "";
		
		if (message.isNBTMessage()){
			nbt = message.getNBTValue();
			data = "NBT: "+nbt.toString();
		}
		else if (message.isStringMessage()){
			if ((nbt = jsonToNBT(message.getSender(),message.key,message.getStringValue())) == null)return;
			data = message.getStringValue();
		}
		else{
			MessageLogger.logError("Received incorrect IMC message format from $0. Expected String or NBTTagCompound, got $1.",message.getSender(),message.getMessageType().getSimpleName());
			return;
		}
		
		if (checkKey(message.getSender(),message.key))cachedMessages.add(new HeeMessage(message.getSender(),message.key,data,nbt));
	}
	
	/* === INTERNALS === */
	
	private static boolean checkKey(String sender, String key){
		if (patternKey.matcher(key).find()){
			MessageLogger.logError("Received incorrect IMC message format from $0. Message key contains invalid characters, only letters and colons allowed. || $1",sender,key);
			return false;
		}
		else return true;
	}
	
	private static NBTTagCompound jsonToNBT(String sender, String key, String value){
		NBTTagCompound nbt = null;
		
		try{
			nbt = (NBTTagCompound)JsonToNBT.func_150315_a(patternFixJson.matcher(value).replaceAll("$1:"));
			nbt.getId(); // throw NPE if null
		}catch(NBTException | ClassCastException | NullPointerException e){
			MessageLogger.logError("Received incorrect IMC String message from $0. Parse error: $1. || $2 $3",sender,e.getMessage(),key,value);
		}
		
		return nbt;
	}
}
