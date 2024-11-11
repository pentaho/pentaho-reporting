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


package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("We cannot test PublishSettns as it is using the preferences-API and thus is not independent.")
public class PublishSettingsTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testGetInstance() {
    PublishSettings settings = PublishSettings.getInstance();
    assertNotNull( settings );
  }

  @Test
  public void testIsRememberSettings() {
    PublishSettings settings = PublishSettings.getInstance();
    settings.setRememberSettings( false );
    assertFalse( settings.isRememberSettings() );
  }

  @Test
  public void testSetRememberSettings() {
    PublishSettings settings = PublishSettings.getInstance();
    assertFalse( settings.isRememberSettings() );
    settings.setRememberSettings( true );
    assertTrue( settings.isRememberSettings() );
  }

}
