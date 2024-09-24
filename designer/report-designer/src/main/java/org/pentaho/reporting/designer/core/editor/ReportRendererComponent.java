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

package org.pentaho.reporting.designer.core.editor;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.DesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.actions.elements.SelectCrosstabBandAction;
import org.pentaho.reporting.designer.core.actions.elements.format.BoldAction;
import org.pentaho.reporting.designer.core.actions.elements.format.EditHyperlinkAction;
import org.pentaho.reporting.designer.core.actions.elements.format.FontColorSelectorComponent;
import org.pentaho.reporting.designer.core.actions.elements.format.FontFamilySelectorComponent;
import org.pentaho.reporting.designer.core.actions.elements.format.FontSizeSelectorComponent;
import org.pentaho.reporting.designer.core.actions.elements.format.ItalicsAction;
import org.pentaho.reporting.designer.core.actions.elements.format.TextAlignmentCenterAction;
import org.pentaho.reporting.designer.core.actions.elements.format.TextAlignmentJustifyAction;
import org.pentaho.reporting.designer.core.actions.elements.format.TextAlignmentLeftAction;
import org.pentaho.reporting.designer.core.actions.elements.format.TextAlignmentRightAction;
import org.pentaho.reporting.designer.core.actions.elements.format.UnderlineAction;
import org.pentaho.reporting.designer.core.actions.global.ShowPreviewPaneAction;
import org.pentaho.reporting.designer.core.editor.preview.ReportPreviewComponent;
import org.pentaho.reporting.designer.core.editor.report.AbstractRenderComponent;
import org.pentaho.reporting.designer.core.editor.report.CrosstabRenderComponent;
import org.pentaho.reporting.designer.core.editor.report.ReportRenderEvent;
import org.pentaho.reporting.designer.core.editor.report.ReportRenderListener;
import org.pentaho.reporting.designer.core.editor.report.ResizeRootBandComponent;
import org.pentaho.reporting.designer.core.editor.report.RootBandRenderComponent;
import org.pentaho.reporting.designer.core.editor.report.RootBandRenderingModel;
import org.pentaho.reporting.designer.core.editor.report.layouting.CrosstabRenderer;
import org.pentaho.reporting.designer.core.editor.report.layouting.ElementRenderer;
import org.pentaho.reporting.designer.core.editor.report.layouting.RootBandRenderer;
import org.pentaho.reporting.designer.core.editor.report.lineal.AllVerticalLinealsComponent;
import org.pentaho.reporting.designer.core.editor.report.lineal.HorizontalLinealComponent;
import org.pentaho.reporting.designer.core.model.HorizontalPositionsModel;
import org.pentaho.reporting.designer.core.util.ActionToggleButton;
import org.pentaho.reporting.designer.core.util.CanvasImageLoader;
import org.pentaho.reporting.designer.core.util.Unit;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.libraries.designtime.swing.ToolbarButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * A component holding the report. It contains the lineals, zoom controller in the upper left corner and as a viewport,
 * it contains the report-layout component.
 *
 * @author Thomas Morgner
 */
public class ReportRendererComponent extends JComponent {
  private static class RightImageBorder extends JComponent {
    public RightImageBorder() {
      setOpaque( false );
      setBackground( new Color( 0, 0, 0, 0 ) );
      setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
      final ImageIcon bottomRightBorder = CanvasImageLoader.getInstance().getRightCornerShadowImage();

      setMinimumSize( new Dimension( bottomRightBorder.getIconWidth(), 0 ) );
      setPreferredSize( new Dimension( bottomRightBorder.getIconWidth(), 0 ) );
    }

    protected void paintComponent( final Graphics g ) {
      super.paintComponent( g );
      //  g.clearRect(0, 0, getWidth(), getHeight());
      final ImageIcon rightBorder = CanvasImageLoader.getInstance().getRightShadowImage();
      g.drawImage( rightBorder.getImage(), 0, 0, rightBorder.getIconWidth(), getHeight(), this );
    }
  }

  private static class BottomImageBorder extends JComponent {
    private ImageIcon bottomBorder;
    private ImageIcon bottomRightBorder;

    public BottomImageBorder() {
      setOpaque( false );
      setBackground( new Color( 0, 0, 0, 0 ) );
      setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
      bottomBorder = CanvasImageLoader.getInstance().getBottomShadowImage();
      bottomRightBorder = CanvasImageLoader.getInstance().getRightCornerShadowImage();
      setMinimumSize( new Dimension( 0, Math.max( bottomBorder.getIconHeight(), bottomRightBorder.getIconHeight() ) ) );
      setPreferredSize(
        new Dimension( 0, Math.max( bottomBorder.getIconHeight(), bottomRightBorder.getIconHeight() ) ) );
    }

