/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import javax.swing.event.ChangeListener;

public interface ParameterUpdateContext {
  public void setParameterValue( final String name, final Object value );

  public void setParameterValue( final String name, final Object value, final boolean autoUpdate );

  public Object getParameterValue( String name );

  public void addChangeListener( ChangeListener changeListener );

  public void removeChangeListener( ChangeListener changeListener );
}
