/*
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
 * Copyright (c) 2001 - 2018 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.LocalImageContainer;
import org.pentaho.reporting.engine.classic.core.URLImageContainer;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.CollectSelectedNodesStep;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.process.RevalidateTextEllipseProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;
import org.pentaho.reporting.engine.classic.core.layout.text.Glyph;
import org.pentaho.reporting.engine.classic.core.layout.text.GlyphList;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

/**
 * The page drawable is the content provider for the Graphics2DOutputTarget. This component is responsible for rendering
 * the current page to a Graphics2D object.
 *
 * @author Thomas Morgner
 */
@SuppressWarnings( "HardCodedStringLiteral" )
public class LogicalPageDrawable extends IterateStructuralProcessStep implements PageDrawable {
  protected static class TextSpec {
    private boolean bold;
    private boolean italics;
    private String fontName;
    private float fontSize;
    private Graphics2D graphics;

    protected TextSpec( final StyleSheet layoutContext, final OutputProcessorMetaData metaData,
        final Graphics2D graphics ) {
      if ( graphics == null ) {
        throw new NullPointerException();
      }
      if ( metaData == null ) {
        throw new NullPointerException();
      }
      if ( layoutContext == null ) {
        throw new NullPointerException();
      }

      this.graphics = graphics;
      fontName = metaData.getNormalizedFontFamilyName( (String) layoutContext.getStyleProperty( TextStyleKeys.FONT ) );
      fontSize = (float) layoutContext.getDoubleStyleProperty( TextStyleKeys.FONTSIZE, 10 );
      bold = layoutContext.getBooleanStyleProperty( TextStyleKeys.BOLD );
      italics = layoutContext.getBooleanStyleProperty( TextStyleKeys.ITALIC );
    }

    public boolean isBold() {
      return bold;
    }

    public boolean isItalics() {
      return italics;
    }

    public String getFontName() {
      return fontName;
    }

    public float getFontSize() {
      return fontSize;
    }

    public Graphics2D getGraphics() {
      return graphics;
    }

    public void close() {
      graphics.dispose();
      graphics = null;
    }
  }

  private static class FontDecorationSpec {
    private double end;
    private double start;
    private double verticalPosition;
    private double lineWidth;
    private Color textColor;

    protected FontDecorationSpec() {
      start = -1;
      end = -1;
    }

    public Color getTextColor() {
      return textColor;
    }

    public void setTextColor( final Color textColor ) {
      this.textColor = textColor;
    }

    public void updateStart( final double start ) {
      if ( this.start < 0 ) {
        this.start = start;
      } else if ( start < this.start ) {
        this.start = start;
      }
    }

    public double getEnd() {
      return end;
    }

    public void updateEnd( final double end ) {
      if ( this.end < 0 ) {
        this.end = end;
      } else if ( end > this.end ) {
        this.end = end;
      }
    }

    public double getStart() {
      return start;
    }

    public double getLineWidth() {
      return lineWidth;
    }

    public void updateLineWidth( final double lineWidth ) {
      if ( lineWidth > this.lineWidth ) {
        this.lineWidth = lineWidth;
      }
    }

    public void updateVerticalPosition( final double verticalPosition ) {
      if ( verticalPosition > this.verticalPosition ) {
        this.verticalPosition = verticalPosition;
      }
    }

    public double getVerticalPosition() {
      return verticalPosition;
    }
  }

  private static class TableContext {
    private TableContext parent;
    private StrictBounds bounds;
    private StrictBounds drawArea;

    private TableContext( final TableContext parent ) {
      this.parent = parent;
      this.bounds = new StrictBounds();
      this.drawArea = new StrictBounds();
    }

    public StrictBounds getDrawArea() {
      return drawArea;
    }

    public StrictBounds getBounds() {
      return bounds;
    }

    public TableContext pop() {
      return parent;
    }
  }

  public static final BasicStroke DEFAULT_STROKE = new BasicStroke( 1 );
  private static final Log logger = LogFactory.getLog( LogicalPageDrawable.class );

  private FontDecorationSpec strikeThrough;
  private FontDecorationSpec underline;
  private boolean outlineMode;
  private LogicalPageBox rootBox;
  private OutputProcessorMetaData metaData;
  private PageFormat pageFormat;
  private double width;
  private double height;
  private CodePointBuffer codePointBuffer;
  private Graphics2D graphics;
  private boolean textLineOverflow;
  private long contentAreaX1;
  private long contentAreaX2;
  private RevalidateTextEllipseProcessStep revalidateTextEllipseProcessStep;
  private StrictBounds drawArea;
  // A reusable rectangle for rendering; not used for decisions
  private Rectangle2D.Double boxArea;
  private TextSpec textSpec;
  private boolean ellipseDrawn;
  private CollectSelectedNodesStep collectSelectedNodesStep;
  private BorderRenderer borderRenderer;
  private boolean drawPageBackground;
  private ResourceManager resourceManager;
  private boolean clipOnWordBoundary;
  private boolean strictClipping;
  private boolean unalignedPageBands;
  private TableContext tableContext;
  private FastStack<Graphics2D> graphicsContexts;
  private StrictBounds pageArea;

  public LogicalPageDrawable() {
    this.graphicsContexts = new FastStack<Graphics2D>();
    this.borderRenderer = new BorderRenderer();
    this.codePointBuffer = new CodePointBuffer( 400 );
    this.boxArea = new Rectangle2D.Double();
    this.drawPageBackground = true;
  }

  @Deprecated
  public LogicalPageDrawable( final LogicalPageBox rootBox, final OutputProcessorMetaData metaData,
      final ResourceManager resourceManager ) {
    this();
    init( rootBox, metaData, resourceManager );
  }

  public void init( final LogicalPageBox rootBox, final OutputProcessorMetaData metaData,
      final ResourceManager resourceManager ) {
    if ( rootBox == null ) {
      throw new NullPointerException();
    }
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }

