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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import java.util.TimeZone;

public class TimeZoneObjectDescription extends AbstractObjectDescription {
  public TimeZoneObjectDescription() {
    super( TimeZone.class );
    setParameterDefinition( "value", String.class );
  }

  public Object createObject() {
    final String o = (String) getParameter( "value" );
    return TimeZone.getTimeZone( o.trim() );
  }

  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( !( o instanceof TimeZone ) ) {
      throw new ObjectFactoryException( "The given object is no java.util.TimeZone. " );
    }
    final TimeZone t = (TimeZone) o;
    setParameter( "value", t.getID() );
  }
}
