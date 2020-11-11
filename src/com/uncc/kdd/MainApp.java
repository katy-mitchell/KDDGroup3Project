package com.uncc.kdd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application 
{
    @Override
    public void start(Stage pStage) throws Exception
    {
        Parent head = FXMLLoader.load(getClass().getResource("actionrule.fxml"));
        pStage.setTitle("Extractor: Action Rules");
        pStage.setScene(new Scene(head, 680, 600));
        pStage.show();
    }

    public static void main(String[] args) 
    {
        launch(args);
    }
}
