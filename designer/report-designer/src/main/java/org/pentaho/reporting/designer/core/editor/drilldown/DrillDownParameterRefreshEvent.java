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

package org.pentaho.reporting.designer.core.editor.drilldown;

import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;

import java.util.EventObject;

public class DrillDownParameterRefreshEvent extends EventObject {
  private DrillDownParameter[] parameter;

  public DrillDownParameterRefreshEvent( final Object source,
                                         final DrillDownParameter[] parameter ) {
    super( source );
    this.parameter = parameter.clone();
  }

  public DrillDownParameter[] getParameter() {
    return parameter.clone();
  }
}
