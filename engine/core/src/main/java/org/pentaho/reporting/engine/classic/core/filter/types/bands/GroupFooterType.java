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


package org.pentaho.reporting.engine.classic.core.filter.types.bands;

import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class GroupFooterType extends AbstractSectionType {
  public static final GroupFooterType INSTANCE = new GroupFooterType();

  public GroupFooterType() {
    super( "group-footer", false );
  }

  public ReportElement create() {
    return new GroupFooter();
  }
}
