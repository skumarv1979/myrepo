package au.com.pack;


public class Flights implements Runnable {

	private String flightNo;
	private ArrivalDeparture arrDep;
	private String fromToStation;
	
	private int secsToWaitBeforeUsingRunway; // 
	
	private int sendReqAfter;// This is just to flight request simulation, trigger the request from pilot to airport

	public Flights(String flightNo, ArrivalDeparture arrDep, String fromToStation, int sendReqAfter) {
		this.flightNo = flightNo;
		this.arrDep = arrDep;
		this.fromToStation = fromToStation;
		this.sendReqAfter = sendReqAfter;
	}

	@Override
	public void run() {
		Airport.getInstance().assignRunway(this);
	}

	@Override
	public String toString() {
		return "Incoming "+(this.arrDep==ArrivalDeparture.ARRIVAL?"Arrival":"Departure") + " Request : Flight "+this.flightNo 
				+ (this.arrDep==ArrivalDeparture.ARRIVAL?" from ":" for ") +fromToStation;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public ArrivalDeparture getArrDep() {
		return arrDep;
	}

	public void setArrDep(ArrivalDeparture arrDep) {
		this.arrDep = arrDep;
	}

	public String getFromToStation() {
		return fromToStation;
	}

	public void setFromToStation(String fromToStation) {
		this.fromToStation = fromToStation;
	}

	public int getSecsToWaitBeforeUsingRunway() {
		return secsToWaitBeforeUsingRunway;
	}

	public void setSecsToWaitBeforeUsingRunway(int secsToWaitBeforeUsingRunway) {
		this.secsToWaitBeforeUsingRunway = secsToWaitBeforeUsingRunway;
	}

	public long getSendReqAfter() {
		return sendReqAfter;
	}

	public void setSendReqAfter(int sendReqAfter) {
		this.sendReqAfter = sendReqAfter;
	}

	public int runwayUsageTime() {
		return this.arrDep==ArrivalDeparture.ARRIVAL?AirportControlSysConstantsConf.ARRIVAL_TIME:AirportControlSysConstantsConf.DEPARTURE_TIME;
	}
	public String getLandingOrTakeOff() {
		return this.arrDep==ArrivalDeparture.ARRIVAL?"landing":"take off";
	}
	public String getDepartureOrArrival() {
		return this.arrDep==ArrivalDeparture.ARRIVAL?"arrival":"departure";
	}
	public String getForOrFrom() {
		return this.arrDep==ArrivalDeparture.ARRIVAL?"from":"for";
	}
}