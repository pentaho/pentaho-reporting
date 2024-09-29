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

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Watermark;

public class WatermarkType extends AbstractSectionType {
  public static final WatermarkType INSTANCE = new WatermarkType();

  public WatermarkType() {
    super( "watermark", false );
  }

  public ReportElement create() {
    return new Watermark();
  }
}
