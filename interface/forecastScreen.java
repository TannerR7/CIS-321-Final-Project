import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeParseException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class forecastScreen{
    public void show(Stage primaryStage, loginInfo info){
        Label title = new Label("Generate Forecast");

        TextField startDate = new TextField();
        startDate.setPromptText("YYYY-MM-DD");

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setAlignment(Pos.CENTER);

        gp.add(new Label("Day"), 0,0);
        gp.add(new Label("Reservations"), 1,0);
        gp.add(new Label("Flag Holiday (0 or 1)"),2,0);

        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        TextField[] reservationArray = new TextField[7];
        TextField[] holidayArray = new TextField[7];

        for (int x = 0; x <= 6; x++){
            gp.add(new Label(days[x]), 0, x+1);

            TextField reservation = new TextField();
            reservation.setPromptText("0");
            gp.add(reservation, 1, x+1);
            reservationArray[x] = reservation;

            TextField holidayFlg = new TextField();
            holidayFlg.setPromptText("0");
            gp.add(holidayFlg, 2, x+1);
            holidayArray[x] = holidayFlg;
        }
        Button btnGen = new Button("Generate Forecast");
        Button btnHub = new Button("Back to Hub");

        Label msg = new Label();

        btnGen.setOnAction(e ->{
            try{
                String foreDate = startDate.getText().trim();

                if(foreDate.isBlank()){
                    msg.setText("Enter the starting date for this forecast");
                    msg.setStyle("-fx-text-fill: red;");
                    return;
                }

                try{
                    LocalDate testingDate = LocalDate.parse(foreDate);

                    if(testingDate.getDayOfWeek() != DayOfWeek.SUNDAY){
                        msg.setText("The starting date must be a sunday");
                        msg.setStyle("-fx-text-fill: red;");
                        return;
                    }

                }catch(DateTimeParseException error){
                    msg.setText("Enter a valid date using the format YYYY-MM-DD");
                    msg.setStyle("-fx-text-fill: red;");
                    return;
                }

                int[] reservations = new int[7];
                int[] holidayFlgs = new int[7];
                for(int x=0; x<= 6; x++){
                    reservations[x] = Integer.parseInt(reservationArray[x].getText().trim());
                    holidayFlgs[x] = Integer.parseInt(holidayArray[x].getText().trim());

                    if (reservations[x] < 0){
                        msg.setText("Reservations must be positive");
                        msg.setStyle("-fx-text-fill: red;");
                        return;
                    }
                    if (holidayFlgs[x] != 0 && holidayFlgs[x] != 1){
                        msg.setText("The holiday flage must be either 0 or 1");
                        msg.setStyle("-fx-text-fill: red;");
                        return;
                    }
                }
                msg.setText("Your inputs are valid");
                msg.setStyle("-fx-text-fill: green;");

                JsonObject foreJson = new JsonObject();

                foreJson.addProperty("restaurant_id", info.getRestaurantID());
                foreJson.addProperty("user_id", info.getUserID());
                foreJson.addProperty("forecast_start", foreDate);

                JsonArray resJson = new JsonArray();
                JsonArray holiJson = new JsonArray();

                for(int x = 0; x<=6; x++){
                    resJson.add(reservations[x]);
                    holiJson.add(holidayFlgs[x]);
                }

                foreJson.add("daily_reservations", resJson);
                foreJson.add("daily_holiday_flags", holiJson);

                try{
                    HttpClient client = HttpClient.newHttpClient();

                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://127.0.0.1:8000/forecast")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(foreJson.toString())).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if(response.statusCode() == 200){
                        msg.setText("Forecast has been generated");
                        msg.setStyle("-fx-text-fill: green;");

                        displayForecast df = new displayForecast();
                        df.show(primaryStage, info, response.body());
                    }else{
                        msg.setText("Forecast has failed generation");
                        msg.setStyle("-fx-text-fill: red;");
                    }

                }catch(Exception error){
                    msg.setText("Can connect to the API");
                    msg.setStyle("-fx-text-fill: red;");
                    error.printStackTrace();
                }

            }catch(NumberFormatException error){
                msg.setText("Enter proper numbers for reservations and holiday flags");
                msg.setStyle("-fx-text-fill: red;");
            }
        });

        btnHub.setOnAction(e ->{
            hubScreen hub = new hubScreen();
            hub.show(primaryStage, info);
        });

        Button btnClose = new Button("Close System");
        btnClose.setOnAction(e->{
            System.exit(0);
        });
        
        VBox screenLayout = new VBox(15);
        screenLayout.setAlignment(Pos.CENTER);
        screenLayout.setPadding(new Insets(30));
        screenLayout.getChildren().addAll(title, new Label("Start Date of Forecast is Sunday"), startDate, gp, btnGen, btnHub, btnClose, msg);

        Scene scene = new Scene(screenLayout, 500, 300);
        primaryStage.setTitle("Forcast Screen");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}