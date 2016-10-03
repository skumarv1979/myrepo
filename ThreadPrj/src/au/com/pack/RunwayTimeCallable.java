package au.com.pack;

import java.util.concurrent.Callable;

public class RunwayTimeCallable implements Callable<RunwayAvailableTimeHelper> {
	Runway runway;

	public RunwayTimeCallable(Runway runway) {
		this.runway = runway;
	}
	@Override
	public RunwayAvailableTimeHelper call() throws Exception {
		return runway.getRunwayBusyTime();
	}

}
