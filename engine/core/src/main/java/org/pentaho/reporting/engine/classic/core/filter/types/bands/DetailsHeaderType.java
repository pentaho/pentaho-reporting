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

import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;

public class DetailsHeaderType extends AbstractSectionType {
  public static ElementType INSTANCE = new DetailsHeaderType();

  public DetailsHeaderType() {
    super( "details-header", false );
  }

  public ReportElement create() {
    return new DetailsHeader();
  }
}
