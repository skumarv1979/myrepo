package au.com.pack;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AirportFlightControlSystem {

	public static void main(String[] args) {
		// Initialize airport

		// To avoid starting the thread at 900 th millisecond
		long tim = System.currentTimeMillis();
		long tmp = tim % 1000;

		if(tmp>0) {
			try {
				Thread.sleep(1000-tmp);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Airport airport = Airport.getInstance();
		airport.airportInitialize();

		List<Flights> flightsList = new ArrayList<Flights>();
		BufferedReader br = null;

		try {

			String sCurrentLine;

			// Configure the PilotToFlightControlRequest.conf file in the class
			// path
			// The format for this file is Flight No, Arrival/Departure(A/D),
			// Source/Destination, Seconds after the request to send to Flight
			// control room
			br = new BufferedReader(new FileReader(
					"PilotToFlightControlRequest.conf"));
			String[] strArr = null;
			int i = 1;
			DecimalFormat df = new DecimalFormat("000");
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine != null && !sCurrentLine.startsWith("#")) {
					/*strArr = sCurrentLine.split(",");
					if (strArr.length == 4) {
						flightsList
								.add(new Flights(
										"TS" + strArr[0],
										("A".equalsIgnoreCase(strArr[1]) ? ArrivalDeparture.ARRIVAL
												: ArrivalDeparture.DEPARTURE),
										strArr[2], Integer.parseInt(strArr[3])));
					}*/
					strArr = sCurrentLine.split(",");
					if (strArr.length == 3) {
						flightsList
								.add(new Flights(
										"TS" + df.format(i),
										("A".equalsIgnoreCase(strArr[0]) ? ArrivalDeparture.ARRIVAL
												: ArrivalDeparture.DEPARTURE),
										strArr[1]+i, Integer.parseInt(strArr[2])));
						i++;
					}
					
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
				100);
		for (Flights flight : flightsList) {
			executor.schedule(flight, flight.getSendReqAfter(),
					TimeUnit.SECONDS);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		Airport.getInstance().setAllRequestsCompleted(true);
		
		// Notify if any runway thread is waiting for request
		ExecutorService tmpThrd  = Executors.newSingleThreadExecutor();
		tmpThrd.execute(new Runnable() {
			@Override
			public void run() {
				int noRunways = Airport.getInstance().getNoOfRunways();
				boolean flg = false;
				do {
					try {
						synchronized (this) {
							wait(7000);							
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int j = 0; j < noRunways; j++) {
						flg = !Airport.getInstance().isAllRequestsCompleted() || Airport.getInstance().getRunway(j).isThereRequestToProcess();
						if(flg) {
							break;
						}
					}
				} while(flg);
				for (int j = 0; j < noRunways; j++) {
					Runway rw = Airport.getInstance().getRunway(j);
					if(!rw.isProcesingComplete()) {
						rw.notifyRunway();
					}
				}
			}
		});
		tmpThrd.shutdown();
	}
}