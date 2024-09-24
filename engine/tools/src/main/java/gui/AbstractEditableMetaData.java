/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package gui;

import org.pentaho.reporting.engine.classic.core.metadata.AbstractMetaData;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import java.util.HashMap;
import java.util.Locale;

public abstract class AbstractEditableMetaData implements EditableMetaData {
  private static class CompoundKey {
    private Locale locale;
    private String attributeName;

    private CompoundKey( final Locale locale, final String attributeName ) {
      this.locale = locale;
      this.attributeName = attributeName;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( !( o instanceof CompoundKey ) ) {
        return false;
      }

      final CompoundKey that = (CompoundKey) o;

      if ( !attributeName.equals( that.attributeName ) ) {
        return false;
      }
      if ( !locale.equals( that.locale ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = locale.hashCode();
      result = 31 * result + attributeName.hashCode();
      return result;
    }
  }

  private HashMap<CompoundKey, String> editResults;
  private AbstractMetaData backend;

  public AbstractEditableMetaData( final AbstractMetaData backend ) {
    if ( backend == null ) {
      throw new NullPointerException();
    }
    this.backend = backend;
    this.editResults = new HashMap<CompoundKey, String>();
  }

  protected AbstractMetaData getBackend() {
    return backend;
  }

  public boolean isModified() {
    return editResults.isEmpty() == false;
  }

  public String getName() {
    final String s = backend.getName();
    final String[] strings = StringUtils.split( s, "." );
    if ( strings.length > 0 ) {
      return strings[ strings.length - 1 ];
    }
    return s;
  }

  public boolean isValidValue( final String attributeName, final Locale locale ) {
    final String s = getMetaAttribute( attributeName, locale );
    if ( "display-name".equals( attributeName ) ||
      "group".equals( attributeName ) ) {
      return StringUtils.isEmpty( s ) == false;
    }
    if ( "ordinal".equals( attributeName ) ||
      "grouping.ordinal".equals( attributeName ) ) {
      return ParserUtil.parseInt( s, Integer.MAX_VALUE ) != Integer.MAX_VALUE;
    }

    return s != null;
  }

  public void setMetaAttribute( final String attributeName, final Locale locale, final String value ) {
    if ( value == null ) {
      editResults.put( new CompoundKey( locale, attributeName ), "" );
    } else {
      editResults.put( new CompoundKey( locale, attributeName ), value );
    }
  }

  public String getMetaAttribute( final String attributeName, final Locale locale ) {
    final CompoundKey key = new CompoundKey( locale, attributeName );
    final String override = editResults.get( key );
    if ( override != null || editResults.containsKey( key ) ) {
      return override;
    }
    return backend.getMetaAttribute( attributeName, locale );
  }

  public final int getGroupingOrdinal( final Locale locale ) {
    final String strOrd = getMetaAttribute( "grouping.ordinal", locale );
    return ParserUtil.parseInt( strOrd, Integer.MAX_VALUE );
  }

  public final int getItemOrdinal( final Locale locale ) {
    final String strOrd = getMetaAttribute( "ordinal", locale );
    return ParserUtil.parseInt( strOrd, Integer.MAX_VALUE );
  }
}
