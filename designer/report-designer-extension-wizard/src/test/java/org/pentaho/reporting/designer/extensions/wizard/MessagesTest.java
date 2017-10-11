/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
