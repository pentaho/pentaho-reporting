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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.internal;

import java.awt.Color;
import java.beans.PropertyEditor;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
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
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
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
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

/**
 * @noinspection HardCodedStringLiteral
 */
public class XmlDocumentWriter extends IterateStructuralProcessStep
{
  private static final String LAYOUT_OUTPUT_NAMESPACE =
      "http://reporting.pentaho.org/namespaces/output/layout-output/pageable/1.0";
  private static final Log logger = LogFactory.getLog(XmlDocumentWriter.class);

  private OutputStream outputStream;
  private StrictBounds drawArea;
  private XmlWriter xmlWriter;
  private FastDecimalFormat pointIntConverter;
  private FastDecimalFormat pointConverter;
  private FastDecimalFormat pointShortConverter;
  private boolean ignoreEmptyBorders = true;
  private OutputProcessorMetaData metaData;

  public XmlDocumentWriter(final OutputStream outputStream,
                           final OutputProcessorMetaData metaData)
  {
    this.metaData = metaData;
    if (outputStream == null)
    {
      throw new NullPointerException();
    }
    this.outputStream = outputStream;
    this.pointConverter = new FastDecimalFormat("0.0####", Locale.US);
    this.pointShortConverter = new FastDecimalFormat("0.#####", Locale.US);
    this.pointIntConverter = new FastDecimalFormat("0", Locale.US);
  }

  public void open() throws IOException
  {
    final DefaultTagDescription td = new DefaultTagDescription();
    td.setNamespaceHasCData(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, false);
    td.setElementHasCData(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "text", true);

    // prepare anything that might needed to be prepared ..
    final String encoding = metaData.getConfiguration().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding",
            EncodingRegistry.getPlatformDefaultEncoding());

