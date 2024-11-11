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
