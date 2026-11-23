import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class loginScreen extends Application{
    @Override
    public void start(Stage primaryStage){
        Label title = new Label("Demand Forecast System");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
        Label message = new Label();
        message.setStyle("-fx-font-size: 15px;");
        Label loginTitle = new Label("Login");
        loginTitle.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");
        TextField enterUsername = new TextField();
        enterUsername.setPromptText("Username");

        PasswordField enterPassword = new PasswordField();
        enterPassword.setPromptText("Password");
    
        Button btnLogin = new Button("Login");
        btnLogin.setOnAction(e ->{
            String username = enterUsername.getText();
            String password = enterPassword.getText();

            if (username.isBlank() || password.isBlank()){
                message.setText("Enter a username and password to Login.");
                return;
            }

            String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

            try{
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://127.0.0.1:8000/login")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
                
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if(response.statusCode() == 200){
                    message.setText("You have been logged in");
                    message.setStyle("-fx-text-fill: green;");

                    JsonObject user = JsonParser.parseString(response.body()).getAsJsonObject();
                    int userID = user.get("user_id").getAsInt();
                    String restaurantID = user.get("restaurant_id").getAsString();
                    String usernameT = user.get("username").getAsString();
                    String position = user.get("position").getAsString();

                    loginInfo infoLogin = new loginInfo(userID, restaurantID, usernameT, position);

                    hubScreen fPage = new hubScreen();
                    fPage.show(primaryStage, infoLogin);

                }else if (response.statusCode() == 401){
                    message.setText("Please enter a valid username or password");
                    message.setStyle("-fx-text-fill: red;");
                }else{
                    message.setText("An error has occured with your login");
                }
            }catch (Exception error){
                message.setText("Can't connect to the API");
                error.printStackTrace();
            }
        });

        Button btnClose = new Button("Close System");
        btnClose.setOnAction(e->{
            primaryStage.close();
        });

        btnClose.setPrefSize(200, 50);
        btnClose.setStyle("-fx-font-size: 25px;");
        btnLogin.setPrefSize(200, 50);
        btnLogin.setStyle("-fx-font-size: 25px;");

        enterPassword.setPrefHeight(50);
        enterPassword.setStyle("-fx-font-size: 25px;");
        enterUsername.setPrefHeight(50);
        enterUsername.setStyle("-fx-font-size: 25px;");

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(20));

        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER_LEFT);
        hb.getChildren().addAll(title);

        HBox tr = new HBox();
        tr.setAlignment(Pos.CENTER_LEFT);
        tr.setSpacing(10);

        Region s = new Region();
        HBox.setHgrow(s, Priority.ALWAYS);

        tr.getChildren().addAll(title, s, btnClose);

        bp.setTop(tr);

        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);
        vb.setMaxWidth(300);
        vb.getChildren().addAll(loginTitle , enterUsername, enterPassword, btnLogin, message);

        bp.setCenter(vb);

        Scene scene = new Scene(bp, 1000, 600);
        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
