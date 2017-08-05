package com.zezetek.fxmap

import java.io.{ByteArrayOutputStream, File, FileInputStream, PrintStream}
import javafx.animation.AnimationTimer
import javafx.application.Platform
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.input.{MouseButton, MouseEvent, ScrollEvent}
import javafx.scene.paint.Paint

import com.zezetek.fxmap.entity.Location
import org.json.JSONObject
import sun.misc.BASE64Encoder

import scala.io.Source
import scala.swing.Color



/**
  * Created by dingb on 2017/1/24.
  */
object Util {

  implicit def toStringHelper(s: String) = new StringHelper(s)

  class StringHelper(s: String) {
    def hex = java.lang.Long.parseLong(s, 16).asInstanceOf[Int]

    def tryToDouble(f: Double => Unit) = try {
      val v = s.toDouble
      f(v)
    } catch {
      case ex: Throwable =>
    }

    def tryToLong(f: Long => Unit) = try {
      val v = s.toLong
      f(v)
    } catch {
      case ex: Throwable =>
    }

    def tryToInt(f: Int => Unit) = try {
      val v = s.toInt
      f(v)
    } catch {
      case ex: Throwable => ex.printStackTrace()
    }

    def isInt = try {
      s.toInt
      true
    } catch {
      case ex: Throwable => false
    }

    def isDouble = try {
      s.toDouble
      true
    } catch {
      case ex: Throwable => false
    }

    def isFloat = try {
      s.toFloat
      true
    } catch {
      case ex: Throwable => false
    }

    def isLong = try {
      s.toLong
      true
    } catch {
      case ex: Throwable => false
    }


  }

  def runLater(func: => Unit): Unit = {
    Platform.runLater(new Runnable {
      override def run(): Unit = func
    })
  }


  implicit def toNodeHelper(node: Node) = new NodeHelper(node)

  class NodeHelper(node: Node) {
    def mousePressed(f: MouseEvent => Unit): Unit = {
      node.setOnMousePressed(new EventHandler[MouseEvent] {
        override def handle(event: MouseEvent): Unit = f(event)
      })
    }

    def mouseReleased(f: MouseEvent => Unit): Unit = {
      node.setOnMouseReleased(new EventHandler[MouseEvent] {
        override def handle(event: MouseEvent): Unit = f(event)
      })
    }

    def mouseMoved(f: MouseEvent => Unit): Unit = {
      node.setOnMouseMoved(new EventHandler[MouseEvent] {
        override def handle(event: MouseEvent): Unit = f(event)
      })
    }


    def mouseDragged(f: MouseEvent => Unit): Unit = {
      node.setOnMouseDragged(new EventHandler[MouseEvent] {
        override def handle(event: MouseEvent): Unit = f(event)
      })
    }


    def mouseClicked(f: MouseEvent => Unit): Unit = {
      node.setOnMouseClicked(new EventHandler[MouseEvent] {
        override def handle(event: MouseEvent): Unit = f(event)
      })
    }


    def onScroll(f: ScrollEvent => Unit): Unit = {
      node.setOnScroll(new EventHandler[ScrollEvent] {
        override def handle(event: ScrollEvent): Unit = f(event)
      })
    }




  }


  implicit def toTextFieldHelper(node: TextField) = new TextFieldHelper(node)

  class TextFieldHelper(node: TextField) {
    def textChanged(f: (ObservableValue[_ <: String], String, String) => Unit): Unit = {
      node.textProperty().addListener(new ChangeListener[String] {
        override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = f(observable, oldValue, newValue)
      })
    }
  }

  implicit def toImageHelper(node: Image) = new ImageHelper(node)

  class ImageHelper(node: Image) {
    def ratioXY = node.getWidth / node.getHeight

    def ratioYX = node.getHeight / node.getWidth
  }


  implicit def toGraphicsContextHelper(node: GraphicsContext) = new GraphicsContextHelper(node)

  class GraphicsContextHelper(g: GraphicsContext) {
    def fillTextCenter(s: String) = g.fillText(s, g.getCanvas.getWidth / 2 - stringWidth(g, s) / 2, g.getCanvas.getHeight / 2 - stringHeight(g, s) / 2)
    def fillTextCenter(s: String, x: Double, y: Double, w: Double, h: Double) = g.fillText(s, x + w / 2 - stringWidth(g, s) / 2, y + h / 2 - stringHeight(g, s) / 2)
    def clean = g.clearRect(0,0,g.getCanvas.getWidth, g.getCanvas.getHeight)
    def use(f: => Unit): Unit = {g.save();f;g.restore();}
    def drawCross3(x: Double, y: Double, length: Double, pCenter: Paint, pBorder: Paint)  = {


      g.setStroke(pBorder)
      g.strokeLine(x - length/2, y- g.getLineWidth, x + length/2, y- g.getLineWidth)
      g.strokeLine(x-g.getLineWidth, y - length/2, x - g.getLineWidth, y + length/2)

      g.setStroke(pBorder)
      g.strokeLine(x - length/2, y + g.getLineWidth, x + length/2, y+g.getLineWidth)
      g.strokeLine(x+g.getLineWidth, y - length/2, x+g.getLineWidth, y + length/2)

      g.setStroke(pCenter)
      g.strokeLine(x - length/2 - g.getLineWidth, y, x + length/2 + g.getLineWidth, y)
      g.strokeLine(x, y - length/2 - g.getLineWidth, x, y + length/2 + g.getLineWidth)
    }


  }


  implicit def toFileHelper(node: File) = new FileHelper(node)

  class FileHelper(node: File) {
    def base64String = {
      val enc = new BASE64Encoder()
      val fis = new FileInputStream(node)
      val baos = new ByteArrayOutputStream()
      enc.encode(fis, baos)
      fis.close
      new String(baos.toByteArray)
    }

    def save(content: String): Unit = {
      val ps = new PrintStream(node)
      ps.print(content)
      ps.close
    }

    def save(o: JSONObject): Unit = save(o.toString)

    def asJson = {
      val src = Source.fromFile(node)
      val jo = new JSONObject(src.getLines().toList.mkString("\n"))
      src.close()
      jo
    }


  }


  def min[T](xs: List[T], count: Int, lt: (T, T) => Boolean): List[T] = xs.sortWith(lt).take(count)
  def min3[T](xs: List[T], lt: (T, T) => Boolean): List[T] = min(xs, 3, lt)
  def stringWidth(gc: GraphicsContext, s: String) = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(s, gc.getFont())
  def stringHeight(gc: GraphicsContext, s: String) = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().getFontMetrics(gc.getFont()).getLineHeight()
  def timerStart(f: Long => Unit): AnimationTimer = {
    val t = new AnimationTimer() {
      override def handle(now: Long): Unit = {
        f(now);
      }
    }
    t.start()
    t
  }
}


