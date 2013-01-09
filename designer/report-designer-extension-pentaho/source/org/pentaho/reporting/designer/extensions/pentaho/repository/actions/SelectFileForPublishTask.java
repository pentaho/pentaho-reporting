package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.IOException;

import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryPublishDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class SelectFileForPublishTask
{
  private RepositoryPublishDialog repositoryBrowserDialog;

  public SelectFileForPublishTask(final Component uiContext)
  {
    final Window parent = LibSwingUtil.getWindowAncestor(uiContext);
    if (parent instanceof Frame)
    {
      repositoryBrowserDialog = new RepositoryPublishDialog((Frame) parent);
    }
    else if (parent instanceof Dialog)
    {
      repositoryBrowserDialog = new RepositoryPublishDialog((Dialog) parent);
    }
    else
    {
      repositoryBrowserDialog = new RepositoryPublishDialog();
    }

    LibSwingUtil.centerFrameOnScreen(repositoryBrowserDialog);
  }

  public String selectFile(final AuthenticationData loginData,
                           final String selectedFile) throws IOException
  {
    return (repositoryBrowserDialog.performOpen(loginData, selectedFile));
  }

  public void setExportType(final String exportType)
  {
    repositoryBrowserDialog.setExportType(exportType);
  }

  public String getExportType()
  {
    return repositoryBrowserDialog.getExportType();
  }

  public void setDescription(final String description)
  {
    repositoryBrowserDialog.setDescription(description);
  }

  public String getDescription()
  {
    return repositoryBrowserDialog.getDescription();
  }

  public void setReportTitle(final String title)
  {
    repositoryBrowserDialog.setReportTitle(title);
  }

  public String getReportTitle()
  {
    return repositoryBrowserDialog.getReportTitle();
  }

  public void setLockOutputType(final boolean lock)
  {
    repositoryBrowserDialog.setLockOutputType(lock);
  }

  public boolean isLockOutputType()
  {
    return repositoryBrowserDialog.isLockOutputType();
  }
}
