package org.snipsnap.util;

/**
 * Generates a range of HTML compatible colors from start to end
 *
 * @author stephan
 * @version $Id$
 **/

public class ColorRange {
  String start;
  String end;
  int steps;

  float dr, dg, db;
  int r1, g1, b1;

  public ColorRange(String start, String end, int steps) {
    start = start.substring(0, 1).equals("#") ? start.substring(1) : start;
    end = end.substring(0, 1).equals("#") ? end.substring(1) : end;
    this.start = start;
    this.end = end;
    this.steps = steps;

    this.r1 = Integer.parseInt(start.substring(0, 2), 16);
    this.g1 = Integer.parseInt(start.substring(2, 4), 16);
    this.b1 = Integer.parseInt(start.substring(4, 6), 16);

    int r2 = Integer.parseInt(end.substring(0, 2), 16);
    int g2 = Integer.parseInt(end.substring(2, 4), 16);
    int b2 = Integer.parseInt(end.substring(4, 6), 16);
    if (steps != 1) {
      this.dr = (r2 - r1) / (steps - 1);
      this.dg = (g2 - g1) / (steps - 1);
      this.db = (b2 - b1) / (steps - 1);
    } else {
      this.dr = this.dg = this.db = 0;
    }
  }

  public String getColor(int step) {
    if (step > this.steps - 1) {
      throw new IllegalArgumentException("steps range from 0 to " + (this.steps - 1));
    }

    int r = (int) (dr * step + r1);
    int g = (int) (dg * step + g1);
    int b = (int) (db * step + b1);

    return "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
  }

}
