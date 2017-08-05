package com.zezetek.fxmap

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import java.util.Base64
import javafx.animation.AnimationTimer
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections
import javafx.embed.swing.SwingFXUtils
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.FXML
import javafx.scene.ImageCursor
import javafx.scene.canvas.Canvas
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.{PropertyValueFactory, TextFieldTableCell}
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input._
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.util.Callback
import javax.imageio.ImageIO

import com.fazecast.jSerialComm.SerialPort
import gnu.io.{CommPort, CommPortIdentifier, RXTXCommDriver}

import scala.collection.JavaConversions._
import Util._
import com.zezetek.fxmap.core.LocationCalc
import com.zezetek.fxmap.entity.{BssInfo, MapInfo, NoMapInfo, TagInfo}
import com.zezetek.fxmap.io.CommReader
import org.json.JSONObject
import sun.misc.{BASE64Decoder, BASE64Encoder}

import scala.io.Source


/**
  * Created by dingb on 2017/1/20.
  */
class MainWindowController {
  @FXML var mainPane: AnchorPane = null
  @FXML var canvasMap: Canvas = null
  @FXML var anchorPaneCanvas: AnchorPane = null
  @FXML var comboBoxComs: ComboBox[String] = null
  @FXML var tableViewTags: TableView[TagInfo] = null
  @FXML var tableViewBss: TableView[BssInfo] = null
  @FXML var tcTagId: TableColumn[TagInfo, Int] = null
  @FXML var tcTagX: TableColumn[TagInfo, Int] = null
  @FXML var tcTagY: TableColumn[TagInfo, Int] = null
  @FXML var tcTagZ: TableColumn[TagInfo, Int] = null
  @FXML var tcTagDa0: TableColumn[TagInfo, Int] = null
  @FXML var tcTagDa1: TableColumn[TagInfo, Int] = null
  @FXML var tcTagDa2: TableColumn[TagInfo, Int] = null
  @FXML var tcTagDa3: TableColumn[TagInfo, Int] = null
  @FXML var tcBssId: TableColumn[BssInfo, Int] = null
  @FXML var tcBssX: TableColumn[BssInfo, Int] = null
  @FXML var tcBssY: TableColumn[BssInfo, Int] = null
  @FXML var tcBssZ: TableColumn[BssInfo, Int] = null
  @FXML var tcBssDa0: TableColumn[BssInfo, Int] = null
  @FXML var tcBssStatus: TableColumn[BssInfo, String] = null
  @FXML var buttonSetOrig: Button = null
  @FXML var labelStatus: Label = null
  @FXML var buttonOpen: Button = null
  @FXML var buttonImage: Button = null
  @FXML var buttonSave: Button = null
  @FXML var buttonMove: Button = null
  @FXML var buttonMesure: Button = null
  @FXML var buttonZoomIn: Button = null
  @FXML var buttonZoomOut: Button = null
  @FXML var buttonZoomNormal: Button = null
  @FXML var toggleButtonGrid: ToggleButton = null
  @FXML var toggleButtonRange: ToggleButton = null
  @FXML var buttonConnect: Button = null
  @FXML var textFieldXLength: TextField = null
  @FXML var textFieldYLength: TextField = null
  @FXML var textFieldWorldRatio: TextField = null
  @FXML var textFieldOrigX: TextField = null
  @FXML var textFieldOrigY: TextField = null
  @FXML var textFieldOrigZ: TextField = null


  var zoomTimes = 0

  def zoom = Math.pow(1.1, zoomTimes)

  def canvasWidth = canvasMap.getWidth

  def canvasHeight = canvasMap.getHeight

  var dragStartX = 0.0
  var dragStartY = 0.0
  var translateX = 0.0
  var translateY = 0.0
  var mapOffsetX = 0.0
  var mapOffsetY = 0.0
  val regex_mc = "mc (.*) (.*) (.*) (.*) (.*) (.*) (.*) (.*) a(.*):(.*)".r
  val regex_ma = "ma (.*) (.*) (.*) (.*) (.*) (.*) (.*) (.*) a(.*):(.*)".r

  val fontOrig = new Font(Font.getDefault.getName, 24)
  val res = new Res

  val MODE_NORMAL = 0
  val MODE_SET_ORIG = 1
  var mode = MODE_NORMAL

  var mapPngFile: File = null
  var mapFile: File = null
  val tagInfos = FXCollections.observableArrayList[TagInfo]()


