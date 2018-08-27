package connected;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


@RestController
public class ConnectedController {
	private static Map<String, Set<String>> cityToNodeMap;
	private static final String filename = "/Users/cameronfish/Desktop/connected-test-project/src/resources/connected/cities.txt";
	
	static
	{
			try {
				cityToNodeMap = parseFileIntoConnections(filename);
			} catch (Exception e) {
				String message = e.getMessage();
				System.err.println("Error with data file :"+filename+".  " + message);
				e.printStackTrace();
				System.exit(2);
			}
		
	}

	public static final String DELIMITER = ",";

    @RequestMapping("/connected")
    public String connected(@RequestParam(value="origin", defaultValue="NoOrigin") String origin,@RequestParam(value="destination", defaultValue="NoDestination") String destination)
    {
    	
    	boolean result = isConnected(cityToNodeMap, origin, destination);
		return returnResult(result);

    }
    
    private static String returnResult(boolean result) {
		if (result) {
			return "yes";
		} else {
			return "no";
		}
	}
    
	/**
	 * @param filename -  the data file that contains the city pairs. 
	 * @return  Map<String, Set<String>> -  where the key is the city name and the value is a set of the cities is is connected to. 
	 * @throws IOException
	 */
	private static Map<String, Set<String>> parseFileIntoConnections(String filename) throws IOException 
	{
		Map<String, Set<String>> cityToNodeMap = new HashMap<String, Set<String>>();

		BufferedReader bufferedReader = null;
		try {
			Reader fileReader = new FileReader(filename);
			bufferedReader = new BufferedReader(fileReader);
			String line = bufferedReader.readLine();
			while (line != null && !line.isEmpty()) {
				String[] cities = line.split(DELIMITER);
				String firstCity = cities[0].trim();
				String secondCity = cities[1].trim();

				Set<String> firstCityConnections = getCityConnections(cityToNodeMap, firstCity);
				Set<String> secondCityConnections = getCityConnections(cityToNodeMap, secondCity);
				firstCityConnections.add(secondCity);
				secondCityConnections.add(firstCity);

				line = bufferedReader.readLine();
			}
		} finally 
		{
			if (bufferedReader != null) 
			{
				bufferedReader.close();
			}
		}

		return cityToNodeMap;
	}

	
	/**
	 * @param map  - the overall list of cities and their connected set of cities.
	 * @param city - the city to add or add to it's connnected city set
	 * @return the associated set of cities for the city
	 */
	private static Set<String> getCityConnections(Map<String, Set<String>> map, String city) {
		if (!map.containsKey(city)) {
			map.put(city, new HashSet<String>());
		}
		return map.get(city);
	}


	/**
	 * @param cityToNodeMap -   map of cities and connected cities
	 * @param origin  
	 * @param destination
	 * @return true/false - are they connected
	 */
	private static boolean isConnected(Map<String, Set<String>> cityToNodeMap, String origin, String destination) {
		
		boolean isFound = origin.equals(destination);  // true is cities are the same
		
		if (cityToNodeMap.containsKey(origin) && cityToNodeMap.containsKey(destination)) 
		{
			Queue<String> citiesToVisit = new LinkedList<String>();
			
			Set<String> citiesAlreadyVisited = new HashSet<String>();

			citiesToVisit.add(origin);

			while (!citiesToVisit.isEmpty() && !isFound) 
			{
				String city = citiesToVisit.poll();
				isFound = city.equals(destination);

				Set<String> possibleConnections = cityToNodeMap.get(city);
				
				for (String possibleCity : possibleConnections) 
				{
					if (!citiesAlreadyVisited.contains(possibleCity)) 
					{
						citiesToVisit.add(possibleCity);
						citiesAlreadyVisited.add(possibleCity);
					}
				}
			}
		}

		return isFound;
	}

    
}
