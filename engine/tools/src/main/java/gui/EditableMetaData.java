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


package gui;

import java.util.Locale;

public interface EditableMetaData {
  public void setMetaAttribute( final String attributeName, final Locale locale, final String value );

  public String getMetaAttribute( final String attributeName, final Locale locale );

  public String getName();

  public int getGroupingOrdinal( Locale locale );

  public int getItemOrdinal( Locale locale );

  public boolean isValidValue( final String attributeName, final Locale locale );

  public boolean isValid( final Locale locale, boolean deepCheck );

  public boolean isModified();

  public String printBundleText( final Locale locale );
}
