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

package org.pentaho.reporting.engine.classic.core.util;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

public class ReportConfigurationTest extends TestCase {
  private static final String key = "org.pentaho.reporting.engine.classic.core.layout.fontrenderer.UseAliasing";

  public ReportConfigurationTest() {
  }

  public ReportConfigurationTest( final String s ) {
    super( s );
  }

  public void testDefaultValueExists() {
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty( key, null );
    final String value = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty( key );
    assertNotNull( value );
  }

  public void testSystemPropertyAccessible() {
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty( key, null );
    final String value = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty( key );
    System.setProperty( key, value + "-sysprop" );
    final String value2 = ClassicEngineBoot.getInstance().getEditableConfig().getConfigProperty( key );
    assertNotNull( value2 );
    assertEquals( value + "-sysprop", value2 );
  }

  public void testLocalReportConfigAccessible() {
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty( key, null );
    final String value = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty( key );
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty( key, value + "-repconf" );
    final String value3 = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty( key );
    assertNotNull( value3 );
    assertEquals( value + "-repconf", value3 );
  }

  protected void tearDown() throws Exception {
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty( key, null );
    System.getProperties().remove( key );
  }
}
