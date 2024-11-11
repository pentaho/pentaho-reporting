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


package org.pentaho.reporting.designer.core.util.undo;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class EditReportConfigUndoEntry implements UndoEntry {
  private HashMap oldConfig;
  private HashMap newConfig;

  public EditReportConfigUndoEntry( final HashMap oldConfig, final HashMap newConfig ) {
    this.oldConfig = oldConfig;
    this.newConfig = newConfig;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final HierarchicalConfiguration configuration =
      (HierarchicalConfiguration) renderContext.getContextRoot().getConfiguration();

    final Iterator newEntries = newConfig.entrySet().iterator();
    while ( newEntries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) newEntries.next();
      final String o = (String) entry.getKey();
      configuration.setConfigProperty( o, null );
    }

    final Iterator oldEntries = oldConfig.entrySet().iterator();
    while ( oldEntries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) oldEntries.next();
      final String o = (String) entry.getKey();
      configuration.setConfigProperty( o, (String) entry.getValue() );
    }
    renderContext.getContextRoot().notifyNodePropertiesChanged();
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final HierarchicalConfiguration configuration =
      (HierarchicalConfiguration) renderContext.getContextRoot().getConfiguration();

    final Iterator newEntries = oldConfig.entrySet().iterator();
    while ( newEntries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) newEntries.next();
      final String o = (String) entry.getKey();
      configuration.setConfigProperty( o, null );
    }

    final Iterator oldEntries = newConfig.entrySet().iterator();
    while ( oldEntries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) oldEntries.next();
      final String o = (String) entry.getKey();
      configuration.setConfigProperty( o, (String) entry.getValue() );
    }
    renderContext.getContextRoot().notifyNodePropertiesChanged();
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }
}
