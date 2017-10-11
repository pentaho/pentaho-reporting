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

package org.pentaho.reporting.ui.datasources.pmd.util;

import org.pentaho.commons.metadata.mqleditor.editor.SwingMqlEditor;
import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.text.JTextComponent;

public class CreateMqlEditorTask implements Runnable {
  private IMetadataDomainRepository repository;
  private DesignTimeContext context;
  private DataSetQuery selectedQuery;
  private JTextComponent queryTextArea;

  public CreateMqlEditorTask( final IMetadataDomainRepository repository,
                              final DesignTimeContext context,
                              final DataSetQuery selectedQuery,
                              final JTextComponent queryTextArea ) {
    this.repository = repository;
    this.context = context;
    this.selectedQuery = selectedQuery;
    this.queryTextArea = queryTextArea;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes
   * the object's <code>run</code> method to be called in that separately executing thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  public void run() {
    try {
      final SwingMqlEditor mqlEditor = new SwingMqlEditor( repository );
      final String queryXml = selectedQuery.getQuery();
      if ( StringUtils.isEmpty( queryXml ) == false ) {
        mqlEditor.setQuery( queryXml );
      }
      mqlEditor.hidePreview();
      mqlEditor.show();
      if ( mqlEditor.getOkClicked() ) {
        final String theQuery = mqlEditor.getQuery();

        if ( theQuery != null ) {
          selectedQuery.setQuery( theQuery );
          queryTextArea.setText( theQuery );
        }
      }
    } catch ( Exception exc ) {
      context.error( exc );
    }

  }
}
