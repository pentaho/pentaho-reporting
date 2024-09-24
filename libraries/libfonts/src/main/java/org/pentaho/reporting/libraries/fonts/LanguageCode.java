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

package org.pentaho.reporting.libraries.fonts;

/**
 * Different language codes are defined for the mac and windows platform. The numbering schema is disjunct, so there are
 * no conflicts between the codes assigned on the Windows platform and the codes assigned on the Macintosh platform.
 *
 * @author Thomas Morgner
 */
public class LanguageCode {
  public static class MacLanguageCode extends LanguageCode {
    public static final LanguageCode ENGLISH = new LanguageCode( "english", 0 );

    public MacLanguageCode( final String name, final int code ) {
      super( name, code );
    }
  }

  public static class MicrosoftLanguageCode extends LanguageCode {
    public static final LanguageCode ENGLISH_US = new LanguageCode( "en_US", 0x0409 );

    public MicrosoftLanguageCode( final String name, final int code ) {
      super( name, code );
    }
  }

  private int code;
  private String name;

  public LanguageCode( final String name, final int code ) {
    if ( name == null ) {
      throw new NullPointerException( "Name must not be null." );
    }
    this.name = name;
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final LanguageCode language = (LanguageCode) o;
    return code == language.code;
  }

  public int hashCode() {
    return code;
  }
}
