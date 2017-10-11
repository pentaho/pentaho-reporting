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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Locator;

public class ObjectFactoryUtility {
  private ObjectFactoryUtility() {
  }

  public static ObjectDescription findDescription( final ClassFactory cf, final Class c, final Locator locator )
    throws ParseException {
    if ( c == null ) {
      throw new NullPointerException( "Class cannot be null" );
    }

    final ObjectDescription directMatch = cf.getDescriptionForClass( c );
    if ( directMatch != null ) {
      return directMatch;
    }
    final ObjectDescription indirectMatch = cf.getSuperClassObjectDescription( c, null );
    if ( indirectMatch != null ) {
      return indirectMatch;
    }
    throw new ParseException( "No object description found for '" + c + '\'', locator );
  }
}
