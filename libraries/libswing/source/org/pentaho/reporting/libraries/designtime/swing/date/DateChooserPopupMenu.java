package org.pentaho.reporting.libraries.designtime.swing.date;

import javax.swing.JPopupMenu;

import org.pentaho.reporting.libraries.base.util.DebugLog;

public class DateChooserPopupMenu extends JPopupMenu
{
  private DateChooserPanel dateChooserPanel;

  public DateChooserPopupMenu(final DateChooserPanel dateChooserPanel)
  {
    this.dateChooserPanel = dateChooserPanel;
  }

  public void setVisible(final boolean b)
  {
    final Boolean isCanceled = (Boolean) getClientProperty("JPopupMenu.firePopupMenuCanceled");
    if (b)
    {
      super.setVisible(true);
    }
    else if (dateChooserPanel.isDateSelected() || Boolean.TRUE.equals(isCanceled))
    {
      super.setVisible(false);
    }
    else
    {
      DebugLog.log("Ignoring close request: isDateSelected=" +
          dateChooserPanel.isDateSelected() + "; isCanceled=" + isCanceled);
    }
  }
}
