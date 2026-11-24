import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
        title.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");
        Label msg = new Label();
        msg.setStyle("-fx-font-size: 15px;");

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

        btnClose.setPrefSize(200, 50);
        btnClose.setStyle("-fx-font-size: 15px;");
        btnHub.setPrefSize(200, 50);
        btnHub.setStyle("-fx-font-size: 15px;");

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(20));

        HBox tr = new HBox();
        tr.setAlignment(Pos.CENTER);
        tr.getChildren().add(title);
        bp.setTop(tr);

        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setMaxHeight(600);
        sp.setContent(gp);
        
        HBox b1 = new HBox(20);
        b1.setAlignment(Pos.CENTER);
        b1.getChildren().addAll(btnHub, btnClose);
        
        VBox vb = new VBox(15);
        vb.setAlignment(Pos.CENTER);
        vb.setMaxWidth(650);
        vb.getChildren().addAll(sp, b1, msg);
        bp.setCenter(vb);



        Scene scene = new Scene(bp);
        primaryStage.setTitle("Display Previous Forecast Screen");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

    }
}
