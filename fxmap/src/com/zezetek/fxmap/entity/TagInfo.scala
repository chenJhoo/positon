package com.zezetek.fxmap.entity

import javafx.beans.property.{SimpleDoubleProperty, SimpleIntegerProperty}

/**
  * Created by dingb on 2017/1/23.
  */
class TagInfo(_id: Int, _da0: Double, _da1: Double, _da2: Double, _da3: Double, _x: Double, _y: Double, _z: Double) {
  var id = new SimpleIntegerProperty(_id)
  var da0 = new SimpleDoubleProperty(_da0)
  var da1 = new SimpleDoubleProperty(_da1)
  var da2 = new SimpleDoubleProperty(_da2)
  var da3 = new SimpleDoubleProperty(_da3)
  var x = new SimpleDoubleProperty(_x)
  var y = new SimpleDoubleProperty(_y)
  var z = new SimpleDoubleProperty(_z)

  def getId = id.get
  def getDa0 = da0.get
  def getDa1 = da1.get
  def getDa2 = da2.get
  def getDa3 = da3.get
  def getX = x.get
  def getY = y.get
  def getZ = z.get

  def setId(v: Int) = id.set(v)
  def setX(v: Double) = x.set(v)
  def setY(v: Double) = y.set(v)
  def setZ(v: Double) = z.set(v)
  def setDa0(v: Double) = da0.set(v)
  def setDa1(v: Double) = da1.set(v)
  def setDa2(v: Double) = da2.set(v)
  def setDa3(v: Double) = da3.set(v)


  def update(_da0: Double, _da1: Double, _da2: Double, _da3: Double, _x: Double, _y: Double, _z: Double): Unit = {
    setDa0(_da0)
    setDa1(_da1)
    setDa2(_da2)
    setDa3(_da3)
    setX(_x)
    setY(_y)
    setZ(_z)

  }


}
