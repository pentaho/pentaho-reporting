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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xml.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackgroundProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellMarker;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;
import org.pentaho.reporting.libraries.formatting.FastDecimalFormat;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.awt.Color;
import java.beans.PropertyEditor;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;

/**
 * @noinspection HardCodedStringLiteral
 */
public class XmlDocumentWriter extends IterateStructuralProcessStep {
  private static final String LAYOUT_OUTPUT_NAMESPACE =
    "http://reporting.pentaho.org/namespaces/output/layout-output/table/1.0";
  private static final Log logger = LogFactory.getLog( XmlDocumentWriter.class );

  private OutputStream outputStream;
  private StrictBounds drawArea;
  private XmlWriter xmlWriter;
  private FastDecimalFormat pointIntConverter;
  private FastDecimalFormat pointConverter;
  private FastDecimalFormat pointShortConverter;
  private boolean ignoreEmptyBorders;
  private CellBackgroundProducer cellBackgroundProducer;
  private OutputProcessorMetaData metaData;
  private boolean tableOpen;

  public XmlDocumentWriter( final OutputStream outputStream,
                            final OutputProcessorMetaData metaData ) {
    this.metaData = metaData;
    if ( outputStream == null ) {
      throw new NullPointerException();
    }

    this.outputStream = outputStream;
    this.pointConverter = new FastDecimalFormat( "0.0####", Locale.US );
    this.pointShortConverter = new FastDecimalFormat( "0.#####", Locale.US );
    this.pointIntConverter = new FastDecimalFormat( "0", Locale.US );
    this.ignoreEmptyBorders = true;
  }

  public void open() throws IOException {
    final DefaultTagDescription td = new DefaultTagDescription();
    td.setNamespaceHasCData( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, false );
    td.setElementHasCData( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "text", true );

    // prepare anything that might needed to be prepared ..
    final String encoding =
        metaData.getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.xml.Encoding",
            EncodingRegistry.getPlatformDefaultEncoding() );

