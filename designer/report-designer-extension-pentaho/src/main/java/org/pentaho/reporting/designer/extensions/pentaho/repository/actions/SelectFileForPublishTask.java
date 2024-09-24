/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryPublishDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.IOException;

public class SelectFileForPublishTask {
  private RepositoryPublishDialog repositoryBrowserDialog;

  public SelectFileForPublishTask( final Component uiContext ) {
    final Window parent = LibSwingUtil.getWindowAncestor( uiContext );
    if ( parent instanceof Frame ) {
      repositoryBrowserDialog = new RepositoryPublishDialog( (Frame) parent );
    } else if ( parent instanceof Dialog ) {
      repositoryBrowserDialog = new RepositoryPublishDialog( (Dialog) parent );
    } else {
      repositoryBrowserDialog = new RepositoryPublishDialog();
    }

    LibSwingUtil.centerFrameOnScreen( repositoryBrowserDialog );
  }

  public String selectFile( final AuthenticationData loginData,
                            final String selectedFile ) throws IOException {
    return ( repositoryBrowserDialog.performOpen( loginData, selectedFile ) );
  }

  public void setExportType( final String exportType ) {
    repositoryBrowserDialog.setExportType( exportType );
  }

  public String getExportType() {
    return repositoryBrowserDialog.getExportType();
  }

  public void setDescription( final String description ) {
    repositoryBrowserDialog.setDescription( description );
  }

  public String getDescription() {
    return repositoryBrowserDialog.getDescription();
  }

  public void setReportTitle( final String title ) {
    repositoryBrowserDialog.setReportTitle( title );
  }

  public String getReportTitle() {
    return repositoryBrowserDialog.getReportTitle();
  }

  public void setLockOutputType( final boolean lock ) {
    repositoryBrowserDialog.setLockOutputType( lock );
  }

  public boolean isLockOutputType() {
    return repositoryBrowserDialog.isLockOutputType();
  }
}
