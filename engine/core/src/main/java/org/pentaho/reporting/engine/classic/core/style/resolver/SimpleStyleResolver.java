/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