    protected void paintComponent( final Graphics g ) {
      super.paintComponent( g );

      final int cornerWidth = bottomRightBorder.getIconWidth();
      g.drawImage( bottomBorder.getImage(), 0, 0, getWidth() - cornerWidth, bottomBorder.getIconHeight(), this );
      g.drawImage( bottomRightBorder.getImage(), getWidth() - cornerWidth, 0, cornerWidth,
        bottomRightBorder.getIconHeight(), this );
    }
  }

  private static class ImagePanel extends JComponent {
    private Image img;

    public ImagePanel( final Image img ) {
      this.img = img;
      final Dimension size = new Dimension( img.getWidth( null ), img.getHeight( null ) );
      setPreferredSize( size );
      setMinimumSize( size );
      setMaximumSize( size );
      setSize( size );
      setLayout( null );
    }

    public void paintComponent( final Graphics g ) {
      g.setColor( getBackground() );
      g.fillRect( 0, 0, getWidth(), getHeight() );
      g.drawImage( img, 0, 0, null );
    }

  }

  private static class LayoutScrollable extends JPanel implements Scrollable {
    private LayoutScrollable() {
      setLayout( new BorderLayout() );
    }

    /**
     * Returns the preferred size of the viewport for a view component. For example, the preferred size of a
     * <code>JList</code> component is the size required to accommodate all of the cells in its list. However, the value
     * of <code>preferredScrollableViewportSize</code> is the size required for <code>JList.getVisibleRowCount</code>
     * rows. A component without any properties that would affect the viewport size should just return
     * <code>getPreferredSize</code> here.
     *
     * @return the preferredSize of a <code>JViewport</code> whose view is this <code>Scrollable</code>
     * @see JViewport#getPreferredSize
     */
    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    /**
     * Components that display logical rows or columns should compute the scroll increment that will completely expose
     * one new row or column, depending on the value of orientation. Ideally, components should handle a partially
     * exposed row or column by returning the distance required to completely expose the item.
     * <p/>
     * Scrolling containers, like JScrollPane, will use this method each time the user requests a unit scroll.
     *
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction   Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "unit" increment for scrolling in the specified direction. This value should always be positive.
     * @see JScrollBar#setUnitIncrement
     */
    public int getScrollableUnitIncrement( final Rectangle visibleRect, final int orientation, final int direction ) {
      return 20;
    }

    /**
     * Components that display logical rows or columns should compute the scroll increment that will completely expose
     * one block of rows or columns, depending on the value of orientation.
     * <p/>
     * Scrolling containers, like JScrollPane, will use this method each time the user requests a block scroll.
     *
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction   Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "block" increment for scrolling in the specified direction. This value should always be positive.
     * @see JScrollBar#setBlockIncrement
     */
    public int getScrollableBlockIncrement( final Rectangle visibleRect, final int orientation, final int direction ) {
      return 100;
    }

    /**
     * Return true if a viewport should always force the width of this <code>Scrollable</code> to match the width of the
     * viewport. For example a normal text view that supported line wrapping would return true here, since it would be
     * undesirable for wrapped lines to disappear beyond the right edge of the viewport. Note that returning true for a
     * Scrollable whose ancestor is a JScrollPane effectively disables horizontal scrolling.
     * <p/>
     * Scrolling containers, like JViewport, will use this method each time they are validated.
     *
     * @return True if a viewport should force the Scrollables width to match its own.
     */
    public boolean getScrollableTracksViewportWidth() {
      return false;
    }

    /**
     * Return true if a viewport should always force the height of this Scrollable to match the height of the viewport.
     * For example a columnar text view that flowed text in left to right columns could effectively disable vertical
     * scrolling by returning true here.
     * <p/>
     * Scrolling containers, like JViewport, will use this method each time they are validated.
     *
     * @return True if a viewport should force the Scrollables height to match its own.
     */
    public boolean getScrollableTracksViewportHeight() {
      return false;
    }
  }

  private class RootBandModelUpdateHandler implements ChangeListener {
    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged( final ChangeEvent e ) {
      registerReport();
    }
  }

  private class LayoutUpdateHandler implements ReportRenderListener {
    private LayoutUpdateHandler() {
    }

