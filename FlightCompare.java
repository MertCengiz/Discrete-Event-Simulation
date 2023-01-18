import java.util.Comparator;

public class FlightCompare implements Comparator<Flight>{
	@Override
	public int compare(Flight o1, Flight o2) {
		// TODO Auto-generated method stub
		if (o1.getAdmissionTime() - o2.getAdmissionTime() == 0)
			return o1.getFlightCode().compareTo(o2.getFlightCode());
		return o1.getAdmissionTime() - o2.getAdmissionTime();
	}
}
