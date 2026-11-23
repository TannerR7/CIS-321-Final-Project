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


public class inputVisitors{
    public void show(Stage primaryStage, loginInfo info){
        Label title = new Label("Input previous weeks customer count");

        TextField forecastID = new TextField();
        forecastID.setPromptText("Enter forecast ID of the previous week");

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setAlignment(Pos.CENTER);

        gp.add(new Label("Day"), 0,0);
        gp.add(new Label("Customer Count"), 1,0);

        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        TextField[] customerArray = new TextField[7];

        for (int x = 0; x <= 6; x++){
            gp.add(new Label(days[x]), 0, x+1);

            TextField customer = new TextField();
            customer.setPromptText("0");
            gp.add(customer, 1, x+1);
            customerArray[x] = customer;
        }
        Button btnSave = new Button("Save Data");
        Button btnHub = new Button("Back to Hub");

        Label msg = new Label();

        btnSave.setOnAction(e ->{
            try{
                String fID = forecastID.getText().trim();

                if(fID.isBlank()){
                    msg.setText("Enter the a forecast ID");
                    msg.setStyle("-fx-text-fill: red;");
                    return;
                }

                int iD = Integer.parseInt(fID);

                if(iD <= 0){
                    msg.setText("Enter a valid forecast ID");
                    msg.setStyle("-fx-text-fill: red;");
                    return;
                }

                int[] count = new int[7];

                for(int x=0; x<= 6; x++){
                    count[x] = Integer.parseInt(customerArray[x].getText().trim());

                    if (count[x] < 0){
                        msg.setText("The customer count must be positive");
                        msg.setStyle("-fx-text-fill: red;");
                        return;
                    }
                }
                msg.setText("Your inputs are valid");
                msg.setStyle("-fx-text-fill: green;");

                JsonObject cusJson = new JsonObject();

                cusJson.addProperty("forecast_id", iD);

                JsonArray cusArray = new JsonArray();

                for(int x = 0; x<=6; x++){
                    cusArray.add(count[x]);
                }

                cusJson.add("actual_visitors", cusArray);

                try{
                    HttpClient client = HttpClient.newHttpClient();

                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://127.0.0.1:8000/actualVisitors")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(cusJson.toString())).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if(response.statusCode() == 200){
                        msg.setText("Customer Count has been saved");
                        msg.setStyle("-fx-text-fill: green;");

                        System.out.println(response.body());
                    }else{
                        msg.setText("Customer count has failed to save");
                        msg.setStyle("-fx-text-fill: red;");
                        System.out.println(response.body());
                    }

                }catch(Exception error){
                    msg.setText("Can connect to the API");
                    msg.setStyle("-fx-text-fill: red;");
                    error.printStackTrace();
                }

            }catch(NumberFormatException error){
                msg.setText("Enter proper numbers for the forecast ID and the customer count");
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
        screenLayout.getChildren().addAll(title, new Label("Forecast ID"), forecastID, gp, btnSave, msg, btnHub, btnClose);

        Scene scene = new Scene(screenLayout, 500, 300);
        primaryStage.setTitle("Input Screen");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}