    public void layoutChanged( final ReportRenderEvent event ) {
      final RootBandRenderingModel renderingModel = getRenderingModel();
      final AbstractReportDefinition report = getReport();
      final HorizontalPositionsModel horizontalPositionsModel = getHorizontalPositionsModel();
      final ElementRenderer[] allRenderers = renderingModel.getAllRenderers();
      final long age = report.getChangeTracker();

      boolean change = false;
      synchronized( horizontalPositionsModel ) {
        // update the horizontal positions ...
        for ( int i = 0; i < allRenderers.length; i++ ) {
          final ElementRenderer renderer = allRenderers[ i ];
          final long[] keys = renderer.getHorizontalEdgePositionKeys();
          if ( horizontalPositionsModel.add( keys, age ) ) {
            change = true;
          }
        }
        if ( horizontalPositionsModel.clear( age ) ) {
          change = true;
        }
      }

      if ( change ) {
        // and then repaint ..
        horizontalPositionsModel.fireChangeEvent();
        for ( int i = 0; i < rootBandRenderers.size(); i++ ) {
          final AbstractRenderComponent component = rootBandRenderers.get( i );
          component.repaint();
        }
      }
    }
  }

  private class ReportPreviewChangeHandler implements ReportModelListener {
    public void nodeChanged( final ReportModelEvent event ) {
      if ( !designVisible ) {
        if ( designerContext.getActiveContext() != renderContext ) {
          showDesign();
        } else {
          previewComponent.updatePreview( renderContext );
        }
      }
    }
  }

  @SuppressWarnings( "deprecation" )
  private static class NoKeysScrollPane extends JScrollPane {
    protected void processKeyEvent( final KeyEvent e ) {
    }

    public boolean keyDown( final Event evt, final int key ) {
      return false;
    }

    public boolean keyUp( final Event evt, final int key ) {
      return false;
    }

    protected boolean processKeyBinding( final KeyStroke ks,
                                         final KeyEvent e,
                                         final int condition,
                                         final boolean pressed ) {
      return false;
    }

  }

  private ReportDocumentContext renderContext;
  private RootBandRenderingModel renderingModel;

  private HorizontalLinealComponent horizontalLinealComponent;
  private JPanel layoutRendererComponent;
  private ArrayList<AbstractRenderComponent> rootBandRenderers;
  private ReportDesignerContext designerContext;
  private boolean designVisible;
  private HorizontalPositionsModel horizontalPositionsModel;

  private CardLayout cardLayout;
  private ReportPreviewComponent previewComponent;
  private JComponent designView;
  private JComponent previewView;

  public ReportRendererComponent( final ReportDesignerContext designerContext,
                                  final ReportRenderContext renderContext ) {
    if ( renderContext == null ) {
      throw new NullPointerException();
    }
    if ( designerContext == null ) {
      throw new NullPointerException();
    }

    this.designVisible = true;
    this.designerContext = designerContext;
    this.rootBandRenderers = new ArrayList<AbstractRenderComponent>();
    this.renderContext = renderContext;
    this.renderingModel = new RootBandRenderingModel( renderContext );
    this.renderingModel.addChangeListener( new RootBandModelUpdateHandler() );
    this.renderingModel.addReportRenderListener( new LayoutUpdateHandler() );

    this.horizontalPositionsModel = HorizontalPositionsModel.getHorizontalPositionsModel( renderContext );

    horizontalLinealComponent = new HorizontalLinealComponent( renderContext, false );

    layoutRendererComponent = new JPanel();
    layoutRendererComponent.setLayout( new BoxLayout( layoutRendererComponent, BoxLayout.Y_AXIS ) );
    layoutRendererComponent.setBackground( new Color( 0, 0, 0, 0 ) );
    layoutRendererComponent.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    layoutRendererComponent.setOpaque( false );

    cardLayout = new CardLayout();
    previewComponent = new ReportPreviewComponent( designerContext );
    getReport().addReportModelListener( new ReportPreviewChangeHandler() );

    previewView = new ImagePanel( CanvasImageLoader.getInstance().getBackgroundImage().getImage() );
    previewView.setLayout( new BorderLayout() );
    previewView.add( previewComponent, BorderLayout.CENTER );

    final ZoomModel zoomModel = renderContext.getZoomModel();
    final JComponent zoomController = new ZoomController( zoomModel );
    final AllVerticalLinealsComponent verticalLinealsComponent = new AllVerticalLinealsComponent( renderingModel );
    verticalLinealsComponent.setOpaque( false );
    verticalLinealsComponent.setBackground( new Color( 0, 0, 0, 0 ) );
    verticalLinealsComponent.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );

    final LayoutScrollable viewPortComponent = new LayoutScrollable();
    viewPortComponent.add( layoutRendererComponent, BorderLayout.NORTH );
    viewPortComponent.setBackground( new Color( 0, 0, 0, 0 ) );
    viewPortComponent.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    viewPortComponent.setOpaque( false );

