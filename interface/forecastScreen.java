import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
        title.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");
        Label sd = new Label("Start Date of Forecast is Sunday");
        sd.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");

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

        btnClose.setPrefSize(200, 50);
        btnClose.setStyle("-fx-font-size: 15px;");
        btnGen.setPrefSize(200, 50);
        btnGen.setStyle("-fx-font-size: 15px;");
        btnHub.setPrefSize(200, 50);
        btnHub.setStyle("-fx-font-size: 15px;");

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(20));

        HBox tr = new HBox();
        tr.setAlignment(Pos.CENTER_LEFT);
        tr.setSpacing(10);
        Region s = new Region();
        HBox.setHgrow(s, Priority.ALWAYS);
        tr.getChildren().addAll(title, s, btnClose);

        bp.setTop(tr);

        HBox b1 = new HBox(20);
        b1.setAlignment(Pos.CENTER);
        b1.getChildren().addAll(btnGen, btnHub);
        bp.setBottom(b1);

        VBox vb = new VBox(15);
        vb.setAlignment(Pos.CENTER);
        vb.setMaxWidth(650);
        vb.getChildren().addAll(sd, startDate, gp, msg);

        bp.setCenter(vb);

        Scene scene = new Scene(bp);
        primaryStage.setTitle("Forcast Screen");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}