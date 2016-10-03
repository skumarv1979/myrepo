package au.com.pack;

public class RunwayAvailableTimeHelper {
	private int threadIndex;
	private int availabletime;
	public RunwayAvailableTimeHelper(int threadIndex, int availabletime) {
		this.threadIndex = threadIndex;
		this.availabletime = availabletime;
	}
	public int getThreadIndex() {
		return threadIndex;
	}
	public void setThreadIndex(int threadIndex) {
		this.threadIndex = threadIndex;
	}
	public int getAvailabletime() {
		return availabletime;
	}
	public void setAvailabletime(int availabletime) {
		this.availabletime = availabletime;
	}
	@Override
	public String toString() {
		return "Thread : "+threadIndex+", Avavilble Time : "+availabletime;
	}
}
