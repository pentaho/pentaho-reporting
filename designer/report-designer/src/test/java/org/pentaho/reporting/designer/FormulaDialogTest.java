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
