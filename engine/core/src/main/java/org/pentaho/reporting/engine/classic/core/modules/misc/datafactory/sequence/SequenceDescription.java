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


package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence;

import java.beans.PropertyEditor;
import java.util.Locale;

public interface SequenceDescription {
  public int getParameterCount();

  public String getParameterName( int position );

  public String getParameterDisplayName( int position, Locale locale );

  public String getParameterDescription( int position, Locale locale );

  public Class getParameterType( int position );

  public String getParameterRole( int position, Locale locale );

  public Object getParameterDefault( int position );

  public String getSequenceGroup( final Locale locale );

  public PropertyEditor getEditor( final int position );

  public String getDisplayName( final Locale locale );

  public String getDescription( final Locale locale );

  public Sequence newInstance();
}
