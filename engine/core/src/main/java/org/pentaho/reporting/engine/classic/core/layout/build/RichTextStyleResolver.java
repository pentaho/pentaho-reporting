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

package org.pentaho.reporting.engine.classic.core.layout.build;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.css.CSSStyleResolver;
import org.pentaho.reporting.engine.classic.core.style.resolver.StyleResolver;
import org.pentaho.reporting.engine.classic.core.util.AbstractStructureVisitor;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;

public class RichTextStyleResolver extends AbstractStructureVisitor {
  private StyleResolver cssStyleResolver;

  public RichTextStyleResolver( ProcessingContext runtime, ReportElement element ) {
    cssStyleResolver = CSSStyleResolver.createDesignTimeResolver(
              element.getReportDefinition(),
              runtime.getResourceManager(),
              runtime.getContentBase(),
              false );
  }

  protected void traverseSection( final Section section ) {
    traverseSectionWithoutSubReports( section );
  }

  private void resolveStyle( final Section section ) {
    inspectElement( section );
    traverseSection( section );
  }

  protected void inspectElement( final ReportElement element ) {
    ResolverStyleSheet resolveStyleSheet = new ResolverStyleSheet();
    cssStyleResolver.resolve( element, resolveStyleSheet );
    element.setComputedStyle( new SimpleStyleSheet( resolveStyleSheet ) );
  }

  public void resolveRichTextStyle( Section section ) {
    resolveStyle( section );
  }
}
