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

package org.pentaho.reporting.engine.classic.core.testsupport;

import org.osjava.sj.SimpleContext;
import org.osjava.sj.loader.JndiLoader;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldenSampleGenerator;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.io.File;
import java.util.Hashtable;

public class DebugJndiContextFactory implements InitialContextFactory {
  public DebugJndiContextFactory() {
  }

  public Context getInitialContext( final Hashtable environment ) throws NamingException {
    final Hashtable hashtable = new Hashtable();
    if ( environment != null ) {
      hashtable.putAll( environment );
    }

    final String o = (String) hashtable.get( "java.naming.factory.initial" );
    if ( StringUtils.isEmpty( o ) == false && DebugJndiContextFactory.class.getName().equals( o ) == false ) {
      final InitialContextFactory contextFactory =
          ObjectUtilities.loadAndInstantiate( o, DebugJndiContextFactory.class, InitialContextFactory.class );
      return contextFactory.getInitialContext( environment );
    }

    hashtable.put( JndiLoader.SIMPLE_DELIMITER, "/" );
    try {
      final File directory = GoldenSampleGenerator.findMarker();
      final File jndi = new File( directory, "jndi" );
      if ( jndi != null ) {
        hashtable.put( SimpleContext.SIMPLE_ROOT, jndi.getAbsolutePath() );
      }
    } catch ( SecurityException se ) {
      // ignore ..
    }
    return new SimpleContext( hashtable );
  }
}
