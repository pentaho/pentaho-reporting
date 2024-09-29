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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import javax.swing.JRadioButton;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.ComponentListCellRenderer;
import org.pentaho.reporting.engine.classic.core.parameters.ListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;

public class RadioButtonParameterComponent extends ListParameterComponent {
  public RadioButtonParameterComponent( final ListParameter listParameter, final ParameterUpdateContext updateContext,
      final ParameterContext parameterContext ) {
    super( listParameter, updateContext, parameterContext );
    getList().setOpaque( false );
    getList().setBorder( null );
    getList().setCellRenderer( new ComponentListCellRenderer( JRadioButton.class ) );
  }
}
