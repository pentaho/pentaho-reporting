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

import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class CrosstabOtherGroupBodyType extends AbstractSectionType {
  public static final CrosstabOtherGroupBodyType INSTANCE = new CrosstabOtherGroupBodyType();

  public CrosstabOtherGroupBodyType() {
    super( "crosstab-other-group-body", true );
  }

  public ReportElement create() {
    return new CrosstabOtherGroupBody();
  }
}
