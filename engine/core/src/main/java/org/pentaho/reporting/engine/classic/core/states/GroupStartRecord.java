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

package org.pentaho.reporting.engine.classic.core.states;

public class GroupStartRecord {
  private int row;
  private String groupName;
  private String generatedGroupName;

  public GroupStartRecord( final int row, final String groupName, final String generatedGroupName ) {
    this.row = row;
    this.groupName = groupName;
    this.generatedGroupName = generatedGroupName;
  }

  public int getRow() {
    return row;
  }

  public String getGroupName() {
    return groupName;
  }

  public String getGeneratedGroupName() {
    return generatedGroupName;
  }
}
