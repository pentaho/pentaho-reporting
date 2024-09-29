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


package org.pentaho.reporting.designer.extensions.wizard;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class WizardModuleTest {

  SubSystem subSystem;

  @Before
  public void setUp() throws Exception {
    subSystem = mock( SubSystem.class );
  }

  @Test
  public void testWizardModule() {
    try {
      WizardModule module = new WizardModule();
      assertNotNull( module );
    } catch ( ModuleInitializeException e ) {
      e.printStackTrace();
    }
  }

  @Test
  public void testInitialize() {
    try {
      WizardModule module = new WizardModule();
      module.initialize( subSystem );
    } catch ( ModuleInitializeException e ) {
      e.printStackTrace();
    }
  }

}
