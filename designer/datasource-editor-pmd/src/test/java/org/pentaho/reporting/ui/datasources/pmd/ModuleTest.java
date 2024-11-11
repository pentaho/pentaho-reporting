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


package org.pentaho.reporting.ui.datasources.pmd;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactory;

public class ModuleTest extends TestCase {
  public ModuleTest() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testModuleExists() {
    assertTrue(
      ClassicEngineBoot.getInstance().getPackageManager().isModuleAvailable( PmdDataSourceModule.class.getName() ) );
  }

  public void testEditorRegistered() {
    DataSourcePlugin editor =
      DataFactoryRegistry.getInstance().getMetaData( PmdDataFactory.class.getName() ).createEditor();
    assertNotNull( editor );

    assertTrue( editor.canHandle( new PmdDataFactory() ) );
  }

}
