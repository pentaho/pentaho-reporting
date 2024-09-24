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

package org.pentaho.reporting.engine.classic.extensions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class ClassicEngineExtensionsInfoTest {

  @Test
  public void testInfo() {
    ProjectInformation info = ClassicEngineExtensionsInfo.getInstance();
    assertThat( info, is( notNullValue() ) );
    assertThat( info.getInfo(), is( equalTo( "http://reporting.pentaho.org/" ) ) );
    assertThat( info.getCopyright(), is( equalTo( "(C)opyright 2000-2011, by Pentaho Corp. and Contributors" ) ) );
    assertThat( info.getLicenseName(), is( equalTo( "LGPL" ) ) );
    assertThat( info.getName(), is( equalTo( "Pentaho Reporting Classic Extensions" ) ) );
  }
}
