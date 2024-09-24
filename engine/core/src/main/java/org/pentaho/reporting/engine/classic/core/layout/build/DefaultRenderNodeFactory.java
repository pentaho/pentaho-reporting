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

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.IncompatibleFeatureException;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.AutoRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.BreakMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.SectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinitionFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.style.DefaultStyleCache;
import org.pentaho.reporting.engine.classic.core.layout.style.ManualBreakIndicatorStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.NonPaddingStyleCache;
import org.pentaho.reporting.engine.classic.core.layout.style.ParagraphPoolboxStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SectionKeepTogetherStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.StyleCache;
import org.pentaho.reporting.engine.classic.core.layout.style.UseMinChunkWidthStyleSheet;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

public class DefaultRenderNodeFactory implements RenderNodeFactory {
  public static final String LAYOUT_PARAGRAPH_LINEBOX = "::paragraph-linebox";
  private static StyleSheet SECTION_DEFAULT_STYLE = new SimpleStyleSheet( new UseMinChunkWidthStyleSheet( true ) );

  private SimpleStyleSheet bandWithoutKeepTogetherStyle;
  private BoxDefinitionFactory boxDefinitionFactory;
  private StyleCache bandCache;
  private SimpleStyleSheet manualBreakBoxStyle;
  private boolean strictCompatibilityMode;
  private OutputProcessorMetaData metaData;

  public DefaultRenderNodeFactory() {
    this.boxDefinitionFactory = new BoxDefinitionFactory();
    this.bandWithoutKeepTogetherStyle = new SimpleStyleSheet( new SectionKeepTogetherStyleSheet( false ) );
  }

