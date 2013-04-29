package org.pentaho.reporting.designer.testsupport;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.pentaho.reporting.designer.core.AbstractReportDesignerContext;

public class TestReportDesignerContext extends AbstractReportDesignerContext
{
  public TestReportDesignerContext()
  {
    super(new TestReportDesignerView());
  }

  public Component getParent()
  {
    return null;
  }

  public JPopupMenu getPopupMenu(final String id)
  {
    return null;
  }

  public JComponent getToolBar(final String id)
  {
    return null;
  }
}
