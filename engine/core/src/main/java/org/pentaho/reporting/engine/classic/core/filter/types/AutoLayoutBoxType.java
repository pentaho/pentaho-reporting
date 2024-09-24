/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.filter.types;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;

import java.util.Locale;

/**
 * A internal type that marks boxes that have been created for layouting purposes.
 *
 * @author Thomas Morgner
 */
public class AutoLayoutBoxType extends AbstractElementType {
  public static final ElementType INSTANCE = new AutoLayoutBoxType();

  public AutoLayoutBoxType() {
    super( "auto-layout-box" );
  }

  public ReportElement create() {
    final Band element = new Band();
    element.setElementType( INSTANCE );
    return element;
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   *          the element.
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    throw new UnsupportedOperationException();
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    throw new UnsupportedOperationException();
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {
    throw new UnsupportedOperationException();
  }
}
