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


package org.pentaho.reporting.designer.core.util.jndi;

import org.osjava.sj.SimpleContext;
import org.osjava.sj.SimpleContextFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.File;
import java.util.Hashtable;

public class SmarterContextFactory extends SimpleContextFactory {
  public SmarterContextFactory() {
  }

  public Context getInitialContext( final Hashtable hashtable ) throws NamingException {
    final Object root = hashtable.get( SimpleContext.SIMPLE_ROOT );
    if ( root == null ) {
      final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
      final String userHome = configuration.getConfigProperty( "user.home" ); // NON-NLS
      if ( userHome != null ) {
        final File directory = new File( userHome, ".pentaho/simple-jndi" );// NON-NLS
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();
        if ( directory.exists() && directory.isDirectory() ) {
          //noinspection unchecked
          hashtable.put( SimpleContext.SIMPLE_ROOT, directory.getAbsolutePath() );
        }
      }
    }
    return new SimpleContext( hashtable );
  }
}
