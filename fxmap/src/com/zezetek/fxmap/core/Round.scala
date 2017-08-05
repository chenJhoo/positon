package com.zezetek.fxmap.core

import java.lang.Math._

import com.zezetek.fxmap.MathUtil
import com.zezetek.fxmap.entity.Location

/**
  * Created by dingb on 2017/1/31.
  */
case class Round(x: Double, y: Double, r: Double) {
  def this(c: Location, r: Double) = this(c.x, c.y, r)
  def crossWith(other: Round) = {
    val res = MathUtil.insect(this, other)
    res!=null && res.length == 2
  }

  /*
  * 相切
  * */
  def cutWith(other: Round) = {
    val res = MathUtil.insect(this, other)
    res!=null && res.length == 1
  }
  def sameWith(other: Round) = {
    val res = MathUtil.insect(this, other)
    res == null
  }

  /*
  * 相离
  * sqrt() 开平方;  pow(x,y) x的y次幂;  abs() 绝对值
  * */
  def awayFrom(other: Round) = sqrt(pow(abs(x - other.x), 2) + pow(abs(y - other.y), 2)) > (r + other.r)

  /*
  * 内含
  * Boolean  true或false  参数转换成一个布尔值，并且返回一个包含该值的 Boolean 对象。
  * */
  def include(other: Round): Boolean = r > other.r && ((sqrt(pow(abs(x - other.x), 2) + pow(abs(y - other.y), 2))  + other.r) <= r)
  def center = new Location(x, y)





}
