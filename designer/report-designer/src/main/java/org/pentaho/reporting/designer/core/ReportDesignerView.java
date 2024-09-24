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

package org.pentaho.reporting.designer.core;

import org.pentaho.reporting.designer.core.xul.ActionSwingMenuitem;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.containers.XulMenupopup;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

public interface ReportDesignerView {
  public static final String REPORT_DESIGNER_VIEW_STATE_PROPERTY = "reportDesignerViewState";
  public static final String PALETTE_VISIBLE_PROPERTY = "paletteVisible";
  public static final String STRUCTURE_VISIBLE_PROPERTY = "structureVisible";
  public static final String PROPERTIES_EDITOR_VISIBLE_PROPERTY = "propertiesEditorVisible";
  public static final String PREVIEW_VISIBLE_PROPERTY = "previewVisible";
  public static final String MESSAGES_VISIBLE_PROPERTY = "messagesVisible";
  public static final String WELCOME_VISIBLE_PROPERTY = "welcomeVisible";
  public static final String FIELD_SELECTOR_VISIBLE_PROPERTY = "fieldSelectorVisible";

  public void addPropertyChangeListener( final PropertyChangeListener listener );

  public void addPropertyChangeListener( final String property, final PropertyChangeListener listener );

  public void removePropertyChangeListener( final PropertyChangeListener listener );

  public void removePropertyChangeListener( final String property, final PropertyChangeListener listener );

  public boolean isStructureVisible();

  public void setStructureVisible( final boolean visible );

  public boolean isPropertiesEditorVisible();

  public void setPropertiesEditorVisible( final boolean visible );

  public boolean isPreviewVisible();

  public void setPreviewVisible( final boolean visible );

  public boolean isMessagesVisible();

  public void setMessagesVisible( final boolean visible );

  public boolean isWelcomeVisible();

  public void setWelcomeVisible( final boolean visible );

  public boolean isFieldSelectorVisible();

  public void setFieldSelectorVisible( final boolean visible );

  public void redrawAll();

  public void showDataTree();

  public Component getParent();

  public JPopupMenu getPopupMenu( final String id );

  public JComponent getToolBar( final String id );

  public <T extends JComponent> T getComponent( String id, Class<T> type );

  public <T extends XulComponent> T getXulComponent( String id, Class<T> type );

  ActionSwingMenuitem createMenuItem( Action action );

  XulMenupopup createPopupMenu( String label, XulComponent parent ) throws XulException;
}
