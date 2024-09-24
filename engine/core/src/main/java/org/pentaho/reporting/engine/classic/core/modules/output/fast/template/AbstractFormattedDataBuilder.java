/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
