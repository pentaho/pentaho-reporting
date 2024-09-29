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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.MissingResourceException;

import javax.swing.KeyStroke;

import org.junit.Before;
import org.junit.Test;

public class MessagesTest {
  static {
    System.setProperty( "java.awt.headless", "true" );
  }

  private static String key = "AnyKey";

  @Before
  public void setUp() throws Exception {
  }

  @Test( expected = MissingResourceException.class )
  public void testGetIcon() {
    Messages.getIcon( key );
  }

  @Test( expected = MissingResourceException.class )
  public void testGetIconLarge() {
    Messages.getIcon( key, true );
  }

  @Test( expected = MissingResourceException.class )
  public void testGetMnemonic() {
    Messages.getMnemonic( key );
  }

  @Test( expected = MissingResourceException.class )
  public void testGetKeyStroke() {
    Messages.getKeyStroke( key );
  }

  @Test
  public void testGetOptionalKeyStroke() {
    KeyStroke result = Messages.getOptionalKeyStroke( key );
    assertNull( result );
  }

  @Test( expected = MissingResourceException.class )
  public void testGetKeyStrokeWithMask() {
    KeyStroke result = Messages.getKeyStroke( key, 1 );
    assertNull( result );
  }

  @Test
  public void testGetOptionalKeyStrokeWithMask() {
    KeyStroke result = Messages.getOptionalKeyStroke( key, 1 );
    assertNull( result );
  }

  @Test
  public void testGetString() {
    String result = Messages.getString( "NewWizardReportAction.ButtonTitle" );
    assertNotNull( result );
  }

}
