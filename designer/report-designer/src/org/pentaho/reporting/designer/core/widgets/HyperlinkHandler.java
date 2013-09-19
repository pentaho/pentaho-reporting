package org.pentaho.reporting.designer.core.widgets;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.SwingUtilities;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.util.ExternalToolLauncher;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExceptionDialog;

public class HyperlinkHandler extends MouseAdapter
{
  private String url;
  private Component parent;

  public HyperlinkHandler(final String url, final Component parent)
  {
    this.url = url;
    this.parent = parent;
  }

  public void mouseClicked(final MouseEvent e)
  {
    try
    {
      ExternalToolLauncher.openURL(url);
    }
    catch (IOException ioe)
    {
      SwingUtilities.invokeLater(new ShowErrorMessageTask(parent, ioe));
    }
  }

  private static class ShowErrorMessageTask implements Runnable
  {
    private Component parent;
    private Exception exception;

    private ShowErrorMessageTask(final Component parent,
                                 final Exception exception)
    {
      this.parent = parent;
      this.exception = exception;
    }

    public void run()
    {
      ExceptionDialog.showExceptionDialog(parent,
          Messages.getString("HyperlinkHandler.Error.Title"),
          Messages.getString("HyperlinkHandler.errBrowser", exception.getLocalizedMessage()),
          exception);
    }
  }

}
