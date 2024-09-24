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

package org.pentaho.reporting.engine.classic.extensions.modules.java14config;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigFactory;

public class Java14ConfigModuleInitializerTest {

  @Test
  public void testPerformInit() throws Exception {
    Java14ConfigModuleInitializer initializer = new Java14ConfigModuleInitializer();
    initializer.performInit();

    assertThat( ConfigFactory.getInstance().getUserStorage(), is( instanceOf( Java14ConfigStorage.class ) ) );
    assertThat( ConfigFactory.getInstance().getSystemStorage(), is( instanceOf( Java14ConfigStorage.class ) ) );
  }
}
