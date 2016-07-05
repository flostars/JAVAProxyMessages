package jobs;

public class GenericJob {

	protected void log(String message){
		System.out.println("[" + getClass() + "] " + message);
	}
}
