package org.pentaho.reporting.designer;

import java.awt.GraphicsEnvironment;

import junit.framework.TestCase;
import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.openformula.ui.FormulaEditorDialog;
import org.pentaho.reporting.designer.core.DefaultReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.util.GUIUtils;
import org.pentaho.reporting.designer.testsupport.TestReportDesignerView;
import org.pentaho.ui.xul.XulException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 08.10.2010
 * Time: 14:56:12
 *
 * @author Thomas Morgner.
 */
public class FormulaDialogTest extends TestCase
{
  public FormulaDialogTest()
  {
  }

  public FormulaDialogTest(final String name)
  {
    super(name);
  }

  public void testGui() throws XulException
  {
    if (GraphicsEnvironment.isHeadless())
    {
      return;
    }
    ReportDesignerBoot.getInstance().start();
    final FormulaEditorDialog dialog =
        GUIUtils.createFormulaEditorDialog(new DefaultReportDesignerContext(null, new TestReportDesignerView()), null);
    dialog.editFormula("=AND(", new FieldDefinition[0]);
  }
}
