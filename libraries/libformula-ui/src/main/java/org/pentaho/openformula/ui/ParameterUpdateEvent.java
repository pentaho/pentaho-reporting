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


package org.pentaho.openformula.ui;

import java.util.EventObject;

public class ParameterUpdateEvent extends EventObject {
  private int parameter;
  private String text;
  private boolean catchAllParameter;

  public ParameterUpdateEvent( final Object source,
                               final int parameter,
                               final String text,
                               final boolean catchAllParameter ) {
    super( source );
    this.parameter = parameter;
    this.text = text;
    this.catchAllParameter = catchAllParameter;
  }

  public int getParameter() {
    return parameter;
  }

  public String getText() {
    return text;
  }

  public boolean isCatchAllParameter() {
    return catchAllParameter;
  }
}
