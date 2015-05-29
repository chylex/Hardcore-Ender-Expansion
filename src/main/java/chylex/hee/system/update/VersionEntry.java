package chylex.hee.system.update;
import java.util.Calendar;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.DragonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

final class VersionEntry implements Comparable<VersionEntry>{
	public final String versionIdentifier;
	public final String modVersionName;
	public final String modVersion;
	public final String[] mcVersions;
	public final String releaseDate;
	public final int buildId;
	private final byte orderId;
	
	VersionEntry(String versionIdentifier, JsonObject node){
		this.versionIdentifier = versionIdentifier;
		modVersion = node.get("modVersion").getAsString();
		
		JsonArray array = node.get("mcVersions").getAsJsonArray();
		mcVersions = new String[array.size()];
		int a = -1;
		for(JsonElement mcVersionNode:array)mcVersions[++a] = mcVersionNode.getAsString();
		
		buildId = node.has("buildId") ? node.get("buildId").getAsInt() : 0;
		releaseDate = node.get("releaseDate").getAsString();
		
		byte i = 0;
		String tmp = modVersion;
		String[] idSplit = versionIdentifier.split(" - ");
		
		if (idSplit.length != 2)Log.error("Incorrect version identifier: $0",versionIdentifier);
		else{
			tmp = idSplit[1];
			
			try{
				i = Byte.parseByte(idSplit[0]);
			}catch(NumberFormatException e){
				Log.error("Incorrect version identifier: $0",versionIdentifier);
			}
		}
		
		orderId = i;
		modVersionName = tmp;
	}
	
	public boolean isSupportedByMC(String mcVersion){
		return ArrayUtils.contains(mcVersions,mcVersion);
	}
	
	public Calendar convertReleaseDate(){
		Calendar cal = Calendar.getInstance();
		cal.clear();
		
		String[] info = StringUtils.split(releaseDate,' ');
		if (info.length != 3)return cal;
		
		int day = DragonUtil.tryParse(info[0],cal.get(Calendar.DAY_OF_MONTH));
		int month = ArrayUtils.indexOf(new String[]{ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" },info[1]);
		int year = DragonUtil.tryParse(info[2],cal.get(Calendar.YEAR));
		
		cal.set(year,month == ArrayUtils.INDEX_NOT_FOUND ? cal.get(Calendar.MONTH) : month,day);
		return cal;
	}

	@Override
	public int compareTo(VersionEntry o){
		return o.orderId-orderId;
	}
	
	@Override
	public int hashCode(){
		return orderId;
	}
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof VersionEntry && ((VersionEntry)obj).orderId == orderId;
	}
}
