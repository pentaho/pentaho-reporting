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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.SwingPreviewModule;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.filesystem.FileConfigStoreModule;
import org.pentaho.reporting.libraries.base.boot.Module;
import org.pentaho.reporting.libraries.base.boot.PackageSorter;
import org.pentaho.reporting.libraries.base.boot.PackageState;

import java.util.ArrayList;

public class PackageStateSortTest extends TestCase {
  private static final Log logger = LogFactory.getLog( PackageStateSortTest.class );

  public PackageStateSortTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testAll() throws Exception {
    final ArrayList states = new ArrayList();
    final Module[] mods = ClassicEngineBoot.getInstance().getPackageManager().getAllModules();
    int swingPreviewPos = 0;
    int fileConfigPos = 0;

    for ( int i = 0; i < mods.length; i++ ) {
      states.add( new PackageState( mods[i] ) );
    }

    PackageSorter.sort( states );

    for ( int i = 0; i < states.size(); i++ ) {
      final PackageState state = (PackageState) states.get( i );

      if ( state.getModule().getClass().equals( SwingPreviewModule.class ) ) {
        logger.debug( "SwingPreviewModule: " + i );
        swingPreviewPos = i;
      }
      if ( state.getModule().getClass().equals( FileConfigStoreModule.class ) ) {
        logger.debug( "File: " + i );
        fileConfigPos = i;
      }
    }

    assertTrue( fileConfigPos < swingPreviewPos );
  }

}
