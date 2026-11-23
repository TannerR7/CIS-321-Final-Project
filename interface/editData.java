import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;

public class editData {
    public void show(Stage primaryStage, loginInfo info){
        Label title = new Label("Generate Forecast");
        Label msg = new Label();
        TextField editDate = new TextField();
        editDate.setPromptText("YYYY-MM-DD");

        ComboBox<String> chooseField = new ComboBox<>();
        chooseField.getItems().addAll("customer_count", "reserved_visitors", "holiday_flag");
        chooseField.setPromptText("Select the field you wish to change");

        TextField correctedValue = new TextField();
        correctedValue.setPromptText("Enter new value");

        Button btnSave = new Button("Save Correction");
        Button btnHub = new Button("Back to Hub");

        btnHub.setOnAction(e ->{
            hubScreen hub = new hubScreen();
            hub.show(primaryStage, info);
        });

        btnSave.setOnAction(e ->{
            try{
                String date = editDate.getText().trim();
                String field = chooseField.getValue();
                String value = correctedValue.getText().trim();

                if(date.isBlank() || value.isBlank() || field == null){
                    msg.setText("One or more fields has not been inputed");
                    msg.setStyle("-fx-text-fill: red;");
                    return;
                }
                
                try{
                    LocalDate.parse(date);
                }catch(DateTimeParseException error){
                    msg.setText("Enter a valid date using the format YYYY-MM-DD");
                    msg.setStyle("-fx-text-fill: red;");
                    return;
                }

                int correctValue = Integer.parseInt(value);

                if(correctValue <0){
                    msg.setText("A corrected value must be positive");
                    msg.setStyle("-fx-text-fill: red;");
                    return;
                }
                if(field.equals("holiday_flag") && correctValue != 0 && correctValue != 1){
                    msg.setText("The holiday flag must be either 0 or 1");
                    msg.setStyle("-fx-text-fill: red;");
                    return;
                }

                msg.setText("Inputs are Valid");
                msg.setStyle("-fx-text-fill: green;");

                JsonObject editJson = new JsonObject();

                editJson.addProperty("restaurant_id", info.getRestaurantID());
                editJson.addProperty("visit_date", date);
                editJson.addProperty("user_id", info.getUserID());
                editJson.addProperty("field_changed", field);
                editJson.addProperty("corrected_value", correctValue);

                try{
                    HttpClient client = HttpClient.newHttpClient();

                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://127.0.0.1:8000/edit-data")).header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(editJson.toString())).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if(response.statusCode() == 200){
                        msg.setText("Your edit has been saved");
                        msg.setStyle("-fx-text-fill: green;");

                        allData ad = new allData();
                        ad.show(primaryStage, info);
                    }else{
                        msg.setText("Edit has failed");
                        msg.setStyle("-fx-text-fill: red;");
                    }

                }catch(Exception error){
                    msg.setText("Can connect to the API");
                    msg.setStyle("-fx-text-fill: red;");
                    error.printStackTrace();
                }
            }catch(NumberFormatException error){
                msg.setText("The corrected value you have enter is invalid");
                msg.setStyle("-fx-text-fill: red;");
            }
        });

        Button btnClose = new Button("Close System");
        btnClose.setOnAction(e->{
            System.exit(0);
        });
        VBox screenLayout = new VBox(15);
        screenLayout.setAlignment(Pos.CENTER);
        screenLayout.setPadding(new Insets(30));
        screenLayout.getChildren().addAll(title, new Label("Edit Date"), editDate, correctedValue, chooseField, btnSave, btnHub, btnClose, msg);

        Scene scene = new Scene(screenLayout, 500, 300);
        primaryStage.setTitle("Edit Screen");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}
