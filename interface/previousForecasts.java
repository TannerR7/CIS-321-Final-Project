import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class previousForecasts {
    public void show(Stage primaryStage, loginInfo info){
        Label title = new Label("Previous Forecasts");
        Label msg = new Label();

        Button btnHub = new Button("Back to Hub");

        btnHub.setOnAction(e ->{
            hubScreen hub = new hubScreen();
            hub.show(primaryStage, info);
        });

        GridPane gp = new GridPane();
        gp.setHgap(15);
        gp.setVgap(15);
        gp.setAlignment(Pos.CENTER);

        gp.add(new Label("Forecast_ID"), 0, 0);
        gp.add(new Label("Date"), 1, 0);
        gp.add(new Label("Prediction"), 2, 0);
        gp.add(new Label("Lower Range Prediction"), 3, 0);
        gp.add(new Label("Upper Range Prediction"), 4, 0);



        try{
            HttpClient client = HttpClient.newHttpClient();

            String url = "http://127.0.0.1:8000/retreiveForecast/" + info.getRestaurantID();

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                msg.setText("Previous forecasts have been Retrieved");
                msg.setStyle("-fx-text-fill: green;");

                JsonArray forecasts = JsonParser.parseString(response.body()).getAsJsonArray();

                for(int x = 0; x < forecasts.size(); x++){
                    JsonObject forecast = forecasts.get(x).getAsJsonObject();

                    int forecastID = forecast.get("forecast_id").getAsInt();
                    String date = forecast.get("date").getAsString();
                    int visitors = forecast.get("predicted_visitors").getAsInt();
                    int low = forecast.get("lower_range").getAsInt();
                    int up = forecast.get("upper_range").getAsInt();

                    gp.add(new Label(String.valueOf(forecastID)), 0, x + 1);
                    gp.add(new Label(date), 1, x + 1);
                    gp.add(new Label(String.valueOf(visitors)), 2, x + 1);
                    gp.add(new Label(String.valueOf(low)), 3, x + 1);
                    gp.add(new Label(String.valueOf(up)), 4, x + 1);
                }
            }else{
                msg.setText("Retreival has failed");
                msg.setStyle("-fx-text-fill: red;");
            }

        }catch(Exception error){
            msg.setText("Can connect to the API");
            msg.setStyle("-fx-text-fill: red;");
            error.printStackTrace();
        }

        Button btnClose = new Button("Close System");
        btnClose.setOnAction(e->{
            System.exit(0);
        });

        VBox screenLayout = new VBox(15);
        screenLayout.setAlignment(Pos.CENTER);
        screenLayout.setPadding(new Insets(15));

        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setContent(gp);
        screenLayout.getChildren().addAll(title, sp, msg, btnHub, btnClose);

        Scene scene = new Scene(screenLayout, 500, 300);
        primaryStage.setTitle("Display Previous Forecast Screen");
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
