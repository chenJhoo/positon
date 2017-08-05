package com.zezetek.fxmap.io

import com.fazecast.jSerialComm.SerialPort

/**
  * Created by dingb on 2017/1/23.
  */
class CommReader(port: SerialPort)  {
  var done = false
  def endRead(): Unit = {
    done = true
  }
  def beginRead(lineRead: (String) => Unit): Unit = {
    val t = new Thread(new Runnable {
      override def run(): Unit = {
        val sb = new StringBuilder
        while(!done) {
          val remain = port.bytesAvailable()
          if(remain > 0) {
            val buf = new Array[Byte](remain)
            val r = port.readBytes(buf, remain)
            if(r > 0) {
              for(i <- 0 until r) {
                sb.append("%c".format(buf(i)))
                if(sb.endsWith("\r\n")) {
                  sb.deleteCharAt(sb.length-1)
                  sb.deleteCharAt(sb.length-1)
                  lineRead(sb.toString)
                  sb.clear
                }
              }
            }
          }
        }
      }
    })
    t.start
  }
}
