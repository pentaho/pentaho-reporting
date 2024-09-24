/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.demo.ancient.demo.groups;

import java.util.Date;

public class LogEvent
{
  private Date timestamp;
  private String event;
  private String description;

  public LogEvent(Date timestamp, String event, String description)
  {
    this.timestamp = timestamp;
    this.event = event;
    this.description = description;
  }

  public String getDescription()
  {
    return description;
  }

  public String getEvent()
  {
    return event;
  }

  public Date getTimestamp()
  {
    return timestamp;
  }
}
