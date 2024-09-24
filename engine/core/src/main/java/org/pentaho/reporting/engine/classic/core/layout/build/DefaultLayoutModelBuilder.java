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

package org.pentaho.reporting.engine.classic.core.layout.build;

import java.awt.Shape;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubReportType;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.TextProducer;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.ProgressMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.style.DynamicHeightWrapperStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.DynamicReplacedContentStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.NonDynamicHeightWrapperStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.NonDynamicReplacedContentStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SubReportStyleSheet;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.states.process.SubReportProcessType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.ReportDrawable;
import org.pentaho.reporting.engine.classic.core.util.ShapeDrawable;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

public class DefaultLayoutModelBuilder implements LayoutModelBuilder, Cloneable {
  private static final Log logger = LogFactory.getLog( DefaultLayoutModelBuilder.class );

  private OutputProcessorMetaData metaData;
  private ReportStateKey stateKey;
  private LayoutModelBuilderContext context;
  private RenderNodeFactory renderNodeFactory;
  private TextProducer textProducer;

  private boolean strictLegacyMode;
  private boolean limitedSubReports;
  private boolean collapseProgressMarker;
  private ProcessingContext processingContext;
  private String legacySectionName;
  private boolean designtime;

  public DefaultLayoutModelBuilder() {
    this( "Section-0" );
  }

  public DefaultLayoutModelBuilder( final String legacySectionName ) {
    this.collapseProgressMarker = true;
    this.legacySectionName = legacySectionName;
  }

  protected boolean isAllowMergeSection() {
    return limitedSubReports == false;
  }

  public void initialize( final ProcessingContext processingContext, final RenderBox parentBox,
      final RenderNodeFactory renderNodeFactory ) {
    if ( parentBox == null ) {
      throw new NullPointerException();
    }
    if ( processingContext == null ) {
      throw new NullPointerException();
    }

    if ( this.processingContext != processingContext ) {
      this.processingContext = processingContext;
      this.metaData = processingContext.getOutputProcessorMetaData();
      this.strictLegacyMode = metaData.isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY );
      this.designtime = metaData.isFeatureSupported( OutputProcessorFeature.DESIGNTIME );

      this.renderNodeFactory = renderNodeFactory;
      this.textProducer = createTextProducer();
    }

