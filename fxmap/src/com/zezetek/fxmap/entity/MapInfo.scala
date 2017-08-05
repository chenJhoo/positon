package com.zezetek.fxmap.entity

import java.io.{ByteArrayInputStream, File}
import javafx.collections.FXCollections
import javafx.scene.image.Image

import com.zezetek.fxmap.Util
import Util._
import com.zezetek.fxmap.io.Web
import org.json.JSONObject
import sun.misc.BASE64Decoder

import scala.collection.JavaConversions._

/**
  * Created by dingb on 2017/2/2.
  */
class MapInfo {
  def changeXLength(v: Double): Unit = {
    xLength = v
    yLength = v * mapImage.ratioYX
  }

  val FILE_TYPE_SVG = 0
  val FILE_TYPE_PNG = 1

  var origX_px = 0.0//原点坐标, 单位PX
  var origY_px = 0.0//原点坐标, 单位PX
  var origZ_px = 0.0//原点坐标, 单位PX
  var xLength = 1000.0 //空间长, 单位 MM
  var yLength = 1000.0 //空间宽, 单位 M
  var zLength = 0.0 //空间高, 单位 M
  var imageType = "png"
  var bss = FXCollections.observableArrayList[BssInfo]()
  var fileType = FILE_TYPE_SVG
  var mapImage: Image = null

  def getBss(id: Int) = bss.find(_.id.get == id).get

  def worldRatio = if(mapImage==null) 1.0 else xLength/mapImage.getWidth
  object ratio {
    def xy = xLength / yLength
    def yx = 1/xy
    def xz = xLength / zLength
    def zx = 1/xz
    def yz = yLength / zLength
    def zy = 1/yz

    var real_virtual = 1.0
  }





  def fromJSON(jo: JSONObject): MapInfo = {
    origX_px = jo.getDouble("origX")
    origY_px = jo.getDouble("origY")
    origZ_px = jo.getDouble("origZ")
    xLength = jo.getDouble("xLength")
    yLength = jo.getDouble("yLength")
    zLength = jo.getDouble("zLength")

    imageType = if(jo.has("imageType")) jo.getString("imageType") else "png"

    def bssLoad(id: Int) = bss.find(_.id.get == id) match {
      case Some(b) =>
        b.setX(jo.getDouble("a" + id + "x").asInstanceOf[Int])
        b.setY(jo.getDouble("a" + id + "y").asInstanceOf[Int])
        if(b.getY == 0) b.setY(1)
        if(b.getX == 0) b.setX(1)
      case _ =>
    }
    bssLoad(0)
    bssLoad(1)
    bssLoad(2)
    bssLoad(3)
    mapImage = new Image(new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(jo.getString("image"))))
    this
  }


  def fromFile(mapFile: File): MapInfo =  fromJSON(mapFile.asJson)
  def fromUrl(url: String): MapInfo =  fromJSON( Web.getJSON(url))





}




