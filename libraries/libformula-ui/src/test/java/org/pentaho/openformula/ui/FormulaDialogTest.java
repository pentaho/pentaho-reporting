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

package org.pentaho.openformula.ui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.pentaho.reporting.libraries.base.util.DebugLog;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormulaDialogTest {

  @Before
  public void setup() {
  }

  @Test
  public void testDialogDefaultProperties()
    throws IllegalAccessException, UnsupportedLookAndFeelException, InstantiationException, ClassNotFoundException {
    if ( GraphicsEnvironment.isHeadless() ) {
      return;
    }

    UIManager.setLookAndFeel( MetalLookAndFeel.class.getName() );
    final FieldDefinition mockFieldDefinition = mock( FieldDefinition.class );

    when( mockFieldDefinition.getName() ).thenReturn( "Name" );
    when( mockFieldDefinition.getDisplayName() ).thenReturn( "Name" );
    when( mockFieldDefinition.getIcon() ).thenReturn( null );

    final FormulaEditorDialog dialog = new FormulaEditorDialog();
    final Dimension minimumSize = dialog.getMinimumSize();
    Assert.assertTrue( minimumSize.getWidth() > 700 );
    Assert.assertTrue( minimumSize.getHeight() > 400 );
    final Dimension size = dialog.getPreferredSize();
    Assert.assertTrue( size.getWidth() > 700 );
    Assert.assertTrue( size.getHeight() > 400 );

    Assert
      .assertEquals( dialog.editFormula( "=IF(condition; TRUE; FALSE)", new FieldDefinition[] { mockFieldDefinition } ),
        "=IF(condition; TRUE; FALSE)" );
  }

  @Test
  public void testRunFormulaDialog() throws IOException {
    if ( GraphicsEnvironment.isHeadless() ) {
      return;
    }

    final Enumeration<URL> resources = getClass().getClassLoader().getResources( "simplelog.properties" );
    while ( resources.hasMoreElements() ) {
      URL url = resources.nextElement();
      System.out.println( url );
    }
    DebugLog.logHere();
    final FormulaEditorDialog d = new FormulaEditorDialog();
    d.editFormula( "=IF(condition; TRUE; FALSE)", new FieldDefinition[] { new TestFieldDefinition( "test" ) } );
  }
}
