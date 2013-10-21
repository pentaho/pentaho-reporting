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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.core;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;

public interface ReportDesignerView
{
  public static final String REPORT_DESIGNER_VIEW_STATE_PROPERTY = "reportDesignerViewState";
  public static final String PALETTE_VISIBLE_PROPERTY = "paletteVisible";
  public static final String STRUCTURE_VISIBLE_PROPERTY = "structureVisible";
  public static final String PROPERTIES_EDITOR_VISIBLE_PROPERTY = "propertiesEditorVisible";
  public static final String PREVIEW_VISIBLE_PROPERTY = "previewVisible";
  public static final String MESSAGES_VISIBLE_PROPERTY = "messagesVisible";
  public static final String WELCOME_VISIBLE_PROPERTY = "welcomeVisible";
  public static final String FIELD_SELECTOR_VISIBLE_PROPERTY = "fieldSelectorVisible";

  public void addPropertyChangeListener(final PropertyChangeListener listener);

  public void addPropertyChangeListener(final String property, final PropertyChangeListener listener);

  public void removePropertyChangeListener(final PropertyChangeListener listener);

  public void removePropertyChangeListener(final String property, final PropertyChangeListener listener);

  public boolean isStructureVisible();

  public void setStructureVisible(final boolean visible);

  public boolean isPropertiesEditorVisible();

  public void setPropertiesEditorVisible(final boolean visible);

  public boolean isPreviewVisible();

  public void setPreviewVisible(final boolean visible);

  public boolean isMessagesVisible();

  public void setMessagesVisible(final boolean visible);

  public boolean isWelcomeVisible();

  public void setWelcomeVisible(final boolean visible);

  public boolean isFieldSelectorVisible();

  public void setFieldSelectorVisible(final boolean visible);

  public void redrawAll();

  public void showDataTree();
}