    final Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, encoding));
    this.xmlWriter = new XmlWriter(writer, td);
    this.xmlWriter.writeXmlDeclaration(encoding);
    final AttributeList attrs = new AttributeList();
    attrs.addNamespaceDeclaration("", XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE);
    xmlWriter.writeTag(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "layout-output", attrs, XmlWriter.OPEN);
  }

  public void close() throws IOException
  {
    // close all ..
    xmlWriter.writeCloseTag();
    xmlWriter.close();
  }

  public void processPhysicalPage(final PageGrid pageGrid,
                                  final LogicalPageBox logicalPage,
                                  final int row,
                                  final int col)
      throws IOException
  {
    final PhysicalPageBox page = pageGrid.getPage(row, col);
    if (page == null)
    {
      return;
    }

    final float width = (float) StrictGeomUtility.toExternalValue(page.getWidth());
    final float height = (float) StrictGeomUtility.toExternalValue(page.getHeight());
    final float marginLeft = (float) StrictGeomUtility.toExternalValue(page.getImageableX());
    final float marginRight = (float) StrictGeomUtility.toExternalValue
        (page.getWidth() - page.getImageableWidth() - page.getImageableX());
    final float marginTop = (float) StrictGeomUtility.toExternalValue(page.getImageableY());
    final float marginBottom = (float) StrictGeomUtility.toExternalValue
        (page.getHeight() - page.getImageableHeight() - page.getImageableY());

    final AttributeList pageAttributes = new AttributeList();
    pageAttributes.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "page-x",
        pointShortConverter.format(page.getGlobalX()));
    pageAttributes.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "page-y",
        pointShortConverter.format(page.getGlobalY()));
    pageAttributes.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "page-width",
        pointShortConverter.format(width));
    pageAttributes.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "page-height",
        pointShortConverter.format(height));
    pageAttributes.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "margin-top",
        pointShortConverter.format(marginTop));
    pageAttributes.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "margin-left",
        pointShortConverter.format(marginLeft));
    pageAttributes.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "margin-bottom",
        pointShortConverter.format(marginBottom));
    pageAttributes.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "margin-right",
        pointShortConverter.format(marginRight));

    xmlWriter.writeTag(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "physical-page", pageAttributes, XmlWriter.OPEN);

    // and now process the box ..
    drawArea = new StrictBounds(page.getGlobalX(), page.getGlobalY(),
        page.getImageableWidth(), page.getImageableHeight());
    processPage(logicalPage);

    xmlWriter.writeCloseTag();
  }

  public void processLogicalPage(final LogicalPageKey key,
                                 final LogicalPageBox logicalPage)
      throws IOException
  {

    final float width = (float) StrictGeomUtility.toExternalValue(logicalPage.getPageWidth());
    final float height = (float) StrictGeomUtility.toExternalValue(logicalPage.getPageHeight());

    final AttributeList pageAttributes = new AttributeList();
    pageAttributes.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "logical-page-number",
        pointIntConverter.format(key.getPosition()));
    pageAttributes.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "width", pointConverter.format(width));
    pageAttributes.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "height", pointConverter.format(height));

    xmlWriter.writeTag(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "logical-page", pageAttributes, XmlWriter.OPEN);

    // and now process the box ..
    drawArea = new StrictBounds(0, 0, logicalPage.getPageWidth(), logicalPage.getPageHeight());
    processPage(logicalPage);

    xmlWriter.writeCloseTag();
  }

  protected void processPage(final LogicalPageBox rootBox)
  {
    final StrictBounds pageBounds = drawArea;
    startProcessing(rootBox.getWatermarkArea());

    final BlockRenderBox headerArea = rootBox.getHeaderArea();
    final BlockRenderBox footerArea = rootBox.getFooterArea();
    final BlockRenderBox repeatFooterArea = rootBox.getRepeatFooterArea();
    final StrictBounds headerBounds =
        new StrictBounds(headerArea.getX(), headerArea.getY(), headerArea.getWidth(), headerArea.getHeight());
    final StrictBounds footerBounds =
        new StrictBounds(footerArea.getX(), footerArea.getY(), footerArea.getWidth(), footerArea.getHeight());
    final StrictBounds repeatFooterBounds = new StrictBounds
        (repeatFooterArea.getX(), repeatFooterArea.getY(), repeatFooterArea.getWidth(), repeatFooterArea.getHeight());
    final StrictBounds contentBounds = new StrictBounds
        (rootBox.getX(), headerArea.getY() + headerArea.getHeight(),
            rootBox.getWidth(), footerArea.getY() - headerArea.getHeight());
    this.drawArea = headerBounds;
    startProcessing(headerArea);
    this.drawArea = contentBounds;
    processBoxChilds(rootBox);
    this.drawArea = repeatFooterBounds;
    startProcessing(repeatFooterArea);
    this.drawArea = footerBounds;
    startProcessing(footerArea);
    this.drawArea = pageBounds;
  }

  private void writeElementAttributes(final RenderNode element) throws IOException
  {
    final ReportAttributeMap attributes = element.getAttributes();
    final ElementType type = element.getElementType();
    final String[] attributeNamespaces = attributes.getNameSpaces();
    Arrays.sort(attributeNamespaces);
    for (int i = 0; i < attributeNamespaces.length; i++)
    {
      final String namespace = attributeNamespaces[i];

      if (AttributeNames.Designtime.NAMESPACE.equals(namespace))
      {
        continue;
      }
      final String[] attributeNames = attributes.getNames(namespace);
      Arrays.sort(attributeNames);
      for (int j = 0; j < attributeNames.length; j++)
      {
        final String name = attributeNames[j];
        final Object value = attributes.getAttribute(namespace, name);
        if (value == null)
        {
          continue;
        }

        if (metaData.isFeatureSupported(XmlPageOutputProcessorMetaData.WRITE_RESOURCEKEYS) == false &&
            value instanceof ResourceKey)
        {
          continue;
        }

        final AttributeMetaData attrMeta = type.getMetaData().getAttributeDescription(namespace, name);
        if (attrMeta == null)
        {
          // if you want to use attributes in this output target, declare the attribute's metadata first.
          continue;
        }

        final AttributeList attList = new AttributeList();
        if (value instanceof String)
        {
          final String s = (String) value;
          if (StringUtils.isEmpty(s))
          {
            continue;
          }

          if (xmlWriter.isNamespaceDefined(namespace) == false &&
              attList.isNamespaceUriDefined(namespace) == false)
          {
            attList.addNamespaceDeclaration("autoGenNs", namespace);
          }

          // preserve strings, but discard anything else. Until a attribute has a definition, we cannot
          // hope to understand the attribute's value. String-attributes can be expressed in XML easily,
          // and string is also how all unknown attributes are stored by the parser.
          attList.setAttribute(namespace, name, String.valueOf(value));
          this.xmlWriter.writeTag(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "attribute", attList, XmlWriter.CLOSE);
          continue;
        }

        if (xmlWriter.isNamespaceDefined(namespace) == false &&
            attList.isNamespaceUriDefined(namespace) == false)
        {
          attList.addNamespaceDeclaration("autoGenNs", namespace);
        }

        try
        {
          final PropertyEditor propertyEditor = attrMeta.getEditor();
          final String textValue;
          if (propertyEditor != null)
          {
            propertyEditor.setValue(value);
            textValue = propertyEditor.getAsText();
          }
          else
          {
            textValue = ConverterRegistry.toAttributeValue(value);
          }

          if (textValue != null)
          {
            if ("".equals(textValue) == false)
            {
              attList.setAttribute(namespace, name, textValue);
              this.xmlWriter.writeTag(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "attribute", attList, XmlWriter.CLOSE);
            }
          }
          else
          {
            if (value instanceof ResourceKey)
            {
              final ResourceKey reskey = (ResourceKey) value;
              final String identifierAsString = reskey.getIdentifierAsString();
              attList.setAttribute(namespace, name, "resource-key:" + reskey.getSchema() + ":" + identifierAsString);
              this.xmlWriter.writeTag(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "attribute", attList, XmlWriter.CLOSE);
            }
            else
            {
              XmlDocumentWriter.logger.debug(
                  "Attribute '" + namespace + '|' + name + "' is not convertible to a text - returned null: " + value);
            }
          }
        }
        catch (BeanException e)
        {
          if (attrMeta.isTransient() == false)
          {
            XmlDocumentWriter.logger.warn(
                "Attribute '" + namespace + '|' + name + "' is not convertible with the bean-methods");
          }
          else
          {
            XmlDocumentWriter.logger.debug(
                "Attribute '" + namespace + '|' + name + "' is not convertible with the bean-methods");
          }
        }
      }
    }

  }

  private AttributeList createBoxAttributeList(final RenderBox box)
  {
    final AttributeList attributeList = new AttributeList();
    if (StringUtils.isEmpty(box.getName()) == false)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "name", box.getName());
    }
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "type", box.getClass().getSimpleName());

    final BoxDefinition definition = box.getBoxDefinition();
    final Border border = definition.getBorder();
    final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();

    final BorderEdge top = border.getTop();
    if (BorderEdge.EMPTY.equals(top) == false || ignoreEmptyBorders == false)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-color",
          convertColorToString(top));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-width",
          pointConverter.format(StrictGeomUtility.toExternalValue(sblp.getBorderTop())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-style",
          String.valueOf(top.getBorderStyle()));
    }

    final BorderEdge left = border.getLeft();
    if (BorderEdge.EMPTY.equals(left) == false || ignoreEmptyBorders == false)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-left-color",
          convertColorToString(left));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-left-width",
          pointConverter.format(StrictGeomUtility.toExternalValue(sblp.getBorderLeft())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-left-style",
          String.valueOf(left.getBorderStyle()));
    }

    final BorderEdge bottom = border.getBottom();
    if (BorderEdge.EMPTY.equals(bottom) == false || ignoreEmptyBorders == false)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-color",
          convertColorToString(bottom));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-width",
          pointConverter.format(StrictGeomUtility.toExternalValue(sblp.getBorderBottom())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-style",
          String.valueOf(bottom.getBorderStyle()));
    }

    final BorderEdge right = border.getRight();
    if (BorderEdge.EMPTY.equals(right) == false || ignoreEmptyBorders == false)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-right-color",
          convertColorToString(right));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-right-width",
          pointConverter.format(StrictGeomUtility.toExternalValue(sblp.getBorderRight())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-right-style",
          String.valueOf(right.getBorderStyle()));
    }

    final BorderCorner topLeft = border.getTopLeft();
    if (isEmptyCorner(topLeft) == false)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-left-x",
          pointConverter.format(StrictGeomUtility.toExternalValue(topLeft.getWidth())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-left-y",
          pointConverter.format(StrictGeomUtility.toExternalValue(topLeft.getHeight())));
    }

    final BorderCorner topRight = border.getTopRight();
    if (isEmptyCorner(topRight) == false)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-right-x",
          pointConverter.format(StrictGeomUtility.toExternalValue(topRight.getWidth())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-top-right-y",
          pointConverter.format(StrictGeomUtility.toExternalValue(topRight.getHeight())));
    }

    final BorderCorner bottomLeft = border.getBottomLeft();
    if (isEmptyCorner(bottomLeft) == false)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-left-x",
          pointConverter.format(StrictGeomUtility.toExternalValue(bottomLeft.getWidth())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-left-y",
          pointConverter.format(StrictGeomUtility.toExternalValue(bottomLeft.getHeight())));
    }

    final BorderCorner bottomRight = border.getBottomRight();
    if (isEmptyCorner(bottomRight) == false)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-right-x",
          pointConverter.format(StrictGeomUtility.toExternalValue(bottomRight.getWidth())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "border-bottom-right-y",
          pointConverter.format(StrictGeomUtility.toExternalValue(bottomRight.getHeight())));
    }

    if (sblp.getMarginTop() > 0)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "margin-top",
          pointConverter.format(StrictGeomUtility.toExternalValue(sblp.getMarginTop())));
    }
    if (sblp.getMarginLeft() > 0)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "margin-left",
          pointConverter.format(StrictGeomUtility.toExternalValue(sblp.getMarginLeft())));
    }
    if (sblp.getMarginBottom() > 0)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "margin-bottom",
          pointConverter.format(StrictGeomUtility.toExternalValue(sblp.getMarginBottom())));
    }
    if (sblp.getMarginRight() > 0)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "margin-right",
          pointConverter.format(StrictGeomUtility.toExternalValue(sblp.getMarginRight())));
    }

    if (definition.getPaddingTop() > 0)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "padding-top",
          pointConverter.format(StrictGeomUtility.toExternalValue(definition.getPaddingTop())));
    }
    if (definition.getPaddingLeft() > 0)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "padding-left",
          pointConverter.format(StrictGeomUtility.toExternalValue(definition.getPaddingLeft())));
    }
    if (definition.getPaddingBottom() > 0)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "padding-bottom",
          pointConverter.format(StrictGeomUtility.toExternalValue(definition.getPaddingBottom())));
    }
    if (definition.getPaddingRight() > 0)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "padding-right",
          pointConverter.format(StrictGeomUtility.toExternalValue(definition.getPaddingRight())));
    }

    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "x",
        pointConverter.format(StrictGeomUtility.toExternalValue(box.getX())));
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "y",
        pointConverter.format(StrictGeomUtility.toExternalValue(box.getY())));
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "width",
        pointConverter.format(StrictGeomUtility.toExternalValue(box.getWidth())));
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "height",
        pointConverter.format(StrictGeomUtility.toExternalValue(box.getHeight())));

    final Color backgroundColor = (Color) box.getStyleSheet().getStyleProperty(ElementStyleKeys.BACKGROUND_COLOR);
    if (backgroundColor != null)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "background-color",
          ColorValueConverter.colorToString(backgroundColor));
    }

    final Color color = (Color) box.getStyleSheet().getStyleProperty(ElementStyleKeys.PAINT);
    if (color != null)
    {
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "color",
          ColorValueConverter.colorToString(color));
    }
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "font-face",
        (String) box.getStyleSheet().getStyleProperty(TextStyleKeys.FONT));
    final Object o = box.getStyleSheet().getStyleProperty(TextStyleKeys.FONTSIZE);
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "font-size", pointIntConverter.format(o));
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "font-style-bold",
        String.valueOf(box.getStyleSheet().getStyleProperty(TextStyleKeys.BOLD)));
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "font-style-italics",
        String.valueOf(box.getStyleSheet().getStyleProperty(TextStyleKeys.ITALIC)));
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "font-style-underline",
        String.valueOf(box.getStyleSheet().getStyleProperty(TextStyleKeys.UNDERLINED)));
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "font-style-strikethrough",
        String.valueOf(box.getStyleSheet().getStyleProperty(TextStyleKeys.STRIKETHROUGH)));

    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "anchor",
        (String) box.getStyleSheet().getStyleProperty(ElementStyleKeys.ANCHOR_NAME));
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "href",
        (String) box.getStyleSheet().getStyleProperty(ElementStyleKeys.HREF_TARGET));
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "href-window",
        (String) box.getStyleSheet().getStyleProperty(ElementStyleKeys.HREF_WINDOW));
    attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "href-title",
        (String) box.getStyleSheet().getStyleProperty(ElementStyleKeys.HREF_TITLE));

    return attributeList;
  }

  private String convertColorToString(final BorderEdge borderEdge)
  {
    final Color o = borderEdge.getColor();
    if (o == null)
    {
      return null;
    }
    return ColorValueConverter.colorToString(o);
  }

  private boolean isEmptyCorner(final BorderCorner corner)
  {
    if (ignoreEmptyBorders == false)
    {
      return false;
    }
    return corner.getWidth() == 0 && corner.getHeight() == 0;
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {

    try
    {
      final int nodeType = box.getNodeType();
      if ((nodeType & LayoutNodeTypes.MASK_BOX_PAGEAREA) == LayoutNodeTypes.MASK_BOX_PAGEAREA)
      {
        final AttributeList list = createBoxAttributeList(box);
        list.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "type", box.getName());
        xmlWriter.writeTag(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "page-area", list, XmlWriter.OPEN);
        writeElementAttributes(box);
        return true;
      }
      else if (nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
      {
        return startBox(box, "p");
      }
      else
      {
        return startBox(box, "block");
      }
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected void finishBlockBox(final BlockRenderBox box)
  {
    finishBox();
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_LINEBOX)
    {
      return startBox(box, "line");
    }
    else
    {
      return startBox(box, "inline");
    }
  }

  protected void finishInlineBox(final InlineRenderBox box)
  {
    finishBox();
  }

  protected boolean startOtherBox(final RenderBox box)
  {
    return startBox(box, "other-box");
  }

  protected void finishOtherBox(final RenderBox box)
  {
    finishBox();
  }

  protected boolean startCanvasBox(final CanvasRenderBox box)
  {
    return startBox(box, "canvas");
  }

  protected void finishCanvasBox(final CanvasRenderBox box)
  {
    finishBox();
  }

  protected void processOtherNode(final RenderNode node)
  {
    try
    {
      final int nodeType = node.getNodeType();
      if (nodeType == LayoutNodeTypes.TYPE_NODE_TEXT)
      {
        final RenderableText text = (RenderableText) node;
        final AttributeList attributeList = new AttributeList();
        attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "x",
            pointConverter.format(StrictGeomUtility.toExternalValue(node.getX())));
        attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "y",
            pointConverter.format(StrictGeomUtility.toExternalValue(node.getY())));
        attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "width",
            pointConverter.format(StrictGeomUtility.toExternalValue(node.getWidth())));
        attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "height",
            pointConverter.format(StrictGeomUtility.toExternalValue(node.getHeight())));
        xmlWriter.writeTag(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "text", attributeList, XmlWriter.OPEN);
        xmlWriter.writeTextNormalized(text.getRawText(), true);
        xmlWriter.writeCloseTag();

      }
      else if (nodeType == LayoutNodeTypes.TYPE_NODE_SPACER)
      {
        final SpacerRenderNode spacer = (SpacerRenderNode) node;
        final AttributeList attributeList = new AttributeList();
        attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "width",
            pointConverter.format(StrictGeomUtility.toExternalValue(node.getWidth())));
        attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "height",
            pointConverter.format(StrictGeomUtility.toExternalValue(node.getHeight())));
        attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "preserve",
            String.valueOf(spacer.isDiscardable() == false));
        xmlWriter.writeTag(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "spacer", attributeList, XmlWriter.CLOSE);
      }
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected void processRenderableContent(final RenderableReplacedContentBox node)
  {
    try
    {
      final RenderableReplacedContent prc = node.getContent();
      final AttributeList attributeList = new AttributeList();
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "x",
          pointConverter.format(StrictGeomUtility.toExternalValue(node.getX())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "y",
          pointConverter.format(StrictGeomUtility.toExternalValue(node.getY())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "width",
          pointConverter.format(StrictGeomUtility.toExternalValue(node.getWidth())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "height",
          pointConverter.format(StrictGeomUtility.toExternalValue(node.getHeight())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "source", String.valueOf(prc.getSource()));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "content-width",
          pointConverter.format(StrictGeomUtility.toExternalValue(prc.getContentWidth())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "content-height",
          pointConverter.format(StrictGeomUtility.toExternalValue(prc.getContentHeight())));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "requested-width",
          convertRenderLength(prc.getRequestedWidth()));
      attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "requested-height",
          convertRenderLength(prc.getRequestedHeight()));

      final Object o = prc.getRawObject();
      if (o != null)
      {
        attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "raw-object-type",
            o.getClass().getName());
      }
      else
      {
        attributeList.setAttribute(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "raw-object-type", "null");
      }
      xmlWriter.writeTag(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, "replaced-content", attributeList, XmlWriter.OPEN);
      writeElementAttributes(node);
      xmlWriter.writeCloseTag();
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  private String convertRenderLength(final RenderLength length)
  {
    if (length == null)
    {
      return null;
    }
    if (RenderLength.AUTO.equals(length))
    {
      return "auto";
    }
    if (length.isPercentage())
    {
      return pointConverter.format(StrictGeomUtility.toExternalValue(-length.getValue())) + '%';
    }
    return pointConverter.format(StrictGeomUtility.toExternalValue(length.getValue()));
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }


  protected boolean startRowBox(final RenderBox box)
  {
    return startBox(box, "row-box");
  }

  protected void finishRowBox(final RenderBox box)
  {
    finishBox();
  }

  private void finishBox()
  {
    try
    {
      xmlWriter.writeCloseTag();
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }

  protected boolean startTableCellBox(final TableCellRenderBox box)
  {
    return startBox(box, "table-cell");
  }

  protected void finishTableCellBox(final TableCellRenderBox box)
  {
    finishBox();
  }

  protected boolean startTableRowBox(final TableRowRenderBox box)
  {
    return startBox(box, "table-row");
  }

  protected void finishTableRowBox(final TableRowRenderBox box)
  {
    finishBox();
  }

  protected boolean startTableSectionBox(final TableSectionRenderBox box)
  {
    return startBox(box, "table-section");
  }

  protected void finishTableSectionBox(final TableSectionRenderBox box)
  {
    finishBox();
  }

  protected boolean startTableColumnGroupBox(final TableColumnGroupNode box)
  {
    return startBox(box, "colgroup");
  }

  protected void finishTableColumnGroupBox(final TableColumnGroupNode box)
  {
    finishBox();
  }

  protected boolean startTableBox(final TableRenderBox box)
  {
    return startBox(box, "table");
  }

  protected void finishTableBox(final TableRenderBox box)
  {
    finishBox();
  }

  protected boolean startAutoBox(final RenderBox box)
  {
    final int layoutNodeType = box.getLayoutNodeType() & LayoutNodeTypes.MASK_BASIC_BOX_TYPE;
    if (layoutNodeType == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      return startBox(box, "block");
    }
    if (layoutNodeType == LayoutNodeTypes.MASK_BOX_CANVAS)
    {
      return startBox(box, "canvas");
    }
    if (layoutNodeType == LayoutNodeTypes.MASK_BOX_INLINE)
    {
      return startBox(box, "inline");
    }
    if (layoutNodeType == LayoutNodeTypes.MASK_BOX_ROW)
    {
      return startBox(box, "row");
    }
    return startBox(box, "auto");
  }

  protected void finishAutoBox(final RenderBox box)
  {
    finishBox();
  }

  protected boolean startBox(final RenderBox box, final String tagName)
  {
    try
    {
      xmlWriter.writeTag(XmlDocumentWriter.LAYOUT_OUTPUT_NAMESPACE, tagName, createBoxAttributeList(box), XmlWriter.OPEN);
      writeElementAttributes(box);
      return true;
    }
    catch (IOException e)
    {
      throw new InvalidReportStateException(e.getMessage(), e);
    }
  }
}