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

package org.pentaho.reporting.engine.classic.demo.ancient.demo.sportscouncil;

public class Record
{
  private String recordType;
  private String orgID;

  public Record(final String recordType, final String orgID)
  {
    this.recordType = recordType;
    this.orgID = orgID;
  }

  public String getOrgID()
  {
    return orgID;
  }

  public String getRecordType()
  {
    return recordType;
  }
}
