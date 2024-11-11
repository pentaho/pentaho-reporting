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
