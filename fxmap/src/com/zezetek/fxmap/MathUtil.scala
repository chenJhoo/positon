package com.zezetek.fxmap

import com.zezetek.fxmap.core.Round
import com.zezetek.fxmap.entity.Location

/**
  * Created by dingb on 2017/2/2.
  */
object MathUtil {
  def double_equals(a: Double, b: Double): Boolean = Math.abs(a - b) < 1e-9;
  def distance_sqr(a: Location, b: Location) = (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
  def distance(a: Location, b: Location) = Math.sqrt(distance_sqr(a, b));
  def insect(circle0: Round, circle1: Round): List[Location] = {
    var d = 0.0
    var a = 0.0
    var b = 0.0
    var c = 0.0
    var p = 0.0
    var q = 0.0
    var r = 0.0

    val cos_value = Array[Double](0, 0)
    val sin_value = Array[Double](0, 0)

    if (double_equals(circle0.x, circle1.x) && double_equals(circle0.y, circle1.y) && double_equals(circle0.r, circle1.r)) return null; // cirecles are the same


    d = distance(circle0.center, circle1.center);
    if (d > circle0.r + circle1.r || d < Math.abs(circle0.r - circle1.r)) Nil; //not insect
    a = 2.0 * circle0.r * (circle0.center.x - circle1.center.x);
    b = 2.0 * circle0.r * (circle0.center.y - circle1.center.y);
    c = circle1.r * circle1.r - circle0.r * circle0.r - distance_sqr(circle0.center, circle1.center);
    p = a * a + b * b;
    q = -2.0 * a * c;
    if (double_equals(d, circle0.r + circle1.r) || double_equals(d, Math.abs(circle0.r - circle1.r))) {
      cos_value(0) = -q / p / 2.0;
      sin_value(0) = Math.sqrt(1 - cos_value(0) * cos_value(0));
      val points = Array[Location](new Location(0,0))
      points(0).x = circle0.r * cos_value(0) + circle0.center.x;
      points(0).y = circle0.r * sin_value(0) + circle0.center.y;

      if (!double_equals(distance_sqr(points(0), circle1.center), circle1.r * circle1.r)) {
        points(0).y = circle0.center.y - circle0.r * sin_value(0);
      }
      return points.toList;
    }

    r = c * c - b * b;
    cos_value(0) = (Math.sqrt(q * q - 4.0 * p * r) - q) / p / 2.0;
    cos_value(1) = (-Math.sqrt(q * q - 4.0 * p * r) - q) / p / 2.0;
    sin_value(0) = Math.sqrt(1 - cos_value(0) * cos_value(0));
    sin_value(1) = Math.sqrt(1 - cos_value(1) * cos_value(1));
    val points = Array[Location](new Location(0,0), new Location(0,0))

    points(0).x = circle0.r * cos_value(0) + circle0.center.x;
    points(1).x = circle0.r * cos_value(1) + circle0.center.x;
    points(0).y = circle0.r * sin_value(0) + circle0.center.y;
    points(1).y = circle0.r * sin_value(1) + circle0.center.y;

    if (!double_equals(distance_sqr(points(0), circle1.center), circle1.r * circle1.r)) {
      points(0).y = circle0.center.y - circle0.r * sin_value(0);
    }
    if (!double_equals(distance_sqr(points(1), circle1.center), circle1.r * circle1.r)) {
      points(1).y = circle0.center.y - circle0.r * sin_value(1);
    }
    if (double_equals(points(0).y, points(1).y)
      && double_equals(points(0).x, points(1).x)) {
      if (points(0).y > 0) {
        points(1).y = -points(1).y;
      } else {
        points(0).y = -points(0).y;
      }
    }
    return points.toList
  }


}
