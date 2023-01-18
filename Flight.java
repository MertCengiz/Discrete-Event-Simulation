
public class Flight {
	private int admissionTime;
	private String flightCode;
	private String flightACC;
	private String departAirport;
	private String arriveAirport;
	private int[] operationTimes;
	private int operationNumber;
	
	// All values given in the specific input lines are put in this class as data fields. Moreover, the information about in which operation does 
	// this flight is also put as a data field. Then, its constructor is created according to these variables. Then, getters and setters are created.
	
	public Flight (int admissionTime, String flightCode, String flightACC, String departAirport, String arriveAirport, int[] operationTimes) {
		this.admissionTime = admissionTime;
		this.flightCode = flightCode;
		this.flightACC = flightACC;
		this.departAirport = departAirport;
		this.arriveAirport = arriveAirport;
		this.operationTimes = operationTimes;
		operationNumber = 0;
	}

	public int getAdmissionTime() {
		return admissionTime;
	}

	public void setAdmissionTime(int admissionTime) {
		this.admissionTime = admissionTime;
	}

	public String getFlightCode() {
		return flightCode;
	}

	public void setFlightCode(String flightCode) {
		this.flightCode = flightCode;
	}

	public String getFlightACC() {
		return flightACC;
	}

	public void setFlightACC(String flightACC) {
		this.flightACC = flightACC;
	}

	public String getDepartAirport() {
		return departAirport;
	}

	public void setDepartAirport(String departAirport) {
		this.departAirport = departAirport;
	}

	public String getArriveAirport() {
		return arriveAirport;
	}

	public void setArriveAirport(String arriveAirport) {
		this.arriveAirport = arriveAirport;
	}

	public int[] getOperationTimes() {
		return operationTimes;
	}

	public void setOperationTimes(int[] operationTimes) {
		this.operationTimes = operationTimes;
	}

	public int getOperationNumber() {
		return operationNumber;
	}

	public void setOperationNumber(int operationNumber) {
		this.operationNumber = operationNumber;
	}
	
	
}
