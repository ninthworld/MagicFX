package sample;

import com.sun.javafx.geom.Vec2f;
import com.sun.prism.paint.Paint;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("MagicFX");
        primaryStage.setScene(new Scene(root, 1280, 800));
        primaryStage.show();

        HBox topHBox = (HBox) primaryStage.getScene().lookup("#topHBox");

        GaussianBlur gaussianBlur = new GaussianBlur();
        gaussianBlur.setRadius(1.6);

        BorderPane cardPane = new BorderPane();
        cardPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2, 2, 2, 2))));

        Image img = new Image(new File("Progenitor Mimic.full.jpg").toURI().toString());
        ImageView imgView = new ImageView();
        imgView.setImage(img);
        imgView.setFitHeight(topHBox.getHeight()*0.84f);
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        imgView.setCache(true);
        imgView.setEffect(gaussianBlur);

        cardPane.setMaxWidth(imgView.getFitWidth());
        cardPane.setMaxHeight(imgView.getFitHeight());
        cardPane.setCenter(imgView);

        topHBox.getChildren().add(cardPane);

        imgView.setOnMouseClicked(e->{
            System.out.printf("Clicked %d\n", e.getClickCount());
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
