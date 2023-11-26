import java.io.*;
import java.util.*;

public class Project3 {
	public static void main(String[] args) {
		try {
			File theFile = new File(args[0]); // The input file is taken as the first argument.
			Scanner theReader = new Scanner (theFile); // It will be read by the scanner.
			
			int whichLine = 1;	// This is a counter for the number of lines.
			
			int numberOfACCs = 0; // The ACC array will be created accoring to this information.
			int numberOfFlights = 0; // This information is given in the inputs.
			String[][] allACCs = new String[numberOfACCs][]; // All ACCs will be put in a ragged array with their ATCs.
			HashMap <String, Integer> accTimes = new HashMap <String, Integer>(); // ACCs and their times will be put in a hash map.
			HashMap<String, Integer> atcTimes = new HashMap<>(); // ATCs and their times will also be put in a hash map.
			PriorityQueue<Flight> theOnlyQueue = new PriorityQueue<Flight>(new FlightCompare()); // The flights will be put in an array.
			TreeSet<Integer> indexes = new TreeSet<>(); // Indexes of ATCs are stored in a tree set.
			
			while (theReader.hasNextLine()) {
				String data = theReader.nextLine(); // This reveals that the input will be taken line by line.
				String[] splittedData = data.split("\\s+"); // Since the letters in the beginning and numbers between blanks
				// has its specific meaning, the data is splitted and each element between blanks are assigned to its variable.
				
				// In the first line, there are two information: the number of ACCs and flights. All of them are taken and
				// the first information is used to form an array of ACCs.
				
				if (whichLine == 1) {
					numberOfACCs = Integer.parseInt(splittedData[0]);
					numberOfFlights = Integer.parseInt(splittedData[1]);
					allACCs = new String[numberOfACCs][1001];
				}
				
				// After that for the number of lines from 2 until the number of ACCs, ACC information is given. The ACCs are created and
				// put the array accordingly. Moreover, ATCs are found via hashing and put into the array for the row of that specific ACC.
				
				if (whichLine >= 2 && whichLine <= numberOfACCs + 1) {
					String ACCCode = splittedData[0];
					allACCs[whichLine - 2][0] = ACCCode;
					for (int i = 1; i < splittedData.length; i++) {
						int theResult = (int)splittedData[i].charAt(0) + 31*(int)splittedData[i].charAt(1) + 31*31*(int)splittedData[i].charAt(2);
						theResult = theResult % 1000;
						if (indexes.contains(theResult) == false)
							indexes.add(theResult);
						else if (indexes.contains(theResult)) {
							int j = 1;
							while (indexes.contains(theResult + j) == true)
								j++;
							theResult = theResult + j;
							indexes.add(theResult);
						}
						String stringResult = Integer.toString(theResult);
						if (theResult < 100 && theResult >= 10)
							stringResult = "0" + Integer.toString(theResult);
						else if (theResult > 0 && theResult < 10)
							stringResult = "00" + Integer.toString(theResult);
						stringResult = splittedData[i] + stringResult;
						allACCs[whichLine - 2][i] = stringResult;
					}					
				}
				
				// In the rest lines in the input file, flights are created according to the values given. Then, they are put into the queue. 
				
				else if (whichLine > numberOfACCs + 1){
					int admissionTime = Integer.parseInt(splittedData[0]);
					String flightCode = splittedData[1];
					String flightACC = splittedData[2];
					String departAirport = splittedData[3];
					String arriveAirport = splittedData[4];
					int[] operationTimes = new int[21];
					for (int i = 5; i < splittedData.length; i++) 
						operationTimes[i-5] = Integer.parseInt(splittedData[i]);
					Flight flight = new Flight(admissionTime, flightCode, flightACC, departAirport, arriveAirport, operationTimes);
					theOnlyQueue.add(flight);
				}
				
				whichLine++; // Counter is incremented.
			}			
			theReader.close(); // After reading everything, the reader is closed.
			
			// Time hash maps are set to zero for all ACCs and ATCs. Since this is an array, a null check must be done.
			
			for (int i = 0; i < allACCs.length; i++) {
				accTimes.put(allACCs[i][0], 0);
				for(int j= 1; j< allACCs[i].length; j++) {
					String airport = allACCs[i][j] != null ? allACCs[i][j].substring(0, 3) : null;
					atcTimes.put(airport, 0);
				}
			}
			
			
			while (theOnlyQueue.isEmpty() == false) { // Queue is not empty means there are still flights to operate.
				int opNum = theOnlyQueue.peek().getOperationNumber(); // Every turn the operation is done according to the operation number.
				// In ACCs, the time of that ACC and the admission time of that flight is incremented, and added the queue again.
				// Of course, operation set is altered according to the state that going to ATC or staying in ACC.
				// If it stays in the ACC, the admission time of that flight increases by both the running and the next running time.
				// If it is going to an ATC, the admission time of that flight increases by just the next running time.
				// There is 30-time limit, so they are checked with another if-else-statement.
				// If the operation exceeds 30, it goes back to the queue, and its operation number does not increase.
				if (opNum == 0 || opNum == 10) {  //ACC inside
					if (theOnlyQueue.peek().getOperationTimes()[opNum] <= 30) {
						Flight opFlight = theOnlyQueue.poll();
						int value = Integer.max(opFlight.getAdmissionTime(), accTimes.get(opFlight.getFlightACC()));
						accTimes.put(opFlight.getFlightACC(), value + opFlight.getOperationTimes()[opNum]);
						if (opFlight.getAdmissionTime() >= value)
							opFlight.setAdmissionTime(value + opFlight.getOperationTimes()[opNum] + opFlight.getOperationTimes()[opNum + 1]);
						else if (opFlight.getAdmissionTime() < value)
							opFlight.setAdmissionTime(value + opFlight.getOperationTimes()[opNum] + opFlight.getOperationTimes()[opNum + 1]);
						opFlight.setOperationNumber(opNum + 2);
						theOnlyQueue.add(opFlight);
					}
					else if (theOnlyQueue.peek().getOperationTimes()[opNum] > 30) {
						Flight opFlight = theOnlyQueue.poll();
						int value = Integer.max(opFlight.getAdmissionTime(), accTimes.get(opFlight.getFlightACC()));
						accTimes.put(opFlight.getFlightACC(), value + 30);
						opFlight.getOperationTimes()[opNum] -= 30;
						opFlight.setAdmissionTime(value + 30);
						theOnlyQueue.add(opFlight);
					}
				}
				else if (opNum == 2 || opNum == 12) { // ACC to ATC
					if (theOnlyQueue.peek().getOperationTimes()[opNum] <= 30) {
						Flight opFlight = theOnlyQueue.poll();
						int value = Integer.max(opFlight.getAdmissionTime(), accTimes.get(opFlight.getFlightACC()));
						accTimes.put(opFlight.getFlightACC(), value + opFlight.getOperationTimes()[opNum]);
						if (opFlight.getAdmissionTime() >= value)
							opFlight.setAdmissionTime(value + opFlight.getOperationTimes()[opNum]);
						else if (opFlight.getAdmissionTime() < value)
							opFlight.setAdmissionTime(value + opFlight.getOperationTimes()[opNum]);
						opFlight.setOperationNumber(opNum + 1);
						theOnlyQueue.add(opFlight);
					}
					else if (theOnlyQueue.peek().getOperationTimes()[opNum] > 30) {
						Flight opFlight = theOnlyQueue.poll();
						int value = Integer.max(opFlight.getAdmissionTime(), accTimes.get(opFlight.getFlightACC()));
						accTimes.put(opFlight.getFlightACC(), value + 30);
						opFlight.getOperationTimes()[opNum] -= 30;
						opFlight.setAdmissionTime(value + 30);
						theOnlyQueue.add(opFlight);
					}
				}
				// In the last operation, if it is ended, the time of ACC is increased and the operation flight disappears.
				else if (opNum == 20) { // The last operation
					if (theOnlyQueue.peek().getOperationTimes()[opNum] <= 30) {
						Flight opFlight = theOnlyQueue.poll();
						int value = Integer.max(opFlight.getAdmissionTime(), accTimes.get(opFlight.getFlightACC()));
						accTimes.put(opFlight.getFlightACC(), value + opFlight.getOperationTimes()[opNum]);
						if (opFlight.getAdmissionTime() >= value)
							opFlight.setAdmissionTime(value + opFlight.getOperationTimes()[opNum]);
						else if (opFlight.getAdmissionTime() < value)
							opFlight.setAdmissionTime(value + opFlight.getOperationTimes()[opNum]);
					}
					else if (theOnlyQueue.peek().getOperationTimes()[opNum] > 30) {
						Flight opFlight = theOnlyQueue.poll();
						int value = Integer.max(opFlight.getAdmissionTime(), accTimes.get(opFlight.getFlightACC()));
						accTimes.put(opFlight.getFlightACC(), value + 30);
						opFlight.getOperationTimes()[opNum] -= 30;
						opFlight.setAdmissionTime(value + 30);
						theOnlyQueue.add(opFlight);
					}
				}
				// In ATCs, just the time of that ATC and the admission time of that flight is incremented, and added the queue again.
				// Of course, operation set is altered according to the state that going to ACC or staying in ATC.
				// If it stays in the ATC, the admission time of that flight increases by both the running and the next running time.
				// If it is going to the ACC, the admission time of that flight increases by just the next running time.
				// There is no 30-time limit, so it is not checked with another if-statement.
				else if (opNum == 3 || opNum == 5 || opNum == 7) { // Departure ATC inside
					Flight opFlight = theOnlyQueue.poll();
					int value = Integer.max(opFlight.getAdmissionTime(), atcTimes.get(opFlight.getDepartAirport()));
					atcTimes.put(opFlight.getDepartAirport(), value + opFlight.getOperationTimes()[opNum]);
					opFlight.setAdmissionTime(value + opFlight.getOperationTimes()[opNum] + opFlight.getOperationTimes()[opNum + 1]);
					opFlight.setOperationNumber(opNum + 2);
					theOnlyQueue.add(opFlight);
				}
				else if	(opNum == 13 || opNum == 15 || opNum == 17) { // Arrival ATC inside
					Flight opFlight = theOnlyQueue.poll();
					int value = Integer.max(opFlight.getAdmissionTime(), atcTimes.get(opFlight.getArriveAirport()));
					atcTimes.put(opFlight.getArriveAirport(), value + opFlight.getOperationTimes()[opNum]);
					opFlight.setAdmissionTime(value + opFlight.getOperationTimes()[opNum] + opFlight.getOperationTimes()[opNum + 1]);
					opFlight.setOperationNumber(opNum + 2);
					theOnlyQueue.add(opFlight);
				}
				else if (opNum == 9) { // Departure ATC to ACC
					Flight opFlight = theOnlyQueue.poll();
					int value = Integer.max(opFlight.getAdmissionTime(), atcTimes.get(opFlight.getDepartAirport()));
					atcTimes.put(opFlight.getDepartAirport(), value + opFlight.getOperationTimes()[opNum]);
					opFlight.setAdmissionTime(value + opFlight.getOperationTimes()[opNum]);
					opFlight.setOperationNumber(opNum + 1);
					theOnlyQueue.add(opFlight);
				} 
				else if (opNum == 19) { // Arrival ATC to ACC
					Flight opFlight = theOnlyQueue.poll();
					int value = Integer.max(opFlight.getAdmissionTime(), atcTimes.get(opFlight.getArriveAirport()));
					atcTimes.put(opFlight.getArriveAirport(), value + opFlight.getOperationTimes()[opNum]);
					opFlight.setAdmissionTime(value + opFlight.getOperationTimes()[opNum]);
					opFlight.setOperationNumber(opNum + 1);
					theOnlyQueue.add(opFlight);
				} 
				
			}
			FileWriter theWriter = new FileWriter(args[1]); // Then, writer is taken as the second argument.
			String logMessage = ""; // A log message is created.
			// For non-null values in the ragged array (ACCs and ATCs) log messages are created.
			for (int i = 0; i < allACCs.length; i++) {					
				for (int j = 0; j < allACCs[i].length; j++) {
					if (allACCs[i][j] != null) {
						logMessage = logMessage.concat(allACCs[i][j] + " ");
						if (j == 0)
							logMessage = logMessage.concat(Integer.toString(accTimes.get(allACCs[i][0])) + " ");
					}
				}
				logMessage = logMessage.concat("\n");
			}
			theWriter.write(logMessage); // Then, the message is written to the output file.
			theWriter.close(); // After writing everything, the writer is closed.
		}
		catch (FileNotFoundException e){
			e.printStackTrace();  // This is called if there is a problem in reading. 
		}
		catch (IOException e) {
			e.printStackTrace(); // This is called if there is a problem in writing. 
		}
	}
}
