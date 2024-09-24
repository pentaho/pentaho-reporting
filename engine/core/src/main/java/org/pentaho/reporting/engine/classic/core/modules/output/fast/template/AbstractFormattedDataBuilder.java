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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.template;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.richtext.RichTextConverter;
import org.pentaho.reporting.engine.classic.core.layout.richtext.RichTextConverterRegistry;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.AbstractStructureVisitor;

public abstract class AbstractFormattedDataBuilder extends AbstractStructureVisitor implements FormattedDataBuilder {
  private ExpressionRuntime runtime;

  public AbstractFormattedDataBuilder() {
  }

  protected void traverseSection( final Section section ) {
    traverseSectionWithoutSubReports( section );
  }

  protected void compute( Band band, ExpressionRuntime runtime ) {
    if ( band.getComputedStyle().getBooleanStyleProperty( ElementStyleKeys.VISIBLE ) == false ) {
      return;
    }

    try {
      this.runtime = runtime;
      inspectElement( band );
      traverseSection( band );
    } finally {
      this.runtime = null;
    }
  }

  public ExpressionRuntime getRuntime() {
    return runtime;
  }

  public static Object filterRichText( final ReportElement element, final Object initialValue ) {
    final Object richTextType =
        element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.RICH_TEXT_TYPE );
    if ( richTextType != null ) {
      final RichTextConverterRegistry registry = RichTextConverterRegistry.getRegistry();
      final RichTextConverter converter = registry.getConverter( String.valueOf( richTextType ) );
      if ( converter != null ) {
        return converter.convert( element, initialValue );
      }
    }
    return initialValue;
  }

}
