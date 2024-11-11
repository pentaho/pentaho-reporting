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


package org.pentaho.reporting.ui.datasources.sequence;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDataFactory;

public class ModuleTest extends TestCase {
  public ModuleTest() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testModuleExists() {
    assertTrue( ClassicEngineBoot.getInstance().getPackageManager()
      .isModuleAvailable( SequenceDataSourceModule.class.getName() ) );
  }

  public void testEditorRegistered() {
    DataSourcePlugin editor =
      DataFactoryRegistry.getInstance().getMetaData( SequenceDataFactory.class.getName() ).createEditor();
    assertNotNull( editor );

    assertTrue( editor.canHandle( new SequenceDataFactory() ) );
  }

}
