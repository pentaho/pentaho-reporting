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


package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class PentahoDrillDownModuleTest {

  SubSystem subSystem;

  @Before
  public void setUp() throws Exception {
    subSystem = mock( SubSystem.class );
  }

  @Test
  public void testPentahoDrillDownModule() {
    try {
      PentahoDrillDownModule module = new PentahoDrillDownModule();
      assertNotNull( module );
    } catch ( ModuleInitializeException e ) {
      e.printStackTrace();
    }
  }

  @Test
  public void testInitialize() {
    try {
      PentahoDrillDownModule module = new PentahoDrillDownModule();
      assertNotNull( module );
      module.initialize( subSystem );
    } catch ( ModuleInitializeException e ) {
      e.printStackTrace();
    }
  }

}
