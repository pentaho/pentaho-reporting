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
