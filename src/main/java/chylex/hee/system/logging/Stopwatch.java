package chylex.hee.system.logging;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;

public final class Stopwatch{
	public static boolean isEnabled = true;
	
	private static final Map<String,StopwatchHandler> runningStopwatches = new HashMap<>();
	private static final DecimalFormat numberFormat = new DecimalFormat("#.##",DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	private static final double toMillis = 0.000001D;
	
	public static void time(String identifier){
		if (!Log.isDebugEnabled() || !isEnabled)return;
		
		if (!runningStopwatches.containsKey(identifier))runningStopwatches.put(identifier,new BasicTimer());
		runningStopwatches.get(identifier).onStart();
	}
	
	public static void timeThreshold(String identifier, long threshold){
		if (!Log.isDebugEnabled() || !isEnabled)return;
		
		StopwatchHandler timer = runningStopwatches.get(identifier);
		if (timer == null || !(timer instanceof ThresholdTimer))runningStopwatches.put(identifier,timer = new ThresholdTimer());
		
		((ThresholdTimer)timer).setThreshold(threshold);
		timer.onStart();
	}
	
	public static void timeAverage(String identifier, int count){
		if (!Log.isDebugEnabled() || !isEnabled)return;
		
		StopwatchHandler timer = runningStopwatches.get(identifier);
		if (timer == null || !(timer instanceof AveragingTimer))runningStopwatches.put(identifier,timer = new AveragingTimer());

		((AveragingTimer)timer).setCount(count);
		timer.onStart();
	}
	
	public static void finish(String identifier){
		if (!Log.isDebugEnabled() || !isEnabled)return;
		
		StopwatchHandler handler = runningStopwatches.get(identifier);
		if (handler != null)handler.onFinish(identifier);
	}
	
	/*
	 * HANDLER CLASSES
	 */
	
	private static abstract class StopwatchHandler{
		public abstract void onStart();
		public abstract void onFinish(String identifier);
	}
	
	private static class BasicTimer extends StopwatchHandler{
		protected long startTime;
		
		@Override
		public void onStart(){
			startTime = System.nanoTime();
		}
		
		@Override
		public void onFinish(String identifier){
			double time = (System.nanoTime()-startTime)*toMillis;
			Log.debug("Stopwatch $0 finished in $2 ~ $1 ms.",identifier,Math.round(time),numberFormat.format(time));
		}
	}
	
	private static class ThresholdTimer extends BasicTimer{
		private long threshold;
		
		public void setThreshold(long threshold){
			this.threshold = threshold;
		}
		
		@Override
		public void onFinish(String identifier){
			double time = (System.nanoTime()-startTime)*toMillis;
			if (time >= threshold)Log.debug("Stopwatch $0 finished above threshold in $2 ~ $1 ms.",identifier,Math.round(time),numberFormat.format(time));
		}
	}
	
	private static class AveragingTimer extends StopwatchHandler{
		protected int count, currentCounter;
		protected long startTime, totalTime;
		
		public void setCount(int count){
			if (count != this.count)this.count = this.currentCounter = count;
		}
		
		@Override
		public void onStart(){
			startTime = System.nanoTime();
		}
		
		@Override
		public void onFinish(String identifier){
			totalTime += System.nanoTime()-startTime;
			
			if (--currentCounter == 0 && count > 0){
				double time = (totalTime*toMillis)/count;
				Log.debug("Stopwatch $0 finished in averagely $2 ~ $1 ms.",identifier,Math.round(time),numberFormat.format(time));
				
				currentCounter = count;
				totalTime = 0L;
			}
		}
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			// should be exactly 55 ms
			
			for(int a = 1; a <= 10; a++){
				try{
					Stopwatch.timeAverage("Stopwatch - test average",10);
					Thread.sleep(10L*a);
					Stopwatch.finish("Stopwatch - test average");
				}catch(InterruptedException e){}
			}
		}
	};
}
