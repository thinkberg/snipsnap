package com.neotis.date;

import java.sql.Date;

public class SDate extends Date {
  public SDate(Date date) {
    super(date.getTime());
  }
}