  var map: MapInfo = new MapInfo
  val noMapInfo = new NoMapInfo

  var mouseX = 0.0
  var mouseY = 0.0

  def init = {
    buttonOpen.setGraphic(new ImageView(res.open_png))
    buttonImage.setGraphic(new ImageView(res.image_png))
    buttonSave.setGraphic(new ImageView(res.save_png))
    buttonSetOrig.setGraphic(new ImageView(res.set_orig))
    buttonMove.setGraphic(new ImageView(res.move32_png))
    buttonMesure.setGraphic(new ImageView(res.mesure_png))
    buttonZoomIn.setGraphic(new ImageView(res.zoomin_png))
    buttonZoomOut.setGraphic(new ImageView(res.zoomout_png))
    buttonZoomNormal.setGraphic(new ImageView(res.zoomnormal_png))
    toggleButtonGrid.setGraphic(new ImageView(res.grid_png))
    toggleButtonRange.setGraphic(new ImageView(res.range_png))
    buttonConnect.setGraphic(new ImageView(res.connect_png))
    textFieldYLength.setEditable(false)
    textFieldWorldRatio.setEditable(false)
    buttonZoomIn.mouseClicked(e => zoomTimes += 1)
    buttonZoomOut.mouseClicked(e => zoomTimes -= 1)
    buttonZoomNormal.mouseClicked(e => zoomTimes = 0)
    textFieldXLength.textChanged((o, oldv, newv) => newv.tryToDouble(v => {
      map.changeXLength(v)
      textFieldYLength.setText(map.yLength.toLong.toString)
      textFieldWorldRatio.setText(map.worldRatio.toString)
    }))


    textFieldOrigX.setText("0")
    textFieldOrigY.setText("0")
    textFieldOrigZ.setText("0")
    textFieldOrigX.textChanged((o, oldv, newv) => newv.tryToDouble(map.origX_px = _))
    textFieldOrigY.textChanged((o, oldv, newv) => newv.tryToDouble(map.origY_px = _))
    textFieldOrigZ.textChanged((o, oldv, newv) => newv.tryToDouble(map.origZ_px = _))


    def initTagCol(col: TableColumn[TagInfo, Int], fld: String) = col.setCellValueFactory(new PropertyValueFactory[TagInfo, Int](fld))
    def initBssCol(col: TableColumn[BssInfo, Int], fld: String) = col.setCellValueFactory(new PropertyValueFactory[BssInfo, Int](fld))
    initTagCol(tcTagId, "id")
    initTagCol(tcTagX, "x")
    initTagCol(tcTagY, "y")
    initTagCol(tcTagZ, "z")
    initTagCol(tcTagDa0, "da0")
    initTagCol(tcTagDa1, "da1")
    initTagCol(tcTagDa2, "da2")
    initTagCol(tcTagDa3, "da3")
    initBssCol(tcBssId, "id")
    initBssCol(tcBssX, "x")
    initBssCol(tcBssY, "y")
    initBssCol(tcBssZ, "z")
    initBssCol(tcBssDa0, "da0")
    tcBssStatus.setCellValueFactory(new PropertyValueFactory[BssInfo, String]("status"))


    map.bss.add(new BssInfo(0, 0, 0, 0, 0))
    map.bss.add(new BssInfo(1, 0, 0, 0, 0))
    map.bss.add(new BssInfo(2, 0, 0, 0, 0))
    map.bss.add(new BssInfo(3, 0, 0, 0, 0))

    tableViewTags.setItems(tagInfos)
    tableViewBss.setItems(map.bss)
    tableViewBss.setRowFactory(new Callback[TableView[BssInfo], TableRow[BssInfo]]() {
      override def call(param: TableView[BssInfo]): TableRow[BssInfo] = {
        val row = new TableRow[BssInfo]
        row.mouseClicked {
          evt => if (evt.getClickCount == 2 && !row.isEmpty && row.getItem != null) {
            new BssEditAlert(row.getItem).show {
              (bss, x, y, z) =>
                println(bss)
                bss.updateXYZ(x, y, z)
                println(x, y, z)
            }
          }
        }
        row
      }
    })
    println(SerialPort.getCommPorts)
    SerialPort.getCommPorts.foreach {
      c => comboBoxComs.getItems.add(c.getSystemPortName)
    }



    val g = canvasMap.getGraphicsContext2D
    anchorPaneCanvas.onScroll(e => if (e.getTextDeltaY > 0) zoomTimes += 1 else zoomTimes -= 1)

    canvasMap.mouseDragged {
      event =>
        if (event.getButton == MouseButton.PRIMARY) {
          translateX = event.getX - dragStartX
          translateY = event.getY - dragStartY
        }
    }


    canvasMap.mousePressed {
      event =>
        if (mode == MODE_SET_ORIG) {
          changeMode(MODE_NORMAL)
          map.origX_px = (-(mapOffsetX + translateX) / zoom + event.getX / zoom)
          map.origY_px = (-(mapOffsetY + translateY) / zoom + event.getY / zoom)
          textFieldOrigX.setText(map.origX_px.toString)
          textFieldOrigY.setText(map.origY_px.toString)
        } else {
          if (event.getButton == MouseButton.PRIMARY) {
            dragStartX = event.getX
            dragStartY = event.getY
          }
        }
    }



    canvasMap.mouseReleased {
      event => if (event.getButton == MouseButton.PRIMARY) {
        mapOffsetX = mapOffsetX + translateX
        mapOffsetY = mapOffsetY + translateY
        translateX = 0.0
        translateY = 0.0
      }
    }

    canvasMap.mouseMoved {
      event =>
        mouseX = event.getX
        mouseY = event.getY
        changeStatus("X: %f Y: %f".format(event.getX, event.getY))
    }
    timerStart {
      now =>
        //updates
        map.bss.foreach(_.sprite.update(now))
        //draws...
        g.clean
        g.use {
          g.translate(mapOffsetX + translateX, mapOffsetY + translateY)
          g.scale(zoom, zoom)
          g.drawImage(map.mapImage, 0, 0)
        }


        g.use {
          g.translate(mapOffsetX + translateX, mapOffsetY + translateY)
          g.scale(zoom, zoom)
          //paint tags
          tagInfos.foreach {
            t =>
              textFieldXLength.getText.tryToDouble {
                d =>
                  val ratio = map.worldRatio
                  if (ratio != 0) {
                    val trig = LocationCalc.trig(map.getBss(0).locationXY, map.getBss(1).locationXY, map.getBss(2).locationXY, map.getBss(3).locationXY, t.getDa0, t.getDa1, t.getDa2, t.getDa3)
                    val x = (trig(0).x / ratio + trig(1).x / ratio + trig(2).x / ratio) / 3f + map.origX_px
                    val y = (trig(0).y / ratio + trig(1).y / ratio + trig(2).y / ratio) / 3f + map.origY_px


                    g.setFill(new Color(1.0, 0, 0, 0.4))
                    g.fillOval(x - 50 / ratio, y - 50 / ratio, 100 / ratio, 100 / ratio)

                    g.setFill(new Color(0, 0, 0.6, 0.3))
                    g.fillPolygon(trig.map(_.x / ratio + map.origX_px).toArray, trig.map(_.y / ratio + map.origY_px).toArray, 3)

                  }
              }
          }
        }
        // paint orig centrer
        g.use {
          g.translate(mapOffsetX + translateX, mapOffsetY + translateY)
          g.setLineWidth(1)
          g.drawCross3((map.origX_px) * zoom, (map.origY_px) * zoom, 16, Color.BLACK, Color.WHITE);
        }
        //draw grid
        if (toggleButtonGrid.isSelected) {
          g.save()
          g.setStroke(Color.GRAY)
          for (x <- 0 until canvasWidth.asInstanceOf[Int] by 32) {
            g.strokeLine(x, 0, x, canvasHeight)
          }
          for (y <- 0 until canvasHeight.asInstanceOf[Int] by 32) {
            g.strokeLine(0, y, canvasWidth, y)
          }
          g.restore()
        }


        if (mode == MODE_SET_ORIG) {
          g.drawCross3(mouseX, mouseY, 16, Color.BLACK, Color.WHITE);
          g.save()
          val c = new Color(0, 0, 1, 0.3)
          g.setFill(c)
          g.fillRect(0, 0, canvasWidth, canvasHeight)
          g.setFill(Color.BLACK)
          g.setFont(fontOrig)
          g.fillText("滚轮缩放, 左键移动, 中间按键设置圆点, 右键取消", 0, fontOrig.getSize)

          g.restore()

        }


        //paint bss

        if (map.mapImage != null) {
          g.use {
            g.translate(mapOffsetX + translateX, mapOffsetY + translateY)
            map.bss.foreach {
              b =>
                val ratio = map.worldRatio
                if (ratio != 0) {
                  g.drawImage(b.sprite.image, ((map.origX_px * ratio + b.x.get) / ratio) * zoom - b.sprite.image.getWidth / 2, ((map.origY_px * ratio + b.y.get) / ratio) * zoom - b.sprite.image.getHeight / 2)
                }
            }
          }
        }


        //paint circle around bss
        if (map.mapImage != null) {
          g.use {
            g.translate(mapOffsetX + translateX, mapOffsetY + translateY)
            g.scale(zoom, zoom)

            def drawCircle(id: Int, r_mm: Double) = {
              map.bss.find(_.id.get == id) match {
                case Some(b) =>
                  if (map.worldRatio != 0) {
                    val r = r_mm / map.worldRatio
                    g.setFill(new Color(0, 1, 0, 0.2))
                    g.fillOval(
                      ((map.origX_px * map.worldRatio + b.x.get) / map.worldRatio) - r,
                      ((map.origY_px * map.worldRatio + b.y.get) / map.worldRatio) - r,
                      r * 2,
                      r * 2
                    )
                    g.setStroke(new Color(0, 1, 0, 0.6))
                    g.strokeOval(
                      ((map.origX_px * map.worldRatio + b.x.get) / map.worldRatio) - r,
                      ((map.origY_px * map.worldRatio + b.y.get) / map.worldRatio) - r,
                      r * 2,
                      r * 2
                    )
                  }
                case None =>
              }
            }
            tagInfos.foreach {
              t =>
                drawCircle(0, t.da0.get)
                drawCircle(1, t.da1.get)
                drawCircle(2, t.da2.get)
                drawCircle(3, t.da3.get)
            }
          }
        }
        noMapInfo.visiable = (map.mapImage == null)

    }

  }


