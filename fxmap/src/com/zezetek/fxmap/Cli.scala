package com.zezetek.fxmap

import java.net.{HttpURLConnection, URL}
import javafx.collections.FXCollections

import com.fazecast.jSerialComm.SerialPort
import com.zezetek.fxmap.Util.runLater
import com.zezetek.fxmap.core.LocationCalc
import com.zezetek.fxmap.entity.{BssInfo, MapInfo, TagInfo}
import com.zezetek.fxmap.io.{CommReader, Web}
import org.json.JSONObject

import scala.io.Source
import scala.collection.JavaConversions._
import Util._

/**
  * Created by dingb on 2017/4/12.
  */
object Cli extends App {
  val regex_mc = "mc (.*) (.*) (.*) (.*) (.*) (.*) (.*) (.*) a(.*):(.*)".r
  val regex_ma = "ma (.*) (.*) (.*) (.*) (.*) (.*) (.*) (.*) a(.*):(.*)".r



  if (args.length != 4) {
    println("usage: com.zezetek.fxmap.Cli com server userid area")
  } else {
    val com = args(0)
    val server = args(1)
    val userid = args(2)
    val area = args(3)
    SerialPort.getCommPorts.find(_.getSystemPortName.toLowerCase == com.toLowerCase) match {
      case None => println("unknown serial port " + com)
      case Some(port) =>
        port.openPort()
        port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
        val reader = new CommReader(port)
        reader.beginRead {
          line =>
            println(line)
            line match {
              case regex_mc(mask, da0, da1, da2, da3, nrangs, rseq, debug, tid, aid) =>
                try {
                  Web.getNothing(
                    "http://%s:3000/tag_update?userid=%s&area=%s&tag_name=%s&da0=%d&da1=%d&da2=%d&da3=%d"
                      .format(server, userid, area, "T" + tid.hex, da0.hex, da1.hex, da2.hex, da3.hex))
                } catch {
                  case ex: Throwable => println(ex.getMessage)
                }
              case regex_ma(mask, da0, da1, da2, da3, nrangs, rseq, debug, tid, aid) =>
                /*
                map.getBss(0).da0.set(0)
                map.getBss(1).da0.set(da1.hex / aa_times)
                map.getBss(2).da0.set(da2.hex / aa_times)
                map.getBss(3).da0.set(da3.hex / aa_times)
                */
              case _ =>
            }
        }

    }


  }


}