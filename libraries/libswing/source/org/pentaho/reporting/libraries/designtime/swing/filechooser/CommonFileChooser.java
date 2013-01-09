package org.pentaho.reporting.libraries.designtime.swing.filechooser;

import java.awt.Component;
import java.io.File;
import javax.swing.filechooser.FileFilter;

public interface CommonFileChooser
{
  public String getFileType();

  public FileFilter[] getFilters();

  public void setFilters(FileFilter[] filter);

  public File getSelectedFile();

  public void setSelectedFile(File file);

  public boolean isAllowMultiSelection();

  public void setAllowMultiSelection(final boolean allowMultiSelection);

  public File[] getSelectedFiles();

  public void setSelectedFiles(File[] file);

  public boolean showDialog(final Component parent, final int mode);
}