  @FXML def onButtonConnectAction: Unit = {
    if(comboBoxComs.getSelectionModel.getSelectedItem == null) {
      val at = new Alert(AlertType.ERROR);
      at.setContentText("没有选择端口")
      at.show()
      return
    }
    val com = SerialPort.getCommPort(comboBoxComs.getSelectionModel.getSelectedItem)
    com.openPort()
    com.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);

    val reader = new CommReader(com)
    reader.beginRead {
      line =>
        println(line)
        val aa_times = 1.0;
        val at_times = 1.0;
        line match {
          case regex_mc(mask, da0, da1, da2, da3, nrangs, rseq, debug, tid, aid) =>
            tagInfos.find(_.id.get() == tid.hex) match {
              case None =>
                val trig = LocationCalc.trig(map.getBss(0).locationXY, map.getBss(1).locationXY, map.getBss(2).locationXY, map.getBss(3).locationXY, da0.hex / at_times, da1.hex / at_times, da2.hex / at_times, da3.hex / at_times)
                val x = (trig(0).x + trig(1).x + trig(2).x) / 3f
                val y = (trig(0).y + trig(1).y + trig(2).y) / 3f
                val z = 0.0

                val t = new TagInfo(tid.hex, x.asInstanceOf[Int], y.asInstanceOf[Int], z.asInstanceOf[Int], da0.hex / at_times, da1.hex / at_times, da2.hex / at_times, da3.hex / at_times)
                tagInfos.add(t)
                runLater(tableViewTags.refresh())
              case Some(t) =>
                if (da0.hex != 0 && da1.hex != 0 && da2.hex != 0 && da3.hex != 0) {
                  val trig = LocationCalc.trig(map.getBss(0).locationXY, map.getBss(1).locationXY, map.getBss(2).locationXY, map.getBss(3).locationXY, t.getDa0, t.getDa1, t.getDa2, t.getDa3)
                  val x = (trig(0).x + trig(1).x + trig(2).x) / 3f
                  val y = (trig(0).y + trig(1).y + trig(2).y) / 3f
                  val z = 0.0

                  t.update(da0.hex / 2, da1.hex / 2, da2.hex / 2, da3.hex / 2, x.asInstanceOf[Int], y.asInstanceOf[Int], z.asInstanceOf[Int])
                  runLater(tableViewTags.refresh())
                }
            }
          case regex_ma(mask, da0, da1, da2, da3, nrangs, rseq, debug, tid, aid) =>
            map.getBss(0).da0.set(0)
            map.getBss(1).da0.set(da1.hex / aa_times)
            map.getBss(2).da0.set(da2.hex / aa_times)
            map.getBss(3).da0.set(da3.hex / aa_times)
            runLater(tableViewBss.refresh())
          case _ =>
        }
    }
  }


  @FXML def handleButtonSetOrigAction: Unit = {
    changeMode(MODE_SET_ORIG)
  }

  @FXML def handleButtonOpenAction: Unit = {
    val fc = new FileChooser
    fc.setTitle("打开地图")
    fc.getExtensionFilters.add(new ExtensionFilter("MAP File (*.map)", "*.map"))
    mapFile = fc.showOpenDialog(null)
    if (mapFile != null) {
      map.fromFile(mapFile)
      mapOffsetX = 0
      mapOffsetY = 0
      translateX = 0
      translateY = 0
      dragStartX = 0
      dragStartY = 0
      zoomTimes = 0
      mode = MODE_NORMAL
      textFieldXLength.setText(map.xLength.toString)
      textFieldYLength.setText(map.yLength.toString)
      textFieldOrigX.setText(map.origX_px.toString)
      textFieldOrigY.setText(map.origY_px.toString)
      textFieldOrigZ.setText(map.origZ_px.toString)


    }
  }

  @FXML def handleButtonSaveAction: Unit = {
    if (map.mapImage == null) {
      val a = new Alert(AlertType.WARNING)
      a.setContentText("没有加载地图图片")
      a.show()
      return
    }
    if (!textFieldXLength.getText.isLong) {
      val a = new Alert(AlertType.WARNING)
      a.setContentText("地图长度没有设置")
      a.show()
      return
    }
    if (!textFieldOrigX.getText.isDouble) {
      val a = new Alert(AlertType.WARNING)
      a.setContentText("原点X坐标没有设置")
      a.show()
      return
    }
    if (!textFieldOrigY.getText.isDouble) {
      val a = new Alert(AlertType.WARNING)
      a.setContentText("原点Y坐标没有设置")
      a.show()
      return
    }
    if (!textFieldOrigZ.getText.isDouble) {
      val a = new Alert(AlertType.WARNING)
      a.setContentText("原点Z坐标没有设置")
      a.show()
      return
    }

    val mapPngBaos = new ByteArrayOutputStream()
    ImageIO.write(SwingFXUtils.fromFXImage(map.mapImage, null), "png", mapPngBaos)

    val json = new JSONObject()
    json.put("png", new BASE64Encoder().encode(mapPngBaos.toByteArray))
    json.put("xLength", textFieldXLength.getText.toLong)
    json.put("yLength", textFieldYLength.getText.toLong)
    json.put("zLength", 0)
    json.put("origX", textFieldOrigX.getText.toDouble)
    json.put("origY", textFieldOrigY.getText.toDouble)
    json.put("origZ", textFieldOrigZ.getText.toDouble)
    def saveBss(id: Int) = map.bss.find(_.id.get == id) match {
      case Some(b) =>
        json.put("a" + id + "x", b.getX)
        json.put("a" + id + "y", b.getY)
        json.put("a" + id + "z", b.getZ)
      case _ =>
    }
    saveBss(0)
    saveBss(1)
    saveBss(2)
    saveBss(3)

    json.put("date", System.currentTimeMillis())


    val fc = new FileChooser
    fc.setTitle("保存地图")
    fc.getExtensionFilters.add(new ExtensionFilter("MAP File (*.map)", "*.map"))
    val mapFile = fc.showSaveDialog(null)
    if (mapFile != null) {
      mapFile.save(json)
    }


  }

  @FXML def handleButtonImageAction: Unit = {
    val fc = new FileChooser
    fc.setTitle("选择地图")
    fc.getExtensionFilters.add(new ExtensionFilter("PNG 图片 (*.png)", "*.png"))
    fc.getExtensionFilters.add(new ExtensionFilter("SVG 矢量图形 (*.svg)", "*.svg"))
    mapPngFile = fc.showOpenDialog(null)
    if (mapPngFile != null) {
      map.mapImage = new Image("file:/" + mapPngFile.getAbsolutePath)
      if (textFieldXLength.getText.isEmpty) textFieldXLength.setText(map.mapImage.getWidth.toString)


    }

  }


  def changeStatus(status: String) = runLater(labelStatus.setText(status))

  def changeMode(m: Int) = {
    mode = m
  }
}
