package com.zezetek.fxmap.entity

import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty, SimpleStringProperty}


/**
  * Created by dingb on 2017/1/27.
  */
class BssInfo(_id: Int, _x: Int, _y: Int, _z: Int, _da0: Int) {
  var id = new SimpleIntegerProperty(_id)
  var x = new SimpleDoubleProperty(_x) //mm
  var y = new SimpleDoubleProperty(_y) //mm
  var z = new SimpleDoubleProperty(_z) //mm
  val da0 = new SimpleDoubleProperty(_da0) //mm
  val status = new SimpleStringProperty("N/A")
  val sprite = new Sprite("bss32", 4, 250)

  def getId = id.get
  def getX = x.get
  def getY = y.get
  def getZ = z.get
  def getDa0 = da0.get()
  def getStatus = status.get()

  def setId(v: Int) = id.set(v)
  def setX(v: Double) = x.set(v)
  def setY(v: Double) = y.set(v)
  def setZ(v: Double) = z.set(v)
  def setDa0(v: Double) = da0.set(v)
  def setStatus(v: String) = status.set(v)

  def updateXYZ(_x: Double, _y: Double, _z: Double): Unit = {
    setX(_x)
    setY(_y)
    setZ(_z)
  }

  def locationXY = new Location(x.get, y.get)

}
