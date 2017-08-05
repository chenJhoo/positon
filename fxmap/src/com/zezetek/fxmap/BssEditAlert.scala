package com.zezetek.fxmap

import java.awt.JobAttributes.DialogType
import java.io.FileNotFoundException
import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.geometry.Insets
import javafx.scene.control._
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.image.ImageView
import javafx.scene.layout.{GridPane, Priority}
import javafx.util.Callback

import Util._
import com.zezetek.fxmap.entity.BssInfo

/**
  * Created by dingb on 2017/1/30.
  */
class BssEditAlert(val bss: BssInfo) {
  def show(changed: (BssInfo, Int, Int, Int) => Unit): Unit = {
    // Create the custom dialog.
    val dialog = new Dialog[String]();
    dialog.setTitle("编辑基站 " + bss.id.get);
    dialog.setHeaderText("基站信息");

    // Set the icon (must be included in the project).
    dialog.setGraphic(new ImageView(this.getClass().getResource("move.png").toString()));

    // Set the button types.
    val saveButtonType = new ButtonType("保存", ButtonData.OK_DONE)
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    // Create the username and password labels and fields.
    val grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));
    val xField = new TextField();
    xField.setPromptText("输入横坐标");
    val yField = new TextField();
    yField.setPromptText("输入纵坐标");
    val zField = new TextField();
    zField.setPromptText("输入垂直坐标");
    grid.add(new Label("X坐标(单位 MM):"), 0, 0);
    grid.add(xField, 1, 0);
    grid.add(new Label("Y坐标(单位 MM):"), 0, 1);
    grid.add(yField, 1, 1);
    grid.add(new Label("Z坐标(单位 MM):"), 0, 2);
    grid.add(zField, 1, 2);
    // Enable/Disable login button depending on whether a username was entered.
    val saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
    // Do some validation (using the Java 8 lambda syntax).
    dialog.getDialogPane().setContent(grid);
    // Request focus on the username field by default.
    runLater{xField.requestFocus()}
    // Convert the result to a username-password-pair when the login button is clicked.
    dialog.setResultConverter(new Callback[ButtonType, String] {
      override def call(dialogButton: ButtonType): String = {
        if (dialogButton == saveButtonType) {
          if(xField.getText.isInt && yField.getText.isInt && zField.getText.isInt) {
            xField.getText.tryToInt {
              x => yField.getText.tryToInt {
                y => zField.getText.tryToInt {
                  z => changed(bss, x, y, z)
                }
              }
            }
          } else {
            val a = new Alert(AlertType.ERROR);
            a.setContentText("所有字段必须填写");
            a.show();
          }
          return null;
        } else {
          return null;
        }
      }
    });
    dialog.showAndWait();
  }
}
