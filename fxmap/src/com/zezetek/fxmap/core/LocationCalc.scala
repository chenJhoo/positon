package com.zezetek.fxmap.core

import Math._

import com.zezetek.fxmap.MathUtil
import com.zezetek.fxmap.Util._
import com.zezetek.fxmap.entity.Location

/**
  * Created by dingb on 2017/1/31.
  */
object LocationCalc {
  private def calc(roundA: Round, roundB: Round, roundC: Round): Location = {
    val A = roundA.center
    val B = roundB.center
    val C = roundC.center
    val Ra = roundA.r
    val Rb = roundB.r
    val Rc = roundC.r

    if(roundA.sameWith(roundB)) {
      roundA.center
    } else if(roundA.include(roundB)) {
      roundB.center
    } else if(roundA.awayFrom(roundB)) {
      val Oab = new Location(A.x + ((B.x - A.x)/(Ra + Rb)) * Ra, A.y + ((B.y - A.y)/(Ra + Rb)) * Ra)
      Oab
    } else if(roundA.cutWith(roundB)) {
      val Oab = new Location(A.x + ((B.x - A.x) / (Ra + Rb)) * Ra, A.y + ((B.y - A.y) / (Ra + Rb)) * Ra)
      Oab
    }else {
      val points = MathUtil.insect(roundA, roundB)
      val Oab = if(points(0).distance(C) < points(1).distance(C)) points(0) else points(1)
      Oab

    }
  }

  def trig(a0: Location, a1: Location, a2: Location, a3: Location, da0: Double, da1: Double, da2: Double, da3: Double): List[Location] = {
    val xs = List(new Round(a0, da0), new Round(a1, da1), new Round(a2, da2), new Round(a3, da3))
    val nears = min3[Round](xs, (a, b) => a.r < b.r)
    List(
      calc(nears(0),nears(1),nears(2)),
      calc(nears(0),nears(2),nears(1)),
      calc(nears(1),nears(2),nears(0))
    )

  }
}




