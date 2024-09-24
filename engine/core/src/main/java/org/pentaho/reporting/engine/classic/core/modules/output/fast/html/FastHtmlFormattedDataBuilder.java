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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.AbstractFormattedDataBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.FastGridLayout;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class FastHtmlFormattedDataBuilder extends AbstractFormattedDataBuilder {
  private final FastGridLayout gridLayout;
  private final FastHtmlPrinter htmlPrinter;
  private HashMap<InstanceID, FastHtmlImageBounds> recordedBounds;
  private HashMap<InstanceID, ReportElement> elements;
  private FastHtmlStyleCache styleCache;

  public FastHtmlFormattedDataBuilder( final FastGridLayout gridLayout, final FastHtmlPrinter htmlPrinter,
      final HashMap<InstanceID, FastHtmlImageBounds> recordedBounds ) {
    this.gridLayout = gridLayout;
    this.htmlPrinter = htmlPrinter;
    this.recordedBounds = recordedBounds;
    this.elements = new HashMap<InstanceID, ReportElement>();
    this.styleCache = new FastHtmlStyleCache();
  }

  public void compute( final Band band, final ExpressionRuntime runtime, final OutputStream out )
    throws ReportProcessingException, ContentProcessingException, IOException {
    elements.clear();
    super.compute( band, runtime );
    this.htmlPrinter.startSection( band );
    this.htmlPrinter.print( runtime, gridLayout, elements, recordedBounds, styleCache );
    this.htmlPrinter.endSection( band, gridLayout );
  }

  protected void inspectElement( final ReportElement element ) {
    this.elements.put( element.getObjectID(), element );
  }
}
