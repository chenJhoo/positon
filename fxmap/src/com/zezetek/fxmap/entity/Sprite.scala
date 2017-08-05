package com.zezetek.fxmap.entity

import javafx.scene.image.Image

import com.zezetek.fxmap.Res

/**
  * Created by dingb on 2017/2/2.
  */
class Sprite(val name:String, val frames: Int, val interval: Long){
  var currentFrame = 0
  var passed = 0L
  val images = for(i <- 0 until frames) yield new Image(classOf[Res].getResourceAsStream("%s_%d.png".format(name,i)))
  def update(now: Long) = {
    if(now - passed > (interval * 1000L * 1000L)) {
      passed = now
      currentFrame +=1
    }
  }
  def image = images(currentFrame % frames)
}
