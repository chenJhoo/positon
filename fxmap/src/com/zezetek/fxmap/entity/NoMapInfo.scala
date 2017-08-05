package com.zezetek.fxmap.entity

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font
import com.zezetek.fxmap.Util._
/**
  * Created by dingb on 2017/2/2.
  */
class NoMapInfo {
  var visiable = false
  val fontOrig = new Font(Font.getDefault.getName, 24)

  def show = visiable = true
  def hide = visiable = false

  def draw(g: GraphicsContext) = {
    if(visiable) {
      val c = new Color(1, 1, 1, 0.3)
      g.setFill(c)
      g.fillRect(0, 0, g.getCanvas.getWidth, g.getCanvas.getHeight)
      g.setFill(Color.BLACK)
      g.setFont(fontOrig)
      g.fillTextCenter("未设置地图文件")
    }
  }
}
