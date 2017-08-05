package com.zezetek.fxmap.io

import java.net.URL

import org.json.{JSONArray, JSONObject}

import scala.io.Source

/**
  * Created by dingb on 2017/4/12.
  */
object Web {
  def getJSON(url: String): JSONObject = {
    val src = Source.fromURL(new URL(url), "utf-8")
    val code = src.getLines().toList.mkString("\n");
    println(code)
    val jo = new JSONObject(code)
    src.close()
    jo
  }
  def getNothing(url: String): Unit = {
    println(url)
    val src = Source.fromURL(new URL(url), "utf-8")
    val code = src.getLines().toList.mkString("\n");
    println(code)
    src.close()
  }


  def getJSONArray(url: String): JSONArray = {
    val src = Source.fromURL(new URL(url), "utf-8")
    val code = src.getLines().toList.mkString("\n");
    println(code)
    val jo = new JSONArray(code)
    src.close()
    jo
  }

}
