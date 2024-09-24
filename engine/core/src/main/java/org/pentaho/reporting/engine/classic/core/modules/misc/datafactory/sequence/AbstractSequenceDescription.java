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

import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.beans.PropertyEditor;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class AbstractSequenceDescription implements SequenceDescription {
  private String bundleName;
  private Class<? extends Sequence> baseClass;

  public AbstractSequenceDescription( final String bundleName, final Class<? extends Sequence> baseClass ) {
    this.bundleName = bundleName;
    this.baseClass = baseClass;
  }

  protected ResourceBundle getBundle( final Locale locale ) {
    try {
      return ResourceBundle.getBundle( bundleName, locale );
    } catch ( final MissingResourceException mre ) {
      // ignore the exception, fall back to explicit english locales. Fail, if that fails too.
      return ResourceBundle.getBundle( bundleName, Locale.ENGLISH );
    }
  }

  public int getParameterCount() {
    try {
      final String string = getBundle( Locale.getDefault() ).getString( "parameter-count" );
      return Integer.parseInt( string );
    } catch ( Exception e ) {
      throw new IllegalStateException( e );
    }
  }

  public String getParameterName( final int position ) {
    return getBundle( Locale.getDefault() ).getString( "parameter." + position + ".name" );
  }

  public String getParameterDisplayName( final int position, final Locale locale ) {
    return getBundle( locale ).getString( "parameter." + position + ".display-name" );
  }

  public String getParameterDescription( final int position, final Locale locale ) {
    return getBundle( locale ).getString( "parameter." + position + ".description" );
  }

  public Class getParameterType( final int position ) {
    try {
      final String string = getBundle( Locale.getDefault() ).getString( "parameter." + position + ".type" );
      return Class.forName( string, false, ObjectUtilities.getClassLoader( AbstractSequenceDescription.class ) );
    } catch ( Exception e ) {
      throw new IllegalStateException( e );
    }
  }

  public String getParameterRole( final int position, final Locale locale ) {
    try {
      return getBundle( Locale.getDefault() ).getString( "parameter." + position + ".role" );
    } catch ( Exception e ) {
      throw new IllegalStateException( e );
    }
  }

  /**
   * This is a design-time default.
   *
   * @param position
   * @return
   */
  public Object getParameterDefault( final int position ) {
    try {
      final String value = getBundle( Locale.getDefault() ).getString( "parameter." + position + ".default-value" );
      if ( StringUtils.isEmpty( value ) ) {
        return null;
      }
      return ConverterRegistry.toPropertyValue( value, getParameterType( position ) );
    } catch ( Exception e ) {
      return null;
    }
  }

  public String getDisplayName( final Locale locale ) {
    try {
      return getBundle( Locale.getDefault() ).getString( "display-name" );
    } catch ( Exception e ) {
      throw new IllegalStateException( e );
    }
  }

  public String getDescription( final Locale locale ) {
    try {
      return getBundle( Locale.getDefault() ).getString( "description" );
    } catch ( Exception e ) {
      throw new IllegalStateException( e );
    }
  }

  public PropertyEditor getEditor( final int position ) {
    try {
      final ResourceBundle bundle = getBundle( Locale.getDefault() );
      final String key = "parameter." + position + ".editor";
      if ( bundle.containsKey( key ) == false ) {
        return null;
      }
      final String string = bundle.getString( key );
      return ObjectUtilities.loadAndInstantiate( string, AbstractSequenceDescription.class, PropertyEditor.class );
    } catch ( Exception e ) {
      return null;
    }
  }

  public String getSequenceGroup( final Locale locale ) {
    try {
      return getBundle( Locale.getDefault() ).getString( "group" );
    } catch ( Exception e ) {
      throw new IllegalStateException( e );
    }
  }

  public Sequence newInstance() {
    try {
      return baseClass.newInstance();
    } catch ( Exception e ) {
      throw new IllegalStateException();
    }
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "AbstractSequenceDescription" );
    sb.append( "{baseClass=" ).append( baseClass );
    sb.append( '}' );
    return sb.toString();
  }
}
