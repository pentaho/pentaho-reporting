/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.filter.types.bands;

import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class GroupHeaderType extends AbstractSectionType {
  public static final GroupHeaderType INSTANCE = new GroupHeaderType();

  public GroupHeaderType() {
    super( "group-header", false );
  }

  public ReportElement create() {
    return new GroupHeader();
  }
}
