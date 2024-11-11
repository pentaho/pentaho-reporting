/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.testsupport;

import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.xul.ActionSwingMenuitem;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.containers.XulMenupopup;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

public class TestReportDesignerView implements ReportDesignerView {
  public TestReportDesignerView() {
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {

  }

  public void addPropertyChangeListener( final String property, final PropertyChangeListener listener ) {

  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {

  }

  public void removePropertyChangeListener( final String property, final PropertyChangeListener listener ) {

  }

  public boolean isStructureVisible() {
    return false;
  }

  public void setStructureVisible( final boolean visible ) {

  }

  public boolean isPropertiesEditorVisible() {
    return false;
  }

  public void setPropertiesEditorVisible( final boolean visible ) {

  }

  public boolean isPreviewVisible() {
    return false;
  }

  public void setPreviewVisible( final boolean visible ) {

  }

  public boolean isMessagesVisible() {
    return false;
  }

  public void setMessagesVisible( final boolean visible ) {

  }

  public boolean isWelcomeVisible() {
    return false;
  }

  public void setWelcomeVisible( final boolean visible ) {

  }

  public boolean isFieldSelectorVisible() {
    return false;
  }

  public void setFieldSelectorVisible( final boolean visible ) {

  }

  public void redrawAll() {

  }

  public void showDataTree() {

  }

  public Component getParent() {
    return null;
  }

  public JPopupMenu getPopupMenu( final String id ) {
    return null;
  }

  public JComponent getToolBar( final String id ) {
    return null;
  }

  public <T extends JComponent> T getComponent( final String id, final Class<T> type ) {
    return null;
  }

  public <T extends XulComponent> T getXulComponent( final String id, final Class<T> type ) {
    return null;
  }

  public ActionSwingMenuitem createMenuItem( final Action action ) {
    return null;
  }

  public XulMenupopup createPopupMenu( final String label, final XulComponent parent ) throws XulException {
    return null;
  }
}
