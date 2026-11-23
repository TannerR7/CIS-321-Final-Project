import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class hubScreen{
    public void show (Stage primaryStage, loginInfo info){
        Label title = new Label("Demand Forecast Hub");

        Button btnForcast = new Button("Generate Forecast");
        Button btnViewForecasts = new Button("View Previous Forecasts");
        Button btnViewData = new Button("View Data");
        Button btnInputVisitors = new Button("Input Last Visitors");
        
        btnForcast.setOnAction(e ->{
            forecastScreen fs = new forecastScreen();
            fs.show(primaryStage, info);
        });

        btnViewForecasts.setOnAction(e ->{
            previousForecasts pf = new previousForecasts();
            pf.show(primaryStage, info);
        });

        btnViewData.setOnAction(e ->{
            allData ad = new allData();
            ad.show(primaryStage, info);
        });

        btnInputVisitors.setOnAction(e ->{
            inputVisitors iv = new inputVisitors();
            iv.show(primaryStage, info);
        });

        Button btnClose = new Button("Close System");
        btnClose.setOnAction(e->{
            System.exit(0);
        });

        VBox screenLayout = new VBox(15);
        screenLayout.setAlignment(Pos.CENTER);
        screenLayout.setPadding(new Insets(30));
        screenLayout.getChildren().addAll(title, btnForcast, btnViewForecasts, btnViewData, btnInputVisitors, btnClose);

        Scene scene = new Scene(screenLayout, 500, 300);
        primaryStage.setTitle("Hub Screen");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}
