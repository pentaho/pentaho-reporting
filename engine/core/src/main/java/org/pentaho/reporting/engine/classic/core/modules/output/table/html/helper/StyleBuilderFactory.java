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


package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;

public interface StyleBuilderFactory {
  public StyleBuilder produceTextStyle( StyleBuilder styleBuilder, final StyleSheet styleSheet,
      final BoxDefinition boxDefinition, final boolean includeBorder,
      final StyleBuilder.StyleCarrier[] parentElementStyle );

  double fixLengthForSafari( double v );
}
