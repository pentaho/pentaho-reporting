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

package org.pentaho.reporting.designer.core.editor.report.lineal;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.lineal.GuideLine;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.model.lineal.LinealModelEvent;
import org.pentaho.reporting.designer.core.model.lineal.LinealModelListener;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.CanvasImageLoader;
import org.pentaho.reporting.designer.core.util.GuideLineDialog;
import org.pentaho.reporting.designer.core.util.Unit;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.libraries.designtime.swing.ColorUtility;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.text.DecimalFormat;

/**
 * A horizontal lineal that is displayed on top of the editor pane. The horizontal lineal model is global for the whole
 * report.
 */
public class HorizontalLinealComponent extends JPanel {
  private class LinealUpdateHandler implements LinealModelListener {
    private LinealUpdateHandler() {
    }

    public void modelChanged( final LinealModelEvent event ) {
      repaint();
    }
  }

  private class DragnDropHandler extends MouseAdapter implements MouseMotionListener {
    private int draggedGuideLineIndex;

    private DragnDropHandler() {
    }

    public void mouseDragged( final MouseEvent e ) {
      final PageDefinition pageDefinition = getPageDefinition();
      if ( pageDefinition == null ) {
        return;
      }

      final LinealModel linealModel = getLinealModel();
      if ( draggedGuideLineIndex == -1 || ( ( linealModel.getGuideLineCount() - 1 ) < draggedGuideLineIndex ) ) {
        return;
      }
      if ( draggedGuideLineIndex != getActiveGuideLineIndex() ) {
        setActiveGuideLineIndex( draggedGuideLineIndex );
      }
      final GuideLine dragged = linealModel.getGuideLine( draggedGuideLineIndex );

      double start = 0;
      if ( isShowLeftBorder() ) {
        start = getLeftBorder();
      }
      final double width = pageDefinition.getWidth();
      final float scaleFactor = getZoomAsMicropoints();
      final double scaledPosition = ( e.getX() / scaleFactor ) - start;
      final double position = Math.min( width, Math.max( (double) 0, scaledPosition ) );
      final GuideLine newGuideLine = new GuideLine( position, dragged.isActive() );
      final UndoManager undo = getRenderContext().getUndo();
      undo.addChange( Messages.getString( "LinealComponent.ChangeGuideUndoName" ),
        new UpdateHorizontalGuidelineUndoEntry( draggedGuideLineIndex, newGuideLine, dragged ) );
      linealModel.updateGuideLine( draggedGuideLineIndex, newGuideLine );
    }

    public void mouseMoved( final MouseEvent e ) {
      updateGuidelineHighlight( e );
    }

    public void mouseClicked( final MouseEvent e ) {
      final PageDefinition pageDefinition = getPageDefinition();
      if ( pageDefinition == null ) {
        return;
      }

      double start = 0;
      if ( isShowLeftBorder() ) {
        start = getLeftBorder();
      }
      final int activeGuideIndex = getActiveGuideIndex( e );
      if ( activeGuideIndex != -1 ) {
        return;
      }

      final LinealModel linealModel = getLinealModel();
      final float scaleFactor = getZoomAsMicropoints();
      final double width = pageDefinition.getWidth();
      final double scaledPosition = ( e.getX() / scaleFactor ) - start;
      final double position = Math.min( width, Math.max( (double) 0, scaledPosition ) );
      final GuideLine guideLine = new GuideLine( position, e.getButton() == MouseEvent.BUTTON1 );

      final UndoManager undo = getRenderContext().getUndo();
      undo.addChange( Messages.getString( "LinealComponent.AddGuideUndoName" ),
        new AddHorizontalGuidelinesUndoEntry( guideLine ) );
      linealModel.addGuidLine( guideLine );
    }

    public void mousePressed( final MouseEvent e ) {
      if ( e.getButton() == MouseEvent.BUTTON1 ) {
        draggedGuideLineIndex = getActiveGuideIndex( e );
        if ( draggedGuideLineIndex == -1 ) {
          setActiveGuideLineIndex( -1 );
        } else {
          setActiveGuideLineIndex( draggedGuideLineIndex );
        }
      }
    }

