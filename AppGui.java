import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;

public class AppGui extends JFrame {
    // this is to store weather data
        private JSONObject WeatherData;


        // this is constructor of gui
       public AppGui(){
           super("Weather App");

           setSize(450,500);

           setLayout(null);

           setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

           setResizable(false);

           addguicomponents();
       }


       // this is adding of gui components
       public void addguicomponents(){
           JTextField text = new JTextField();
           text.setBounds(15,15,320,29);
           add(text);

           JLabel Weatherpic = new JLabel(loadimage("src/sunny.png"));
           Weatherpic.setBounds(68,35,300,300);
           add(Weatherpic);

           JLabel WeatherIn = new JLabel("Weather in :");
           WeatherIn.setBounds(25,320,450,30);
           WeatherIn.setFont(new Font("MV boli",Font.BOLD,22));
           add(WeatherIn);

           JLabel Temperaturetxt = new JLabel("25" +"\u00B0" +"C");
           Temperaturetxt.setBounds(110,370,100,30);
           Temperaturetxt.setFont(new Font("MV boli",Font.BOLD,30));
           add(Temperaturetxt);

           JLabel Weathertxt = new JLabel("Sunny");
           Weathertxt.setBounds(230,370,150,35);
           Weathertxt.setFont(new Font("MV boli",Font.BOLD,33));
           add(Weathertxt);

           JButton search = new JButton("search");
           search.setBounds(340,15,80,28);
           search.setFont(new Font("Dialog",Font.PLAIN,15));
           search.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   String userInput1 = text.getText();
                   String userInput = userInput1.substring(0,1).toUpperCase() + userInput1.substring(1);
                   System.out.println("this is user input " + userInput);

                   // this replaces " " with + and splits with quotation like(united states) -> "united","states"
                   if (userInput.replaceAll("\\s","").length() <= 0){
                       return;
                   }

                   // fetching weather from getweatherdata
                   WeatherData = AppApi.getWeatherData(userInput);
                   System.out.println("this is weather data after fetching api " + WeatherData);

                   if (WeatherData == null) {
                       WeatherIn.setText("Weather data not found.");
                       Temperaturetxt.setText("");
                       Weathertxt.setText("");
                       Weatherpic.setIcon(null);
                       return;
                   }

                   //getting weather condition
                   String WeatherCondition = (String) WeatherData.get("weather_condition");
                   System.out.println("this is weather condition " + WeatherCondition);

                   //assigning weather image according to weather
                   switch (WeatherCondition) {
                       case "Clear":
                           Weatherpic.setIcon(loadimage("src/sunny.png"));
                           break;
                       case "Cloudy":
                           Weatherpic.setIcon(loadimage("src/cloudy.png"));
                           break;
                       default:
                           Weatherpic.setIcon(loadimage("src/rainy.png"));
                           break;
                   }

                   // assigning temperature value
                   double temperature = (double) WeatherData.get("temperature");
                   System.out.println(temperature);
                   String TemperatureStr = String.format("%.0f°C",temperature);

                   System.out.println(TemperatureStr);
                   // setting GUI according to the updated data
                   Temperaturetxt.setText(TemperatureStr);

                   Weathertxt.setText(WeatherCondition);

                   WeatherIn.setText("Weather in: " + userInput);
               }
           });
           add(search);

       }

       private ImageIcon loadimage(String resourcePath){
           try{

               // getting the image
               BufferedImage image = ImageIO.read(new File(resourcePath));

               return new ImageIcon(image);

           }catch (IOException e){
               e.printStackTrace();
           }
           System.out.println("couldn't find the image");
           return null;
       }

}
