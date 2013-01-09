package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.IOException;

import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryOpenDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class SelectFileFromRepositoryTask
{
  private Component uiContext;
  private RepositoryOpenDialog repositoryBrowserDialog;
  private String[] filters;

  public SelectFileFromRepositoryTask(final Component uiContext)
  {
    this.uiContext = uiContext;
  }

  public String[] getFilters()
  {
    return filters;
  }

  public void setFilters(final String[] filters)
  {
    this.filters = filters;
  }

  public String selectFile(final AuthenticationData loginData,
                           final String selectedFile) throws IOException
  {
    if (repositoryBrowserDialog == null)
    {
      final Window parent = LibSwingUtil.getWindowAncestor(uiContext);
      if (parent instanceof Frame)
      {
        repositoryBrowserDialog = new RepositoryOpenDialog((Frame) parent);
      }
      else if (parent instanceof Dialog)
      {
        repositoryBrowserDialog = new RepositoryOpenDialog((Dialog) parent);
      }
      else
      {
        repositoryBrowserDialog = new RepositoryOpenDialog();
      }

      if (filters != null)
      {
        repositoryBrowserDialog.setFilters(filters);
      }
      LibSwingUtil.centerFrameOnScreen(repositoryBrowserDialog);
    }

    return (repositoryBrowserDialog.performOpen(loginData, selectedFile));
  }
}