    public void mouseReleased( final MouseEvent e ) {
      setActiveGuideLineIndex( -1 );
    }
  }

  private class LinealPropertiesAction extends AbstractAction {
    private GuideLine guideLine;
    private int index;

    private LinealPropertiesAction( final GuideLine guideLine, final int index ) {
      super( Messages.getString( "LinealComponent.Properties" ) );
      this.guideLine = guideLine;
      this.index = index;
    }

    public void actionPerformed( final ActionEvent e ) {
      final LinealModel linealModel = getLinealModel();
      if ( linealModel == null ) {
        return;
      }

      final GuideLineDialog spinnerDialog;
      final Component parent = HorizontalLinealComponent.this;
      final Window window = LibSwingUtil.getWindowAncestor( parent );
      if ( window instanceof JDialog ) {
        spinnerDialog = new GuideLineDialog( (JDialog) window );
      } else if ( window instanceof JFrame ) {
        spinnerDialog = new GuideLineDialog( (JFrame) window );
      } else {
        spinnerDialog = new GuideLineDialog();
      }

      spinnerDialog.setUnit( WorkspaceSettings.getInstance().getUnit() );
      spinnerDialog.setPosition( guideLine.getPosition() );

      if ( spinnerDialog.showDialog() ) {
        final GuideLine newGuideLine = new GuideLine( spinnerDialog.getPosition(), guideLine.isActive() );
        linealModel.updateGuideLine( index, newGuideLine );
        this.guideLine = newGuideLine;
      }
    }
  }

  private class DeactivateGuidelineAction extends AbstractAction {
    private final GuideLine guideLine;
    private int index;

    private DeactivateGuidelineAction( final GuideLine guideLine, final int index ) {
      super( Messages.getString( "LinealComponent.Deactivate" ) );
      this.guideLine = guideLine;
      this.index = index;
    }

    public void actionPerformed( final ActionEvent e ) {
      final GuideLine newGuideLine = new GuideLine( guideLine.getPosition(), false );
      final LinealModel linealModel = getLinealModel();
      final UndoManager undo = getRenderContext().getUndo();
      undo.addChange( Messages.getString( "LinealComponent.DeactivateGuideUndoEntry" ),
        new UpdateHorizontalGuidelineUndoEntry( index, newGuideLine, guideLine ) );
      linealModel.updateGuideLine( index, newGuideLine );
    }
  }

  private class ActivateGuidelineAction extends AbstractAction {
    private final GuideLine oldGuideLine;
    private int index;

    private ActivateGuidelineAction( final GuideLine guideLine, final int index ) {
      super( Messages.getString( "LinealComponent.Activate" ) );
      this.oldGuideLine = guideLine;
      this.index = index;
    }

    public void actionPerformed( final ActionEvent e ) {
      final GuideLine newGuideLine = new GuideLine( oldGuideLine.getPosition(), true );
      final UndoManager undo = getRenderContext().getUndo();
      final LinealModel linealModel = getLinealModel();
      undo.addChange( Messages.getString( "LinealComponent.ActivateGuideUndoEntry" ),
        new UpdateHorizontalGuidelineUndoEntry( index, newGuideLine, oldGuideLine ) );
      linealModel.updateGuideLine( index, newGuideLine );
    }
  }

  private class DeleteGuidelineAction extends AbstractAction {
    private final GuideLine guideLine;

    private DeleteGuidelineAction( final GuideLine guideLine ) {
      super( Messages.getString( "LinealComponent.Delete" ) );
      this.guideLine = guideLine;
    }

    public void actionPerformed( final ActionEvent e ) {
      final LinealModel linealModel = getLinealModel();
      final UndoManager undo = getRenderContext().getUndo();
      undo.addChange( Messages.getString( "LinealComponent.DeleteGuideUndoName" ),
        new RemoveHorizontalGuidelineUndoEntry( guideLine ) );
      linealModel.removeGuideLine( guideLine );
    }
  }

  private class GuideLinePopupHandler extends MouseAdapter {
    public void mouseEntered( final MouseEvent e ) {
      updateGuidelineHighlight( e );
    }

    public void mouseExited( final MouseEvent e ) {
      if ( getActiveGuidLine() != null ) {
        setActiveGuideLineIndex( -1 );
        repaint();
      }
    }

