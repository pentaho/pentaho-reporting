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

package org.pentaho.reporting.designer.core.status;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.inspections.InspectionResult;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Todo: Document me!
 * <p/>
 * Date: 26.04.2009 Time: 12:39:20
 *
 * @author Thomas Morgner.
 */
public class MessagesStatusGadget extends JLabel implements InspectionResultListener {
  private static class ClearWarningMessageAction implements ActionListener {
    private final JLabel label;

    private ClearWarningMessageAction( final JLabel label ) {
      this.label = label;
    }

    public void actionPerformed( final ActionEvent e ) {
      final Container container = label.getParent();
      if ( container != null ) {
        container.remove( label );
        container.repaint();
      }
    }
  }

  private class ActiveContextChangeListener implements PropertyChangeListener {
    private ActiveContextChangeListener() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      final ReportRenderContext oldContext = (ReportRenderContext) evt.getOldValue();
      final ReportRenderContext activeContext = (ReportRenderContext) evt.getNewValue();
      updateActiveContext( oldContext, activeContext );
    }
  }

  private boolean firstTime;
  private ImageIcon errorIcon;
  private ImageIcon noErrorIcon;
  private ReportDesignerContext designerContext;

  public MessagesStatusGadget( final ReportDesignerContext designerContext ) {
    this.designerContext = designerContext;
    firstTime = true;
    errorIcon = IconLoader.getInstance().getWarningIcon();
    noErrorIcon = IconLoader.getInstance().getNoErrorIcon();

    setHorizontalAlignment( JLabel.CENTER );
    setIcon( noErrorIcon );
    setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

    addMouseListener( new MouseSelectionHandler() );

    final ActiveContextChangeListener changeHandler = new ActiveContextChangeListener();

    designerContext.addPropertyChangeListener( ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, changeHandler );
    updateActiveContext( null, designerContext.getActiveContext() );
  }

  private void updateActiveContext( final ReportDocumentContext oldContext,
                                    final ReportDocumentContext activeContext ) {
    if ( oldContext != null ) {
      oldContext.removeInspectionListener( this );
    }
    if ( activeContext != null ) {
      activeContext.addInspectionListener( this );
    }
  }

  protected void handleExceptionClick() {
    setIcon( IconLoader.getInstance().getNoErrorIcon() );

    designerContext.getView().setMessagesVisible( true );
  }

  private class MouseSelectionHandler extends MouseAdapter {
    @Override
    public void mouseClicked( final MouseEvent ex ) {
      handleExceptionClick();
    }
  }


  private JLabel createPopup() {
    final JLabel label = new JLabel( Messages.getString( "StatusBar.Inspection.Message" ) );
    label.setForeground( new Color( 0, 0, 0 ) );
    label.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( Color.YELLOW ),
      BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) ) );
    label.setBackground( new Color( 255, 255, 0, 125 ) );
    label.setOpaque( true );

    final Timer t = new Timer( 5000, new ClearWarningMessageAction( label ) );
    t.setRepeats( false );
    t.start();
    return label;
  }

  public void notifyInspectionStarted() {
    // ignore ...
  }

  public void notifyInspectionResult( final InspectionResult result ) {
    if ( firstTime ) {
      firstTime = false;

      Component rootPane = getParent();
      while ( rootPane != null && !( rootPane instanceof JRootPane ) ) {
        rootPane = rootPane.getParent();
      }
      if ( rootPane != null ) {
        final JRootPane jRootPane = (JRootPane) rootPane;

        final JLabel errorPopup = createPopup();
        final JLayeredPane jLayeredPane = jRootPane.getLayeredPane();
        jLayeredPane.add( errorPopup, JLayeredPane.POPUP_LAYER );

        final Rectangle rectangle = SwingUtilities.convertRectangle
          ( getParent(), getBounds(), jRootPane.getLayeredPane() );
        final Dimension dimension = errorPopup.getPreferredSize();
        errorPopup.setBounds( (int) ( rectangle.getX() - dimension.width ),
          (int) ( rectangle.getY() - dimension.height ), dimension.width, dimension.height );
        jRootPane.getLayeredPane().revalidate();
        jRootPane.getLayeredPane().repaint();
      }
    }
    if ( getIcon() == noErrorIcon ) {
      setIcon( errorIcon );
    }
    repaint();
  }
}

