package com.zezetek.fxmap

import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.{AnchorPane, BorderPane}
import javafx.stage.{Stage, WindowEvent}

/**
  * Created by dingb on 2017/1/20.
  */
object Start extends App {

  Application.launch(classOf[Start])
}


class Start extends Application {
  override def start(primaryStage: Stage): Unit = {
    val fxmlLoader = new FXMLLoader();

    val root: AnchorPane = fxmlLoader.load(getClass.getResourceAsStream("MainWindow.fxml"))
    val mainWindowController = fxmlLoader.getController.asInstanceOf[MainWindowController]
    mainWindowController.init


    mainWindowController.canvasMap.widthProperty().bind(mainWindowController.anchorPaneCanvas.widthProperty())
    mainWindowController.canvasMap.heightProperty().bind(mainWindowController.anchorPaneCanvas.heightProperty())

    val scene = new Scene(root, 1024, 768);
    primaryStage.setTitle("厘米级室内定位系统 - 地图编辑器")
    primaryStage.setScene(scene)
    primaryStage.show()
    primaryStage.setOnCloseRequest(new EventHandler[WindowEvent] {
      override def handle(event: WindowEvent): Unit = System.exit(0)
    })
  }
}