  public void initialize( final OutputProcessorMetaData metaData ) {
    this.metaData = metaData;
    this.bandCache = new DefaultStyleCache( "rnf" );

    final boolean paddingsDisabled = metaData.isFeatureSupported( OutputProcessorFeature.DISABLE_PADDING );
    if ( paddingsDisabled ) {
      this.bandCache = new NonPaddingStyleCache( bandCache );
    }

    this.strictCompatibilityMode = metaData.isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY );
  }

  public LogicalPageBox createPage( final ReportDefinition report, final StyleSheet style ) {
    if ( report == null ) {
      throw new NullPointerException();
    }
    final SimpleStyleSheet reportStyle = bandCache.getStyleSheet( style );
    final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( reportStyle );
    return new LogicalPageBox( report, reportStyle, boxDefinition );
  }

  public RenderBox produceSectionBox( final String layoutType, final ReportStateKey stateKey ) {
    if ( strictCompatibilityMode ) {
      final BoxDefinition boxDefinition = BoxDefinition.EMPTY;
      return new SectionRenderBox( bandWithoutKeepTogetherStyle, new InstanceID(), boxDefinition,
          AutoLayoutBoxType.INSTANCE, ReportAttributeMap.emptyMap(), stateKey );
    }

    final RenderBox renderBox =
        createBox( layoutType, stateKey, SECTION_DEFAULT_STYLE, AutoLayoutBoxType.INSTANCE, ReportAttributeMap
            .emptyMap(), new InstanceID() );
    renderBox.getStaticBoxLayoutProperties().setPlaceholderBox( StaticBoxLayoutProperties.PlaceholderType.SECTION );
    renderBox.getStaticBoxLayoutProperties().setSectionContext( true );
    return renderBox;
  }

  public RenderBox produceRenderBox( final ReportElement band, final StyleSheet style, final String layoutType,
      final ReportStateKey stateKey ) {
    final ElementType elementType = band.getElementType();

    final RenderBox box =
        createBox( layoutType, stateKey, style, elementType, band.getAttributes(), band.getObjectID() );

    // for the sake of debugging ..
    final String name = band.getName();
    if ( name != null && name.length() != 0 && name.startsWith( Band.ANONYMOUS_BAND_PREFIX ) == false ) {
      box.setName( name );
    }
    return box;
  }

  private RenderBox createBox( final String layoutType, final ReportStateKey stateKey,
      final StyleSheet elementStyleSheet, final ElementType elementType, final ReportAttributeMap attributes,
      final InstanceID objectID ) {
    if ( BandStyleKeys.LAYOUT_AUTO.equals( layoutType ) ) {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( elementStyleSheet );
      return new AutoRenderBox( objectID, stateKey, styleSheet, attributes, elementType );
    }
    if ( BandStyleKeys.LAYOUT_BLOCK.equals( layoutType ) ) {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( elementStyleSheet );
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( styleSheet );
      return new BlockRenderBox( styleSheet, objectID, boxDefinition, elementType, attributes, stateKey );
    }
    if ( LAYOUT_PARAGRAPH_LINEBOX.equals( layoutType ) ) {
      // The non-inheritable styles will be applied to the auto-generated paragraph box. The inlinebox itself
      // only receives the inheritable styles so that it can inherit it to its next child ..
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( new ParagraphPoolboxStyleSheet( elementStyleSheet ) );
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( styleSheet );
      return new InlineRenderBox( styleSheet, objectID, boxDefinition, elementType, attributes, stateKey );
    } else if ( BandStyleKeys.LAYOUT_INLINE.equals( layoutType ) ) {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( elementStyleSheet );
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( styleSheet );
      return new InlineRenderBox( styleSheet, objectID, boxDefinition, elementType, attributes, stateKey );
    }
    if ( BandStyleKeys.LAYOUT_ROW.equals( layoutType ) ) {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( elementStyleSheet );
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( styleSheet );
      return new RowRenderBox( styleSheet, objectID, boxDefinition, elementType, attributes, stateKey );
    }
    if ( BandStyleKeys.LAYOUT_TABLE.equals( layoutType ) ) {
      if ( strictCompatibilityMode ) {
        throw new IncompatibleFeatureException( "A report with a legacy mode of pre-4.0 cannot handle table layouts. "
            + "Migrate your report to version 4.0 or higher.", ClassicEngineBoot.computeVersionId( 4, 0, 0 ) );
      }
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( elementStyleSheet );
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( styleSheet );
      return new TableRenderBox( styleSheet, objectID, boxDefinition, elementType, attributes, stateKey );
    }
    if ( BandStyleKeys.LAYOUT_TABLE_BODY.equals( layoutType ) || BandStyleKeys.LAYOUT_TABLE_HEADER.equals( layoutType )
        || BandStyleKeys.LAYOUT_TABLE_FOOTER.equals( layoutType ) ) {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( elementStyleSheet );
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( styleSheet );
      return new TableSectionRenderBox( styleSheet, objectID, boxDefinition, elementType, attributes, stateKey );
    }
    if ( BandStyleKeys.LAYOUT_TABLE_ROW.equals( layoutType ) ) {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( elementStyleSheet );
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( styleSheet );
      return new TableRowRenderBox( styleSheet, objectID, boxDefinition, elementType, attributes, stateKey );
    }
    if ( BandStyleKeys.LAYOUT_TABLE_CELL.equals( layoutType ) ) {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( elementStyleSheet );
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( styleSheet );
      return new TableCellRenderBox( styleSheet, objectID, boxDefinition, elementType, attributes, stateKey );
    }
    if ( BandStyleKeys.LAYOUT_TABLE_COL.equals( layoutType ) ) {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( elementStyleSheet );
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( styleSheet );
      return new TableColumnNode( styleSheet, objectID, boxDefinition, elementType, attributes, stateKey );
    }
    if ( BandStyleKeys.LAYOUT_TABLE_COL_GROUP.equals( layoutType ) ) {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( elementStyleSheet );
      return new TableColumnGroupNode( styleSheet, attributes );
    }

    // assume 'Canvas' by default ..
    final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( elementStyleSheet );
    final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( styleSheet );
    return new CanvasRenderBox( styleSheet, objectID, boxDefinition, elementType, attributes, stateKey );
  }

  @Deprecated
  public RenderBox createAutoParagraph( final ReportStateKey stateKey ) {
    return new ParagraphRenderBox( SimpleStyleSheet.EMPTY_STYLE, new InstanceID(), BoxDefinition.EMPTY,
        AutoLayoutBoxType.INSTANCE, ReportAttributeMap.EMPTY_MAP, stateKey );
  }

  public RenderBox createAutoParagraph( final ReportElement band, final StyleSheet bandStyle,
      final ReportStateKey stateKey ) {
    final SimpleStyleSheet styleSheet = bandCache.getStyleSheet( bandStyle );
    final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition( styleSheet );

    final ParagraphRenderBox paragraphBox =
        new ParagraphRenderBox( styleSheet, band.getObjectID(), boxDefinition, band.getElementType(), band
            .getAttributes(), stateKey );
    paragraphBox.setName( band.getName() );
    return paragraphBox;
  }

  public RenderBox produceSubReportPlaceholder( final ReportElement report, final StyleSheet style,
      final ReportStateKey stateKey ) {
    String layout;
    if ( metaData.isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY ) ) {
      layout = BandStyleKeys.LAYOUT_BLOCK;
    } else {
      layout = (String) style.getStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_BLOCK );
      // Todo: PRD-5172: Filter out inline subreports
      if ( BandStyleKeys.LAYOUT_INLINE.equals( layout ) ) {
        layout = BandStyleKeys.LAYOUT_BLOCK;
      }
    }

    final RenderBox box =
        createBox( layout, stateKey, style, report.getElementType(), report.getAttributes(), report.getObjectID() );
    box.getStaticBoxLayoutProperties().setPlaceholderBox( StaticBoxLayoutProperties.PlaceholderType.COMPLEX );
    box.markAsContentRefHolder();
    // for the sake of debugging ..
    final String name = report.getName();
    if ( name != null && name.startsWith( Band.ANONYMOUS_BAND_PREFIX ) == false ) {
      box.setName( name );
    }
    return box;
  }

  public BoxDefinition getBoxDefinition( final StyleSheet style ) {
    return boxDefinitionFactory.getBoxDefinition( style );
  }

  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( e );
    }
  }

  public StyleSheet createAutoGeneratedSectionStyleSheet( final StyleSheet style ) {
    return bandWithoutKeepTogetherStyle;
  }

  public RenderBox createPageBreakIndicatorBox( final ReportStateKey stateKey, final long range ) {
    if ( this.manualBreakBoxStyle == null ) {
      final ManualBreakIndicatorStyleSheet mbis = new ManualBreakIndicatorStyleSheet();
      this.manualBreakBoxStyle = new SimpleStyleSheet( mbis );
    }

    final RenderBox sectionBox =
        new BreakMarkerRenderBox( manualBreakBoxStyle, new InstanceID(), BoxDefinition.EMPTY,
            AutoLayoutBoxType.INSTANCE, ReportAttributeMap.EMPTY_MAP, stateKey, range );
    sectionBox.setName( "pagebreak" );
    sectionBox.close();
    return sectionBox;
  }

  public StyleSheet createStyle( final StyleSheet style ) {
    return bandCache.getStyleSheet( style );
  }

  public void close() {
    bandCache.printPerformanceStats();
  }

  public RenderableReplacedContentBox createReplacedContent( final ReportElement element, final StyleSheet style,
      final Object value, final Object rawValue, final ReportStateKey stateKey ) {
    final ResourceKey rawKey;
    if ( rawValue instanceof ResourceKey ) {
      rawKey = (ResourceKey) rawValue;
    } else {
      rawKey = null;
    }

    final SimpleStyleSheet elementStyle = bandCache.getStyleSheet( style );
    final RenderableReplacedContent content = new RenderableReplacedContent( elementStyle, value, rawKey, metaData );
    final BoxDefinition boxDefinition = getBoxDefinition( elementStyle );
    final RenderableReplacedContentBox child =
        new RenderableReplacedContentBox( elementStyle, element.getObjectID(), boxDefinition, element.getElementType(),
            element.getAttributes(), stateKey, content );
    child.setName( element.getName() );
    return child;
  }
}
