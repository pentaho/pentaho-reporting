/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.style.resolver;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.DoubleKeyedCounter;
import org.pentaho.reporting.engine.classic.core.util.SingleKeyedCounter;
import org.pentaho.reporting.libraries.base.util.FastStack;

public class SimpleStyleResolver implements StyleResolver {
  private SingleKeyedCounter<String> usageCounter;
  private DoubleKeyedCounter<String, Long> extendedCounter;
  private boolean designTime;

  public SimpleStyleResolver() {
    this( false );
  }

  public SimpleStyleResolver( final boolean designTime ) {
    this.designTime = designTime;
    usageCounter = new SingleKeyedCounter<String>();
    extendedCounter = new DoubleKeyedCounter<String, Long>();
  }

  public static SimpleStyleSheet resolveOneTime( final ReportElement element ) {
    final SimpleStyleResolver styleResolver = new SimpleStyleResolver( true );
    final ResolverStyleSheet resolverTarget = new ResolverStyleSheet();
    styleResolver.resolve( element, resolverTarget );
    return new SimpleStyleSheet( resolverTarget );
  }

  public void resolve( final ReportElement element, final ResolverStyleSheet resolverTarget ) {
    // add(element);
    resolverTarget.clear();
    resolverTarget.setId( element.getStyle().getId() );

    resolveParent( element, resolverTarget );

    resolverTarget.addAll( element.getStyle() );
    resolverTarget.addDefault( element.getDefaultStyleSheet() );
  }

  public void resolveParent( final ReportElement element, final ElementStyleSheet resolverTarget ) {
    if ( designTime == false ) {
      final Section parentSection = element.getParentSection();
      if ( parentSection == null ) {
        return;
      }

      final SimpleStyleSheet computedStyle = parentSection.getComputedStyle();
      resolverTarget.addInherited( computedStyle );
    }

    final FastStack<Section> parentSections = new FastStack<Section>();

    ReportElement e = element;
    while ( e.getParentSection() != null ) {
      final Section section = e.getParentSection();
      parentSections.push( section );

      e = section;
    }

    while ( parentSections.isEmpty() == false ) {
      final Section section = parentSections.pop();
      resolverTarget.addInherited( section.getStyle() );
    }
  }

  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append( usageCounter.printStatistic() );
    b.append( extendedCounter.printStatistic() );
    return b.toString();
  }

  public SimpleStyleResolver clone() {
    try {
      return (SimpleStyleResolver) super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }
}
