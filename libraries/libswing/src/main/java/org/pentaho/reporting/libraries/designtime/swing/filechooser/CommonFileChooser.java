/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.designtime.swing.filechooser;

import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public interface CommonFileChooser {
  public String getFileType();

  public FileFilter[] getFilters();

  public void setFilters( FileFilter[] filter );

  public File getSelectedFile();

  public void setSelectedFile( File file );

  public boolean isAllowMultiSelection();

  public void setAllowMultiSelection( final boolean allowMultiSelection );

  public File[] getSelectedFiles();

  public void setSelectedFiles( File[] file );

  public boolean showDialog( final Component parent, final int mode );
}
