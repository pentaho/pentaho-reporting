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
 * Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
 * All rights reserved.
 */

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
