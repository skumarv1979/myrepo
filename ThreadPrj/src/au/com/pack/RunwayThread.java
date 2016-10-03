package au.com.pack;

import org.apache.log4j.Logger;



public class RunwayThread extends Thread {
	Runway runway;
	String name;
	private static final Logger LOGGER = Logger
			.getLogger(RunwayThread.class.getName());
	public RunwayThread(Runway runway, String name) {
		this.runway = runway;
		this.name = name;
	}
	@Override
	public void run() {
		while(!Airport.getInstance().isAllRequestsCompleted() || runway.isThereRequestToProcess()) {//
			//LOGGER.info((!Airport.getInstance().isAllRequestsCompleted() || runway.isThereRequestToProcess())+", "+!Airport.getInstance().isAllRequestsCompleted()+", "+runway.isThereRequestToProcess());
			runway.processFlight();
		}
		runway.setProcesingComplete(true);
		LOGGER.info("Completed processing all the requests!!! closing the Runway "+runway.getId());
}
}