    final Writer writer = new BufferedWriter( new OutputStreamWriter( outputStream, encoding ) );
    this.xmlWriter = new XmlWriter( writer, td );
    this.xmlWriter.writeXmlDeclaration( null );
    final AttributeList attrs = new AttributeList();
    attrs.addNamespaceDeclaration( "", XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE );
    xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "layout-output", attrs, XmlWriter.OPEN );
  }

  public void close() throws IOException {
    // close all ..
    xmlWriter.writeCloseTag();
    xmlWriter.close();
  }

  public void processTableContent( final LogicalPageBox logicalPageBox,
                                   final OutputProcessorMetaData metaData,
                                   final TableContentProducer contentProducer,
                                   final boolean incremental ) throws IOException {

    // Start a new page.
    final SheetLayout sheetLayout = contentProducer.getSheetLayout();
    final int columnCount = contentProducer.getColumnCount();
    final int rowCount = contentProducer.getRowCount();
    final int startRow = contentProducer.getFinishedRows();
    final int finishRow = contentProducer.getFilledRows();
    if ( incremental && startRow == finishRow ) {
      return;
    }

    if ( cellBackgroundProducer == null ) {
      this.cellBackgroundProducer =
          new CellBackgroundProducer( metaData
              .isFeatureSupported( AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE ), metaData
              .isFeatureSupported( OutputProcessorFeature.UNALIGNED_PAGEBANDS ) );
    }

    if ( tableOpen == false ) {
      tableOpen = true;
      final AttributeList pageAttributes = new AttributeList();
      pageAttributes.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "col-count", pointIntConverter
          .format( columnCount ) );
      pageAttributes.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "row-count", pointIntConverter
          .format( rowCount ) );
      pageAttributes.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "sheet-name", contentProducer
          .getSheetName() );
      xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "table", pageAttributes, XmlWriter.OPEN );
      xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "cols", XmlWriter.OPEN );
      for ( int i = 0; i < columnCount; i++ ) {
        final double cellWidth = StrictGeomUtility.toExternalValue( sheetLayout.getCellWidth( i, i + 1 ) );
        xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "column", "width", pointShortConverter
            .format( cellWidth ), XmlWriter.CLOSE );
      }
      xmlWriter.writeCloseTag();
    }

    for ( int row = startRow; row < finishRow; row++ ) {
      final AttributeList rowAttributes = new AttributeList();
      final double rowHeight = StrictGeomUtility.toExternalValue( sheetLayout.getRowHeight( row ) );
      rowAttributes.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "height", pointShortConverter
          .format( rowHeight ) );
      xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "row", rowAttributes, XmlWriter.OPEN );

      for ( short col = 0; col < columnCount; col++ ) {
        final RenderBox content = contentProducer.getContent( row, col );

        if ( content == null ) {
          final CellMarker.SectionType sectionType = contentProducer.getSectionType( row, col );
          final RenderBox backgroundBox = contentProducer.getBackground( row, col );
          final CellBackground background;
          if ( backgroundBox != null ) {
            background =
                cellBackgroundProducer.getBackgroundForBox( logicalPageBox, sheetLayout, col, row, 1, 1, true,
                    sectionType, backgroundBox );
          } else {
            background =
                cellBackgroundProducer.getBackgroundAt( logicalPageBox, sheetLayout, col, row, true, sectionType );
          }
          if ( background == null ) {
            xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "empty-cell", XmlWriter.CLOSE );
            continue;
          }

          // A empty cell with a defined background ..
          xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "empty-cell",
              createCellAttributes( background ), XmlWriter.OPEN );

          writeAttributes( background.getAttributes(), background.getElementType() );
          xmlWriter.writeCloseTag();
          continue;
        }

        if ( content.isCommited() == false ) {
          throw new InvalidReportStateException( "Uncommited content encountered" );
        }

        final long contentOffset = contentProducer.getContentOffset( row, col );
        final TableRectangle rectangle =
            sheetLayout.getTableBounds( content.getX(), content.getY() + contentOffset, content.getWidth(), content
                .getHeight(), null );
        if ( rectangle.isOrigin( col, row ) == false ) {
          // A spanned cell ..
          xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "spanned-cell", XmlWriter.CLOSE );
          continue;
        }

        final CellMarker.SectionType sectionType = contentProducer.getSectionType( row, col );
        final CellBackground realBackground =
            cellBackgroundProducer.getBackgroundForBox( logicalPageBox, sheetLayout, rectangle.getX1(), rectangle
                .getY1(), rectangle.getColumnSpan(), rectangle.getRowSpan(), false, sectionType, content );

        final AttributeList attributeList;
        if ( realBackground != null ) {
          attributeList = createCellAttributes( realBackground );
        } else {
          attributeList = new AttributeList();
        }
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "row-span", pointIntConverter
            .format( rectangle.getRowSpan() ) );
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "col-span", pointIntConverter
            .format( rectangle.getColumnSpan() ) );
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "href", (String) content.getStyleSheet()
            .getStyleProperty( ElementStyleKeys.HREF_TARGET ) );
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "href-window", (String) content
            .getStyleSheet().getStyleProperty( ElementStyleKeys.HREF_WINDOW ) );
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "href-title", (String) content
            .getStyleSheet().getStyleProperty( ElementStyleKeys.HREF_TITLE ) );

        // export the cell and all content ..
        xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "cell", attributeList, XmlWriter.OPEN );
        writeAttributes( content.getAttributes(), content.getElementType() );
        processBoxChilds( content );
        xmlWriter.writeCloseTag();
        content.setFinishedTable( true );
      }
      xmlWriter.writeCloseTag();
    }

    if ( incremental == false ) {
      xmlWriter.writeCloseTag(); // table
      tableOpen = false;
    }
  }

  private AttributeList createBoxAttributeList( final RenderBox box ) {
    final AttributeList attributeList = new AttributeList();
    if ( StringUtils.isEmpty( box.getName() ) == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "name", box.getName() );
    }
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "type", box.getClass().getSimpleName() );

    final BoxDefinition definition = box.getBoxDefinition();
    final Border border = definition.getBorder();
    final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();

    final BorderEdge top = border.getTop();
    if ( BorderEdge.EMPTY.equals( top ) == false || ignoreEmptyBorders == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-color",
        ColorValueConverter.colorToString( top.getColor() ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-width",
        pointConverter.format( StrictGeomUtility.toExternalValue( sblp.getBorderTop() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-style",
        String.valueOf( top.getBorderStyle() ) );
    }

    final BorderEdge left = border.getLeft();
    if ( BorderEdge.EMPTY.equals( left ) == false || ignoreEmptyBorders == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-left-color",
        ColorValueConverter.colorToString( left.getColor() ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-left-width",
        pointConverter.format( StrictGeomUtility.toExternalValue( sblp.getBorderLeft() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-left-style",
        String.valueOf( left.getBorderStyle() ) );
    }

    final BorderEdge bottom = border.getBottom();
    if ( BorderEdge.EMPTY.equals( bottom ) == false || ignoreEmptyBorders == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-color",
        ColorValueConverter.colorToString( bottom.getColor() ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-width",
        pointConverter.format( StrictGeomUtility.toExternalValue( sblp.getBorderBottom() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-style",
        String.valueOf( bottom.getBorderStyle() ) );
    }

    final BorderEdge right = border.getRight();
    if ( BorderEdge.EMPTY.equals( right ) == false || ignoreEmptyBorders == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-right-color",
        ColorValueConverter.colorToString( right.getColor() ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-right-width",
        pointConverter.format( StrictGeomUtility.toExternalValue( sblp.getBorderRight() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-right-style",
        String.valueOf( right.getBorderStyle() ) );
    }

    final BorderCorner topLeft = border.getTopLeft();
    if ( isEmptyCorner( topLeft ) == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-left-x",
        pointConverter.format( StrictGeomUtility.toExternalValue( topLeft.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-left-y",
        pointConverter.format( StrictGeomUtility.toExternalValue( topLeft.getHeight() ) ) );
    }

    final BorderCorner topRight = border.getTopRight();
    if ( isEmptyCorner( topRight ) == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-right-x",
        pointConverter.format( StrictGeomUtility.toExternalValue( topRight.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-right-y",
        pointConverter.format( StrictGeomUtility.toExternalValue( topRight.getHeight() ) ) );
    }

    final BorderCorner bottomLeft = border.getBottomLeft();
    if ( isEmptyCorner( bottomLeft ) == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-left-x",
        pointConverter.format( StrictGeomUtility.toExternalValue( bottomLeft.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-left-y",
        pointConverter.format( StrictGeomUtility.toExternalValue( bottomLeft.getHeight() ) ) );
    }

    final BorderCorner bottomRight = border.getBottomRight();
    if ( isEmptyCorner( bottomRight ) == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-right-x",
        pointConverter.format( StrictGeomUtility.toExternalValue( bottomRight.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-right-y",
        pointConverter.format( StrictGeomUtility.toExternalValue( bottomRight.getHeight() ) ) );
    }

    if ( sblp.getMarginTop() > 0 ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "margin-top",
        pointConverter.format( StrictGeomUtility.toExternalValue( sblp.getMarginTop() ) ) );
    }
    if ( sblp.getMarginLeft() > 0 ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "margin-left",
        pointConverter.format( StrictGeomUtility.toExternalValue( sblp.getMarginLeft() ) ) );
    }
    if ( sblp.getMarginBottom() > 0 ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "margin-bottom",
        pointConverter.format( StrictGeomUtility.toExternalValue( sblp.getMarginBottom() ) ) );
    }
    if ( sblp.getMarginRight() > 0 ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "margin-right",
        pointConverter.format( StrictGeomUtility.toExternalValue( sblp.getMarginRight() ) ) );
    }

    if ( definition.getPaddingTop() > 0 ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "padding-top",
        pointConverter.format( StrictGeomUtility.toExternalValue( definition.getPaddingTop() ) ) );
    }
    if ( definition.getPaddingLeft() > 0 ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "padding-left",
        pointConverter.format( StrictGeomUtility.toExternalValue( definition.getPaddingLeft() ) ) );
    }
    if ( definition.getPaddingBottom() > 0 ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "padding-bottom",
        pointConverter.format( StrictGeomUtility.toExternalValue( definition.getPaddingBottom() ) ) );
    }
    if ( definition.getPaddingRight() > 0 ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "padding-right",
        pointConverter.format( StrictGeomUtility.toExternalValue( definition.getPaddingRight() ) ) );
    }

    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "x",
      pointConverter.format( StrictGeomUtility.toExternalValue( box.getX() ) ) );
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "y",
      pointConverter.format( StrictGeomUtility.toExternalValue( box.getY() ) ) );
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "width",
      pointConverter.format( StrictGeomUtility.toExternalValue( box.getWidth() ) ) );
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "height",
      pointConverter.format( StrictGeomUtility.toExternalValue( box.getHeight() ) ) );

    final Color backgroundColor = (Color) box.getStyleSheet().getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );
    if ( backgroundColor != null ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "background-color",
        ColorValueConverter.colorToString( backgroundColor ) );
    }

    final Color color = (Color) box.getStyleSheet().getStyleProperty( ElementStyleKeys.PAINT );
    if ( color != null ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "color",
        ColorValueConverter.colorToString( color ) );
    }
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "font-face",
      (String) box.getStyleSheet().getStyleProperty( TextStyleKeys.FONT ) );
    final Object o = box.getStyleSheet().getStyleProperty( TextStyleKeys.FONTSIZE );
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "font-size", pointIntConverter.format( o ) );
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "font-style-bold", String.valueOf(
      box.getStyleSheet().getStyleProperty( TextStyleKeys.BOLD ) ) );
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "font-style-italics", String.valueOf(
      box.getStyleSheet().getStyleProperty( TextStyleKeys.ITALIC ) ) );
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "font-style-underline", String.valueOf(
      box.getStyleSheet().getStyleProperty( TextStyleKeys.UNDERLINED ) ) );
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "font-style-strikethrough", String.valueOf(
      box.getStyleSheet().getStyleProperty( TextStyleKeys.STRIKETHROUGH ) ) );

    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "anchor",
      (String) box.getStyleSheet().getStyleProperty( ElementStyleKeys.ANCHOR_NAME ) );
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "href",
      (String) box.getStyleSheet().getStyleProperty( ElementStyleKeys.HREF_TARGET ) );
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "href-window",
      (String) box.getStyleSheet().getStyleProperty( ElementStyleKeys.HREF_WINDOW ) );
    attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "href-title",
      (String) box.getStyleSheet().getStyleProperty( ElementStyleKeys.HREF_TITLE ) );

    return attributeList;
  }

  private void writeElementAttributes( final RenderNode element ) throws IOException {
    final ReportAttributeMap attributes = element.getAttributes();
    final ElementType type = element.getElementType();
    writeAttributes( attributes, type );
  }

  private void writeAttributes( final ReportAttributeMap attributes, ElementType type ) throws IOException {
    if ( type == null ) {
      type = AutoLayoutBoxType.INSTANCE;
    }

    final Set<AttributeMap.DualKey> collection = attributes.keySet();
    final AttributeMap.DualKey[] attributeNames = collection.toArray( new AttributeMap.DualKey[ collection.size() ] );
    Arrays.sort( attributeNames, new DualKeySorter() );
    for ( int j = 0; j < attributeNames.length; j++ ) {

      final String namespace = attributeNames[ j ].namespace;
      if ( AttributeNames.Designtime.NAMESPACE.equals( namespace ) ) {
        continue;
      }

      final String name = attributeNames[ j ].name;
      final Object value = attributes.getAttribute( namespace, name );
      if ( value == null ) {
        continue;
      }

      if ( metaData.isFeatureSupported( XmlTableOutputProcessorMetaData.WRITE_RESOURCEKEYS ) == false
        && value instanceof ResourceKey ) {
        continue;
      }


      final AttributeMetaData attrMeta = type.getMetaData().getAttributeDescription( namespace, name );
      if ( attrMeta == null ) {
        // if you want to use attributes in this output target, declare the attribute's metadata first.
        continue;
      }

      final AttributeList attList = new AttributeList();
      if ( value instanceof String ) {
        final String s = (String) value;
        if ( StringUtils.isEmpty( s ) ) {
          continue;
        }

        if ( xmlWriter.isNamespaceDefined( namespace ) == false
          && attList.isNamespaceUriDefined( namespace ) == false ) {
          attList.addNamespaceDeclaration( "autoGenNs", namespace );
        }

        // preserve strings, but discard anything else. Until a attribute has a definition, we cannot
        // hope to understand the attribute's value. String-attributes can be expressed in XML easily,
        // and string is also how all unknown attributes are stored by the parser.
        attList.setAttribute( namespace, name, s );
        this.xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "attribute", attList, XmlWriter.CLOSE );
        continue;
      }

      if ( xmlWriter.isNamespaceDefined( namespace ) == false
        && attList.isNamespaceUriDefined( namespace ) == false ) {
        attList.addNamespaceDeclaration( "autoGenNs", namespace );
      }

      try {
        final PropertyEditor propertyEditor = attrMeta.getEditor();
        final String textValue;
        if ( propertyEditor != null ) {
          propertyEditor.setValue( value );
          textValue = propertyEditor.getAsText();
        } else {
          textValue = ConverterRegistry.toAttributeValue( value );
        }

        if ( textValue != null ) {
          if ( "".equals( textValue ) == false ) {
            attList.setAttribute( namespace, name, textValue );
            this.xmlWriter
              .writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "attribute", attList, XmlWriter.CLOSE );
          }
        } else {
          if ( value instanceof ResourceKey ) {
            final ResourceKey reskey = (ResourceKey) value;
            final String identifierAsString = reskey.getIdentifierAsString();
            attList.setAttribute( namespace, name, "resource-key:" + reskey.getSchema() + ":" + identifierAsString );
            this.xmlWriter
              .writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "attribute", attList, XmlWriter.CLOSE );
          } else {
            XmlDocumentWriter.logger.debug(
              "Attribute '" + namespace + '|' + name + "' is not convertible to a text - returned null: " + value );
          }
        }
      } catch ( BeanException e ) {
        if ( attrMeta.isTransient() == false ) {
          XmlDocumentWriter.logger.warn(
            "Attribute '" + namespace + '|' + name + "' is not convertible with the bean-methods" );
        } else {
          XmlDocumentWriter.logger.debug(
            "Attribute '" + namespace + '|' + name + "' is not convertible with the bean-methods" );
        }
      }
    }
  }

  private boolean isEmptyCorner( final BorderCorner corner ) {
    if ( ignoreEmptyBorders == false ) {
      return false;
    }
    return corner.getWidth() == 0 && corner.getHeight() == 0;
  }

  private AttributeList createCellAttributes( final CellBackground border ) {
    final AttributeList attributeList = new AttributeList();

    final BorderEdge top = border.getTop();
    if ( BorderEdge.EMPTY.equals( top ) == false || ignoreEmptyBorders == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-color",
        ColorValueConverter.colorToString( top.getColor() ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-width",
        pointConverter.format( StrictGeomUtility.toExternalValue( top.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-style",
        String.valueOf( top.getBorderStyle() ) );
    }

    final BorderEdge left = border.getLeft();
    if ( BorderEdge.EMPTY.equals( left ) == false || ignoreEmptyBorders == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-left-color",
        ColorValueConverter.colorToString( left.getColor() ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-left-width",
        pointConverter.format( StrictGeomUtility.toExternalValue( left.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-left-style",
        String.valueOf( left.getBorderStyle() ) );
    }

    final BorderEdge bottom = border.getBottom();
    if ( BorderEdge.EMPTY.equals( bottom ) == false || ignoreEmptyBorders == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-color",
        ColorValueConverter.colorToString( bottom.getColor() ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-width",
        pointConverter.format( StrictGeomUtility.toExternalValue( bottom.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-style",
        String.valueOf( bottom.getBorderStyle() ) );
    }

    final BorderEdge right = border.getRight();
    if ( BorderEdge.EMPTY.equals( right ) == false || ignoreEmptyBorders == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-right-color",
        ColorValueConverter.colorToString( right.getColor() ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-right-width",
        pointConverter.format( StrictGeomUtility.toExternalValue( right.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-right-style",
        String.valueOf( right.getBorderStyle() ) );
    }

    final BorderCorner topLeft = border.getTopLeft();
    if ( isEmptyCorner( topLeft ) == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-left-x",
        pointConverter.format( StrictGeomUtility.toExternalValue( topLeft.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-left-y",
        pointConverter.format( StrictGeomUtility.toExternalValue( topLeft.getHeight() ) ) );
    }

    final BorderCorner topRight = border.getTopRight();
    if ( isEmptyCorner( topRight ) == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-right-x",
        pointConverter.format( StrictGeomUtility.toExternalValue( topRight.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-right-y",
        pointConverter.format( StrictGeomUtility.toExternalValue( topRight.getHeight() ) ) );
    }

    final BorderCorner bottomLeft = border.getBottomLeft();
    if ( isEmptyCorner( bottomLeft ) == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-left-x",
        pointConverter.format( StrictGeomUtility.toExternalValue( bottomLeft.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-left-y",
        pointConverter.format( StrictGeomUtility.toExternalValue( bottomLeft.getHeight() ) ) );
    }

    final BorderCorner bottomRight = border.getBottomRight();
    if ( isEmptyCorner( bottomRight ) == false ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-right-x",
        pointConverter.format( StrictGeomUtility.toExternalValue( bottomRight.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-right-y",
        pointConverter.format( StrictGeomUtility.toExternalValue( bottomRight.getHeight() ) ) );
    }

    final Color backgroundColor = border.getBackgroundColor();
    if ( backgroundColor != null ) {
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "background-color",
        ColorValueConverter.colorToString( backgroundColor ) );
    }

    final String[] anchors = border.getAnchors();
    if ( anchors.length > 0 ) {
      final StringBuilder anchorText = new StringBuilder( 100 );
      for ( int i = 0; i < anchors.length; i++ ) {
        final String anchor = anchors[ i ];
        if ( i == 0 ) {
          anchorText.append( ' ' );
        }
        anchorText.append( anchor );
      }
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "anchor", anchorText.toString() );
    }
    return attributeList;
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {

    try {
      final int nodeType = box.getNodeType();
      if ( ( nodeType & LayoutNodeTypes.MASK_BOX_PAGEAREA ) == LayoutNodeTypes.MASK_BOX_PAGEAREA ) {
        final AttributeList list = createBoxAttributeList( box );
        list.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "type", box.getName() );
        xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "page-area", list, XmlWriter.OPEN );
        writeElementAttributes( box );
        return true;
      } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
        return startBox( box, "p" );
      } else {
        return startBox( box, "block" );
      }
    } catch ( IOException e ) {
      throw new InvalidReportStateException( e.getMessage(), e );
    }
  }

  protected void finishBlockBox( final BlockRenderBox box ) {
    finishBox();
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_LINEBOX ) {
      return startBox( box, "line" );
    } else {
      return startBox( box, "inline" );
    }
  }

  protected void finishInlineBox( final InlineRenderBox box ) {
    finishBox();
  }

  protected boolean startOtherBox( final RenderBox box ) {
    try {
      xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "other-box", createBoxAttributeList( box ),
        XmlWriter.OPEN );
      writeElementAttributes( box );
      return true;
    } catch ( IOException e ) {
      throw new InvalidReportStateException( e.getMessage(), e );
    }
  }

  protected void finishOtherBox( final RenderBox box ) {
    finishBox();
  }

  protected boolean startCanvasBox( final CanvasRenderBox box ) {
    return startBox( box, "canvas" );
  }

  private boolean startBox( final RenderBox box, final String tagName ) {
    try {
      xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, tagName, createBoxAttributeList( box ),
        XmlWriter.OPEN );
      writeElementAttributes( box );
      return true;
    } catch ( IOException e ) {
      throw new InvalidReportStateException( e.getMessage(), e );
    }
  }

  protected void finishCanvasBox( final CanvasRenderBox box ) {
    finishBox();
  }

  protected boolean startRowBox( final RenderBox box ) {
    try {
      xmlWriter
        .writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "row", createBoxAttributeList( box ), XmlWriter.OPEN );
      writeElementAttributes( box );
      return true;
    } catch ( IOException e ) {
      throw new InvalidReportStateException( e.getMessage(), e );
    }
  }

  protected void finishRowBox( final RenderBox box ) {
    finishBox();
  }

  private void finishBox() {
    try {
      xmlWriter.writeCloseTag();
    } catch ( IOException e ) {
      throw new InvalidReportStateException( e.getMessage(), e );
    }
  }

  protected void processOtherNode( final RenderNode node ) {
    try {
      final int nodeType = node.getNodeType();
      if ( nodeType == LayoutNodeTypes.TYPE_NODE_TEXT ) {
        final RenderableText text = (RenderableText) node;
        final AttributeList attributeList = new AttributeList();
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "x",
          pointConverter.format( StrictGeomUtility.toExternalValue( node.getX() ) ) );
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "y",
          pointConverter.format( StrictGeomUtility.toExternalValue( node.getY() ) ) );
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "width",
          pointConverter.format( StrictGeomUtility.toExternalValue( node.getWidth() ) ) );
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "height",
          pointConverter.format( StrictGeomUtility.toExternalValue( node.getHeight() ) ) );
        xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "text", attributeList, XmlWriter.OPEN );
        xmlWriter.writeTextNormalized( text.getRawText(), true );
        xmlWriter.writeCloseTag();

      } else if ( nodeType == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
        final RenderableComplexText renderableComplexText = (RenderableComplexText) node;
        final AttributeList attributeList = new AttributeList();
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "x",
          pointConverter.format( StrictGeomUtility.toExternalValue( node.getX() ) ) );
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "y",
          pointConverter.format( StrictGeomUtility.toExternalValue( node.getY() ) ) );
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "width",
          pointConverter.format( StrictGeomUtility.toExternalValue( node.getWidth() ) ) );
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "height",
          pointConverter.format( StrictGeomUtility.toExternalValue( node.getHeight() ) ) );

        final String text = renderableComplexText.getRawText();
        xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "text", attributeList, XmlWriter.OPEN );
        xmlWriter.writeTextNormalized( text, true );
        xmlWriter.writeCloseTag();

      } else if ( nodeType == LayoutNodeTypes.TYPE_NODE_SPACER ) {
        final SpacerRenderNode spacer = (SpacerRenderNode) node;
        final AttributeList attributeList = new AttributeList();
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "width",
          pointConverter.format( StrictGeomUtility.toExternalValue( node.getWidth() ) ) );
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "height",
          pointConverter.format( StrictGeomUtility.toExternalValue( node.getHeight() ) ) );
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "preserve",
          String.valueOf( spacer.isDiscardable() == false ) );
        xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "spacer", attributeList, XmlWriter.CLOSE );
      }
    } catch ( IOException e ) {
      throw new InvalidReportStateException( e.getMessage(), e );
    }
  }

  protected void processRenderableContent( final RenderableReplacedContentBox node ) {
    try {
      final RenderableReplacedContent prc = node.getContent();
      final AttributeList attributeList = new AttributeList();
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "x",
        pointConverter.format( StrictGeomUtility.toExternalValue( node.getX() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "y",
        pointConverter.format( StrictGeomUtility.toExternalValue( node.getY() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "width",
        pointConverter.format( StrictGeomUtility.toExternalValue( node.getWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "height",
        pointConverter.format( StrictGeomUtility.toExternalValue( node.getHeight() ) ) );
      attributeList
        .setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "source", String.valueOf( prc.getSource() ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "content-width",
        pointConverter.format( StrictGeomUtility.toExternalValue( prc.getContentWidth() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "content-height",
        pointConverter.format( StrictGeomUtility.toExternalValue( prc.getContentHeight() ) ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "requested-width",
        convertRenderLength( prc.getRequestedWidth() ) );
      attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "requested-height",
        convertRenderLength( prc.getRequestedHeight() ) );

      final Object o = prc.getRawObject();
      if ( o != null ) {
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "raw-object-type",
          o.getClass().getName() );
      } else {
        attributeList.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "raw-object-type", "null" );
      }
      xmlWriter
        .writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "replaced-content", attributeList, XmlWriter.OPEN );
      writeElementAttributes( node );
      xmlWriter.writeCloseTag();
    } catch ( IOException e ) {
      throw new InvalidReportStateException( e.getMessage(), e );
    }

  }

  private String convertRenderLength( final RenderLength length ) {
    if ( length == null ) {
      return null;
    }
    if ( RenderLength.AUTO.equals( length ) ) {
      return "auto";
    }
    if ( length.isPercentage() ) {
      return pointConverter.format( StrictGeomUtility.toExternalValue( -length.getValue() ) ) + '%';
    }
    return pointConverter.format( StrictGeomUtility.toExternalValue( length.getValue() ) );
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    processBoxChilds( box );
  }

  protected boolean startTableCellBox( final TableCellRenderBox box ) {
    try {
      AttributeList attrs = createBoxAttributeList( box );
      attrs.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "col-span", String.valueOf( box.getColSpan() ) );
      attrs.setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "row-span", String.valueOf( box.getRowSpan() ) );
      attrs
        .setAttribute( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "col-index", String.valueOf( box.getColumnIndex() ) );
      xmlWriter.writeTag( XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "table-cell", attrs, XmlWriter.OPEN );
      writeElementAttributes( box );
      return true;
    } catch ( IOException e ) {
      throw new InvalidReportStateException( e.getMessage(), e );
    }
  }

  protected void finishTableCellBox( final TableCellRenderBox box ) {
    finishBox();
  }

  protected boolean startTableRowBox( final TableRowRenderBox box ) {
    return startBox( box, "table-row" );
  }

  protected void finishTableRowBox( final TableRowRenderBox box ) {
    finishBox();
  }

  protected boolean startTableSectionBox( final TableSectionRenderBox box ) {
    return startBox( box, "table-section" );
  }

  protected void finishTableSectionBox( final TableSectionRenderBox box ) {
    finishBox();
  }

  protected boolean startTableColumnGroupBox( final TableColumnGroupNode box ) {
    return startBox( box, "colgroup" );
  }

  protected void finishTableColumnGroupBox( final TableColumnGroupNode box ) {
    finishBox();
  }

  protected boolean startTableBox( final TableRenderBox box ) {
    return startBox( box, "table" );
  }

  protected void finishTableBox( final TableRenderBox box ) {
    finishBox();
  }

  protected boolean startAutoBox( final RenderBox box ) {
    return startBox( box, "auto" );
  }

  protected void finishAutoBox( final RenderBox box ) {
    finishBox();
  }

  private static class DualKeySorter implements Comparator<AttributeMap.DualKey> {
    public int compare( final AttributeMap.DualKey o1, final AttributeMap.DualKey o2 ) {
      if ( o1 == null && o2 == null ) {
        return 0;
      }
      if ( o1 == null ) {
        return -1;
      }
      if ( o2 == null ) {
        return 1;
      }
      int ns = o1.namespace.compareTo( o2.namespace );
      if ( ns != 0 ) {
        return ns;
      }
      return o1.name.compareTo( o2.name );
    }
  }

}
