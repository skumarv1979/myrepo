package au.com.pack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;


public class Airport {
	private List<Runway> runways = new ArrayList<Runway>();
	private boolean allRequestsCompleted;
	private static Airport airport = null;
	private static final Logger LOGGER = Logger
			.getLogger(Airport.class.getName());

	private Airport() {

	}

	public static Airport getInstance() { // Does it require synchronized
		if (airport == null) {
			airport = new Airport();
			airport.configureRunways();
		}
		return airport;
	}
	
	private void configureRunways() {
		for(int i=0;i<AirportControlSysConstantsConf.NUMBER_OF_RUNWAYS;i++) {
			runways.add(new Runway(i, i+""));
		}
	}
	public Runway getRunway(int idx) {
		return runways.get(idx);
	}
	public int getNoOfRunways() {
		return runways.size();
	}

	public synchronized void assignRunway(Flights flight) {
		LOGGER.info(flight.toString());
		Queue<RunwayAvailableTimeHelper> rwAvailTimeList = new LinkedList<RunwayAvailableTimeHelper>();
		ExecutorService executor = Executors.newFixedThreadPool(2);
		List<Future<RunwayAvailableTimeHelper>> futList = new ArrayList<Future<RunwayAvailableTimeHelper>>();
		for (Runway runway : runways) {
			Future<RunwayAvailableTimeHelper> future = executor.submit(new RunwayTimeCallable(runway));
			futList.add(future);
		}
		for (Future<RunwayAvailableTimeHelper> fut : futList) {
			try {
				rwAvailTimeList.add(fut.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
		RunwayAvailableTimeHelper runwayHlpr = getRunwayToUse(rwAvailTimeList);
		Runway runway = this.getRunway(runwayHlpr.getThreadIndex());
		if(runwayHlpr.getAvailabletime()<AirportControlSysConstantsConf.PILOT_NOTICE_TIME) {
			flight.setSecsToWaitBeforeUsingRunway(AirportControlSysConstantsConf.PILOT_NOTICE_TIME-runwayHlpr.getAvailabletime());
		}
		runway.addToFlightsQueue(flight);
		LOGGER.info("Scheduled Flight "+flight.getFlightNo()+" for "+getScheduleTime(runway, flight, runwayHlpr.getAvailabletime()) + " on Runway "+runway.getName());
		
	}
	public RunwayAvailableTimeHelper getRunwayToUse(Queue<RunwayAvailableTimeHelper> runwayHelperList) {
			RunwayAvailableTimeHelper runwayToSend  = runwayHelperList.remove();
			for (RunwayAvailableTimeHelper runwayAvailableTimeHelper : runwayHelperList) {
				if(runwayAvailableTimeHelper.getAvailabletime() < runwayToSend.getAvailabletime()) {
					runwayToSend = runwayAvailableTimeHelper;
				}
			}
		return runwayToSend;
	}
	public int bufferTimeAfterProcessing() {
		return AirportControlSysConstantsConf.RW_BUFR_TIME_AFTR_PRCSING;
	}
	public void airportInitialize() {
		for (Runway runway : runways) {
			new RunwayThread(runway, "Unique").start();
		}
	}
	public String getScheduleTime(Runway runway, Flights flight, int waitTime) {
		long currentTime = System.currentTimeMillis();
		long scheduledTime = currentTime + (waitTime<AirportControlSysConstantsConf.PILOT_NOTICE_TIME?AirportControlSysConstantsConf.PILOT_NOTICE_TIME:waitTime)*1000;
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		return sdf.format(new Date(scheduledTime));
	}

	public synchronized boolean isAllRequestsCompleted() {
		return allRequestsCompleted;
	}

	public synchronized void setAllRequestsCompleted(boolean allRequestsCompleted) {
		this.allRequestsCompleted = allRequestsCompleted;
	}
}
