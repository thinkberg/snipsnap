package com.neotis.date;

import java.sql.Date;

/**
 * Simple Date.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 **/
public class SDate extends Date {
  public SDate(Date date) {
    super(date.getTime());
  }
}
