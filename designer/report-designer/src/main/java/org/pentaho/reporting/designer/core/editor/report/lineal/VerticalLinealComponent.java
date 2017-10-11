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
import org.pentaho.reporting.designer.core.editor.ZoomModel;
import org.pentaho.reporting.designer.core.editor.ZoomModelListener;
import org.pentaho.reporting.designer.core.editor.report.layouting.AbstractElementRenderer;
import org.pentaho.reporting.designer.core.editor.report.layouting.ElementRenderer;
import org.pentaho.reporting.designer.core.model.lineal.GuideLine;
import org.pentaho.reporting.designer.core.model.lineal.LinealModel;
import org.pentaho.reporting.designer.core.model.lineal.LinealModelEvent;
import org.pentaho.reporting.designer.core.model.lineal.LinealModelListener;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.CanvasImageLoader;
import org.pentaho.reporting.designer.core.util.GuideLineDialog;
import org.pentaho.reporting.designer.core.util.Unit;
import org.pentaho.reporting.designer.core.util.dnd.InsertationUtil;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.libraries.designtime.swing.ColorUtility;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * A single vertical lineal for one of the rootbands. The lineal needs access to the corresponding root element to get
 * access to the lineal-model and to be able to listen to changes in the element's height.
 */
public class VerticalLinealComponent extends JComponent {
  protected static final String ADD_GUIDE_LINE = "addVerticalGuideLine";
  protected static final String REMOVE_GUIDE_LINE = "removeVerticalGuideLine";
  protected static final String MOVE_GUIDE_LINE = "moveGuideLine";
  protected static final String ACTIVATE_GUIDE_LINE = "activateGuideLine";
  protected static final String DEACTIVATE_GUIDE_LINE = "deactivateGuideLine";

  private class GuidelinePopupHandler extends MouseAdapter {
    private GuidelinePopupHandler() {
    }

    public void mouseEntered( final MouseEvent e ) {
      updateGuidelineHighlight( e );
    }

    public void mouseExited( final MouseEvent e ) {
      final int padding = (int) getPadding();
      if ( e.getX() < padding ) {
        return;
      }

      if ( getActiveGuidLine() != null ) {
        setActiveGuidLine( null );
        repaint();
      }
    }

    public void mouseClicked( final MouseEvent e ) {
      updateGuidelineHighlight( e );
      popup( e );
    }

    public void mousePressed( final MouseEvent e ) {
      updateGuidelineHighlight( e );
      popup( e );
    }

    public void mouseReleased( final MouseEvent e ) {
      updateGuidelineHighlight( e );
      popup( e );
    }

  }

  private class GuidelinePropertiesAction extends AbstractAction {
    private GuideLine guideLine;
    private int index;