    public void mouseClicked( final MouseEvent e ) {
      updateGuidelineHighlight( e );
      tryShowPopup( e );
    }

    public void mousePressed( final MouseEvent e ) {
      updateGuidelineHighlight( e );
      tryShowPopup( e );
    }

    public void mouseReleased( final MouseEvent e ) {
      updateGuidelineHighlight( e );
      tryShowPopup( e );
    }
  }

  private class PageFormatUpdateHandler implements ReportModelListener {
    private PageFormatUpdateHandler() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      if ( event.getElement() == event.getReport() ) {
        refresh();
      }
    }
  }

  private class UnitSettingsListener implements SettingsListener {
    private UnitSettingsListener() {
    }

    public void settingsChanged() {
      refresh();
    }
  }

  public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat( "0.0##" );
  public static final DecimalFormat DECIMAL_FORMAT_NUMBERS_ONE_DIGIT = new DecimalFormat( "0.0" );
  public static final DecimalFormat DECIMAL_FORMAT_NUMBERS_INTEGER = new DecimalFormat( "0" );

  private LinealModel linealModel;
  private int activeGuideLineIndex;
  private boolean showLeftBorder;
  private PageDefinition pageDefinition;
  private ReportDocumentContext renderContext;
  private HorizontalLinealComponent.LinealUpdateHandler linealModelListener;

  public HorizontalLinealComponent( final ReportRenderContext renderContext, final boolean showLeftBorder ) {
    if ( renderContext == null ) {
      throw new NullPointerException();
    }

    this.renderContext = renderContext;
    this.renderContext.getContextRoot().addReportModelListener( new PageFormatUpdateHandler() );
    this.linealModelListener = new LinealUpdateHandler();

    this.showLeftBorder = showLeftBorder;

    final DragnDropHandler dndHandler = new DragnDropHandler();
    addMouseListener( dndHandler );
    addMouseMotionListener( dndHandler );

    addMouseListener( new GuideLinePopupHandler() );

    WorkspaceSettings.getInstance().addSettingsListener( new UnitSettingsListener() );

    refresh();
  }

  public PageDefinition getPageDefinition() {
    return pageDefinition;
  }

  protected void refresh() {
    if ( linealModel != null ) {
      this.linealModel.removeLinealModelListener( linealModelListener );
    }

    this.pageDefinition = renderContext.getContextRoot().getPageDefinition();
    final AbstractReportDefinition abstractReportDefinition = this.renderContext.getReportDefinition();
    this.linealModel = ModelUtility.getHorizontalLinealModel( abstractReportDefinition );
    this.linealModel.addLinealModelListener( linealModelListener );
    revalidate();
    repaint();
  }

  protected void tryShowPopup( final MouseEvent me ) {
    if ( !me.isPopupTrigger() ) {
      return;
    }

    double start = 0;
    if ( pageDefinition == null ) {
      return;
    }

    if ( showLeftBorder ) {
      start = getLeftBorder();
    }

    final GuideLine[] guideLines = linealModel.getGuideLines();
    for ( int i = 0; i < guideLines.length; i++ ) {
      final GuideLine guideLine = guideLines[ i ];
      final int x = (int) ( ( guideLine.getPosition() + start ) * getZoomAsMicropoints() );

      if ( x <= me.getX() + 2 && x >= me.getX() - 2 ) {
        final JPopupMenu popupMenu = createPopupMenu( guideLine, i );
        popupMenu.show( HorizontalLinealComponent.this, me.getX(), me.getY() );
        break;
      }
    }
  }

  private JPopupMenu createPopupMenu( final GuideLine guideLine, final int index ) {
    final JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.add( new LinealPropertiesAction( guideLine, index ) );
    if ( guideLine.isActive() ) {
      popupMenu.add( new DeactivateGuidelineAction( guideLine, index ) );
    } else {
      popupMenu.add( new ActivateGuidelineAction( guideLine, index ) );
    }

    popupMenu.add( new DeleteGuidelineAction( guideLine ) );
    return popupMenu;
  }

  protected int getActiveGuideIndex( final MouseEvent e ) {
    if ( pageDefinition == null ) {
      setToolTipText( null );
      return -1;
    }

    final GuideLine[] lines = linealModel.getGuideLines();
    final Unit unit = WorkspaceSettings.getInstance().getUnit();
    for ( int i = 0; i < lines.length; i++ ) {
      final GuideLine guideLine = lines[ i ];
      double start = 0;
      if ( showLeftBorder ) {
        start = getLeftBorder();
      }
      final int x = (int) ( ( guideLine.getPosition() + start ) * getZoomAsMicropoints() );
      if ( x <= e.getX() + 2 && x >= e.getX() - 2 ) {
        final double unitValue = unit.convertFromPoints( guideLine.getPosition() );
        setToolTipText( DECIMAL_FORMAT.format( unitValue ) );
        return i;
      }
    }

    setToolTipText( null );
    return -1;
  }

  public Dimension getPreferredSize() {
    if ( pageDefinition != null ) {
      double end = pageDefinition.getWidth();
      end += getLeftBorder();
      end += getRightBorder();
      return new Dimension( (int) ( end * getZoomAsMicropoints() ), 15 );
    } else {
      return new Dimension( 0, 15 );
    }
  }

  protected void paintComponent( final Graphics g ) {
    super.paintComponent( g );
    if ( pageDefinition == null ) {
      return;
    }

    final double start = getLeftBorder();
    final float scaleFactor = getZoomAsMicropoints();

    double end = pageDefinition.getWidth();
    end += getLeftBorder();
    g.setColor( Color.WHITE );
    g.fillRect( 0, 0, (int) ( ( end - start ) * scaleFactor ), getHeight() );

    g.setColor( Color.LIGHT_GRAY );
    g.drawLine( 0, 0, 0, getHeight() - 1 );
    g.drawLine( 0, getHeight() - 1, (int) ( ( end - start ) * scaleFactor ), getHeight() - 1 );


    final ImageIcon rightBorder = CanvasImageLoader.getInstance().getRightShadowImage();
    g.drawImage( rightBorder.getImage(), (int) ( ( end - start ) * scaleFactor ), 0, rightBorder.getIconWidth(),
      getHeight(), null );

    drawGuideLines( g );
    drawDots( g, start, end );
    drawNumbers( g, start, end );
  }

  protected double getLeftBorder() {
    if ( pageDefinition == null ) {
      return 0;
    }
    if ( isShowLeftBorder() == false ) {
      return 0;
    }
    final PageFormat pageFormat = pageDefinition.getPageFormat( 0 );
    final PageFormatFactory pageFormatFactory = PageFormatFactory.getInstance();
    return pageFormatFactory.getLeftBorder( pageFormat.getPaper() );
  }

  protected double getRightBorder() {
    if ( pageDefinition == null ) {
      return 0;
    }
    final PageFormat pageFormat = pageDefinition.getPageFormat( 0 );
    final PageFormatFactory pageFormatFactory = PageFormatFactory.getInstance();
    return pageFormatFactory.getRightBorder( pageFormat.getPaper() );
  }

  private void drawGuideLines( final Graphics g ) {
    final GuideLine[] guideLines = linealModel.getGuideLines();
    double startOffset = 0;
    if ( showLeftBorder ) {
      startOffset = getLeftBorder();
    }

    final Color guideColor = WorkspaceSettings.getInstance().getGuideColor();
    final Color guideFill = ColorUtility.convertToGray( guideColor, 0.3f );
    final Color guideHighlight = ColorUtility.convertToDarker( guideFill );

    final Color disabledGuideColor = ColorUtility.convertToDarker( ColorUtility.convertToGray( guideColor, 0 ) );
    final Color disabledGuideFill = ColorUtility.convertToDarker( ColorUtility.convertToGray( guideFill, 0 ) );
    final Color disabledHighlightGuide =
      ColorUtility.convertToDarker( ColorUtility.convertToGray( guideHighlight, 0 ) );

    final float scaleFactor = getZoomAsMicropoints();
    final int so = (int) ( startOffset * scaleFactor );
    for ( final GuideLine guideLine : guideLines ) {
      final int x = (int) ( guideLine.getPosition() * scaleFactor ) + so;
      if ( guideLine.isActive() ) {
        g.setColor( guideFill );
      } else {
        g.setColor( disabledGuideFill );
      }
      g.fillRect( x - 2, 1, 4, 13 );
      if ( guideLine.isActive() ) {
        g.setColor( guideColor );
      } else {
        g.setColor( disabledGuideColor );
      }
      g.drawRect( x - 2, 0, 4, 14 );
    }

    g.setColor( Color.BLUE );
    final GuideLine highlightGuideLine = getActiveGuidLine();

    if ( highlightGuideLine != null ) {
      final int x = (int) ( highlightGuideLine.getPosition() * scaleFactor ) + so;
      if ( highlightGuideLine.isActive() ) {
        g.setColor( guideFill );
      } else {
        g.setColor( disabledGuideFill );
      }
      g.fillRect( x - 2, 1, 4, 13 );
      if ( highlightGuideLine.isActive() ) {
        g.setColor( guideHighlight );
      } else {
        g.setColor( disabledHighlightGuide );
      }
      g.drawRect( x - 2, 0, 4, 14 );
    }
  }

  private void drawDots( final Graphics g, final double start, final double end ) {
    g.setColor( Color.GRAY );

    final Unit unit = WorkspaceSettings.getInstance().getUnit();
    final float scaleFactor = getZoomAsMicropoints();
    final double factorForUnitAndScale = unit.getTickSize( (double) scaleFactor );

    final double increment = unit.getDotsPerUnit() * factorForUnitAndScale;

    for ( double i = start + increment / 2; i < end; i += increment ) {
      final int x = (int) ( i * scaleFactor );
      g.drawLine( x, 7, x, 9 );
    }
  }

  private void drawNumbers( final Graphics g, final double start, final double end ) {
    final float scaleFactor = getZoomAsMicropoints();
    final Unit unit = WorkspaceSettings.getInstance().getUnit();
    final double factorForUnitAndScale = unit.getTickSize( (double) scaleFactor );

    final double increment = unit.getDotsPerUnit() * factorForUnitAndScale;

    DecimalFormat df = DECIMAL_FORMAT_NUMBERS_INTEGER;
    if ( factorForUnitAndScale < 1 ) {
      df = DECIMAL_FORMAT_NUMBERS_ONE_DIGIT;
    }

    g.setColor( Color.BLACK );
    double number = 0;
    for ( double i = start; i < end - increment / 2; i += increment ) {
      final int x = (int) ( i * scaleFactor );

      if ( number > 0 ) {
        final String s = df.format( number );
        final Rectangle2D sb = g.getFontMetrics().getStringBounds( s, g );
        g.drawString( s, (int) ( ( sb.getX() - sb.getWidth() / 2 ) + x ), (int) -sb.getY() );
      }
      number += factorForUnitAndScale;
    }
  }

  public LinealModel getLinealModel() {
    return linealModel;
  }

  public float getZoomAsMicropoints() {
    return renderContext.getZoomModel().getZoomAsPercentage();
  }

  protected void updateGuidelineHighlight( final MouseEvent e ) {
    final int agIndex = getActiveGuideIndex( e );
    final int oldIndex = this.activeGuideLineIndex;
    if ( agIndex == -1 ) {
      activeGuideLineIndex = -1;
    } else {
      activeGuideLineIndex = agIndex;
    }
    if ( activeGuideLineIndex != oldIndex ) {
      repaint();
    }
  }

  protected GuideLine getActiveGuidLine() {
    final int lineCount = linealModel.getGuideLineCount();
    if ( activeGuideLineIndex != -1 && activeGuideLineIndex < lineCount ) {
      return linealModel.getGuideLine( activeGuideLineIndex );
    }
    return null;
  }

  protected int getActiveGuideLineIndex() {
    return activeGuideLineIndex;
  }

  protected void setActiveGuideLineIndex( final int activeGuideLineIndex ) {
    this.activeGuideLineIndex = activeGuideLineIndex;
  }

  protected ReportDocumentContext getRenderContext() {
    return renderContext;
  }

  protected boolean isShowLeftBorder() {
    return showLeftBorder;
  }

  public void setShowLeftBorder( final boolean showLeftBorder ) {
    this.showLeftBorder = showLeftBorder;
    refresh();
  }
}
