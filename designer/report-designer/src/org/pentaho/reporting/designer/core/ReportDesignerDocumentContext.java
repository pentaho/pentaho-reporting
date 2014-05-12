/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.designer.core;

import java.beans.PropertyChangeListener;
import javax.swing.Icon;

import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;

public interface ReportDesignerDocumentContext<T>
{
  UndoManager getUndo();
  DocumentContextSelectionModel getSelectionModel();

  T getContextRoot();
  String getTabName();
  Icon getIcon();
  void dispose();
  String getDocumentFile();

  void addPropertyChangeListener(final PropertyChangeListener listener);
  void addPropertyChangeListener(final String property, final PropertyChangeListener listener);
  void removePropertyChangeListener(final PropertyChangeListener listener);
  void removePropertyChangeListener(final String property, final PropertyChangeListener listener);

  void onDocumentActivated();

  void removeInspectionListener(InspectionResultListener listener);
  void addInspectionListener(InspectionResultListener listener);

  AuthenticationStore getAuthenticationStore();

  boolean isChanged();
}
