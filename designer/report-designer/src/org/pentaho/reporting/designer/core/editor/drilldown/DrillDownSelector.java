package org.pentaho.reporting.designer.core.editor.drilldown;

import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

/**
* Todo: Document me!
* <p/>
* Date: 05.08.2010
* Time: 13:49:17
*
* @author Thomas Morgner.
*/
public interface DrillDownSelector
{
  public DrillDownUiProfile getSelectedProfile();
  public void setSelectedProfile(DrillDownUiProfile profile);
  public JComponent getComponent();
  public void addChangeListener(ChangeListener changeListener);
  public void removeChangeListener(ChangeListener changeListener);
}
