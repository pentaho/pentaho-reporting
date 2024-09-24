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

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines.xml;

import java.awt.Color;
import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.BundleStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineModule;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineStyleKeys;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

public class SparklineStyleSetWriteHandler implements BundleStyleSetWriteHandler {
  public SparklineStyleSetWriteHandler() {
  }

  public void writeStyle( final XmlWriter writer, final ElementStyleSheet style ) throws IOException {
    final AttributeList bandStyleAtts = new AttributeList();
    if ( style.isLocalKey( SparklineStyleKeys.HIGH_COLOR ) ) {
      final Color value = (Color) style.getStyleProperty( SparklineStyleKeys.HIGH_COLOR );
      bandStyleAtts.setAttribute( SparklineModule.NAMESPACE, "high-color", ColorValueConverter.colorToString( value ) );
    }
    if ( style.isLocalKey( SparklineStyleKeys.MEDIUM_COLOR ) ) {
      final Color value = (Color) style.getStyleProperty( SparklineStyleKeys.MEDIUM_COLOR );
      bandStyleAtts
          .setAttribute( SparklineModule.NAMESPACE, "medium-color", ColorValueConverter.colorToString( value ) );
    }
    if ( style.isLocalKey( SparklineStyleKeys.LOW_COLOR ) ) {
      final Color value = (Color) style.getStyleProperty( SparklineStyleKeys.LOW_COLOR );
      bandStyleAtts.setAttribute( SparklineModule.NAMESPACE, "low-color", ColorValueConverter.colorToString( value ) );
    }
    if ( style.isLocalKey( SparklineStyleKeys.LAST_COLOR ) ) {
      final Color value = (Color) style.getStyleProperty( SparklineStyleKeys.LAST_COLOR );
      bandStyleAtts.setAttribute( SparklineModule.NAMESPACE, "last-color", ColorValueConverter.colorToString( value ) );
    }

    if ( bandStyleAtts.isEmpty() == false ) {
      writer.writeTag( SparklineModule.NAMESPACE, "spark-styles", bandStyleAtts, XmlWriter.CLOSE );
    }
  }
}
