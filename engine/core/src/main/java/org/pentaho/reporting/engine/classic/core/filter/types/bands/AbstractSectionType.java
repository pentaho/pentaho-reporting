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


package org.pentaho.reporting.engine.classic.core.filter.types.bands;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.AbstractElementType;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.SectionType;

public abstract class AbstractSectionType extends AbstractElementType implements SectionType {
  private boolean restricted;

  protected AbstractSectionType( final String id, final boolean restricted ) {
    super( id );
    this.restricted = restricted;
  }

  public boolean isRestricted() {
    return restricted;
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return null;
  }

  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return null;
  }
}