    this.context = new DefaultLayoutModelBuilderContext( null, parentBox );
  }

  protected TextProducer createTextProducer() {
    return new TextProducer( metaData );
  }

  protected ProcessingContext getProcessingContext() {
    return processingContext;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  public void updateState( final ReportStateKey stateKey ) {
    this.stateKey = stateKey;
  }

  public InstanceID startBox( final ReportElement element ) {
    final StyleSheet computedStyle = element.getComputedStyle();
    final String layout = (String) computedStyle.getStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_CANVAS );
    return startBox( element, computedStyle, layout, false );
  }

  private InstanceID startBox( final ReportElement element, final StyleSheet styleSheet, final String layout,
      final boolean auto ) {
    closeAutoGeneratedPostfixBoxes();

    if ( BandStyleKeys.LAYOUT_AUTO.equals( layout ) ) {
      this.context =
          new DefaultLayoutModelBuilderContext( this.context, renderNodeFactory.produceRenderBox( element, styleSheet,
              layout, stateKey ) );
    } else if ( BandStyleKeys.LAYOUT_INLINE.equals( layout ) ) {
      if ( this.context.getRenderBox().isAcceptInlineBoxes() == false ) {
        // parent context is not a inline-inside context.
        // So we need to create a auto-paragraph wrapper to open a inline-context
        this.context =
            new DefaultLayoutModelBuilderContext( this.context, renderNodeFactory.createAutoParagraph( element,
                styleSheet, stateKey ) );

        // PRD-3750 - A empty inline-band that creates a auto-paragraph reserves space on the vertical axis.
        if ( metaData.isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY )
            || metaData.isFeatureSupported( OutputProcessorFeature.PRD_3750 ) ) {
          this.context.setAutoGeneratedWrapperBox( true );

          this.context =
              new DefaultLayoutModelBuilderContext( this.context, renderNodeFactory.produceRenderBox( element,
                  styleSheet, DefaultRenderNodeFactory.LAYOUT_PARAGRAPH_LINEBOX, stateKey ) );
        }
      } else {
        this.context =
            new DefaultLayoutModelBuilderContext( this.context, renderNodeFactory.produceRenderBox( element,
                styleSheet, layout, stateKey ) );
      }
    } else if ( this.context.getRenderBox().isAcceptInlineBoxes() ) {
      // inline elements only accept inline element childs
      this.context =
          new DefaultLayoutModelBuilderContext( this.context, renderNodeFactory.produceRenderBox( element, styleSheet,
              BandStyleKeys.LAYOUT_INLINE, stateKey ) );
    } else if ( BandStyleKeys.LAYOUT_TABLE_CELL.equals( layout ) ) {
      // a table body always needs a table parent ..
      if ( LayoutNodeTypes.TYPE_BOX_TABLE_ROW != this.context.getRenderBox().getLayoutNodeType() ) {
        startBox( element, renderNodeFactory.createAutoGeneratedSectionStyleSheet( styleSheet ),
            BandStyleKeys.LAYOUT_TABLE_ROW, true );
      }
      this.context =
          new DefaultLayoutModelBuilderContext( this.context, renderNodeFactory.produceRenderBox( element, styleSheet,
              layout, stateKey ) );
    } else if ( BandStyleKeys.LAYOUT_TABLE_ROW.equals( layout ) ) {
      // a table body always needs a table parent ..
      if ( LayoutNodeTypes.TYPE_BOX_TABLE_SECTION != this.context.getRenderBox().getLayoutNodeType() ) {
        startBox( element, renderNodeFactory.createAutoGeneratedSectionStyleSheet( styleSheet ),
            BandStyleKeys.LAYOUT_TABLE_BODY, true );
      }
      this.context =
          new DefaultLayoutModelBuilderContext( this.context, renderNodeFactory.produceRenderBox( element, styleSheet,
              layout, stateKey ) );
    } else if ( BandStyleKeys.LAYOUT_TABLE_BODY.equals( layout ) || BandStyleKeys.LAYOUT_TABLE_FOOTER.equals( layout )
        || BandStyleKeys.LAYOUT_TABLE_HEADER.equals( layout ) ) {
      // a table body always needs a table parent ..
      if ( LayoutNodeTypes.TYPE_BOX_TABLE != this.context.getRenderBox().getLayoutNodeType() ) {
        startBox( element, renderNodeFactory.createAutoGeneratedSectionStyleSheet( styleSheet ),
            BandStyleKeys.LAYOUT_TABLE, true );
      }
      this.context =
          new DefaultLayoutModelBuilderContext( this.context, renderNodeFactory.produceRenderBox( element, styleSheet,
              layout, stateKey ) );
    } else {
      // handle ordinary elements, block, canvas, row ..
      this.context =
          new DefaultLayoutModelBuilderContext( this.context, renderNodeFactory.produceRenderBox( element, styleSheet,
              layout, stateKey ) );
    }

    this.context.setAutoGeneratedWrapperBox( auto );
    this.context.setEmpty( isEmptyElement( element, styleSheet, metaData ) );
    if ( !auto ) {
      if ( isControlBand( styleSheet ) ) {
        this.context.getRenderBox().getStaticBoxLayoutProperties().setPlaceholderBox(
            StaticBoxLayoutProperties.PlaceholderType.SECTION );
      } else {
        this.context.getRenderBox().getStaticBoxLayoutProperties().setPlaceholderBox(
            StaticBoxLayoutProperties.PlaceholderType.NONE );
      }
    }
    this.textProducer.startText();
    return this.context.getRenderBox().getInstanceId();
  }

  private static boolean isEmptyElement( final ReportElement band, final StyleSheet style,
      final OutputProcessorMetaData metaData ) {
    if ( isControlBand( style ) ) {
      return false;
    }

    if ( metaData.isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY ) ) {
      if ( band instanceof Band ) {
        final Band b = (Band) band;
        if ( b.getElementCount() > 0 ) {
          return false;
        }
      }
    }

    if ( BandStyleKeys.LAYOUT_AUTO.equals( style.getStyleProperty( BandStyleKeys.LAYOUT ) ) ) {
      // A auto-band is considered empty.
      return true;
    }

    // A band is not empty, if it has a defined minimum or preferred height
    if ( isLengthDefined( ElementStyleKeys.HEIGHT, style ) ) {
      return false;
    }
    if ( isLengthDefined( ElementStyleKeys.WIDTH, style ) ) {
      return false;
    }
    if ( isLengthDefined( ElementStyleKeys.POS_Y, style ) ) {
      return false;
    }
    if ( isLengthDefined( ElementStyleKeys.POS_X, style ) ) {
      return false;
    }
    if ( isLengthDefined( ElementStyleKeys.MIN_HEIGHT, style ) ) {
      return false;
    }
    if ( isLengthDefined( ElementStyleKeys.MIN_WIDTH, style ) ) {
      return false;
    }
    if ( isLengthDefined( ElementStyleKeys.PADDING_TOP, style ) ) {
      return false;
    }
    if ( isLengthDefined( ElementStyleKeys.PADDING_LEFT, style ) ) {
      return false;
    }
    if ( isLengthDefined( ElementStyleKeys.PADDING_BOTTOM, style ) ) {
      return false;
    }
    if ( isLengthDefined( ElementStyleKeys.PADDING_RIGHT, style ) ) {
      return false;
    }
    if ( BorderStyle.NONE.equals( style.getStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, BorderStyle.NONE ) ) == false ) {
      return false;
    }
    if ( BorderStyle.NONE.equals( style.getStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, BorderStyle.NONE ) ) == false ) {
      return false;
    }
    if ( BorderStyle.NONE.equals( style.getStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, BorderStyle.NONE ) ) == false ) {
      return false;
    }
    if ( BorderStyle.NONE.equals( style.getStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, BorderStyle.NONE ) ) == false ) {
      return false;
    }
    if ( style.getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR ) != null ) {
      return false;
    }

    if ( metaData.isExtraContentElement( band.getStyle(), band.getAttributes() ) ) {
      return false;
    }
    return true;
  }

  public static boolean isControlBand( final StyleSheet style ) {
    if ( style.getStyleProperty( BandStyleKeys.COMPUTED_SHEETNAME ) != null ) {
      return true;
    }
    if ( style.getStyleProperty( BandStyleKeys.BOOKMARK ) != null ) {
      return true;
    }
    if ( BandStyleKeys.LAYOUT_INLINE.equals( style.getStyleProperty( BandStyleKeys.LAYOUT ) ) == false ) {
      if ( Boolean.TRUE.equals( style.getStyleProperty( BandStyleKeys.PAGEBREAK_AFTER ) ) ) {
        return true;
      }
      if ( Boolean.TRUE.equals( style.getStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE ) ) ) {
        return true;
      }
    }
    return false;
  }

  private static boolean isLengthDefined( final StyleKey key, final StyleSheet styleSheet ) {
    if ( key.isInheritable() ) {
      if ( styleSheet.isLocalKey( key ) == false ) {
        return false;
      }
    }

    final Object o = styleSheet.getStyleProperty( key, null );
    if ( o == null ) {
      return false;
    }
    if ( o instanceof Number == false ) {
      return false;
    }
    final Number n = (Number) o;
    return n.doubleValue() != 0;
  }

  public void startSection() {
    final String layoutMode;
    if ( metaData.isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY ) ) {
      layoutMode = BandStyleKeys.LAYOUT_BLOCK;
    } else {
      layoutMode = BandStyleKeys.LAYOUT_AUTO;
    }

    final RenderBox renderBox = renderNodeFactory.produceSectionBox( layoutMode, null );
    if ( isAllowMergeSection() ) {
      this.context = new BandSectionLayoutModelBuilderContext( this.metaData, this.context, renderBox );
    } else {
      this.context = new DefaultLayoutModelBuilderContext( this.context, renderBox );
    }
    this.context.setEmpty( true );

    if ( legacySectionName != null ) {
      this.context.getRenderBox().setName( legacySectionName );
    }

    this.textProducer.startText();
  }

  public void startSection( final ReportElement element, final int sectionSize ) {
    final StyleSheet resolverStyleSheet = element.getComputedStyle();
    final String layoutMode;
    final boolean legacyMode = metaData.isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY );
    if ( legacyMode ) {
      layoutMode = BandStyleKeys.LAYOUT_BLOCK;
    } else {
      String layout = (String) resolverStyleSheet.getStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_AUTO );
      if ( BandStyleKeys.LAYOUT_INLINE.equals( layout ) && !this.context.getRenderBox().isAcceptInlineBoxes() ) {
        layoutMode = BandStyleKeys.LAYOUT_BLOCK;
      } else {
        layoutMode = layout;
      }
    }

    final GroupSection groupSection =
        new GroupSection( renderNodeFactory.produceRenderBox( element, resolverStyleSheet, layoutMode, null ),
            renderNodeFactory.createAutoGeneratedSectionStyleSheet( resolverStyleSheet ), sectionSize, legacyMode );
    this.context = new SectionLayoutModelBuilderContext( this.context, groupSection, legacyMode );
    this.context.setEmpty( true );

    if ( element instanceof GroupBody || element instanceof Group ) {
      // PRD-3154 - do we need to set placeholder to true?
      // todo: PRD-3154: This is black magic, placeholder box true is evil.
      // Need to evaluate side-effects of this beast. Is it safe for keep-together boxes?
      this.context.getRenderBox().getStaticBoxLayoutProperties().setPlaceholderBox(
          StaticBoxLayoutProperties.PlaceholderType.SECTION );
    }
    this.textProducer.startText();
  }

  private void closeAutoGeneratedPostfixBoxes() {
  }

  public boolean isEmptyElementsHaveSignificance() {
    if ( designtime ) {
      return true;
    }

    final RenderBox box = this.context.getRenderBox();
    return box.isEmptyNodesHaveSignificance();
  }

  public boolean isEmptyElementsHaveSignificanceInParent() {
    final LayoutModelBuilderContext parent = this.context.getParent();
    if ( parent == null ) {
      return false;
    }

    final RenderBox box = parent.getRenderBox();
    return box.isEmptyNodesHaveSignificance();
  }

  private void ensureEmptyChildIsAdded( final RenderBox parent, final ReportElement element ) {
    final StyleSheet resolverStyleSheet = element.getComputedStyle();
    final RenderBox box;
    if ( parent.isAcceptInlineBoxes() ) {
      box =
          renderNodeFactory.produceRenderBox( element, resolverStyleSheet, BandStyleKeys.LAYOUT_INLINE, getStateKey() );
    } else {
      box = renderNodeFactory.produceRenderBox( element, resolverStyleSheet, BandStyleKeys.LAYOUT_BLOCK, getStateKey() );
    }
    box.getStaticBoxLayoutProperties().setPlaceholderBox( StaticBoxLayoutProperties.PlaceholderType.SECTION );
    box.close();
    parent.addChild( box );
  }

  public void processContent( final ReportElement element, final Object computedValue, final Object rawValue ) {
    if ( computedValue == null ) {
      final StyleSheet resolvedStyle = element.getComputedStyle();
      final RenderBox parentRenderBox = this.context.getRenderBox();
      if ( parentRenderBox.isEmptyNodesHaveSignificance()
          || metaData.isExtraContentElement( resolvedStyle, element.getAttributes() ) ) {
        ensureEmptyChildIsAdded( parentRenderBox, element );
        this.context.setEmpty( false );
      }
      return;
    }

    if ( String.class.equals( computedValue.getClass() ) ) {
      processText( element, (String) computedValue, rawValue );
    } else if ( computedValue instanceof Shape ) {
      final StyleSheet resolvedStyle = element.getComputedStyle();
      final Shape shape = (Shape) computedValue;
      final ReportDrawable reportDrawable =
          new ShapeDrawable( shape, resolvedStyle.getBooleanStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO ) );
      processReportDrawable( element, reportDrawable, rawValue );
    } else if ( computedValue instanceof ReportDrawable ) {
      processReportDrawable( element, (ReportDrawable) computedValue, rawValue );
    } else if ( computedValue instanceof ImageContainer || computedValue instanceof DrawableWrapper ) {
      processReplacedContent( element, computedValue, rawValue );
    } else if ( DrawableWrapper.isDrawable( computedValue ) ) {
      processReplacedContent( element, new DrawableWrapper( computedValue ), rawValue );
    } else {
      processText( element, String.valueOf( computedValue ), rawValue );
    }
  }

  private boolean isTableContext( RenderNode node ) {
    while ( node != null ) {
      if ( ( node.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_TABLE ) == LayoutNodeTypes.MASK_BOX_TABLE ) {
        return true;
      }
      node = node.getParent();
    }
    return false;
  }

  protected void processText( final ReportElement element, String computedValue, final Object rawValue ) {
    final SimpleStyleSheet resolverStyleSheet = element.getComputedStyle();
    if ( computedValue != null && resolverStyleSheet.getBooleanStyleProperty( TextStyleKeys.TRIM_TEXT_CONTENT ) ) {
      computedValue = computedValue.trim();
    }

    if ( this.context.getRenderBox().isAcceptInlineBoxes() == false ) {
      final StyleSheet elementStyle;
      final int parentNodeType = this.context.getRenderBox().getLayoutNodeType();
      if ( strictLegacyMode && ( parentNodeType & LayoutNodeTypes.MASK_BOX_CANVAS ) == LayoutNodeTypes.MASK_BOX_CANVAS ) {
        // A table always claims all elements as dynamic. Use the max-height to limit the expansion of elements.
        if ( isTableContext( this.context.getRenderBox() ) == false
            && resolverStyleSheet.getBooleanStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT ) == false ) {
          elementStyle = new NonDynamicHeightWrapperStyleSheet( resolverStyleSheet );
        } else {
          elementStyle = new DynamicHeightWrapperStyleSheet( resolverStyleSheet );
        }
      } else {
        elementStyle = resolverStyleSheet;
      }

      this.textProducer.startText();

      final RenderBox renderBox = renderNodeFactory.createAutoParagraph( element, elementStyle, stateKey );
      final RenderNode[] renderNodes = textProducer.getRenderNodes( element, elementStyle, computedValue );
      renderBox.addChilds( renderNodes );
      renderBox.setRawValue( rawValue );

      this.context = new DefaultLayoutModelBuilderContext( this.context, renderBox );
      this.context.setEmpty( renderNodes.length == 0 && isEmptyElement( element, resolverStyleSheet, metaData ) );
      this.context = this.context.close();
    } else {
      final StyleSheet safeElementStyle = renderNodeFactory.createStyle( resolverStyleSheet );
      final RenderBox renderBox =
          renderNodeFactory.produceRenderBox( element, resolverStyleSheet, BandStyleKeys.LAYOUT_INLINE, stateKey );
      final RenderNode[] renderNodes = textProducer.getRenderNodes( element, safeElementStyle, computedValue );
      renderBox.addChilds( renderNodes );
      renderBox.setRawValue( rawValue );

      this.context = new DefaultLayoutModelBuilderContext( this.context, renderBox );
      this.context.setEmpty( renderNodes.length == 0 && isEmptyElement( element, resolverStyleSheet, metaData ) );
      this.context = this.context.close();
    }
  }

  protected void processReportDrawable( final ReportElement element, final ReportDrawable reportDrawable,
      final Object rawValue ) {

    final SimpleStyleSheet resolverStyleSheet = element.getComputedStyle();
    reportDrawable.setStyleSheet( resolverStyleSheet );
    reportDrawable.setConfiguration( processingContext.getConfiguration() );
    reportDrawable.setResourceBundleFactory( processingContext.getResourceBundleFactory() );

    if ( reportDrawable instanceof DrawableWrapper ) {
      processReplacedContent( element, reportDrawable, rawValue );
    } else {
      processReplacedContent( element, new DrawableWrapper( reportDrawable ), rawValue );
    }
  }

  protected void processReplacedContent( final ReportElement element, final Object value, final Object rawValue ) {
    final RenderBox box = this.context.getRenderBox();
    final SimpleStyleSheet resolverStyleSheet = element.getComputedStyle();

    final StyleSheet elementStyle;
    if ( box.isAcceptInlineBoxes() == false ) {
      if ( isTableContext( box ) == false
          && resolverStyleSheet.getBooleanStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT ) == false ) {
        elementStyle = new NonDynamicReplacedContentStyleSheet( resolverStyleSheet );
      } else {
        elementStyle = new DynamicReplacedContentStyleSheet( resolverStyleSheet );
      }
    } else {
      elementStyle = resolverStyleSheet;
    }

    final RenderableReplacedContentBox child =
        renderNodeFactory.createReplacedContent( element, elementStyle, value, rawValue, stateKey );
    child.setName( element.getName() );
    this.context.addChild( child );
    this.context.setEmpty( false );
  }

  public boolean finishBox() {
    boolean empty = this.context.isEmpty();
    if ( empty ) {
      if ( isEmptyElementsHaveSignificanceInParent() ) {
        this.context.setEmpty( false );
        empty = false;
      }
    }

    this.context = this.context.close();
    while ( this.context.isAutoGeneratedWrapperBox() ) {
      this.context = this.context.close();
    }
    return empty;
  }

  public void endSection() {
    this.context = this.context.close();
    while ( this.context.isAutoGeneratedWrapperBox() ) {
      this.context = this.context.close();
    }
  }

  public InstanceID createSubflowPlaceholder( final ReportElement element ) {
    final StyleSheet resolverStyleSheet = element.getComputedStyle();
    final RenderBox subReportBox =
        renderNodeFactory.produceSubReportPlaceholder( element, resolverStyleSheet, stateKey );
    this.context.addChild( subReportBox );
    this.context.setEmpty( false );
    return subReportBox.getInstanceId();
  }

  public InlineSubreportMarker processSubReport( final SubReport element ) {
    if ( isLimitedSubReports() ) {
      logger.debug( "Not adding subreport: Subreports in header or footer area are not allowed." );
      return null;
    }

    final RenderBox parentBox = this.context.getRenderBox();
    if ( parentBox.isAcceptInlineBoxes() ) {
      logger.warn( "Not adding subreport: Subreports in inline-contexts are not supported." );
      return null;
    }

    final StyleSheet resolverStyleSheet = element.getComputedStyle();
    final RenderBox subReportBox =
        renderNodeFactory.produceSubReportPlaceholder( element, resolverStyleSheet, stateKey );
    this.context.addChild( subReportBox );
    this.context.setEmpty( false );
    final InstanceID subReportBoxId = subReportBox.getInstanceId();
    // the box will be closed
    return new InlineSubreportMarker( element, subReportBoxId, SubReportProcessType.INLINE );
  }

  public boolean isEmpty() {
    return context.isEmpty();
  }

  public void print() {
    ModelPrinter.INSTANCE.print( context.getRenderBox() );
  }

  protected LayoutModelBuilderContext getContext() {
    return context;
  }

  protected void setContext( final LayoutModelBuilderContext context ) {
    this.context = context;
  }

  protected TextProducer getTextProducer() {
    return textProducer;
  }

  protected RenderNodeFactory getRenderNodeFactory() {
    return renderNodeFactory;
  }

  protected ReportStateKey getStateKey() {
    return stateKey;
  }

  public void startSubFlow( final InstanceID insertationPoint ) {
    final RenderBox box;
    if ( insertationPoint == null ) {
      throw new IllegalStateException();
    }

    final RenderBox rootBox = getLayoutRoot();
    box = (RenderBox) rootBox.findNodeById( insertationPoint );
    if ( box == null ) {
      dontPushBoxToContext();
    } else {
      pushBoxToContext( box, false );
    }
  }

  protected void pushBoxToContext( final RenderBox box, final boolean empty ) {
    this.context = new DefaultLayoutModelBuilderContext( this.context, box );
    this.context.setEmpty( empty );
    this.textProducer.startText();
  }

  protected void dontPushBoxToContext() {
    this.context = new DummyLayoutModelBuilderContext( this.context );
    this.context.setEmpty( true );
  }

  public void startSubFlow( final ReportElement element ) {
    final StyleSheet resolverStyleSheet = element.getComputedStyle();

    final RenderBox box;
    if ( metaData.isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY ) ) {
      final StyleSheet styleSheet =
          new SubReportStyleSheet( resolverStyleSheet.getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE ),
              ( resolverStyleSheet.getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_AFTER ) ) );

      final SimpleStyleSheet reportStyle = new SimpleStyleSheet( styleSheet );
      final BoxDefinition boxDefinition = renderNodeFactory.getBoxDefinition( reportStyle );
      box =
          new BlockRenderBox( reportStyle, element.getObjectID(), boxDefinition, SubReportType.INSTANCE, element
              .getAttributes(), null );
    } else {
      box = renderNodeFactory.produceRenderBox( element, resolverStyleSheet, BandStyleKeys.LAYOUT_BLOCK, stateKey );
    }

    box.getStaticBoxLayoutProperties().setPlaceholderBox( StaticBoxLayoutProperties.PlaceholderType.SECTION );
    if ( element.getName() != null ) {
      box.setName( "Banded-SubReport-Section" + ": name=" + element.getName() );
    } else {
      box.setName( "Banded-SubReport-Section" );
    }

    pushBoxToContext( box, false );
  }

  private RenderBox getLayoutRoot() {
    LayoutModelBuilderContext context = this.context;
    while ( context != null ) {
      if ( context.getParent() == null ) {
        return context.getRenderBox();
      }
      context = context.getParent();
    }
    throw new IllegalStateException();
  }

  public void suspendSubFlow() {
    this.context = this.context.getParent();
  }

  public void endSubFlow() {
    endSection();
  }

  public void addProgressMarkerBox() {
    final RenderBox parent = this.context.getRenderBox();
    final RenderNode child = parent.getLastChild();
    if ( isCollapseProgressMarker() && child != null && child.getNodeType() == LayoutNodeTypes.TYPE_BOX_PROGRESS_MARKER ) {
      final ProgressMarkerRenderBox markerRenderBox = (ProgressMarkerRenderBox) child;
      markerRenderBox.setStateKey( stateKey );
    } else {
      final ProgressMarkerRenderBox markerBox = new ProgressMarkerRenderBox();
      markerBox.setStateKey( stateKey );
      this.context.addChild( markerBox );
      markerBox.close();
    }
    this.context.setEmpty( false );
  }

  public void addManualPageBreakBox( final long range ) {
    final RenderBox breakIndicatorBox = renderNodeFactory.createPageBreakIndicatorBox( stateKey, range );

    this.context.addChild( breakIndicatorBox );
    this.context.setEmpty( false );
  }

  public void setCollapseProgressMarker( final boolean collapseProgressMarker ) {
    this.collapseProgressMarker = collapseProgressMarker;
  }

  public boolean isCollapseProgressMarker() {
    return collapseProgressMarker;
  }

  public void setLimitedSubReports( final boolean limitedSubReports ) {
    this.limitedSubReports = limitedSubReports;
  }

  public boolean isLimitedSubReports() {
    return limitedSubReports;
  }

  public DefaultLayoutModelBuilder clone() {
    try {
      return (DefaultLayoutModelBuilder) super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( e );
    }
  }

  public LayoutModelBuilder deriveForPageBreak() {
    final DefaultLayoutModelBuilder clone = clone();
    clone.context = context.deriveForPagebreak();
    return clone;
  }

  public LayoutModelBuilder deriveForStorage( final RenderBox clonedContent ) {
    final DefaultLayoutModelBuilder clone = clone();
    clone.context = context.deriveForStorage( clonedContent );
    return clone;
  }

  public void restoreStateAfterRollback() {
    LayoutModelBuilderContext c = context;
    while ( c != null ) {
      c.restoreStateAfterRollback();
      c = c.getParent();
    }
  }

  public void validateAfterCommit() {
    LayoutModelBuilderContext c = context;
    while ( c != null ) {
      c.validateAfterCommit();
      c = c.getParent();
    }
  }

  public void performParanoidModelCheck( final RenderBox logicalPageBox ) {
    LayoutModelBuilderContext c = context;
    while ( c != null ) {
      c.performParanoidModelCheck();

      final RenderBox renderBox = c.getRenderBox();
      testIsLogicalPageParent( renderBox, logicalPageBox );

      c = c.getParent();
    }
  }

  private void testIsLogicalPageParent( RenderBox b, final RenderBox logicalPageBox ) {
    while ( b != null ) {
      if ( b == logicalPageBox ) {
        return;
      }
      b = b.getParent();
    }
    throw new IllegalStateException();
  }

  public void legacyFlagNotEmpty() {
    context.setEmpty( false );
  }

  public void legacyAddPlaceholder( final ReportElement element ) {
    final RenderBox parentRenderBox = this.context.getRenderBox();
    ensureEmptyChildIsAdded( parentRenderBox, element );
    this.context.setEmpty( false );
  }

  public RenderNode dangerousRawAccess() {
    return context.getRenderBox();
  }

  public void close() {
  }

}
