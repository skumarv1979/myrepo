package au.com.pack;

public enum ArrivalDeparture {
	ARRIVAL("A"), DEPARTURE("D");

	private String statusCode;

	private ArrivalDeparture(String s) {
		statusCode = s;
	}

	public String getStatusCode() {
		return statusCode;
	}


}