    private GuidelinePropertiesAction( final GuideLine guideLine, final int index ) {
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
      final Component parent = VerticalLinealComponent.this;
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
        new UpdateVerticalGuidelineUndoEntry( index, newGuideLine, guideLine, getInstanceID() ) );
      linealModel.updateGuideLine( index, newGuideLine );
    }

  }

  private class ActivateGuidelineAction extends AbstractAction {
    private final GuideLine guideLine;
    private int index;

    private ActivateGuidelineAction( final GuideLine guideLine, final int index ) {
      super( Messages.getString( "LinealComponent.Activate" ) );
      this.guideLine = guideLine;
      this.index = index;
    }

    public void actionPerformed( final ActionEvent e ) {
      final GuideLine newGuideLine = new GuideLine( guideLine.getPosition(), true );
      final LinealModel linealModel = getLinealModel();
      final UndoManager undo = getRenderContext().getUndo();
      undo.addChange( Messages.getString( "LinealComponent.ActivateGuideUndoEntry" ),
        new UpdateVerticalGuidelineUndoEntry( index, newGuideLine, guideLine, getInstanceID() ) );
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
        new RemoveVerticalGuidelineUndoEntry( guideLine, getInstanceID() ) );
      linealModel.removeGuideLine( guideLine );
    }
  }

  private class RootBandChangeHandler implements ChangeListener, ZoomModelListener {
    private RootBandChangeHandler() {
    }

    public void zoomFactorChanged() {
      VerticalLinealComponent.this.invalidate();
      VerticalLinealComponent.this.revalidate();
      VerticalLinealComponent.this.repaint();
    }

    public void stateChanged( final ChangeEvent e ) {
      VerticalLinealComponent.this.invalidate();
      VerticalLinealComponent.this.revalidate();
      VerticalLinealComponent.this.repaint();
    }
  }

  private class PageFormatUpdateHandler implements ReportModelListener {
    private PageFormatUpdateHandler() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      if ( event.getElement() == event.getReport() ) {
        revalidate();
        repaint();

        updatePageDefinition( event.getReport().getPageDefinition() );
      }
    }
  }

  private class LinealUpdateHandler implements LinealModelListener {
    private LinealUpdateHandler() {
    }

    public void modelChanged( final LinealModelEvent event ) {
      setActiveGuidLine( null );
      repaint();
    }
  }

  private class DragAndDropHandler extends MouseAdapter implements MouseMotionListener {
    private int guideLineIndex;

    private DragAndDropHandler() {
    }

    public void mouseClicked( final MouseEvent e ) {
      final int padding = (int) getPadding();
      if ( e.getX() < padding ) {
        return;
      }

      double start = 0;
      final PageDefinition pageDefinition = getPageDefinition();
      if ( pageDefinition == null ) {
        return;
      }

      if ( isShowTopBorder() ) {
        start = getTopBorder();
      }
      final boolean activeGuide = getActiveGuideIndex( e ) == -1;
      if ( activeGuide == false ) {
        return;
      }

      final LinealModel linealModel = getLinealModel();
      final ZoomModel zoomModel = getZoomModel();
      final float pageHeight = pageDefinition.getHeight();
      final double scaledHeight = ( e.getY() / zoomModel.getZoomAsPercentage() ) - start;
      final double position = Math.min( (double) pageHeight, Math.max( (double) 0, scaledHeight ) );
      final GuideLine guideLine = new GuideLine( position, e.getButton() == MouseEvent.BUTTON1 );

      final UndoManager undo = getRenderContext().getUndo();
      undo.addChange( Messages.getString( "LinealComponent.AddGuideUndoName" ),
        new AddVerticalGuidelineUndoEntry( guideLine, getInstanceID() ) );
      linealModel.addGuidLine( guideLine );
    }

    public void mousePressed( final MouseEvent e ) {
      final int padding = (int) getPadding();
      if ( e.getX() < padding ) {
        return;
      }

      if ( e.getButton() == MouseEvent.BUTTON1 ) {
        guideLineIndex = getActiveGuideIndex( e );
        if ( guideLineIndex != -1 ) {
          setDraggedGuideLine( getLinealModel().getGuideLine( guideLineIndex ) );
        }
      }
    }

    public void mouseReleased( final MouseEvent e ) {
      guideLineIndex = -1;
      setDraggedGuideLine( null );
    }

    public void mouseDragged( final MouseEvent e ) {
      final GuideLine dragged = getDraggedGuideLine();
      if ( dragged == null || guideLineIndex == -1 ) {
        return;
      }

      double start = 0;
      final PageDefinition pageDefinition = getPageDefinition();
      if ( pageDefinition == null ) {
        return;
      }

      if ( isShowTopBorder() ) {
        start = getTopBorder();
      }
      final ZoomModel zoomModel = getZoomModel();
      final LinealModel linealModel = getLinealModel();

      final double scaledPos = ( e.getY() / zoomModel.getZoomAsPercentage() ) - start;
      final float pageHeight = pageDefinition.getHeight();
      final double position = Math.min( (double) pageHeight, Math.max( (double) 0, scaledPos ) );
      final GuideLine newGuideLine = new GuideLine( position, dragged.isActive() );
      final UndoManager undo = getRenderContext().getUndo();
      undo.addChange( Messages.getString( "LinealComponent.ChangeGuideUndoName" ),
        new UpdateVerticalGuidelineUndoEntry( guideLineIndex, newGuideLine, dragged, getInstanceID() ) );
      linealModel.updateGuideLine( guideLineIndex, newGuideLine );
    }

    public void mouseMoved( final MouseEvent e ) {
      updateGuidelineHighlight( e );
    }
  }

  private class UnitSettingsListener implements SettingsListener {
    private UnitSettingsListener() {
    }

    public void settingsChanged() {
      repaint();
    }
  }

  private final DecimalFormat decimalFormat = new DecimalFormat( "0.0##" );
  private final DecimalFormat decimalFormatNumbersOneDigit = new DecimalFormat( "0.0" );
  private final DecimalFormat decimalFormatInteger = new DecimalFormat( "0" );

  private PageDefinition pageDefinition;
  private boolean showTopBorder;
  private ZoomModel zoomModel;
  private LinealModel linealModel;
  private GuideLine activeGuidLine;
  private GuideLine draggedGuideLine;
  private LinealUpdateHandler linealUpdateHandler;
  private ElementRenderer reportElement;
  private RootBandChangeHandler changeHandler;
  private ReportDocumentContext renderContext;
  private String name;
  private double padding;
  private boolean renderNamesVertically;

  public VerticalLinealComponent( final boolean showTopBorder,
                                  final ReportDocumentContext renderContext ) {
    if ( renderContext == null ) {
      throw new NullPointerException();
    }

    setFont( new Font( Font.DIALOG, Font.PLAIN, 12 ) );

    this.renderContext = renderContext;
    this.renderContext.getContextRoot().addReportModelListener( new PageFormatUpdateHandler() );
    this.changeHandler = new RootBandChangeHandler();
    this.showTopBorder = showTopBorder;
    this.zoomModel = renderContext.getZoomModel();
    this.linealModel = new LinealModel();
    this.linealUpdateHandler = new LinealUpdateHandler();
    this.linealModel.addLinealModelListener( linealUpdateHandler );
    this.renderContext.getZoomModel().addZoomModelListener( changeHandler );

    final DragAndDropHandler andDropHandler = new DragAndDropHandler();
    addMouseListener( andDropHandler );
    addMouseMotionListener( andDropHandler );
    addMouseListener( new GuidelinePopupHandler() );

    WorkspaceSettings.getInstance().addSettingsListener( new UnitSettingsListener() );

  }

  public boolean isRenderNamesVertically() {
    return renderNamesVertically;
  }

  public void setRenderNamesVertically( final boolean renderNamesVertically ) {
    this.renderNamesVertically = renderNamesVertically;
  }

  public PageDefinition getPageDefinition() {
    return pageDefinition;
  }

  protected void updatePageDefinition( final PageDefinition pageDefinition ) {
    this.pageDefinition = pageDefinition;
  }

  public void setPageDefinition( final PageDefinition pageDefinition, final ElementRenderer reportElement ) {
    if ( this.reportElement != null ) {
      this.reportElement.removeChangeListener( changeHandler );
    }
    this.linealModel.removeLinealModelListener( linealUpdateHandler );

    this.reportElement = reportElement;
    this.pageDefinition = pageDefinition;
    if ( reportElement == null ) {
      this.linealModel = new LinealModel();
      this.name = null;
    } else {
      this.linealModel = reportElement.getVerticalLinealModel();
      this.name = reportElement.getElementType().getMetaData().getDisplayName( Locale.getDefault() );
    }
    this.linealModel.addLinealModelListener( linealUpdateHandler );

    if ( this.reportElement != null ) {
      this.reportElement.addChangeListener( changeHandler );
    }

    revalidate();
    repaint();
  }

  protected void popup( final MouseEvent me ) {
    if ( !me.isPopupTrigger() ) {
      return;
    }
    final int padding = (int) getPadding();
    if ( me.getX() < padding ) {
      return;
    }

    double start = 0;
    if ( pageDefinition == null ) {
      return;
    }

    if ( showTopBorder ) {
      start = getTopBorder();
    }

    final GuideLine[] guideLines = linealModel.getGuideLines();
    for ( int i = 0; i < guideLines.length; i++ ) {
      final GuideLine guideLine = guideLines[ i ];
      final int y = (int) ( ( guideLine.getPosition() + start ) * zoomModel.getZoomAsPercentage() );

      if ( y <= me.getY() + 2 && y >= me.getY() - 2 ) {
        final JPopupMenu popupMenu = createPopupMenu( guideLine, i );
        popupMenu.show( VerticalLinealComponent.this, me.getX(), me.getY() );

        break;
      }
    }
  }

  /**
   * Sets the font for this component.
   *
   * @param font the desired <code>Font</code> for this component
   * @see Component#getFont
   */
  public void setFont( final Font font ) {
    super.setFont( font );

    if ( renderNamesVertically ) {
      final Rectangle2D bounds = font.getStringBounds( "100%", new FontRenderContext( null, true, true ) );
      this.padding = bounds.getWidth() - bounds.getHeight();
    } else {
      final Rectangle2D bounds =
        font.getStringBounds( "xxxPage Headerxxx", new FontRenderContext( null, true, true ) ); // NON-NLS
      this.padding = bounds.getWidth();
    }
  }

  protected double getPadding() {
    return padding;
  }

  protected double getTopBorder() {
    if ( pageDefinition == null ) {
      return 0;
    }
    final PageFormat pageFormat = pageDefinition.getPageFormat( 0 );
    final PageFormatFactory pageFormatFactory = PageFormatFactory.getInstance();
    return pageFormatFactory.getTopBorder( pageFormat.getPaper() );
  }

  protected boolean isShowTopBorder() {
    return showTopBorder;
  }

  protected double getBottomBorder() {
    if ( pageDefinition == null ) {
      return 0;
    }
    final PageFormat pageFormat = pageDefinition.getPageFormat( 0 );
    final PageFormatFactory pageFormatFactory = PageFormatFactory.getInstance();
    return pageFormatFactory.getBottomBorder( pageFormat.getPaper() );
  }

  private JPopupMenu createPopupMenu( final GuideLine guideLine, final int index ) {
    final JPopupMenu popupMenu = new JPopupMenu();

    popupMenu.add( new GuidelinePropertiesAction( guideLine, index ) );

    if ( guideLine.isActive() ) {
      popupMenu.add( new DeactivateGuidelineAction( guideLine, index ) );
    } else {
      popupMenu.add( new ActivateGuidelineAction( guideLine, index ) );
    }

    popupMenu.add( new DeleteGuidelineAction( guideLine ) );
    return popupMenu;
  }

  protected final int getActiveGuideIndex( final MouseEvent e ) {
    if ( pageDefinition == null ) {
      setToolTipText( null );
      return -1;
    }

    final Unit unit = WorkspaceSettings.getInstance().getUnit();
    final GuideLine[] lines = linealModel.getGuideLines();
    for ( int i = 0; i < lines.length; i++ ) {
      final GuideLine guideLine = lines[ i ];
      double start = 0;
      if ( showTopBorder ) {
        start = getTopBorder();
      }
      final int y = (int) ( ( guideLine.getPosition() + start ) * zoomModel.getZoomAsPercentage() );
      if ( y <= e.getY() + 2 && y >= e.getY() - 2 ) {
        final double unitValue = unit.convertFromPoints( guideLine.getPosition() );
        setToolTipText( decimalFormat.format( unitValue ) );
        return i;
      }
    }

    setToolTipText( null );
    return -1;
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  public Dimension getPreferredSize() {
    final float zoom = renderContext.getZoomModel().getZoomAsPercentage();
    final int padding = (int) getPadding();
    final int width = ( padding + 15 );
    if ( reportElement == null || zoom == 0 ) {
      return new Dimension( width, 0 );
    }

    final double height = reportElement.getLayoutHeight();
    if ( showTopBorder && pageDefinition != null ) {
      return new Dimension( width, (int) ( zoom * ( getTopBorder() + height ) ) );
    }

    return new Dimension( width, (int) ( zoom * height ) );
  }

  protected void paintComponent( final Graphics graphics ) {
    super.paintComponent( graphics );

    final int padding = (int) getPadding();

    final Graphics2D g2 = (Graphics2D) graphics.create();
    // if band for which this lineal component wraps is selected, change
    // transparency level to show focus
    g2.setColor( new Color( 255, 255, 255, 0 ) );
    if ( reportElement instanceof AbstractElementRenderer ) {
      final Object selectedBand = InsertationUtil.getInsertationPoint( renderContext );
      final AbstractElementRenderer renderer = (AbstractElementRenderer) reportElement;
      if ( selectedBand == renderer.getElement() ) {
        g2.setColor( new Color( 128, 128, 128, 128 ) );
      }
    }
    // draw background
    g2.fillRect( 0, 0, padding, getHeight() );

    // set draw color for text
    g2.setColor( getForeground() );

    final Rectangle componentBounds = getBounds();
    if ( name != null ) {
      if ( renderNamesVertically ) {
        final Rectangle2D sb = g2.getFontMetrics().getStringBounds( name, g2 );
        drawRotatedText( g2, 0, (int) ( getHeight() / 2 - ( sb.getWidth() + 1 ) / 2 ), name );
      } else {
        final Rectangle2D stringBounds = g2.getFontMetrics().getStringBounds( name, g2 );
        final int x = (int) Math.max( 0, componentBounds.getCenterX() - ( stringBounds.getWidth() / 2 ) );
        final int y = (int) Math.max( 0, ( componentBounds.getHeight() / 2 ) - ( stringBounds.getHeight() / 2 ) );

        // center vertically and horizontally
        drawText( g2, x - 7, y, name );
      }
    }

    final ImageIcon leftBorder = CanvasImageLoader.getInstance().getLeftShadowImage();
    g2.drawImage( leftBorder.getImage(), padding - 8, 0, leftBorder.getIconWidth(), getHeight(), null );

    g2.translate( padding, 0 );
    final int effectiveWidth = Math.max( 0, getWidth() - padding );
    g2.clipRect( 0, 0, effectiveWidth + 1, getHeight() );

    double start = 0;
    double end = 0;
    if ( pageDefinition != null ) {
      end = pageDefinition.getHeight();
      if ( showTopBorder ) {
        start = getTopBorder();

        end += getTopBorder();
        end += getBottomBorder();
      }
    }

    final float scaleFactor = zoomModel.getZoomAsPercentage();

    // background of track
    g2.setColor( Color.WHITE );
    g2.fillRect( 0, (int) ( start * scaleFactor ), effectiveWidth, getHeight() );

    g2.setColor( Color.LIGHT_GRAY );
    g2.drawLine( effectiveWidth - 1, (int) ( start * scaleFactor ), effectiveWidth - 1, getHeight() );

    drawDots( g2, start, end );
    drawGuideLines( g2 );
    drawNumbers( g2, start, end );

    g2.dispose();
  }

  private void drawDots( final Graphics g, double start, final double end ) {
    g.setColor( Color.GRAY );

    final Unit unit = WorkspaceSettings.getInstance().getUnit();
    final float zoomAsPercentage = zoomModel.getZoomAsPercentage();
    final double factorForUnitAndScale = unit.getTickSize( (double) zoomAsPercentage );
    final double increment = unit.getDotsPerUnit() * factorForUnitAndScale;

    start += increment / 2;
    for ( double i = start; i < end; i += increment ) {
      final int x = (int) ( i * zoomAsPercentage );
      g.drawLine( 9, x, 10, x );
    }
  }

  private void drawNumbers( final Graphics g, final double start, final double end ) {
    final Unit unit = WorkspaceSettings.getInstance().getUnit();
    final float zoomAsPercentage = zoomModel.getZoomAsPercentage();
    final double factorForUnitAndScale = unit.getTickSize( (double) zoomAsPercentage );

    final double increment = unit.getDotsPerUnit() * factorForUnitAndScale;

    DecimalFormat df = decimalFormatInteger;
    if ( factorForUnitAndScale < 1 ) {
      df = decimalFormatNumbersOneDigit;
    }

    g.setColor( Color.GRAY );
    double number = 0;
    for ( double i = start; i < end - increment / 2; i += increment ) {
      final int x = (int) ( i * zoomAsPercentage );

      if ( number > 0 ) {
        final String s = df.format( number );
        final Rectangle2D sb = g.getFontMetrics().getStringBounds( s, g );
        drawRotatedText( (Graphics2D) g, 0, (int) ( x - ( sb.getWidth() + 1 ) / 2 ), s );
      }
      number += factorForUnitAndScale;
    }
  }

  private void drawRotatedText( final Graphics2D g2d, final int x, final int y, final String text ) {
    final FontRenderContext fontRenderContext = g2d.getFontRenderContext();
    final Font font = getFont();
    final Rectangle2D sb = font.getStringBounds( text, fontRenderContext );
    final int width = (int) sb.getWidth() + 4;

    final LineMetrics lineMetrics = font.getLineMetrics( text, fontRenderContext );
    final float ascent = lineMetrics.getAscent();
    final int height = (int) Math.ceil( lineMetrics.getHeight() );

    g2d.setFont( font );
    final AffineTransform oldTransform = g2d.getTransform();

    g2d.setColor( getForeground() );

    final AffineTransform trans = new AffineTransform();
    trans.concatenate( oldTransform );
    trans.translate( x, y - 2 );
    trans.rotate( Math.PI * 3 / 2, height / 2, width / 2 );
    g2d.setTransform( trans );
    g2d.drawString( text, ( height - width ) / 2, ( width - height ) / 2 + ascent );
    g2d.setTransform( oldTransform );
  }

  private void drawText( final Graphics2D g2d, final int x, final int y, final String text ) {
    final FontRenderContext fontRenderContext = g2d.getFontRenderContext();
    final Font font = getFont();
    final LineMetrics lineMetrics = font.getLineMetrics( text, fontRenderContext );
    final float ascent = lineMetrics.getAscent();
    g2d.setFont( font );
    g2d.setColor( getForeground() );
    g2d.drawString( text, x, y + ascent );
  }

  private void drawGuideLines( final Graphics g ) {
    final GuideLine[] guideLines = linealModel.getGuideLines();
    double startOffset = 0;
    if ( showTopBorder ) {
      startOffset = getTopBorder();
    }

    final Color guideColor = WorkspaceSettings.getInstance().getGuideColor();
    final Color guideFill = ColorUtility.convertToGray( guideColor, 0.3f );
    final Color guideHighlight = ColorUtility.convertToDarker( guideFill );

    final Color disabledGuideColor = ColorUtility.convertToDarker( ColorUtility.convertToGray( guideColor, 0 ) );
    final Color disabledGuideFill = ColorUtility.convertToDarker( ColorUtility.convertToGray( guideFill, 0 ) );
    final Color disabledHighlightGuide =
      ColorUtility.convertToDarker( ColorUtility.convertToGray( guideHighlight, 0 ) );

    final int so = (int) ( startOffset * zoomModel.getZoomAsPercentage() );
    for ( final GuideLine guideLine : guideLines ) {
      final int y = (int) ( guideLine.getPosition() * zoomModel.getZoomAsPercentage() ) + so;
      if ( guideLine.isActive() ) {
        g.setColor( guideFill );
      } else {
        g.setColor( disabledGuideFill );
      }
      g.fillRect( 1, y - 2, 13, 4 );
      if ( guideLine.isActive() ) {
        g.setColor( guideColor );
      } else {
        g.setColor( disabledGuideColor );
      }
      g.drawRect( 0, y - 2, 14, 4 );
    }

    GuideLine highlightGuideLine = activeGuidLine;
    if ( draggedGuideLine != null ) {
      highlightGuideLine = draggedGuideLine;
    }

    if ( highlightGuideLine != null ) {
      final int y = (int) ( ( highlightGuideLine.getPosition() ) * zoomModel.getZoomAsPercentage() ) + so;
      if ( highlightGuideLine.isActive() ) {
        g.setColor( guideFill );
      } else {
        g.setColor( disabledGuideFill );
      }
      g.fillRect( 1, y - 2, 13, 4 );
      if ( highlightGuideLine.isActive() ) {
        g.setColor( guideHighlight );
      } else {
        g.setColor( disabledHighlightGuide );
      }
      g.drawRect( 0, y - 2, 14, 4 );
    }
  }

  protected GuideLine getActiveGuidLine() {
    return activeGuidLine;
  }

  protected void setActiveGuidLine( final GuideLine activeGuidLine ) {
    this.activeGuidLine = activeGuidLine;
  }

  protected GuideLine getDraggedGuideLine() {
    return draggedGuideLine;
  }

  protected void setDraggedGuideLine( final GuideLine draggedGuideLine ) {
    this.draggedGuideLine = draggedGuideLine;
  }

  protected LinealModel getLinealModel() {
    return linealModel;
  }

  protected ZoomModel getZoomModel() {
    return zoomModel;
  }

  protected ReportDocumentContext getRenderContext() {
    return renderContext;
  }

  protected void updateGuidelineHighlight( final MouseEvent e ) {
    final int padding = (int) getPadding();
    if ( e.getX() < padding ) {
      return;
    }

    final int agIndex = getActiveGuideIndex( e );
    final GuideLine ag;
    if ( agIndex == -1 ) {
      ag = null;
    } else {
      ag = linealModel.getGuideLine( agIndex );
    }
    if ( activeGuidLine != ag ) {
      activeGuidLine = ag;
      repaint();
    }
  }

  protected InstanceID getInstanceID() {
    if ( reportElement == null ) {
      return null;
    }
    return reportElement.getRepresentationId();
  }
}
