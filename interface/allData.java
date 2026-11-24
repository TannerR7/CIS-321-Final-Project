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

public class allData {
    public void show(Stage primaryStage, loginInfo info){
        Label title = new Label("Data");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
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

        btnClose.setPrefSize(200, 50);
        btnClose.setStyle("-fx-font-size: 15px;");
        btnHub.setPrefSize(200, 50);
        btnHub.setStyle("-fx-font-size: 15px;");
        btnEdit.setPrefSize(200, 50);
        btnEdit.setStyle("-fx-font-size: 15px;");

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
        b1.setAlignment(Pos.CENTER_RIGHT);
        b1.getChildren().add(btnClose);
        bp.setBottom(b1);

        HBox b2 = new HBox(20);
        b2.setAlignment(Pos.CENTER);
        b2.getChildren().addAll(btnEdit, btnHub);

        VBox vb = new VBox(15);
        vb.setAlignment(Pos.CENTER);
        vb.setMaxWidth(550);
        vb.getChildren().addAll(sp, b2, msg);
        bp.setCenter(vb);
        
        Scene scene = new Scene(bp);
        primaryStage.setTitle("All Data Display Screen");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

    }
}

