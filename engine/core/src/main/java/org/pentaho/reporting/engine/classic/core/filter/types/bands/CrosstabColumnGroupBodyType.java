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

import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class CrosstabColumnGroupBodyType extends AbstractSectionType {
  public static final CrosstabColumnGroupBodyType INSTANCE = new CrosstabColumnGroupBodyType();

  public CrosstabColumnGroupBodyType() {
    super( "crosstab-column-group-body", true );
  }

  public ReportElement create() {
    return new CrosstabColumnGroupBody();
  }
}
