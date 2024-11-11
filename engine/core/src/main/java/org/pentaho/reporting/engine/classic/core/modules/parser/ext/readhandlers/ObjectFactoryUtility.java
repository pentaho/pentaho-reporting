/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
