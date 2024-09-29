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


package org.pentaho.reporting.ui.datasources.mondrian;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.BandedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DenormalizedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.LegacyBandedMDXDataFactory;

public class ModuleTest extends TestCase {
  public ModuleTest() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testModuleExists() {
    assertTrue( ClassicEngineBoot.getInstance().getPackageManager()
      .isModuleAvailable( MondrianDataSourceModule.class.getName() ) );
  }

  public void testEditorRegistered() {
    DataSourcePlugin editor =
      DataFactoryRegistry.getInstance().getMetaData( LegacyBandedMDXDataFactory.class.getName() ).createEditor();
    assertNotNull( editor );

    assertTrue( editor.canHandle( new LegacyBandedMDXDataFactory() ) );
  }

  public void testEditorRegistered2() {
    DataSourcePlugin editor =
      DataFactoryRegistry.getInstance().getMetaData( DenormalizedMDXDataFactory.class.getName() ).createEditor();
    assertNotNull( editor );

    assertTrue( editor.canHandle( new DenormalizedMDXDataFactory() ) );
  }

  public void testEditorRegistered3() {
    DataSourcePlugin editor =
      DataFactoryRegistry.getInstance().getMetaData( BandedMDXDataFactory.class.getName() ).createEditor();
    assertNotNull( editor );

    assertTrue( editor.canHandle( new BandedMDXDataFactory() ) );
  }

}
