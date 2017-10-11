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

package org.pentaho.reporting.libraries.formula;

import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class DefaultFormulaContextFactory implements FormulaContextFactory {
  private static class Key {
    private Locale locale;
    private TimeZone timeZone;

    public Key( final Locale locale, final TimeZone timeZone ) {
      this.locale = locale;
      this.timeZone = timeZone;
    }

    public Locale getLocale() {
      return locale;
    }

    public TimeZone getTimeZone() {
      return timeZone;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final Key key = (Key) o;

      if ( locale != null ? !locale.equals( key.locale ) : key.locale != null ) {
        return false;
      }
      if ( timeZone != null ? !timeZone.equals( key.timeZone ) : key.timeZone != null ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = locale != null ? locale.hashCode() : 0;
      result = 31 * result + ( timeZone != null ? timeZone.hashCode() : 0 );
      return result;
    }
  }

  public static final DefaultFormulaContextFactory INSTANCE = new DefaultFormulaContextFactory();
  private HashMap<Key, DefaultFormulaContext> contexts;

  public DefaultFormulaContextFactory() {
    contexts = new HashMap<Key, DefaultFormulaContext>();
  }

  public synchronized FormulaContext create( final Locale locale, final TimeZone timeZone ) {
    final Key k = new Key( locale, timeZone );
    final DefaultFormulaContext cached = contexts.get( k );
    if ( cached != null ) {
      return cached;
    }

    final DefaultFormulaContext created =
      new DefaultFormulaContext( LibFormulaBoot.getInstance().getGlobalConfig(), locale, timeZone );
    contexts.put( k, created );
    return created;
  }

  public synchronized void clear() {
    contexts.clear();
  }
}
