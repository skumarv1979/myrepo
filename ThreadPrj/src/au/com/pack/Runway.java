package au.com.pack;

import java.util.LinkedList;

import org.apache.log4j.Logger;

public class Runway {// implements Runnable
	private int id;
	private String name;
	private LinkedList<Flights> flightsQueue = new LinkedList<Flights>();
	private long processStartTime;// This is the time when the object is removed
									// from the wait queue of this runway
	private Flights processingFlight;
	
	private boolean isProcesingComplete;
	private static final Logger LOGGER = Logger
			.getLogger(Runway.class.getName());

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Runway(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public synchronized void notifyRunway() {
		notifyAll();
	}

	public synchronized void waitRunway() {
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public synchronized void waitRunway(int waitTime) {
		try {
			wait(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public long getProcessStartTime() {
		return processStartTime;
	}

	public void setProcessStartTime(long processStartTime) {
		this.processStartTime = processStartTime;
	}

	public Flights getProcessingFlight() {
		return processingFlight;
	}

	public void setProcessingFlight(Flights processingFlight) {
		this.processingFlight = processingFlight;
	}
	
	public synchronized void addToFlightsQueue(Flights flight) {
		//LOGGER.info(Airport.getInstance().currentTime()+" added to Runway "+this.id);
		flightsQueue.add(flight);
		if(this.processingFlight==null) {
			notifyRunway();
		}
	}

	public synchronized Flights removeFromFlightsQueue() {
		if(!flightsQueue.isEmpty()) {
			Flights tmp = flightsQueue.remove();
			this.setProcessStartTime(System.currentTimeMillis());
			this.setProcessingFlight(tmp);
			return tmp;
		}
		return null;
	}
	public synchronized boolean isThereRequestToProcess() {
		//LOGGER.info(Airport.getInstance().currentTime()+" added to Runway "+this.id);
		//LOGGER.info("Empty "+flightsQueue.isEmpty()+", processing "+processingFlight);
		return (!flightsQueue.isEmpty() || processingFlight!=null);
	}
	public synchronized RunwayAvailableTimeHelper getRunwayBusyTime() {
		//LOGGER.info("calculating available time for Runway "+this);
		int cumBusyTime = getFlightsQueueBusyTime();
		int rwUsageTime = 0;
		Flights prcsingFlight = this.getProcessingFlight();
		//LOGGER.info(Airport.getInstance().currentTime() +" --------- "+prcsingFlight);
		if(prcsingFlight!=null) {
			/*LOGGER.info(Airport.getInstance().currentTime()
					+ " Did it reach here=========="
					+ prcsingFlight.getFlightNo() + ", ...." + cumBusyTime);*/
			int secsToWaitBeforeUsingRunway = prcsingFlight
					.getSecsToWaitBeforeUsingRunway();
			rwUsageTime = secsToWaitBeforeUsingRunway
					+ prcsingFlight.runwayUsageTime()
					+ Airport.getInstance().bufferTimeAfterProcessing()
					- ((int)Math.round((((double)(System.currentTimeMillis() - this
						.getProcessStartTime())) / 1000.0)));
			//LOGGER.info("waitTime "+secsToWaitBeforeUsingRunway+", "+prcsingFlight.runwayUsageTime()+", "+Airport.getInstance().bufferTimeAfterProcessing()+", "+System.currentTimeMillis());
			/*LOGGER.info(Airport.getInstance().currentTime()
					+ " Exiting runway busy time..........");*/
		}
		return new RunwayAvailableTimeHelper(this.id, cumBusyTime
				+ rwUsageTime);
	}

	public synchronized int getFlightsQueueBusyTime() {
		int cumBusyTime = 0;
		//if (!flightsQueue.isEmpty()) {
			/*LOGGER.info(Airport.getInstance().currentTime()
					+ " Entering runway busy time<<<<<<<<<<<" + this.getName());*/
		//LOGGER.info("What is this : "+flightsQueue);
			for (Flights flights : flightsQueue) {
				cumBusyTime += flights.getSecsToWaitBeforeUsingRunway()
						+ flights.runwayUsageTime()
						+ Airport.getInstance().bufferTimeAfterProcessing();
			}
		//}
		return cumBusyTime;
	}
	public synchronized boolean isFlightsQueueEmpty() {
		return flightsQueue.isEmpty();
		
	}

	public void processFlight() {
		if (isFlightsQueueEmpty()) {
			// synchronized (this) {
			this.setProcessStartTime(01);
			LOGGER.info("Runway "
					+ this.getName()
					+ " is clear, ready / waiting for flight requests");
			this.waitRunway();
			//LOGGER.info(Airport.getInstance().currentTime() +" Resume wait");
		} else {
			Flights processingFlight = removeFromFlightsQueue();
			if(processingFlight !=null) {
			if (processingFlight.getSecsToWaitBeforeUsingRunway() > 0) {
				//LOGGER.info("Waiting for seconds "+this.id);
				this.waitRunway(processingFlight
						.getSecsToWaitBeforeUsingRunway() * 1000);
				//LOGGER.info(Airport.getInstance().currentTime() +" Resume wait() overloaded");
			}
			LOGGER.info("Flight "
					+ processingFlight.getFlightNo() + " commencing "
					+ (processingFlight.getLandingOrTakeOff()) + " on Runway "
					+ this.getName());
			try {
				Thread.sleep(processingFlight.runwayUsageTime() * 1000); // processing
																			// time
				this.setProcessingFlight(null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LOGGER.info("Flight "
					+ processingFlight.getFlightNo() + " completed "
					+ (processingFlight.getLandingOrTakeOff()) + " on Runway "
					+ this.getName());
			this.waitRunway(1000); // buffer time
			/*try {
				//Thread.sleep(1000); // buffer time
				this.waitRunway(1000);
				this.setProcessingFlight(null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
			}
		}
	}
	@Override
	public String toString() {
		return this.id+", StartTime : "+processStartTime+", Total time elapsed "+(System.currentTimeMillis() - processStartTime);
	}

	public synchronized boolean isProcesingComplete() {
		return isProcesingComplete;
	}

	public synchronized void setProcesingComplete(boolean isProcesingComplete) {
		this.isProcesingComplete = isProcesingComplete;
	}
}
