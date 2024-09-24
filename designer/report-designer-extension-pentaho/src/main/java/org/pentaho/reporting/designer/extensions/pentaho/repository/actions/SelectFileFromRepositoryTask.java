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
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryOpenDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.IOException;

public class SelectFileFromRepositoryTask {
  private Component uiContext;
  private RepositoryOpenDialog repositoryBrowserDialog;
  private String[] filters;

  public SelectFileFromRepositoryTask( final Component uiContext ) {
    this.uiContext = uiContext;
  }

  public String[] getFilters() {
    return filters;
  }

  public void setFilters( final String[] filters ) {
    this.filters = filters;
  }

  public String selectFile( final AuthenticationData loginData,
                            final String selectedFile ) throws IOException {
    if ( repositoryBrowserDialog == null ) {
      final Window parent = LibSwingUtil.getWindowAncestor( uiContext );
      if ( parent instanceof Frame ) {
        repositoryBrowserDialog = new RepositoryOpenDialog( (Frame) parent );
      } else if ( parent instanceof Dialog ) {
        repositoryBrowserDialog = new RepositoryOpenDialog( (Dialog) parent );
      } else {
        repositoryBrowserDialog = new RepositoryOpenDialog();
      }

      if ( filters != null ) {
        repositoryBrowserDialog.setFilters( filters );
      }
      LibSwingUtil.centerFrameOnScreen( repositoryBrowserDialog );
    }

    return ( repositoryBrowserDialog.performOpen( loginData, selectedFile ) );
  }
}
