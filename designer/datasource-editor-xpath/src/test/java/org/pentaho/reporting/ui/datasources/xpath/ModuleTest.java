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


package org.pentaho.reporting.ui.datasources.xpath;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.xpath.XPathDataFactory;

public class ModuleTest extends TestCase {
  public ModuleTest() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testModuleExists() {
    assertTrue(
      ClassicEngineBoot.getInstance().getPackageManager().isModuleAvailable( XPathDataSourceModule.class.getName() ) );
  }

  public void testEditorRegistered() {
    DataSourcePlugin editor =
      DataFactoryRegistry.getInstance().getMetaData( XPathDataFactory.class.getName() ).createEditor();
    assertNotNull( editor );

    assertTrue( editor.canHandle( new XPathDataFactory() ) );
  }

}
