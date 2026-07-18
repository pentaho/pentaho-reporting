/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
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
