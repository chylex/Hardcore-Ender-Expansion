package chylex.hee.system.logging;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class Stopwatch{
	private static final Map<String,StopwatchHandler> runningStopwatches = new HashMap<>();
	
	public static void time(String identifier){
		if (!Log.forceDebugEnabled)return;
		
		if (!runningStopwatches.containsKey(identifier))runningStopwatches.put(identifier,new BasicTimer());
		runningStopwatches.get(identifier).onStart();
	}
	
	public static void timeThreshold(String identifier, long threshold){
		if (!Log.forceDebugEnabled)return;
		
		StopwatchHandler timer = runningStopwatches.get(identifier);
		if (timer == null || !(timer instanceof ThresholdTimer))runningStopwatches.put(identifier,timer = new ThresholdTimer());

		((ThresholdTimer)timer).setThreshold(threshold);
		timer.onStart();
	}
	
	public static void timeAverage(String identifier, int count){
		if (!Log.forceDebugEnabled)return;
		
		StopwatchHandler timer = runningStopwatches.get(identifier);
		if (timer == null || !(timer instanceof AveragingTimer))runningStopwatches.put(identifier,timer = new AveragingTimer());

		((AveragingTimer)timer).setCount(count);
		timer.onStart();
	}
	
	public static void finish(String identifier){
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
			Log.debug("Stopwatch $0 finished in $1 ms.",identifier,TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-startTime));
		}
	}
	
	private static class ThresholdTimer extends BasicTimer{
		private long threshold;
		
		public void setThreshold(long threshold){
			this.threshold = threshold;
		}
		
		@Override
		public void onFinish(String identifier){
			long time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-startTime);
			if (time >= threshold)Log.debug("Stopwatch $0 finished above threshold in $1 ms.",identifier,time);
		}
	}
	
	private static class AveragingTimer extends StopwatchHandler{
		protected int count,currentCounter;
		protected long startTime,totalTime;
		
		public void setCount(int count){
			if (count != this.count)this.count = this.currentCounter = count;
		}
		
		@Override
		public void onStart(){
			startTime = System.nanoTime();
		}
		
		@Override
		public void onFinish(String identifier){
			totalTime += TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-startTime);
			
			if (--currentCounter == 0 && count > 0){
				Log.debug("Stopwatch $0 finished in averagely $1 ms.",identifier,totalTime/count);
				currentCounter = count;
				totalTime = 0L;
			}
		}
	}
}