    this.resourceManager = resourceManager;
    this.metaData = metaData;
    this.rootBox = rootBox;
    this.width = StrictGeomUtility.toExternalValue( rootBox.getPageWidth() );
    this.height = StrictGeomUtility.toExternalValue( rootBox.getPageHeight() );

    final Paper paper = new Paper();
    paper.setImageableArea( 0, 0, width, height );

    this.pageFormat = new PageFormat();
    this.pageFormat.setPaper( paper );

    this.strictClipping =
        "true".equals( metaData.getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.StrictClipping" ) );
    this.outlineMode =
        "true".equals( metaData.getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.debug.OutlineMode" ) );
    if ( "true".equals( metaData.getConfiguration().getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.debug.PrintPageContents" ) ) ) {
      ModelPrinter.INSTANCE.print( rootBox );
    }

    this.unalignedPageBands = metaData.isFeatureSupported( OutputProcessorFeature.UNALIGNED_PAGEBANDS );
    revalidateTextEllipseProcessStep = new RevalidateTextEllipseProcessStep( metaData );
    collectSelectedNodesStep = new CollectSelectedNodesStep();
    this.clipOnWordBoundary =
        "true".equals( metaData.getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.LastLineBreaksOnWordBoundary" ) );
  }

  public LogicalPageBox getLogicalPageBox() {
    return rootBox;
  }

  protected ResourceManager getResourceManager() {
    return resourceManager;
  }

  public boolean isClipOnWordBoundary() {
    return clipOnWordBoundary;
  }

  public boolean isOutlineMode() {
    return outlineMode;
  }

  public void setOutlineMode( final boolean outlineMode ) {
    this.outlineMode = outlineMode;
  }

  protected StrictBounds getDrawArea() {
    return drawArea;
  }

  public PageFormat getPageFormat() {
    return (PageFormat) pageFormat.clone();
  }

  /**
   * Returns the preferred size of the drawable. If the drawable is aspect ratio aware, these bounds should be used to
   * compute the preferred aspect ratio for this drawable.
   *
   * @return the preferred size.
   */
  public Dimension getPreferredSize() {
    return new Dimension( (int) width, (int) height );
  }

  public double getHeight() {
    return height;
  }

  public double getWidth() {
    return width;
  }

  /**
   * Returns true, if this drawable will preserve an aspect ratio during the drawing.
   *
   * @return true, if an aspect ratio is preserved, false otherwise.
   */
  @SuppressWarnings( "UnusedDeclaration" )
  public boolean isPreserveAspectRatio() {
    return true;
  }

  public boolean isDrawPageBackground() {
    return drawPageBackground;
  }

  public void setDrawPageBackground( final boolean drawPageBackground ) {
    this.drawPageBackground = drawPageBackground;
  }

  /**
   * Draws the object.
   *
   * @param graphics
   *          the graphics device.
   * @param area
   *          the area inside which the object should be drawn.
   */
  public void draw( final Graphics2D graphics, final Rectangle2D area ) {
    final Graphics2D g2 = (Graphics2D) graphics.create();
    if ( isDrawPageBackground() ) {
      g2.setPaint( Color.white );
      g2.fill( area );
    }
    g2.translate( -area.getX(), -area.getY() );

    try {
      final StrictBounds pageBounds =
          StrictGeomUtility.createBounds( area.getX(), area.getY(), area.getWidth(), area.getHeight() );
      this.pageArea = pageBounds;
      this.drawArea = pageBounds;
      this.graphics = g2;

      if ( startBlockBox( rootBox ) ) {
        processRootBand( pageBounds );
      }
      finishBlockBox( rootBox );
    } finally {
      this.graphics = null;
      this.drawArea = null;
      g2.dispose();
    }
  }

  protected void processRootBand( final StrictBounds pageBounds ) {
    final Shape clip = this.graphics.getClip();

    boolean watermarkOnTop = getMetaData().isFeatureSupported( OutputProcessorFeature.WATERMARK_PRINTED_ON_TOP );
    if ( !watermarkOnTop ) {
      startProcessing( rootBox.getWatermarkArea() );
    }

    final BlockRenderBox headerArea = rootBox.getHeaderArea();
    final BlockRenderBox footerArea = rootBox.getFooterArea();
    final BlockRenderBox repeatFooterArea = rootBox.getRepeatFooterArea();
    final StrictBounds headerBounds =
        new StrictBounds( headerArea.getX(), headerArea.getY(), headerArea.getWidth(), headerArea.getHeight() );
    final StrictBounds footerBounds =
        new StrictBounds( footerArea.getX(), footerArea.getY(), footerArea.getWidth(), footerArea.getHeight() );
    final StrictBounds repeatFooterBounds =
        new StrictBounds( repeatFooterArea.getX(), repeatFooterArea.getY(), repeatFooterArea.getWidth(),
            repeatFooterArea.getHeight() );
    final StrictBounds contentBounds =
        new StrictBounds( rootBox.getX(), headerArea.getY() + headerArea.getHeight(), rootBox.getWidth(),
            repeatFooterArea.getY() - headerArea.getHeight() );

    final double headerHeight = StrictGeomUtility.toExternalValue( drawArea.getHeight() );

    setDrawArea( headerBounds );
    this.graphics.clip( createClipRect( drawArea ) );
    startProcessing( headerArea );

    if ( unalignedPageBands ) {
      this.graphics.translate( 0, headerHeight );
    }

    setDrawArea( contentBounds );
    this.graphics.setClip( clip );
    this.graphics.clip( createClipRect( drawArea ) );
    processBoxChilds( rootBox );

    if ( unalignedPageBands ) {
      this.graphics.translate( 0, -headerHeight );
      this.graphics.translate( 0, height
          - StrictGeomUtility.toExternalValue( footerBounds.getHeight() + repeatFooterBounds.getHeight() ) );
    }

    setDrawArea( repeatFooterBounds );
    this.graphics.setClip( clip );
    this.graphics.clip( createClipRect( drawArea ) );
    startProcessing( repeatFooterArea );

    if ( unalignedPageBands ) {
      this.graphics.translate( 0, StrictGeomUtility.toExternalValue( repeatFooterBounds.getHeight() ) );
    }
    setDrawArea( footerBounds );
    this.graphics.setClip( clip );
    this.graphics.clip( createClipRect( drawArea ) );
    startProcessing( footerArea );

    setDrawArea( pageBounds );
    this.graphics.setClip( clip );

    if ( watermarkOnTop ) {
      startProcessing( rootBox.getWatermarkArea() );
      this.graphics.setClip( clip );
    }

  }

  protected Rectangle2D createClipRect( final StrictBounds bounds ) {
    return StrictGeomUtility.createAWTRectangle( bounds.getX() - 1, bounds.getY() - 1, bounds.getWidth() + 2, bounds
        .getHeight() + 2 );
  }

  protected LogicalPageBox getRootBox() {
    return rootBox;
  }

  protected void setDrawArea( final StrictBounds drawArea ) {
    this.drawArea = pageArea.createIntersection( drawArea );
  }

  protected void drawOutlineBox( final Graphics2D g2, final RenderBox box ) {
    final int nodeType = box.getNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      g2.setPaint( Color.magenta );
    } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_LINEBOX ) {
      g2.setPaint( Color.orange );
    } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_TABLE ) == LayoutNodeTypes.MASK_BOX_TABLE ) {
      g2.setPaint( Color.cyan );
    } else {
      g2.setPaint( Color.lightGray );
    }
    final double x = StrictGeomUtility.toExternalValue( box.getX() );
    final double y = StrictGeomUtility.toExternalValue( box.getY() );
    final double w = StrictGeomUtility.toExternalValue( box.getWidth() );
    final double h = StrictGeomUtility.toExternalValue( box.getHeight() );
    boxArea.setFrame( x, y, w, h );
    g2.draw( boxArea );
  }

  protected void processLinksAndAnchors( final RenderNode box ) {
    final StyleSheet styleSheet = box.getStyleSheet();
    final String target = (String) styleSheet.getStyleProperty( ElementStyleKeys.HREF_TARGET );
    final String title = (String) styleSheet.getStyleProperty( ElementStyleKeys.HREF_TITLE );
    if ( target != null || title != null ) {
      final String window = (String) styleSheet.getStyleProperty( ElementStyleKeys.HREF_WINDOW );
      drawHyperlink( box, target, window, title );
    }

    final String anchor = (String) styleSheet.getStyleProperty( ElementStyleKeys.ANCHOR_NAME );
    if ( anchor != null ) {
      drawAnchor( box );
    }

    final String bookmark = (String) styleSheet.getStyleProperty( BandStyleKeys.BOOKMARK );
    if ( bookmark != null ) {
      drawBookmark( box, bookmark );
    }
  }

  protected void drawBookmark( final RenderNode box, final String bookmark ) {
  }

  protected void drawHyperlink( final RenderNode box, final String target, final String window, final String title ) {
  }

  public boolean startCanvasBox( final CanvasRenderBox box ) {
    return startBox( box );
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    return startBox( box );
  }

  protected boolean startRowBox( final RenderBox box ) {
    return startBox( box );
  }

  protected boolean startTableCellBox( final TableCellRenderBox box ) {
    return startBox( box );
  }

  protected boolean startBox( final RenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    if ( box instanceof LogicalPageBox == false ) {
      if ( box.isBoxVisible( drawArea ) == false ) {
        box.isBoxVisible( drawArea );
        return false;
      }
    }

    renderBoxBorderAndBackground( box );

    processLinksAndAnchors( box );
    return true;
  }

  protected boolean startTableRowBox( final TableRowRenderBox box ) {
    return startBox( box );
  }

  protected boolean startTableSectionBox( final TableSectionRenderBox box ) {
    if ( box.getDisplayRole() != TableSectionRenderBox.Role.HEADER ) {
      final StrictBounds bounds = tableContext.getBounds();
      if ( bounds.getHeight() != 0 ) {
        // clip the printable area to an infinite large area below the header.
        // Pdf output has a limit of 32768 for its floating point numbers (16-bit),
        // any larger value yields an invalid clipping area.
        final StrictBounds clipBounds =
            new StrictBounds( bounds.getX(), bounds.getY() + bounds.getHeight(), StrictGeomUtility
                .toInternalValue( Short.MAX_VALUE ), StrictGeomUtility.toInternalValue( Short.MAX_VALUE ) );
        clip( clipBounds );
        tableContext.getDrawArea().setRect( drawArea );
        drawArea.setRect( drawArea.createIntersection( clipBounds ) );
      }
    }
    return startBox( box );
  }

  protected void finishTableSectionBox( final TableSectionRenderBox box ) {
    if ( box.getDisplayRole() == TableSectionRenderBox.Role.HEADER ) {
      tableContext.getBounds().setRect( box.getX(), box.getY(), box.getWidth(), box.getHeight() );
    } else if ( tableContext.getBounds().getHeight() != 0 ) {
      drawArea.setRect( tableContext.getDrawArea() );
      clearClipping();
    }
  }

  protected boolean startTableBox( final TableRenderBox box ) {
    tableContext = new TableContext( tableContext );
    return startBox( box );
  }

  protected void finishTableBox( final TableRenderBox box ) {
    tableContext = tableContext.pop();
  }

  protected boolean startTableColumnGroupBox( final TableColumnGroupNode box ) {
    return false;
  }

  protected boolean startAutoBox( final RenderBox box ) {
    return startBox( box );
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    if ( box.isBoxVisible( drawArea ) == false ) {
      return false;
    }

    renderBoxBorderAndBackground( box );

    TextSpec textSpec = getTextSpec();
    if ( textSpec != null ) {
      textSpec.close();
      setTextSpec( null );
    }

    final FontDecorationSpec newUnderlineSpec = computeUnderline( box, underline );
    if ( underline != null && newUnderlineSpec == null ) {
      drawTextDecoration( underline );
      underline = null;
    } else {
      underline = newUnderlineSpec;
    }

    final FontDecorationSpec newStrikeThroughSpec = computeStrikeThrough( box, strikeThrough );
    if ( strikeThrough != null && newStrikeThroughSpec == null ) {
      drawTextDecoration( strikeThrough );
      strikeThrough = null;
    } else {
      strikeThrough = newStrikeThroughSpec;
    }

    processLinksAndAnchors( box );
    return true;
  }

  protected boolean isIgnoreBorderWhenDrawingOutline() {
    return false;
  }

  protected void renderBoxBorderAndBackground( final RenderBox box ) {
    final Graphics2D g2 = getGraphics();
    if ( isOutlineMode() ) {
      drawOutlineBox( g2, box );
      if ( isIgnoreBorderWhenDrawingOutline() ) {
        return;
      }
    }

    if ( box.getBoxDefinition().getBorder().isEmpty() == false ) {
      borderRenderer.paintBackgroundAndBorder( box, g2 );
    } else {
      final Color backgroundColor = (Color) box.getStyleSheet().getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );
      if ( backgroundColor != null ) {
        final double x = StrictGeomUtility.toExternalValue( box.getX() );
        final double y = StrictGeomUtility.toExternalValue( box.getY() );
        final double w = StrictGeomUtility.toExternalValue( box.getWidth() );
        final double h = StrictGeomUtility.toExternalValue( box.getHeight() );
        boxArea.setFrame( x, y, w, h );
        g2.setColor( backgroundColor );
        g2.fill( boxArea );
      }
    }
  }

  protected Rectangle2D.Double getBoxArea() {
    return boxArea;
  }

  protected TextSpec getTextSpec() {
    return textSpec;
  }

  protected void setTextSpec( final TextSpec textSpec ) {
    this.textSpec = textSpec;
  }

  private FontDecorationSpec computeUnderline( final RenderBox box, FontDecorationSpec oldSpec ) {
    final StyleSheet styleSheet = box.getStyleSheet();
    if ( styleSheet.getBooleanStyleProperty( TextStyleKeys.UNDERLINED ) == false ) {
      return null;
    }
    if ( oldSpec == null ) {
      oldSpec = new FontDecorationSpec();
    }
    final double size = box.getStyleSheet().getDoubleStyleProperty( TextStyleKeys.FONTSIZE, 0 );
    final double lineWidth = Math.max( 1, size / 20.0 );
    oldSpec.updateLineWidth( lineWidth );
    oldSpec.setTextColor( (Color) box.getStyleSheet().getStyleProperty( ElementStyleKeys.PAINT ) );
    return oldSpec;
  }

  private FontDecorationSpec computeStrikeThrough( final RenderBox box, FontDecorationSpec oldSpec ) {
    final StyleSheet styleSheet = box.getStyleSheet();
    if ( styleSheet.getBooleanStyleProperty( TextStyleKeys.STRIKETHROUGH ) == false ) {
      return null;
    }
    if ( oldSpec == null ) {
      oldSpec = new FontDecorationSpec();
    }

    final double size = box.getStyleSheet().getDoubleStyleProperty( TextStyleKeys.FONTSIZE, 0 );
    final double lineWidth = Math.max( 1, size / 20.0 );
    oldSpec.updateLineWidth( lineWidth );
    oldSpec.setTextColor( (Color) box.getStyleSheet().getStyleProperty( ElementStyleKeys.PAINT ) );
    return oldSpec;
  }

  private boolean isStyleActive( final StyleKey key, final RenderBox parent ) {
    if ( ( parent.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_INLINE ) != LayoutNodeTypes.MASK_BOX_INLINE ) {
      return false;
    }
    return parent.getStyleSheet().getBooleanStyleProperty( key );
  }

  protected void finishInlineBox( final InlineRenderBox box ) {
    TextSpec textSpec = getTextSpec();
    if ( textSpec != null ) {
      textSpec.close();
      setTextSpec( null );
    }
    final RenderBox parent = box.getParent();
    if ( underline != null ) {
      if ( isStyleActive( TextStyleKeys.UNDERLINED, parent ) == false ) {
        // The parent has no underline style, but this box has. So finish up the underline.
        drawTextDecoration( underline );
        underline = null;
      }
    } else {
      // maybe this inlinebox has no underline, but the parent has ...
      underline = computeUnderline( box, null );
    }

    if ( strikeThrough != null ) {
      if ( isStyleActive( TextStyleKeys.STRIKETHROUGH, parent ) == false ) {
        // The parent has no underline style, but this box has. So finish up the underline.
        drawTextDecoration( strikeThrough );
        strikeThrough = null;
      }
    } else {
      strikeThrough = computeStrikeThrough( box, null );
    }
  }

  private void drawTextDecoration( final FontDecorationSpec decorationSpec ) {
    final Graphics2D graphics = (Graphics2D) getGraphics().create();
    graphics.setColor( decorationSpec.getTextColor() );
    graphics.setStroke( new BasicStroke( (float) decorationSpec.getLineWidth() ) );
    graphics.draw( new Line2D.Double( decorationSpec.getStart(), decorationSpec.getVerticalPosition(), decorationSpec
        .getEnd(), decorationSpec.getVerticalPosition() ) );
    graphics.dispose();
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    this.contentAreaX1 = box.getContentAreaX1();
    this.contentAreaX2 = box.getContentAreaX2();
    this.textSpec = null;

    RenderBox lineBox = (RenderBox) box.getFirstChild();
    if ( lineBox != null ) {
      final boolean needClipping = lineBox.getHeight() > box.getHeight() && !box.isBoxOverflowX();
      if ( needClipping ) {
        // clip
        StrictBounds safeBounds =
            new StrictBounds( lineBox.getX(), lineBox.getY(), lineBox.getWidth() * 3 / 2, lineBox.getHeight() );
        clip( safeBounds );
      }
      while ( lineBox != null ) {
        processTextLine( lineBox, contentAreaX1, contentAreaX2 );
        lineBox = (RenderBox) lineBox.getNext();
      }
      if ( needClipping ) {
        clearClipping();
      }
    }

    if ( textSpec != null ) {
      throw new IllegalStateException();
    }
  }

  protected void processTextLine( final RenderBox lineBox, final long contentAreaX1, final long contentAreaX2 ) {
    if ( lineBox.isNodeVisible( drawArea ) == false ) {
      return;
    }

    final boolean overflowProperty = lineBox.getParent().getStaticBoxLayoutProperties().isOverflowX();
    this.textLineOverflow = ( ( lineBox.getX() + lineBox.getWidth() ) > contentAreaX2 ) && overflowProperty == false;

    this.ellipseDrawn = false;
    if ( textLineOverflow ) {
      revalidateTextEllipseProcessStep.compute( lineBox, contentAreaX1, contentAreaX2 );
    }

    underline = null;
    strikeThrough = null;

    startProcessing( lineBox );
  }

  public long getContentAreaX2() {
    return contentAreaX2;
  }

  public void setContentAreaX2( final long contentAreaX2 ) {
    this.contentAreaX2 = contentAreaX2;
  }

  public long getContentAreaX1() {
    return contentAreaX1;
  }

  public void setContentAreaX1( final long contentAreaX1 ) {
    this.contentAreaX1 = contentAreaX1;
  }

  public boolean isTextLineOverflow() {
    return textLineOverflow;
  }

  public void setTextLineOverflow( final boolean textLineOverflow ) {
    this.textLineOverflow = textLineOverflow;
  }

  protected void processOtherNode( final RenderNode node ) {
    if ( node.isNodeVisible( drawArea ) == false ) {
      return;
    }

    final int type = node.getNodeType();
    if ( isTextLineOverflow() ) {
      if ( node.isVirtualNode() ) {
        if ( ellipseDrawn == false ) {
          if ( isClipOnWordBoundary() == false && type == LayoutNodeTypes.TYPE_NODE_TEXT ) {
            final RenderableText text = (RenderableText) node;
            final long ellipseSize = extractEllipseSize( node );
            final long x1 = text.getX();
            final long effectiveAreaX2 = ( contentAreaX2 - ellipseSize );

            if ( x1 < contentAreaX2 ) {
              // The text node that is printed will overlap with the ellipse we need to print.
              drawText( text, effectiveAreaX2 );
            }
          } else if ( isClipOnWordBoundary() == false && type == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
            final RenderableComplexText text = (RenderableComplexText) node;
            // final long ellipseSize = extractEllipseSize(node);
            final long x1 = text.getX();
            // final long effectiveAreaX2 = (contentAreaX2 - ellipseSize);

            if ( x1 < contentAreaX2 ) {
              // The text node that is printed will overlap with the ellipse we need to print.
              final Graphics2D g2;
              if ( getTextSpec() == null ) {
                g2 = (Graphics2D) getGraphics().create();
                final StyleSheet layoutContext = text.getStyleSheet();
                configureGraphics( layoutContext, g2 );
                g2.setStroke( LogicalPageDrawable.DEFAULT_STROKE );

                if ( RenderUtility.isFontSmooth( layoutContext, metaData ) ) {
                  g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
                } else {
                  g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
                }
              } else {
                g2 = getTextSpec().getGraphics();
              }

              drawComplexText( text, g2 );
            }
          }

          ellipseDrawn = true;

          final RenderBox parent = node.getParent();
          if ( parent != null ) {
            final RenderBox textEllipseBox = parent.getTextEllipseBox();
            if ( textEllipseBox != null ) {
              processBoxChilds( textEllipseBox );
            }
          }
          return;
        }
      }
    }

    if ( type == LayoutNodeTypes.TYPE_NODE_TEXT ) {
      final RenderableText text = (RenderableText) node;
      if ( underline != null ) {
        final ExtendedBaselineInfo baselineInfo = text.getBaselineInfo();
        final long underlinePos = text.getY() + baselineInfo.getUnderlinePosition();
        underline.updateVerticalPosition( StrictGeomUtility.toExternalValue( underlinePos ) );
        underline.updateStart( StrictGeomUtility.toExternalValue( text.getX() ) );
        underline.updateEnd( StrictGeomUtility.toExternalValue( text.getX() + text.getWidth() ) );
      }

      if ( strikeThrough != null ) {
        final ExtendedBaselineInfo baselineInfo = text.getBaselineInfo();
        final long strikethroughPos = text.getY() + baselineInfo.getStrikethroughPosition();
        strikeThrough.updateVerticalPosition( StrictGeomUtility.toExternalValue( strikethroughPos ) );
        strikeThrough.updateStart( StrictGeomUtility.toExternalValue( text.getX() ) );
        strikeThrough.updateEnd( StrictGeomUtility.toExternalValue( text.getX() + text.getWidth() ) );
      }

      if ( isTextLineOverflow() ) {
        final long ellipseSize = extractEllipseSize( node );
        final long x1 = text.getX();
        final long x2 = x1 + text.getWidth();
        final long effectiveAreaX2 = ( contentAreaX2 - ellipseSize );
        if ( x2 <= effectiveAreaX2 ) {
          // the text will be fully visible.
          drawText( text );
        } else {
          if ( x1 < contentAreaX2 ) {
            // The text node that is printed will overlap with the ellipse we need to print.
            drawText( text, effectiveAreaX2 );
          }

          final RenderBox parent = node.getParent();
          if ( parent != null ) {
            final RenderBox textEllipseBox = parent.getTextEllipseBox();
            if ( textEllipseBox != null ) {
              processBoxChilds( textEllipseBox );
            }
          }

          ellipseDrawn = true;
        }
      } else {
        drawText( text );
      }
    } else if ( type == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
      final RenderableComplexText text = (RenderableComplexText) node;
      final long x1 = text.getX();

      if ( x1 < contentAreaX2 ) {
        // The text node that is printed will overlap with the ellipse we need to print.
        final Graphics2D g2;
        if ( getTextSpec() == null ) {
          g2 = (Graphics2D) getGraphics().create();
          final StyleSheet layoutContext = text.getStyleSheet();
          configureGraphics( layoutContext, g2 );
          g2.setStroke( LogicalPageDrawable.DEFAULT_STROKE );

          if ( RenderUtility.isFontSmooth( layoutContext, metaData ) ) {
            g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
          } else {
            g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
          }
        } else {
          g2 = getTextSpec().getGraphics();
        }

        drawComplexText( text, g2 );
      }
    }
  }

  protected void processRenderableContent( final RenderableReplacedContentBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return;
    }

    if ( box.isBoxVisible( drawArea ) == false ) {
      return;
    }

    renderBoxBorderAndBackground( box );
    processLinksAndAnchors( box );
    drawReplacedContent( box );
  }

  private long extractEllipseSize( final RenderNode node ) {
    if ( node == null ) {
      return 0;
    }
    final RenderBox parent = node.getParent();
    if ( parent == null ) {
      return 0;
    }
    final RenderBox textEllipseBox = parent.getTextEllipseBox();
    if ( textEllipseBox == null ) {
      return 0;
    }
    return textEllipseBox.getWidth();
  }

  protected void drawReplacedContent( final RenderableReplacedContentBox content ) {
    final Graphics2D g2 = getGraphics();
    final Object o = content.getContent().getRawObject();
    if ( o instanceof Image ) {
      drawImage( content, (Image) o );
    } else if ( o instanceof DrawableWrapper ) {
      final DrawableWrapper d = (DrawableWrapper) o;
      drawDrawable( content, g2, d );
    } else if ( o instanceof LocalImageContainer ) {
      final LocalImageContainer imageContainer = (LocalImageContainer) o;
      final Image image = imageContainer.getImage();
      drawImage( content, image );
    } else if ( o instanceof URLImageContainer ) {
      final URLImageContainer imageContainer = (URLImageContainer) o;
      if ( imageContainer.isLoadable() == false ) {
        LogicalPageDrawable.logger.info( "URL-image cannot be rendered, as it was declared to be not loadable." );
        return;
      }

      final ResourceKey sourceURL = imageContainer.getResourceKey();
      if ( sourceURL == null ) {
        LogicalPageDrawable.logger.info( "URL-image cannot be rendered, as it did not return a valid URL." );
      }

      try {
        final Resource resource = resourceManager.create( sourceURL, null, Image.class );
        final Image image = (Image) resource.getResource();
        drawImage( content, image );
      } catch ( ResourceException e ) {
        LogicalPageDrawable.logger.info( "URL-image cannot be rendered, as the image was not loadable.", e );
      }
    } else {
      LogicalPageDrawable.logger.debug( "Unable to handle " + o );
    }
  }

  /**
   * To be overriden in the PDF drawable.
   *
   * @param content
   *          the render-node that defines the anchor.
   */
  protected void drawAnchor( final RenderNode content ) {

  }

  /**
   * @param content
   * @param image
   */
  protected boolean drawImage( final RenderableReplacedContentBox content, Image image ) {
    final StyleSheet layoutContext = content.getStyleSheet();
    final boolean shouldScale = layoutContext.getBooleanStyleProperty( ElementStyleKeys.SCALE );

    final int x = (int) StrictGeomUtility.toExternalValue( content.getX() );
    final int y = (int) StrictGeomUtility.toExternalValue( content.getY() );
    final int width = (int) StrictGeomUtility.toExternalValue( content.getWidth() );
    final int height = (int) StrictGeomUtility.toExternalValue( content.getHeight() );

    if ( width == 0 || height == 0 ) {
      LogicalPageDrawable.logger.debug( "Error: Image area is empty: " + content );
      return false;
    }

    WaitingImageObserver obs = new WaitingImageObserver( image );
    obs.waitImageLoaded();
    final int imageWidth = image.getWidth( obs );
    final int imageHeight = image.getHeight( obs );
    if ( imageWidth < 1 || imageHeight < 1 ) {
      return false;
    }

    final Rectangle2D.Double drawAreaBounds = new Rectangle2D.Double( x, y, width, height );
    final AffineTransform scaleTransform;

    final Graphics2D g2;
    if ( shouldScale == false ) {
      double deviceScaleFactor = 1;
      final double devResolution = metaData.getNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION );
      if ( metaData.isFeatureSupported( OutputProcessorFeature.IMAGE_RESOLUTION_MAPPING ) ) {
        if ( devResolution != 72.0 && devResolution > 0 ) {
          // Need to scale the device to its native resolution before attempting to draw the image..
          deviceScaleFactor = ( 72.0 / devResolution );
        }
      }

      final int clipWidth = Math.min( width, (int) Math.ceil( deviceScaleFactor * imageWidth ) );
      final int clipHeight = Math.min( height, (int) Math.ceil( deviceScaleFactor * imageHeight ) );
      final ElementAlignment horizontalAlignment =
          (ElementAlignment) layoutContext.getStyleProperty( ElementStyleKeys.ALIGNMENT );
      final ElementAlignment verticalAlignment =
          (ElementAlignment) layoutContext.getStyleProperty( ElementStyleKeys.VALIGNMENT );
      final int alignmentX = (int) RenderUtility.computeHorizontalAlignment( horizontalAlignment, width, clipWidth );
      final int alignmentY = (int) RenderUtility.computeVerticalAlignment( verticalAlignment, height, clipHeight );

      g2 = (Graphics2D) getGraphics().create();
      g2.clip( drawAreaBounds );
      g2.translate( x, y );
      g2.translate( alignmentX, alignmentY );
      g2.clip( new Rectangle2D.Float( 0, 0, clipWidth, clipHeight ) );
      g2.scale( deviceScaleFactor, deviceScaleFactor );

      scaleTransform = null;
    } else {
      g2 = (Graphics2D) getGraphics().create();
      g2.clip( drawAreaBounds );
      g2.translate( x, y );
      g2.clip( new Rectangle2D.Float( 0, 0, width, height ) );

      final double scaleX;
      final double scaleY;

      final boolean keepAspectRatio = layoutContext.getBooleanStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO );
      if ( keepAspectRatio ) {
        final double scaleFactor = Math.min( width / (double) imageWidth, height / (double) imageHeight );
        scaleX = scaleFactor;
        scaleY = scaleFactor;
      } else {
        scaleX = width / (double) imageWidth;
        scaleY = height / (double) imageHeight;
      }

      final int clipWidth = (int) ( scaleX * imageWidth );
      final int clipHeight = (int) ( scaleY * imageHeight );

      final ElementAlignment horizontalAlignment =
          (ElementAlignment) layoutContext.getStyleProperty( ElementStyleKeys.ALIGNMENT );
      final ElementAlignment verticalAlignment =
          (ElementAlignment) layoutContext.getStyleProperty( ElementStyleKeys.VALIGNMENT );
      final int alignmentX = (int) RenderUtility.computeHorizontalAlignment( horizontalAlignment, width, clipWidth );
      final int alignmentY = (int) RenderUtility.computeVerticalAlignment( verticalAlignment, height, clipHeight );

      g2.translate( alignmentX, alignmentY );

      final Object contentCached = content.getContent().getContentCached();
      if ( contentCached instanceof Image ) {
        image = (Image) contentCached;
        scaleTransform = null;
      } else if ( metaData.isFeatureSupported( OutputProcessorFeature.PREFER_NATIVE_SCALING ) == false ) {
        image =
            RenderUtility.scaleImage( image, clipWidth, clipHeight, RenderingHints.VALUE_INTERPOLATION_BICUBIC, true );
        content.getContent().setContentCached( image );
        obs = new WaitingImageObserver( image );
        obs.waitImageLoaded();
        scaleTransform = null;
      } else {
        scaleTransform = AffineTransform.getScaleInstance( scaleX, scaleY );
      }
    }

    while ( g2.drawImage( image, scaleTransform, obs ) == false ) {
      obs.waitImageLoaded();
      if ( obs.isError() ) {
        LogicalPageDrawable.logger.warn( "Error while loading the image during the rendering." );
        break;
      }
    }

    g2.dispose();
    return true;
  }

  protected boolean drawDrawable( final RenderableReplacedContentBox content, final Graphics2D g2,
      final DrawableWrapper d ) {
    final double x = StrictGeomUtility.toExternalValue( content.getX() );
    final double y = StrictGeomUtility.toExternalValue( content.getY() );
    final double width = StrictGeomUtility.toExternalValue( content.getWidth() );
    final double height = StrictGeomUtility.toExternalValue( content.getHeight() );

    if ( ( width < 0 || height < 0 ) || ( width == 0 && height == 0 ) ) {
      return false;
    }

    final Graphics2D clone = (Graphics2D) g2.create();

    final StyleSheet styleSheet = content.getStyleSheet();
    final Object attribute = styleSheet.getStyleProperty( ElementStyleKeys.ANTI_ALIASING );
    if ( attribute != null ) {
      if ( Boolean.TRUE.equals( attribute ) ) {
        clone.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
      } else if ( Boolean.FALSE.equals( attribute ) ) {
        clone.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
      }

    }
    if ( RenderUtility.isFontSmooth( styleSheet, metaData ) ) {
      clone.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
    } else {
      clone.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
    }

    if ( strictClipping == false ) {
      final double extraPadding;
      final Object o = styleSheet.getStyleProperty( ElementStyleKeys.STROKE );
      if ( o instanceof BasicStroke ) {
        final BasicStroke stroke = (BasicStroke) o;
        extraPadding = stroke.getLineWidth() / 2.0;
      } else {
        extraPadding = 0.5;
      }

      final Rectangle2D.Double clipBounds =
          new Rectangle2D.Double( x - extraPadding, y - extraPadding, width + 2 * extraPadding, height + 2
              * extraPadding );

      clone.clip( clipBounds );
      clone.translate( x, y );
    } else {
      final Rectangle2D.Double clipBounds = new Rectangle2D.Double( x, y, width + 1, height + 1 );

      clone.clip( clipBounds );
      clone.translate( x, y );
    }
    configureGraphics( styleSheet, clone );
    configureStroke( styleSheet, clone );
    final Rectangle2D.Double bounds = new Rectangle2D.Double( 0, 0, width, height );
    d.draw( clone, bounds );
    clone.dispose();
    return true;
  }

  protected void drawText( final RenderableText renderableText ) {
    drawText( renderableText, renderableText.getX() + renderableText.getWidth() );
  }

  /**
   * Renders the glyphs stored in the text node.
   *
   * @param renderableText
   *          the text node that should be rendered.
   * @param contentX2
   */
  protected void drawText( final RenderableText renderableText, final long contentX2 ) {
    if ( renderableText.getLength() == 0 ) {
      // This text is empty.
      return;
    }

    final long posX = renderableText.getX();
    final long posY = renderableText.getY();

    final Graphics2D g2;
    if ( getTextSpec() == null ) {
      g2 = (Graphics2D) getGraphics().create();
      final StyleSheet layoutContext = renderableText.getStyleSheet();
      configureGraphics( layoutContext, g2 );
      g2.setStroke( LogicalPageDrawable.DEFAULT_STROKE );

      if ( RenderUtility.isFontSmooth( layoutContext, metaData ) ) {
        g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
      } else {
        g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
      }
    } else {
      g2 = getTextSpec().getGraphics();
    }

    // This shifting is necessary to make sure that all text is rendered like in the previous versions.
    // In the earlier versions, we did not really obey to the baselines of the text, we just hoped and prayed.
    // Therefore, all text was printed at the bottom of the text elements. With the introduction of the full
    // font metrics setting, this situation got a little bit better, for the price that text-elements became
    // nearly unpredictable ..
    //
    // The code below may be weird, but at least it is predictable weird.

    final FontMetrics fm = g2.getFontMetrics();
    final Rectangle2D rect = fm.getMaxCharBounds( g2 );
    final long awtBaseLine = StrictGeomUtility.toInternalValue( -rect.getY() );

    final GlyphList gs = renderableText.getGlyphs();
    if ( metaData.isFeatureSupported( OutputProcessorFeature.FAST_FONTRENDERING )
        && isNormalTextSpacing( renderableText ) ) {
      final int maxLength = renderableText.computeMaximumTextSize( contentX2 );
      final String text = gs.getText( renderableText.getOffset(), maxLength, codePointBuffer );
      final float y = (float) StrictGeomUtility.toExternalValue( posY + awtBaseLine );
      g2.drawString( text, (float) StrictGeomUtility.toExternalValue( posX ), y );
    } else {
      final ExtendedBaselineInfo baselineInfo = renderableText.getBaselineInfo();
      final int maxPos = renderableText.getOffset() + renderableText.computeMaximumTextSize( contentX2 );
      long runningPos = posX;
      final long baseline = baselineInfo.getBaseline( baselineInfo.getDominantBaseline() );
      final long baselineDelta = awtBaseLine - baseline;
      final float y = (float) ( StrictGeomUtility.toExternalValue( posY + awtBaseLine + baselineDelta ) );
      for ( int i = renderableText.getOffset(); i < maxPos; i++ ) {
        final Glyph g = gs.getGlyph( i );
        g2.drawString( gs.getGlyphAsString( i, codePointBuffer ), (float) StrictGeomUtility
            .toExternalValue( runningPos ), y );
        runningPos += RenderableText.convert( g.getWidth() ) + g.getSpacing().getMinimum();
      }
    }
    g2.dispose();
  }

  protected void drawComplexText( final RenderableComplexText renderableComplexText, final Graphics2D g2 ) {
    final long posX = renderableComplexText.getX();
    final long posY = renderableComplexText.getY();

    float baseline = renderableComplexText.getParagraphFontMetrics().getAscent();
    final float y = (float) StrictGeomUtility.toExternalValue( posY ) + baseline;

    renderableComplexText.getTextLayout().draw( g2, (float) StrictGeomUtility.toExternalValue( posX ), y );

    g2.dispose();
  }

  protected final CodePointBuffer getCodePointBuffer() {
    return codePointBuffer;
  }

  protected boolean isNormalTextSpacing( final RenderableText text ) {
    return text.isNormalTextSpacing();
  }

  protected void configureStroke( final StyleSheet layoutContext, final Graphics2D g2 ) {
    final Stroke styleProperty = (Stroke) layoutContext.getStyleProperty( ElementStyleKeys.STROKE );
    if ( styleProperty != null ) {
      g2.setStroke( styleProperty );
    } else {
      // Apply a default one ..
      g2.setStroke( LogicalPageDrawable.DEFAULT_STROKE );
    }
  }

  protected void configureGraphics( final StyleSheet layoutContext, final Graphics2D g2 ) {
    final boolean bold = layoutContext.getBooleanStyleProperty( TextStyleKeys.BOLD );
    final boolean italics = layoutContext.getBooleanStyleProperty( TextStyleKeys.ITALIC );

    int style = Font.PLAIN;
    if ( bold ) {
      style |= Font.BOLD;
    }
    if ( italics ) {
      style |= Font.ITALIC;
    }

    final Color cssColor = (Color) layoutContext.getStyleProperty( ElementStyleKeys.PAINT );
    g2.setColor( cssColor );

    final int fontSize =
        layoutContext.getIntStyleProperty( TextStyleKeys.FONTSIZE, (int) metaData
            .getNumericFeatureValue( OutputProcessorFeature.DEFAULT_FONT_SIZE ) );

    final String fontName =
        metaData.getNormalizedFontFamilyName( (String) layoutContext.getStyleProperty( TextStyleKeys.FONT ) );
    g2.setFont( new Font( fontName, style, fontSize ) );
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  public void clip( final StrictBounds bounds ) {
    final Graphics2D g = getGraphics();
    graphicsContexts.push( g );

    graphics = (Graphics2D) g.create();
    graphics.clip( StrictGeomUtility.createAWTRectangle( bounds ) );
  }

  public void clearClipping() {
    graphics.dispose();
    graphics = graphicsContexts.pop();
  }

  public Graphics2D getGraphics() {
    return graphics;
  }

  /**
   * Retries the nodes under the given coordinate which have a given attribute set. If name and namespace are null, all
   * nodes are returned. The nodes returned are listed in their respective hierarchical order.
   *
   * @param x
   *          the x coordinate
   * @param y
   *          the y coordinate
   * @param namespace
   *          the namespace on which to filter on
   * @param name
   *          the name on which to filter on
   * @return the ordered list of nodes.
   */
  public RenderNode[] getNodesAt( final double x, final double y, final String namespace, final String name ) {
    return collectSelectedNodesStep.getNodesAt( this.rootBox, StrictGeomUtility.createBounds( x, y, 1, 1 ), namespace,
        name );
  }

  public RenderNode[] getNodesAt( final double x, final double y, final double width, final double height,
      final String namespace, final String name ) {
    return collectSelectedNodesStep.getNodesAt( this.rootBox, StrictGeomUtility.createBounds( x, y, width, height ),
        namespace, name );
  }

}
