/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://snipsnap.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */

package org.snipsnap.snip.filter.macro.table;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 * A Table implementation primarly for the
 * table macro
 *
 * @author stephan
 * @version $Id$
 */

public class Table {
  int indexRow = 0;
  int indexCol = 0;
  List rows = new ArrayList(10);
  List currentRow;

  public Table() {
    currentRow = new ArrayList(10);
  }

  public void addCell(String content) {
    currentRow.add(content);
    indexCol++;
  }

  public void newRow() {
    rows.add(currentRow);
    indexRow++;
    currentRow = new ArrayList(indexCol);
    indexCol=0;
  }

  public int calc() {
      return 0;
  }

  public void appendTo(StringBuffer buffer) {
    buffer.append("<table class=\"snip-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
    List[] outputRows = (List[]) rows.toArray(new List[0]);
    int rowSize = outputRows.length;
    boolean odd=true;
    for (int i=0; i<rowSize; i++) {
      buffer.append("<tr valign=\"top\" ");
      if (i==0) {
        buffer.append(" class=\"snip-table-header\">");
      } else if (odd) {
        buffer.append(" class=\"snip-table-odd\">");
        odd = false;
      } else {
        buffer.append(" class=\"snip-table-even\">");
        odd = true;
      }
      String[] outputCols = (String[]) outputRows[i].toArray(new String[0]);
      int colSize = outputCols.length;
      for (int j=0; j<colSize; j++) {
        buffer.append("<td>");
        buffer.append(outputCols[j]);
        buffer.append("</td>");
      }
      buffer.append("</tr>");
    }
    buffer.append("</table>");
  }
}
