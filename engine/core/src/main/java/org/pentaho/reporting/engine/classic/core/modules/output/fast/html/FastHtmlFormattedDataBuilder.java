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
