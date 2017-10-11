/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
