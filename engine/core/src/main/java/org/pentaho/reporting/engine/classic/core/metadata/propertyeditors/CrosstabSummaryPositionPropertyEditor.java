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


package org.pentaho.reporting.engine.classic.core.metadata.propertyeditors;

import org.pentaho.reporting.engine.classic.core.CrosstabSummaryPosition;

public class CrosstabSummaryPositionPropertyEditor extends EnumPropertyEditor {
  public CrosstabSummaryPositionPropertyEditor() {
    super( CrosstabSummaryPosition.class, true );
  }
}
