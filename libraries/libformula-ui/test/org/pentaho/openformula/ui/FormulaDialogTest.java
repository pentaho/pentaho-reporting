/*
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
 * Copyright (c) 2008 - 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.openformula.ui;

import java.awt.GraphicsEnvironment;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormulaDialogTest
{

  @Before
  public void setup()
  {
  }

  @Ignore("this test case is to ensure that we have at least one test case and prevent compiler issues")
  @Test
  public void testNothing()
  {
  }

  @Test
  public void testDialogDefaultProperties()
      throws IllegalAccessException, UnsupportedLookAndFeelException, InstantiationException, ClassNotFoundException
  {
    if (GraphicsEnvironment.isHeadless())
    {
      return;
    }

    UIManager.setLookAndFeel(MetalLookAndFeel.class.getName());
    FieldDefinition mockFieldDefinition = mock(FieldDefinition.class);

    when(mockFieldDefinition.getName()).thenReturn("Name");
    when(mockFieldDefinition.getDisplayName()).thenReturn("Name");
    when(mockFieldDefinition.getIcon()).thenReturn(null);

    final FormulaEditorDialog dialog = new FormulaEditorDialog();
    Assert.assertEquals("java.awt.Dimension[width=778,height=442]", dialog.getMinimumSize().toString());
    Assert.assertEquals("java.awt.Dimension[width=821,height=519]", dialog.getPreferredSize().toString());
    Assert.assertEquals("java.awt.Dimension[width=821,height=519]", dialog.getSize().toString());

    Assert.assertEquals(dialog.editFormula("=IF(condition; TRUE; FALSE)", new FieldDefinition[]{mockFieldDefinition}), "=IF(condition; TRUE; FALSE)");
  }

  @Test
  public void testRunFormulaDialog()
  {
    if (GraphicsEnvironment.isHeadless())
    {
      return;
    }

    final FormulaEditorDialog d = new FormulaEditorDialog();
    d.editFormula("=IF(condition; TRUE; FALSE)", new FieldDefinition[] { new TestFieldDefinition("test")});
  }
}
