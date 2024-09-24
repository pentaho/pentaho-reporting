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
