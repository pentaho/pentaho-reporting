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

import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class CrosstabRowGroupBodyType extends AbstractSectionType {
  public static final CrosstabRowGroupBodyType INSTANCE = new CrosstabRowGroupBodyType();

  public CrosstabRowGroupBodyType() {
    super( "crosstab-row-group-body", true );
  }

  public ReportElement create() {
    return new CrosstabRowGroupBody();
  }
}
