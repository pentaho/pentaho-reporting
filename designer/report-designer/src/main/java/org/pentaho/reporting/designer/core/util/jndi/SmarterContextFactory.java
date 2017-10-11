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
