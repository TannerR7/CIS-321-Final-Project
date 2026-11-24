import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class displayForecast {
    public void show(Stage primaryStage, loginInfo info, String responseInfo){
        JsonObject response = JsonParser.parseString(responseInfo).getAsJsonObject();

        int forecastID = response.get("forecast_id").getAsInt();
        JsonArray predictions = response.getAsJsonArray("predictions");

        Label title = new Label("Forecast (ID:" + forecastID + ") for the week");
        title.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");
        
        GridPane gp = new GridPane();

        gp.setHgap(15);
        gp.setVgap(15);
        gp.setAlignment(Pos.CENTER);

        gp.add(new Label("Date"), 0, 0);
        gp.add(new Label("Prediction"), 1, 0);
        gp.add(new Label("Lower Range Prediction"), 2, 0);
        gp.add(new Label("Upper Range Prediction"), 3, 0);
        
        for(int x = 0; x < predictions.size(); x++){
            JsonObject prediciton = predictions.get(x).getAsJsonObject();

            String date = prediciton.get("date").getAsString();
            int visitors = prediciton.get("predicted_visitors").getAsInt();
            int low = prediciton.get("lower_range").getAsInt();
            int up = prediciton.get("upper_range").getAsInt();

            gp.add(new Label(date), 0, x + 1);
            gp.add(new Label(String.valueOf(visitors)), 1, x + 1);
            gp.add(new Label(String.valueOf(low)), 2, x + 1);
            gp.add(new Label(String.valueOf(up)), 3, x + 1);
        }

        Button btnHub = new Button("Back to Hub");

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
        btnHub.setPrefSize(200, 50);
        btnHub.setStyle("-fx-font-size: 15px;");

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(20));

        HBox tr = new HBox();
        tr.setAlignment(Pos.CENTER);
        tr.getChildren().add(title);
        bp.setTop(tr);

        HBox b1 = new HBox(20);
        b1.setAlignment(Pos.CENTER);
        b1.getChildren().addAll(btnHub, btnClose);

        VBox vb = new VBox(15);
        vb.setAlignment(Pos.CENTER);
        vb.setMaxWidth(500);
        vb.getChildren().addAll(gp, b1);
        bp.setCenter(vb);


        Scene scene = new Scene(bp);
        primaryStage.setTitle("Display Forecast Screen");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}
