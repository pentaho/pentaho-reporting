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

package org.pentaho.reporting.designer;

import junit.framework.TestCase;
import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.openformula.ui.FormulaEditorDialog;
import org.pentaho.reporting.designer.core.DefaultReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.util.GUIUtils;
import org.pentaho.reporting.designer.testsupport.TestReportDesignerView;
import org.pentaho.ui.xul.XulException;

import java.awt.*;

/**
 * Todo: Document me!
 * <p/>
 * Date: 08.10.2010 Time: 14:56:12
 *
 * @author Thomas Morgner.
 */
public class FormulaDialogTest extends TestCase {
  public FormulaDialogTest() {
  }

  public FormulaDialogTest( final String name ) {
    super( name );
  }

  public void testGui() throws XulException {
    if ( GraphicsEnvironment.isHeadless() ) {
      return;
    }
    ReportDesignerBoot.getInstance().start();
    final FormulaEditorDialog dialog =
      GUIUtils.createFormulaEditorDialog( new DefaultReportDesignerContext( new TestReportDesignerView() ), null );
    dialog.editFormula( "=AND(", new FieldDefinition[ 0 ] );
  }
}
