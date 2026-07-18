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



package org.pentaho.reporting.designer.core;

import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public interface ReportDesignerDocumentContext<T> {
  UndoManager getUndo();

  DocumentContextSelectionModel getSelectionModel();

  T getContextRoot();

  String getTabName();

  Icon getIcon();

  void dispose();

  String getDocumentFile();

  void addPropertyChangeListener( final PropertyChangeListener listener );

  void addPropertyChangeListener( final String property, final PropertyChangeListener listener );

  void removePropertyChangeListener( final PropertyChangeListener listener );

  void removePropertyChangeListener( final String property, final PropertyChangeListener listener );

  void onDocumentActivated();

  void removeInspectionListener( InspectionResultListener listener );

  void addInspectionListener( InspectionResultListener listener );

  AuthenticationStore getAuthenticationStore();

  boolean isChanged();
}
