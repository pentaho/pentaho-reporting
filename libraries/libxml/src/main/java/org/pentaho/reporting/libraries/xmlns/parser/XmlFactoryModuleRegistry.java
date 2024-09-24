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

package org.pentaho.reporting.libraries.xmlns.parser;

import java.util.ArrayList;

/**
 * A helper class intended to be used by concrete implementations of the AbstractXmlResourceFactory.
 */
public class XmlFactoryModuleRegistry {
  private ArrayList<Class<? extends XmlFactoryModule>> registeredHandlers;

  public XmlFactoryModuleRegistry() {
    registeredHandlers = new ArrayList<Class<? extends XmlFactoryModule>>();
  }

  public void register( final Class<? extends XmlFactoryModule> readHandler ) {
    registeredHandlers.add( readHandler );
  }

  public XmlFactoryModule[] getRegisteredHandlers() {
    final ArrayList<XmlFactoryModule> handlers = new ArrayList<XmlFactoryModule>();
    for ( int i = 0; i < registeredHandlers.size(); i++ ) {
      try {
        final Class<? extends XmlFactoryModule> aClass = registeredHandlers.get( i );
        handlers.add( aClass.newInstance() );
      } catch ( Exception e ) {
        throw new IllegalStateException( e );
      }
    }
    return handlers.toArray( new XmlFactoryModule[ handlers.size() ] );
  }
}
