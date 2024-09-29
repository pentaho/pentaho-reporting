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
