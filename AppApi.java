import com.sun.source.doctree.SeeTree;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.xml.stream.FactoryConfigurationError;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AppApi {

    // this is to get weather data
    public static JSONObject getWeatherData(String locationName){
        // getting location
        JSONArray locationData = getLocationData(locationName);

        //checks if location null and avoids null exception
        if (locationData == null || locationData.isEmpty()) {
            System.out.println("No location data found for: " + locationName);
            return null;
        }

        //gets location and stores location
        JSONObject location = (JSONObject) locationData.get(0);
        //through location, we fetch longitude and latitude
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude
                +"&hourly=temperature_2m,weather_code";

        try {
            //fetching response and storing its response
            HttpURLConnection connection = fetchApiResponse(urlString);

            //checks response code
            if (connection.getResponseCode() != 200){
                System.out.println("Error: could not connect to API");
                return null;
            }

            //this stores user's input
            StringBuilder resultJSon = new StringBuilder();
            //we get the data through this
            Scanner scanner = new Scanner(connection.getInputStream());

            // continues if there is a input
            while (scanner.hasNext()){
                //this sends the next input to string builder
                resultJSon.append(scanner.nextLine());
            }
            scanner.close();
            connection.disconnect();

            // this is to parse the data
            JSONParser parser = new JSONParser();
            JSONObject resultJson = (JSONObject) parser.parse(String.valueOf(resultJSon));

            // getting the hourly data
            JSONObject hourly = (JSONObject) resultJson.get("hourly");

            // getting the time
            JSONArray time = (JSONArray) hourly.get("time");
            int index = FindIndexOfCurrentTime(time);

            // getting the temperature for current time
            JSONArray temperaturetxt = (JSONArray) hourly.get("temperature_2m");
            // storing the temperature
            double temperature = (double) temperaturetxt.get(index);
            System.out.println("this is the temperaturetxt or index "+temperature);

            // getting the weather condition
            JSONArray WeatherCode = (JSONArray) hourly.get("weather_code");
            System.out.println("this is the weather code "+WeatherCode);
            // with weather code we decided what's the weather
            String WeatherCondition = convertWeatherCode((long) WeatherCode.get(index));
            System.out.println("this is weather condition "+WeatherCondition);

            // returning the received data
            JSONObject weatherdata = new JSONObject();
            weatherdata.put("temperature",temperature);
            weatherdata.put("weather_condition",WeatherCondition);
            System.out.println("this is the Weather data "+weatherdata);

            return weatherdata;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // this is to get location data
    public static JSONArray getLocationData(String locationName){
        // replacing values to sent to api
        locationName = locationName.replaceAll(" ","+");

        String URLString = "https://geocoding-api.open-meteo.com/v1/search?name="+locationName+
                "&count=10&language=en&format=json";

        try{
            //establishing connection
            HttpURLConnection connection = fetchApiResponse(URLString);

             // checks if connection is established successfully
            if (connection.getResponseCode() != 200){
                System.out.println("Error: could not connect to API");
                return null;
            }else {

                // this stores the user input
                StringBuilder resultJson = new StringBuilder();
                //we get the data through this
                Scanner scanner = new Scanner(connection.getInputStream());

                // continuous if there is an input in textfield
                while (scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }
                scanner.close();
                connection.disconnect();

                // to parse  the data
                JSONParser parser = new JSONParser();
                //storing the result string in resultJson
                JSONObject resultJsonobj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // assigning values and returning it
                JSONArray locationData = (JSONArray) resultJsonobj.get("results");
                return locationData;

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // this is to get response which was fetched
    private static HttpURLConnection fetchApiResponse(String URLString){
        // handling any errors
        try{
            // getting the api url
            URL url = new URL(URLString);

            // opening a connection with api
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // getting the neccessary information
            conn.setRequestMethod("GET");

            // this connects with the api
            conn.connect();

            return conn;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    // this is to find the index of current time
    private static int FindIndexOfCurrentTime(JSONArray timeList){
        // we are getting the current time from another function
        String currentTime = getcurrentTime();

        // we are traversing through the timelist
        for (int i = 0;i<timeList.size();i++){
            // we are getting time from timelist
            String Time = (String) timeList.get(i);
            // we are comparing time and timelist and returning the index (if it is true)
            if (Time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }

        return 0;
    }

    // this is to get the cuurent time
    public static String getcurrentTime(){
        // we are getting the current time
        LocalDateTime CurrentDateTime = LocalDateTime.now();

        // we are formatting the time pattern according to the api to fetch the weather
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // we are storing the formatted time
        String formattedtime = CurrentDateTime.format(formatter);

        return formattedtime;
    }

    // this is to convert the weather code into text
    private static String convertWeatherCode(long weathercode){
        // the string stores the weather condition after code conversion
        String weatherCondtition = "";

        // we are assigning the weather condition according to weather code which we fetched
        if(weathercode == 0L){
            weatherCondtition = "Clear";
        } else if (weathercode > 0L && weathercode <= 3L) {
            weatherCondtition = "Cloudy";            
        } else {
            weatherCondtition = "Rainy";
        }

        System.out.println(weatherCondtition);
        return weatherCondtition;
    }
}
