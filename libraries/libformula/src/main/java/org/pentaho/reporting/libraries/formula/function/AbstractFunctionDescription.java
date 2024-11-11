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


package org.pentaho.reporting.libraries.formula.function;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Creation-Date: 04.11.2006, 18:30:33
 *
 * @author Thomas Morgner
 */
public abstract class AbstractFunctionDescription implements FunctionDescription {
  private String bundleName;
  private String cannonicalName;
  private static final long serialVersionUID = 6174633076234746415L;

  protected AbstractFunctionDescription( final String cannonicalName,
                                         final String bundleName ) {
    if ( bundleName == null ) {
      throw new NullPointerException();
    }
    if ( cannonicalName == null ) {
      throw new NullPointerException();
    }
    this.bundleName = bundleName;
    this.cannonicalName = cannonicalName;
  }

  public String getCanonicalName() {
    return cannonicalName;
  }

  public boolean isVolatile() {
    return false;
  }

  /**
   * Returns the default value for an optional parameter. If the value returned here is null, then this either means,
   * that the parameter is mandatory or that the default value is computed by the expression itself.
   *
   * @param position
   * @return null.
   */
  public Object getDefaultValue( final int position ) {
    return null;
  }

  public boolean isInfiniteParameterCount() {
    return false;
  }

  protected ResourceBundle getBundle( final Locale locale ) {
    try {
      return ResourceBundle.getBundle( bundleName, locale );
    } catch ( final MissingResourceException mre ) {
      // ignore the exception, fall back to explicit english locales. Fail, if that fails too.
      return ResourceBundle.getBundle( bundleName, Locale.ENGLISH );
    }
  }

  public String getDisplayName( final Locale locale ) {
    return getBundle( locale ).getString( "display-name" );
  }

  public String getDescription( final Locale locale ) {
    return getBundle( locale ).getString( "description" );
  }

  public String getParameterDisplayName( final int position, final Locale locale ) {
    return getBundle( locale ).getString( "parameter." + position + ".display-name" );
  }

  public String getParameterDescription( final int position, final Locale locale ) {
    return getBundle( locale ).getString( "parameter." + position + ".description" );
  }

  public boolean isDeprecated() {
    return false;
  }

  public boolean isExperimental() {
    return false;
  }
}
