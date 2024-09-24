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
