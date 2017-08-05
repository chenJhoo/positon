package com.zezetek.fxmap.entity

import java.lang.Math._

/**
  * Created by dingb on 2017/1/31.
  */
class Location(var x: Double, var y: Double) {
  def distance(o: Location): Double = sqrt( pow(x - o.x, 2) + pow(y - o.y, 2) )
  override def toString = "(%.3f, %.3f)".format(x, y)
}