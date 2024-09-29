/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
