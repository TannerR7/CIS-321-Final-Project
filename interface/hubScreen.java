import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;


public class hubScreen{
    public void show (Stage primaryStage, loginInfo info){
        Label title = new Label("Demand Forecast Hub");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

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

        btnClose.setPrefSize(200, 50);
        btnClose.setStyle("-fx-font-size: 15px;");
        btnForcast.setPrefSize(200, 50);
        btnForcast.setStyle("-fx-font-size: 15px;");
        btnInputVisitors.setPrefSize(200, 50);
        btnInputVisitors.setStyle("-fx-font-size: 15px;");
        btnViewData.setPrefSize(200, 50);
        btnViewData.setStyle("-fx-font-size: 15px;");
        btnViewForecasts.setPrefSize(200, 50);
        btnViewForecasts.setStyle("-fx-font-size: 15px;");

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(20));
        
        HBox tr = new HBox();
        tr.setAlignment(Pos.CENTER);
        tr.getChildren().add(title);
        bp.setTop(tr);

        HBox b1 = new HBox(20);
        b1.setAlignment(Pos.CENTER);
        b1.getChildren().addAll(btnForcast, btnInputVisitors);

        HBox b2 = new HBox(20);
        b2.setAlignment(Pos.CENTER);
        b2.getChildren().addAll(btnViewForecasts, btnViewData);

        VBox vb = new VBox(15);
        vb.setAlignment(Pos.CENTER);
        vb.setMaxWidth(1000);
        vb.getChildren().addAll(b1, b2);

        bp.setCenter(vb);

        HBox b3 = new HBox(20);
        b3.setAlignment(Pos.CENTER_RIGHT);
        b3.getChildren().add(btnClose);

        bp.setBottom(b3);

        Scene scene = new Scene(bp);
        primaryStage.setTitle("Hub Screen");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}