    // effectively disable all key events on scroller
    // PRD-1441
    final JScrollPane reportScrollPane = new NoKeysScrollPane();
    reportScrollPane.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    reportScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
    reportScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
    reportScrollPane.setColumnHeaderView( horizontalLinealComponent );
    reportScrollPane.setCorner( JScrollPane.UPPER_LEFT_CORNER, zoomController );
    reportScrollPane.setRowHeaderView( verticalLinealsComponent );
    reportScrollPane.setViewportView( viewPortComponent );
    reportScrollPane.setFocusTraversalKeysEnabled( false );
    reportScrollPane.setBackground( new Color( 0, 0, 0, 0 ) );
    reportScrollPane.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    reportScrollPane.setOpaque( false );

    final JViewport viewport = reportScrollPane.getViewport();
    viewport.setOpaque( false );
    viewPortComponent.setBackground( new Color( 0, 0, 0, 0 ) );
    viewPortComponent.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );

    reportScrollPane.getRowHeader().setOpaque( false );
    ( (JComponent) reportScrollPane.getRowHeader().getView() ).setOpaque( false );
    reportScrollPane.getColumnHeader().setOpaque( false );
    ( (JComponent) reportScrollPane.getColumnHeader().getView() ).setOpaque( false );


    designView = new ImagePanel( CanvasImageLoader.getInstance().getBackgroundImage().getImage() );
    designView.setLayout( new BorderLayout() );
    designView.setOpaque( true );

    designView.add( createToolbar(), BorderLayout.NORTH );
    designView.add( reportScrollPane, BorderLayout.CENTER );

    add( designView, "design" ); // NON-NLS
    add( previewView, "preview" ); // NON-NLS

    setLayout( cardLayout );
    showDesign();
    registerReport();
  }

  public boolean isDesignVisible() {
    return designVisible;
  }

  public void showPreview() {
    designVisible = false;
    previewView.setVisible( true );
    previewComponent.updatePreview( renderContext );
    designView.setVisible( false );
    cardLayout.last( this );
    repaint();
  }

  public void showDesign() {
    designVisible = true;
    previewView.setVisible( false );
    previewComponent.updatePreview( null );
    designView.setVisible( true );
    cardLayout.first( this );
    repaint();
  }

  public HorizontalPositionsModel getHorizontalPositionsModel() {
    return horizontalPositionsModel;
  }

  private JToolBar createToolbar() {
    final ShowPreviewPaneAction previewAction = new ShowPreviewPaneAction();
    previewAction.setReportDesignerContext( designerContext );

    final EditHyperlinkAction hyperlinkAction = new EditHyperlinkAction();
    hyperlinkAction.setReportDesignerContext( designerContext );

    final FontFamilySelectorComponent familySelectorComponent = new FontFamilySelectorComponent();
    familySelectorComponent.setReportDesignerContext( designerContext );

    final FontSizeSelectorComponent sizeSelectorComponent = new FontSizeSelectorComponent();
    sizeSelectorComponent.setReportDesignerContext( designerContext );

    final FontColorSelectorComponent colorSelectorComponent = new FontColorSelectorComponent();
    colorSelectorComponent.setReportDesignerContext( designerContext );

    final JToolBar toolBar = new JToolBar();
    toolBar.setFloatable( false );
    toolBar.setOpaque( true );
    toolBar.add( new ToolbarButton( previewAction ) );
    toolBar.add( new JToolBar.Separator() );
    toolBar.add( familySelectorComponent );
    toolBar.add( sizeSelectorComponent );
    toolBar.add( new JToolBar.Separator() );
    toolBar.add( createButton( new BoldAction() ) );
    toolBar.add( createButton( new ItalicsAction() ) );
    toolBar.add( createButton( new UnderlineAction() ) );
    toolBar.add( new JToolBar.Separator() );
    toolBar.add( colorSelectorComponent );
    toolBar.add( new JToolBar.Separator() );
    toolBar.add( createButton( new TextAlignmentLeftAction() ) );
    toolBar.add( createButton( new TextAlignmentCenterAction() ) );
    toolBar.add( createButton( new TextAlignmentRightAction() ) );
    toolBar.add( createButton( new TextAlignmentJustifyAction() ) );
    toolBar.add( new JToolBar.Separator() );
    toolBar.add( new ToolbarButton( hyperlinkAction ) );

    // Add special crosstab band selection icon
    if ( getRenderContext().getReportDefinition() instanceof CrosstabElement ) {
      final SelectCrosstabBandAction selectCrosstabBandAction = new SelectCrosstabBandAction();
      selectCrosstabBandAction.setReportDesignerContext( designerContext );
      toolBar.add( new ToolbarButton( selectCrosstabBandAction ) );
      toolBar.add( new JToolBar.Separator() );
    }

    return toolBar;
  }

  private JToggleButton createButton( final DesignerContextAction action ) {
    final ActionToggleButton button = new ActionToggleButton();
    action.addPropertyChangeListener( new ActionSelectedHandler( button ) );
    action.setReportDesignerContext( designerContext );
    button.putClientProperty( "hideActionText", Boolean.TRUE ); // NON-NLS
    button.setFocusable( false );
    button.setAction( action );
    return button;
  }

  public RootBandRenderingModel getRenderingModel() {
    return renderingModel;
  }

  public AbstractReportDefinition getReport() {
    return renderContext.getReportDefinition();
  }

  public void dispose() {
    for ( int i = 0; i < rootBandRenderers.size(); i++ ) {
      final AbstractRenderComponent o = rootBandRenderers.get( i );
      o.dispose();
    }
    rootBandRenderers.clear();
    previewComponent.dispose();
  }

  protected void registerReport() {
    layoutRendererComponent.removeAll();

    for ( int i = 0; i < rootBandRenderers.size(); i++ ) {
      final AbstractRenderComponent o = rootBandRenderers.get( i );
      o.dispose();
    }
    rootBandRenderers.clear();

    final ElementRenderer[] allRenderers = renderingModel.getAllRenderers();
    for ( int i = 0; i < allRenderers.length; i++ ) {
      final ElementRenderer allRenderer = allRenderers[ i ];
      final AbstractRenderComponent renderComponent;

      if ( allRenderer instanceof RootBandRenderer ) {
        final RootBandRenderer rootRenderer = (RootBandRenderer) allRenderer;
        final ReportDocumentContext context = rootRenderer.getReportRenderContext();
        final ReportDefinition reportDefinition = context.getReportDefinition();

        // Increase crosstab canvas height during a drag-n-drop operation of a new crosstab
        if ( reportDefinition instanceof CrosstabElement ) {
          rootRenderer.setVisualHeight( Unit.INCH.getDotsPerUnit() * 1.5 * 2 );
        }

        final RootBandRenderComponent bandComponent =
          new RootBandRenderComponent( designerContext, renderContext, false );
        bandComponent.setShowTopBorder( false );
        bandComponent.setShowLeftBorder( false );
        bandComponent
          .installRenderer( rootRenderer, horizontalLinealComponent.getLinealModel(), horizontalPositionsModel );
        renderComponent = bandComponent;
      } else if ( allRenderer instanceof CrosstabRenderer ) {
        final CrosstabRenderer rootRenderer = (CrosstabRenderer) allRenderer;
        rootRenderer.setVisualHeight( Unit.INCH.getDotsPerUnit() * 1.5 * 2 );

        final CrosstabRenderComponent bandComponent = new CrosstabRenderComponent( designerContext, renderContext );
        bandComponent.setShowTopBorder( false );
        bandComponent.setShowLeftBorder( false );
        bandComponent
          .installRenderer( rootRenderer, horizontalLinealComponent.getLinealModel(), horizontalPositionsModel );
        renderComponent = bandComponent;
      } else {
        renderComponent = null;
      }

      if ( renderComponent != null ) {
        final JPanel renderWrapper = new JPanel( new GridBagLayout() );
        renderWrapper.setOpaque( false );
        renderWrapper.setBackground( new Color( 0, 0, 0, 0 ) );
        renderWrapper.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        renderWrapper.add( renderComponent, gbc );

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        renderWrapper.add( new RightImageBorder(), gbc );

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        renderWrapper.add( new ResizeRootBandComponent( true, allRenderer, renderContext ), gbc );

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        renderWrapper.add( new RightImageBorder(), gbc );

        layoutRendererComponent.add( renderWrapper );

        rootBandRenderers.add( renderComponent );
      }
    }

    layoutRendererComponent.add( new BottomImageBorder() );

    revalidate();
    repaint();
  }

  public ReportDocumentContext getRenderContext() {
    return renderContext;
  }

  private class ActionSelectedHandler implements PropertyChangeListener {
    private JToggleButton button;

    public ActionSelectedHandler( final JToggleButton aButton ) {
      this.button = aButton;
    }

    public void propertyChange( final PropertyChangeEvent event ) {
      final ToggleStateAction theAction = (ToggleStateAction) event.getSource();
      this.button.setSelected( theAction.isSelected() );
    }
  }
}
