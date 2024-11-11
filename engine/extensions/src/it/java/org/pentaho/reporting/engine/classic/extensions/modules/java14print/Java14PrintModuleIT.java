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


package org.pentaho.reporting.engine.classic.extensions.modules.java14print;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.boot.ModuleInfo;

public class Java14PrintModuleIT {

  private static final String[] MODULE_REQUIRED_CLASSESS = new String[] {
    "org.pentaho.reporting.engine.classic.core.modules.gui.print.AWTPrintingGUIModule",
    "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.Graphics2DPageableModule" };

  @BeforeClass
  public static void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testInitialize() throws Exception {
    Java14PrintModule module = new Java14PrintModule();
    module.initialize( null );

    assertThat( module.getDescription(), is( equalTo( "A dialog component for the printing (JDK 1.4 style)." ) ) );
    assertThat( module.getMajorVersion(), is( equalTo( "3" ) ) );
    assertThat( module.getMinorVersion(), is( equalTo( "5" ) ) );
    assertThat( module.getPatchLevel(), is( equalTo( "0" ) ) );
    assertThat( module.getName(), is( equalTo( "gui-swing-print-jdk14" ) ) );
    assertThat( module.getProducer(), is( equalTo( "The Pentaho Reporting Project" ) ) );
    assertThat( module.getSubSystem(), is( equalTo( "printing-gui" ) ) );

    ModuleInfo[] requiredModules = module.getRequiredModules();
    assertThat( requiredModules.length, is( equalTo( 2 ) ) );
    assertThat( new String[] { requiredModules[0].getModuleClass(), requiredModules[1].getModuleClass() },
        is( arrayContainingInAnyOrder( MODULE_REQUIRED_CLASSESS ) ) );
  }

}
