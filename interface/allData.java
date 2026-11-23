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

public class allData {
    public void show(Stage primaryStage, loginInfo info){
        Label title = new Label("Data");
        Label msg = new Label();

        Button btnHub = new Button("Back to Hub");
        btnHub.setOnAction(e ->{
            hubScreen hub = new hubScreen();
            hub.show(primaryStage, info);
        });

        Button btnEdit = new Button("Edit Button");
        btnEdit.setOnAction(e ->{
            editData edit = new editData();
            edit.show(primaryStage, info);
        });

        GridPane gp = new GridPane();
        gp.setHgap(15);
        gp.setVgap(15);
        gp.setAlignment(Pos.CENTER);

        gp.add(new Label("Visitor_ID"), 0, 0);
        gp.add(new Label("Date"), 1, 0);
        gp.add(new Label("Customer Count"), 2, 0);
        gp.add(new Label("Reserved Visitors"), 3, 0);
        gp.add(new Label("Holiday Flags"), 4, 0);



        try{
            HttpClient client = HttpClient.newHttpClient();

            String url = "http://127.0.0.1:8000/retrieveData/" + info.getRestaurantID();

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                msg.setText("Data have been Retrieved");
                msg.setStyle("-fx-text-fill: green;");

                JsonArray datas = JsonParser.parseString(response.body()).getAsJsonArray();

                for(int x = 0; x < datas.size(); x++){
                    JsonObject data = datas.get(x).getAsJsonObject();

                    int visitorID = data.get("visitor_id").getAsInt();
                    String date = data.get("date").getAsString();
                    int customer = data.get("customer_count").getAsInt();
                    String resVisit = data.get("reserved_visitors").isJsonNull() ? "N/A": data.get("reserved_visitors").getAsString();
                    int holiFlg = data.get("holiday_flag").getAsInt();

                    gp.add(new Label(String.valueOf(visitorID)), 0, x + 1);
                    gp.add(new Label(date), 1, x + 1);
                    gp.add(new Label(String.valueOf(customer)), 2, x + 1);
                    gp.add(new Label(resVisit), 3, x + 1);
                    gp.add(new Label(String.valueOf(holiFlg)), 4, x + 1);
                }
            }else{
                msg.setText("Retreival has failed");
                msg.setStyle("-fx-text-fill: red;");
            }

        }catch(Exception error){
            msg.setText("Unable to retrieve or display your data");
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
        screenLayout.getChildren().addAll(title, sp, msg, btnEdit, btnHub, btnClose);

        Scene scene = new Scene(screenLayout, 500, 300);
        primaryStage.setTitle("All Data Display Screen");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

    }
}

