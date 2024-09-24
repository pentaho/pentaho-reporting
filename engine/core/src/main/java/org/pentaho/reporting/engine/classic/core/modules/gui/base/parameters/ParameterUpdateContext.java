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

import javax.swing.event.ChangeListener;

public interface ParameterUpdateContext {
  public void setParameterValue( final String name, final Object value );

  public void setParameterValue( final String name, final Object value, final boolean autoUpdate );

  public Object getParameterValue( String name );

  public void addChangeListener( ChangeListener changeListener );

  public void removeChangeListener( ChangeListener changeListener );
}
