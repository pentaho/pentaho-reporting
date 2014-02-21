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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import java.awt.font.TextLayout;
import java.io.IOException;
import java.text.NumberFormat;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.URLImageContainer;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.imagemap.parser.ImageMapWriter;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.layout.process.util.RichTextSpec;
import org.pentaho.reporting.engine.classic.core.layout.text.GlyphList;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.DefaultTextExtractor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.DefaultStyleBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlOutputProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlTextExtractorState;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.StyleBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.StyleManager;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.CharacterEntityParser;
import org.pentaho.reporting.libraries.xmlns.writer.HtmlCharacterEntities;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

/**
 * Creation-Date: 02.11.2007, 15:58:29
 *
 * @author Thomas Morgner
 */
public class HtmlTextExtractor extends DefaultTextExtractor
{
  private static final String DIV_TAG = "div";
  private static final String HREF_ATTR = "href";
  private static final String TARGET_ATTR = "target";
  private static final String TITLE_ATTR = "title";
  private static final String A_TAG = "a";
  private static final String BR_TAG = "br";
  private static final String SPAN_TAG = "span";
  private static final String IMG_TAG = "img";
  private static final String SRC_ATTR = "src";
  private static final String USEMAP_ATTR = "usemap";
  private static final String PT_UNIT = "pt";
  private static final String PX_UNIT = "px";
  private static final String ALT_ATTR = "alt";

  private OutputProcessorMetaData metaData;
  private XmlWriter xmlWriter;
  private StyleManager styleManager;
  private StyleBuilder styleBuilder;
  private HtmlContentGenerator contentGenerator;
  private CharacterEntityParser characterEntityParser;
  private boolean result;
  private boolean safariLengthFix;
  private RenderBox firstElement;
  private boolean useWhitespacePreWrap;
  private boolean enableRoundBorderCorner;
  private HtmlTextExtractorState processStack;
  private boolean enableInheritedLinkStyle;

  public HtmlTextExtractor(final OutputProcessorMetaData metaData,
                           final XmlWriter xmlWriter,
                           final StyleManager styleManager,
                           final HtmlContentGenerator contentGenerator)
  {
    super(metaData);
    if (xmlWriter == null)
    {
      throw new NullPointerException();
    }
    if (styleManager == null)
    {
      throw new NullPointerException();
    }
    if (contentGenerator == null)
    {
      throw new NullPointerException();
    }

    this.contentGenerator = contentGenerator;
    this.metaData = metaData;
    this.xmlWriter = xmlWriter;
    this.styleManager = styleManager;
    this.styleBuilder = new DefaultStyleBuilder();
    this.characterEntityParser = HtmlCharacterEntities.getEntityParser();
    this.safariLengthFix = ("true".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.html.SafariLengthHack")));
    this.useWhitespacePreWrap = ("true".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.html.UseWhitespacePreWrap")));
    this.enableRoundBorderCorner = ("true".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.html.EnableRoundBorderCorner")));
    this.enableInheritedLinkStyle = ("true".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.html.LinksInheritStyle")));
  }

  public boolean performOutput(final RenderBox content, final StyleBuilder.StyleCarrier[] cellStyle) throws IOException
  {
    styleBuilder.clear();
    clearText();
    setRawResult(null);
    result = false;
    processStack = new HtmlTextExtractorState(null, false, cellStyle);

    firstElement = content;

    try
    {
      final int nodeType = content.getNodeType();
      if (nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
      {
        processParagraphCell((ParagraphRenderBox) content);
      }
      else if (nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT)
      {
        processRenderableContent((RenderableReplacedContentBox) content);
      }
      else
      {
        processBoxChilds(content);
      }
    }
    finally
    {
      processStack = null;
      firstElement = null;
    }
    return result;
  }

  /**
   * Prints the contents of a canvas box. This can happen only once per cell, as every canvas box creates its
   * own cell at some point. If for some strange reason a canvas box appears in the middle of a box-structure,
   * your layouter is probably a mess and this method will treat the box as a generic content container.
   *
   * @param box the canvas box
   * @return true, if the child content will be processed, false otherwise.
   */
  public boolean startCanvasBox(final CanvasRenderBox box)
  {
    if (box.getStaticBoxLayoutProperties().isVisible() == false)
    {
      return false;
    }

    try
    {
      final ReportAttributeMap attrs = box.getAttributes();
      if (firstElement != box)
      {
        final AttributeList attrList = new AttributeList();
        HtmlPrinter.applyHtmlAttributes(attrs, attrList);
        if (attrList.isEmpty() == false)
        {
          xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, DIV_TAG, attrList, XmlWriterSupport.OPEN);
        }
        processStack = new HtmlTextExtractorState(processStack, true);
      }
      else
      {
        processStack = new HtmlTextExtractorState(processStack, false);
      }

      final Object rawContent = attrs.getAttribute(AttributeNames.Html.NAMESPACE,
          AttributeNames.Html.EXTRA_RAW_CONTENT);
      if (rawContent != null)
      {
        xmlWriter.writeText(String.valueOf(rawContent));
      }

      final StyleSheet styleSheet = box.getStyleSheet();
      final String target = (String) styleSheet.getStyleProperty(ElementStyleKeys.HREF_TARGET);
      if (target != null)
      {
        handleLinkOnElement(styleSheet, target);
        processStack = new HtmlTextExtractorState(processStack, true);
      }
      else
      {
        processStack = new HtmlTextExtractorState(processStack, false);
      }

      if (Boolean.TRUE.equals(attrs.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.SUPPRESS_CONTENT)))
      {
        return false;
      }

      return true;
    }
    catch (IOException e)
    {
      throw new HtmlOutputProcessingException("Failed to perform IO", e);
    }
  }

  private String normalizeWindow(final String window)
  {
    if ("_top".equalsIgnoreCase(window)) //NON-NLS
    {
      return "_top"; //NON-NLS
    }
    if ("_self".equalsIgnoreCase(window)) //NON-NLS
    {
      return "_self"; //NON-NLS
    }
    if ("_parent".equalsIgnoreCase(window)) //NON-NLS
    {
      return "_parent"; //NON-NLS
    }
    if ("_blank".equalsIgnoreCase(window)) //NON-NLS
    {
      return "_blank"; //NON-NLS
    }
    return window;
  }

  public void finishCanvasBox(final CanvasRenderBox box)
  {
    if (box.getStaticBoxLayoutProperties().isVisible() == false)
    {
      return;
    }
    try
    {
      if (processStack.isWrittenTag())
      {
        xmlWriter.writeCloseTag();
      }
      processStack = processStack.getParent();

      final Object rawFooterContent = box.getAttributes().getAttribute(AttributeNames.Html.NAMESPACE,
          AttributeNames.Html.EXTRA_RAW_FOOTER_CONTENT);
      if (rawFooterContent != null)
      {
        xmlWriter.writeText(String.valueOf(rawFooterContent));
      }

      if (processStack.isWrittenTag())
      {
        xmlWriter.writeCloseTag();
      }
      processStack = processStack.getParent();
    }
    catch (IOException e)
    {
      throw new HtmlOutputProcessingException("Failed to perform IO", e);
    }
  }

  /**
   * Prints a paragraph cell. This is a special entry point used by the processContent method and is never
   * called from elsewhere. This method assumes that the attributes of the paragraph have been processed as
   * part of the table-cell processing.
   *
   * @param box the paragraph box
   * @throws IOException if an IO error occured.
   */
  protected void processParagraphCell(final ParagraphRenderBox box) throws IOException
  {
    if (box.getStaticBoxLayoutProperties().isVisible() == false)
    {
      return;
    }

    final StyleSheet styleSheet = box.getStyleSheet();
    final String target = (String) styleSheet.getStyleProperty(ElementStyleKeys.HREF_TARGET);
    if (target != null)
    {
      handleLinkOnElement(styleSheet, target);
      processStack = new HtmlTextExtractorState(processStack, true);
    }
    else
    {
      processStack = new HtmlTextExtractorState(processStack, false);
    }

    if (Boolean.TRUE.equals
        (box.getAttributes().getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.SUPPRESS_CONTENT)) == false)
    {
      processParagraphChilds(box);
    }

    if (processStack.isWrittenTag())
    {
      xmlWriter.writeCloseTag();
    }
    processStack = processStack.getParent();

  }

  protected void addEmptyBreak()
  {
    try
    {
      xmlWriter.writeText(" ");
    }
    catch (IOException e)
    {
      throw new HtmlOutputProcessingException("Failed to perform IO", e);
    }
  }

  protected void addSoftBreak()
  {
    try
    {
      xmlWriter.writeText(" ");
    }
    catch (IOException e)
    {
      throw new HtmlOutputProcessingException("Failed to perform IO", e);
    }
  }

  protected void addLinebreak()
  {
    try
    {
      result = true;
      xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, BR_TAG, XmlWriterSupport.CLOSE);
    }
    catch (IOException e)
    {
      throw new HtmlOutputProcessingException("Failed to perform IO", e);
    }
  }

  protected boolean startBlockBox(final BlockRenderBox box)
  {
    if (box.getStaticBoxLayoutProperties().isVisible() == false)
    {
      return false;
    }

    try
    {
      final AttributeList attrList = new AttributeList();
      final ReportAttributeMap attrs = box.getAttributes();
      if (firstElement != box)
      {
        HtmlPrinter.applyHtmlAttributes(attrs, attrList);
        final StyleBuilder style = HtmlPrinter.produceTextStyle
            (styleBuilder, box, true, safariLengthFix,
                useWhitespacePreWrap, enableRoundBorderCorner, processStack.getStyle());
        styleManager.updateStyle(style, attrList);
      }

      xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, DIV_TAG, attrList, XmlWriterSupport.OPEN);
      processStack = new HtmlTextExtractorState(processStack, true, styleBuilder.toArray());

      final Object rawContent = attrs.getAttribute(AttributeNames.Html.NAMESPACE,
          AttributeNames.Html.EXTRA_RAW_CONTENT);
      if (rawContent != null)
      {
        xmlWriter.writeText(String.valueOf(rawContent));
      }

      final StyleSheet styleSheet = box.getStyleSheet();
      if (firstElement != box)
      {
        writeLocalAnchor(box);
      }
      final String target = (String) styleSheet.getStyleProperty(ElementStyleKeys.HREF_TARGET);
      if (target != null)
      {
        handleLinkOnElement(styleSheet, target);
        processStack = new HtmlTextExtractorState(processStack, true);
      }
      else
      {
        processStack = new HtmlTextExtractorState(processStack, false);
      }

      if (Boolean.TRUE.equals(attrs.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.SUPPRESS_CONTENT)))
      {
        return false;
      }

      return true;
    }
    catch (IOException e)
    {
      throw new HtmlOutputProcessingException("Failed to perform IO", e);
    }
  }

  private void handleLinkOnElement(final StyleSheet styleSheet, final String target) throws IOException
  {
    final String window = (String) styleSheet.getStyleProperty(ElementStyleKeys.HREF_WINDOW);
    final AttributeList linkAttr = new AttributeList();
    linkAttr.setAttribute(HtmlPrinter.XHTML_NAMESPACE, HREF_ATTR, target);
    if (window != null && StringUtils.startsWithIgnoreCase(target, "javascript:") == false) //NON-NLS
    {
      linkAttr.setAttribute(HtmlPrinter.XHTML_NAMESPACE, TARGET_ATTR, normalizeWindow(window));
    }
    final String title = (String) styleSheet.getStyleProperty(ElementStyleKeys.HREF_TITLE);
    if (title != null)
    {
      linkAttr.setAttribute(HtmlPrinter.XHTML_NAMESPACE, TITLE_ATTR, title);
    }
    if (enableInheritedLinkStyle)
    {
      styleBuilder = createLinkStyle(styleBuilder);
      styleManager.updateStyle(styleBuilder, linkAttr);
    }
    xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, A_TAG, linkAttr, XmlWriterSupport.OPEN);
  }

  private StyleBuilder createLinkStyle(StyleBuilder b)
  {
    if (b == null)
    {
      b = new DefaultStyleBuilder();
    }

    b.append(StyleBuilder.CSSKeys.FONT_STYLE, "inherit");
    b.append(StyleBuilder.CSSKeys.FONT_FAMILY, "inherit");
    b.append(StyleBuilder.CSSKeys.FONT_WEIGHT, "inherit");
    b.append(StyleBuilder.CSSKeys.FONT_SIZE, "inherit");
    b.append(StyleBuilder.CSSKeys.TEXT_DECORATION, "inherit");
    b.append(StyleBuilder.CSSKeys.COLOR, "inherit");
    return b;
  }

  protected void finishBlockBox(final BlockRenderBox box)
  {
    if (box.getStaticBoxLayoutProperties().isVisible() == false)
    {
      return;
    }

    try
    {
      if (processStack.isWrittenTag())
      {
        xmlWriter.writeCloseTag();
      }
      processStack = processStack.getParent();

      final Object rawFooterContent = box.getAttributes().getAttribute(AttributeNames.Html.NAMESPACE,
          AttributeNames.Html.EXTRA_RAW_FOOTER_CONTENT);
      if (rawFooterContent != null)
      {
        xmlWriter.writeText(String.valueOf(rawFooterContent));
      }

      if (processStack.isWrittenTag())
      {
        xmlWriter.writeCloseTag();
      }
      processStack = processStack.getParent();
    }
    catch (IOException e)
    {
      throw new HtmlOutputProcessingException("Failed to perform IO", e);
    }
  }

  /**
   * Like a canvas box, a row-box should be split into several cells already. Therefore we treat it as a generic
   * content container instead.
   *
   * @param box
   * @return
   */
  protected boolean startRowBox(final RenderBox box)
  {
    if (box.getStaticBoxLayoutProperties().isVisible() == false)
    {
      return false;
    }

    try
    {
      final AttributeList attrList = new AttributeList();
      final ReportAttributeMap attrs = box.getAttributes();
      if (firstElement != box)
      {
        HtmlPrinter.applyHtmlAttributes(attrs, attrList);
        final StyleBuilder style = HtmlPrinter.produceTextStyle
            (styleBuilder, box, true, safariLengthFix,
                useWhitespacePreWrap, enableRoundBorderCorner, processStack.getStyle());
        styleManager.updateStyle(style, attrList);
      }

      xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, DIV_TAG, attrList, XmlWriterSupport.OPEN);
      processStack = new HtmlTextExtractorState(processStack, true, styleBuilder.toArray());

      final Object rawContent = attrs.getAttribute(AttributeNames.Html.NAMESPACE,
          AttributeNames.Html.EXTRA_RAW_CONTENT);
      if (rawContent != null)
      {
        xmlWriter.writeText(String.valueOf(rawContent));
      }

      final StyleSheet styleSheet = box.getStyleSheet();
      if (firstElement != box)
      {
        writeLocalAnchor(box);
      }

      final String target = (String) styleSheet.getStyleProperty(ElementStyleKeys.HREF_TARGET);
      if (target != null)
      {
        handleLinkOnElement(styleSheet, target);
        processStack = new HtmlTextExtractorState(processStack, true);
      }
      else
      {
        processStack = new HtmlTextExtractorState(processStack, false);
      }

      if (Boolean.TRUE.equals(attrs.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.SUPPRESS_CONTENT)))
      {
        return false;
      }

      return true;
    }
    catch (IOException e)
    {
      throw new HtmlOutputProcessingException("Failed to perform IO", e);
    }
  }

  protected void finishRowBox(final RenderBox box)
  {
    if (box.getStaticBoxLayoutProperties().isVisible() == false)
    {
      return;
    }

    try
    {
      if (processStack.isWrittenTag())
      {
        xmlWriter.writeCloseTag();
      }
      processStack = processStack.getParent();

      final Object rawFooterContent = box.getAttributes().getAttribute(AttributeNames.Html.NAMESPACE,
          AttributeNames.Html.EXTRA_RAW_FOOTER_CONTENT);
      if (rawFooterContent != null)
      {
        xmlWriter.writeText(String.valueOf(rawFooterContent));
      }

      if (processStack.isWrittenTag())
      {
        xmlWriter.writeCloseTag();
      }
      processStack = processStack.getParent();
    }
    catch (IOException e)
    {
      throw new HtmlOutputProcessingException("Failed to perform IO", e);
    }
  }

  protected boolean startInlineBox(final InlineRenderBox box)
  {
    if (box.getStaticBoxLayoutProperties().isVisible() == false)
    {
      return false;
    }

    try
    {
      final ReportAttributeMap attrs = box.getAttributes();
      if (firstElement != box)
      {
        final AttributeList attrList = new AttributeList();
        HtmlPrinter.applyHtmlAttributes(attrs, attrList);
        final StyleBuilder style = HtmlPrinter.produceTextStyle
            (styleBuilder, box, true, safariLengthFix,
                useWhitespacePreWrap, enableRoundBorderCorner, processStack.getStyle());
        styleManager.updateStyle(style, attrList);

        if (attrList.isEmpty() == false)
        {
          xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, SPAN_TAG, attrList, XmlWriterSupport.OPEN);
          processStack = new HtmlTextExtractorState(processStack, true, styleBuilder.toArray());
        }
        else
        {
          processStack = new HtmlTextExtractorState(processStack, false);
        }
      }
      else
      {
        processStack = new HtmlTextExtractorState(processStack, false);
      }

      final Object rawContent = attrs.getAttribute(AttributeNames.Html.NAMESPACE,
          AttributeNames.Html.EXTRA_RAW_CONTENT);
      if (rawContent != null)
      {
        xmlWriter.writeText(String.valueOf(rawContent));
      }

      final StyleSheet styleSheet = box.getStyleSheet();
      if (firstElement != box)
      {
        writeLocalAnchor(box);
      }

      final String target = (String) styleSheet.getStyleProperty(ElementStyleKeys.HREF_TARGET);
      if (target != null)
      {
        handleLinkOnElement(styleSheet, target);
        processStack = new HtmlTextExtractorState(processStack, true);
      }
      else
      {
        processStack = new HtmlTextExtractorState(processStack, false);
      }

      if (Boolean.TRUE.equals(attrs.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.SUPPRESS_CONTENT)))
      {
        return false;
      }

      return true;
    }
    catch (IOException e)
    {
      throw new HtmlOutputProcessingException("Failed to perform IO", e);
    }
  }

  protected void finishInlineBox(final InlineRenderBox box)
  {
    if (box.getStaticBoxLayoutProperties().isVisible() == false)
    {
      return;
    }

    try
    {
      if (processStack.isWrittenTag())
      {
        xmlWriter.writeCloseTag();
      }
      processStack = processStack.getParent();

      final Object rawFooterContent = box.getAttributes().getAttribute(AttributeNames.Html.NAMESPACE,
          AttributeNames.Html.EXTRA_RAW_FOOTER_CONTENT);
      if (rawFooterContent != null)
      {
        xmlWriter.writeText(String.valueOf(rawFooterContent));
      }

      if (processStack.isWrittenTag())
      {
        xmlWriter.writeCloseTag();
      }
      processStack = processStack.getParent();
    }
    catch (IOException e)
    {
      throw new HtmlOutputProcessingException("Failed to perform IO", e);
    }
  }


  protected void processOtherNode(final RenderNode node)
  {
    try
    {
      final int nodeType = node.getNodeType();
      if (nodeType == LayoutNodeTypes.TYPE_NODE_TEXT || nodeType == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT)
      {
        super.processOtherNode(node);
        return;
      }

      if (node.isVirtualNode())
      {
        return;
      }

      if (nodeType == LayoutNodeTypes.TYPE_NODE_SPACER)
      {
        final SpacerRenderNode spacer = (SpacerRenderNode) node;
        final int count = Math.max(1, spacer.getSpaceCount());
        for (int i = 0; i < count; i++)
        {
          xmlWriter.writeText(" ");
        }
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException("Failed", e);
    }
  }

  protected void processRenderableContent(final RenderableReplacedContentBox node)
  {
    try
    {
      final ReportAttributeMap map = node.getAttributes();
      final AttributeList attrs = new AttributeList();
      HtmlPrinter.applyHtmlAttributes(map, attrs);
      if (attrs.isEmpty() == false)
      {
        xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, DIV_TAG, attrs, XmlWriterSupport.OPEN);
      }

      writeLocalAnchor(node);

      final StyleSheet styleSheet = node.getStyleSheet();
      final String target = (String) styleSheet.getStyleProperty(ElementStyleKeys.HREF_TARGET);
      if (target != null)
      {
        handleLinkOnElement(styleSheet, target);
      }

      processReplacedContent(node);

      if (target != null)
      {
        xmlWriter.writeCloseTag();
      }
      if (attrs.isEmpty() == false)
      {
        xmlWriter.writeCloseTag();
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException("Failed", e);
    }
    catch (ContentIOException e)
    {
      throw new RuntimeException("Failed", e);
    }
  }

  private void writeLocalAnchor(final RenderNode node) throws IOException
  {
    final StyleSheet styleSheet = node.getStyleSheet();
    final String anchor = (String) styleSheet.getStyleProperty(ElementStyleKeys.ANCHOR_NAME);
    if (anchor != null)
    {
      xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, A_TAG, "name", anchor, XmlWriterSupport.CLOSE);
    }
  }

  /**
   * @noinspection StringConcatenation
   */
  private void processReplacedContent(final RenderableReplacedContentBox node) throws IOException, ContentIOException
  {

    final RenderableReplacedContent rc = node.getContent();
    final Object rawObject = rc.getRawObject();
    // We have to do three things here. First, we have to check what kind
    // of content we deal with.
    if (rawObject instanceof URLImageContainer)
    {
      final URLImageContainer urlImageContainer = (URLImageContainer) rawObject;
      final ResourceKey source = urlImageContainer.getResourceKey();
      if (source != null)
      {
        // Cool, we have access to the raw-data. Thats always nice as we
        // dont have to recode the whole thing. We can only recode images, not drawables.
        if (contentGenerator.isRegistered(source) == false)
        {
          // Write image reference; return the name of the reference. This method will
          // return null, if the image is not recognized (it is no JPG, PNG or GIF image)
          final String name = contentGenerator.writeRaw(source);
          if (name != null)
          {
            // Write image reference ..
            final AttributeList attrList = new AttributeList();
            attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, SRC_ATTR, name);
            attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "border", "0"); //NON-NLS
            // width and height and scaling and so on ..
            final StyleBuilder imgStyle = produceImageStyle(node);
            if (imgStyle == null)
            {
              final AttributeList clipAttrList = new AttributeList();
              final StyleBuilder divStyle = produceClipStyle(node);
              styleManager.updateStyle(divStyle, clipAttrList);

              xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, DIV_TAG, clipAttrList, XmlWriter.OPEN);
              xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, IMG_TAG, attrList, XmlWriter.CLOSE);
              xmlWriter.writeCloseTag();
            }
            else
            {
              styleManager.updateStyle(imgStyle, attrList);
              xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, IMG_TAG, attrList, XmlWriter.CLOSE);
            }

            contentGenerator.registerContent(source, name);
            result = true;
            return;
          }
          else
          {
            // Mark this object as non-readable. This way we dont retry the failed operation
            // over and over again.
            contentGenerator.registerFailure(source);
          }
        }
        else
        {
          final String cachedName = contentGenerator.getRegisteredName(source);
          if (cachedName != null)
          {
            final AttributeList attrList = new AttributeList();
            attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, SRC_ATTR, cachedName);
            attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "border", "0"); //NON-NLS
            // width and height and scaling and so on ..
            final StyleBuilder imgStyle = produceImageStyle(node);
            if (imgStyle == null)
            {
              final AttributeList clipAttrList = new AttributeList();
              final StyleBuilder divStyle = produceClipStyle(node);
              styleManager.updateStyle(divStyle, clipAttrList);

              xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, DIV_TAG, clipAttrList, XmlWriter.OPEN);
              xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, IMG_TAG, attrList, XmlWriter.CLOSE);
              xmlWriter.writeCloseTag();
            }
            else
            {
              styleManager.updateStyle(imgStyle, attrList);
              xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, IMG_TAG, attrList, XmlWriter.CLOSE);
            }
            result = true;
            return;
          }
        }
      }
    }

    // Fallback: (At the moment, we only support drawables and images.)
    final ReportAttributeMap attributes = node.getAttributes();
    if (rawObject instanceof ImageContainer)
    {
      final String type = RenderUtility.getEncoderType(attributes);
      final float quality = RenderUtility.getEncoderQuality(attributes);

      // Make it a PNG file ..
      //xmlWriter.writeComment("Image content source:" + source);
      final String name = contentGenerator.writeImage((ImageContainer) rawObject, type, quality, true);
      if (name != null)
      {
        // Write image reference ..
        final AttributeList attrList = new AttributeList();
        attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, SRC_ATTR, name);
        attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "border", "0"); //NON-NLS
        final Object titleText = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.TITLE);
        if (titleText != null)
        {
          attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, TITLE_ATTR, String.valueOf(titleText));
        }

        final Object altText = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ALT);
        if (altText != null)
        {
          attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, ALT_ATTR, String.valueOf(altText));
        }
        // width and height and scaling and so on ..
        final StyleBuilder imgStyle = produceImageStyle(node);
        if (imgStyle == null)
        {
          final AttributeList clipAttrList = new AttributeList();
          final StyleBuilder divStyle = produceClipStyle(node);
          styleManager.updateStyle(divStyle, clipAttrList);

          xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, DIV_TAG, clipAttrList, XmlWriterSupport.OPEN);
          xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, IMG_TAG, attrList, XmlWriterSupport.CLOSE);
          xmlWriter.writeCloseTag();
        }
        else
        {
          styleManager.updateStyle(imgStyle, attrList);
          xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, IMG_TAG, attrList, XmlWriterSupport.CLOSE);
        }
        result = true;
      }

      return;
    }

    if (rawObject instanceof DrawableWrapper)
    {
      // render it into an Buffered image and make it a PNG file.
      final DrawableWrapper drawable = (DrawableWrapper) rawObject;
      final StrictBounds cb = new StrictBounds(node.getX(), node.getY(), node.getWidth(), node.getHeight());
      final ImageContainer image = RenderUtility.createImageFromDrawable(drawable, cb, node,
          metaData);
      if (image == null)
      {
        //xmlWriter.writeComment("Drawable content [No image generated]:" + source);
        return;
      }

      final String type = RenderUtility.getEncoderType(attributes);
      final float quality = RenderUtility.getEncoderQuality(attributes);

      final String name = contentGenerator.writeImage(image, type, quality, true);
      if (name == null)
      {
        //xmlWriter.writeComment("Drawable content [No image written]:" + source);
        return;
      }

      //xmlWriter.writeComment("Drawable content:" + source);
      // Write image reference ..
      final ImageMap imageMap;
      final AttributeList attrList = new AttributeList();
      attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, SRC_ATTR, name);
      attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "border", "0"); //NON-NLS

      final Object imageMapNameOverride = attributes.getAttribute
          (AttributeNames.Html.NAMESPACE, AttributeNames.Html.IMAGE_MAP_OVERRIDE);
      if (imageMapNameOverride != null)
      {
        attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, USEMAP_ATTR, String.valueOf(imageMapNameOverride));
        imageMap = null;
      }
      else
      {
        // only generate a image map, if the user does not specify their own onw via the override.
        // Of course, they would have to provide the map by other means as well.
        imageMap = RenderUtility.extractImageMap(node);

        if (imageMap != null)
        {
          final String mapName = imageMap.getAttribute(HtmlPrinter.XHTML_NAMESPACE, "name");
          if (mapName != null)
          {
            attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, USEMAP_ATTR, "#" + mapName);
          }
          else
          {
            final String generatedName = "generated_" + name + "_map"; //NON-NLS
            imageMap.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "name", generatedName);
            //noinspection MagicCharacter
            attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, USEMAP_ATTR, '#' + generatedName);//NON-NLS
          }
        }
      }

      final Object titleText = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.TITLE);
      if (titleText != null)
      {
        attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, TITLE_ATTR, String.valueOf(titleText));
      }

      final Object altText = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ALT);
      if (altText != null)
      {
        attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, ALT_ATTR, String.valueOf(altText));
      }
      // width and height and scaling and so on ..
      final StyleBuilder imgStyle = produceImageStyle(node);
      if (imgStyle == null)
      {
        final AttributeList clipAttrList = new AttributeList();
        final StyleBuilder divStyle = produceClipStyle(node);
        styleManager.updateStyle(divStyle, clipAttrList);

        xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, DIV_TAG, clipAttrList, XmlWriterSupport.OPEN);
        xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, IMG_TAG, attrList, XmlWriterSupport.CLOSE);
        xmlWriter.writeCloseTag();
      }
      else
      {
        styleManager.updateStyle(imgStyle, attrList);
        xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, IMG_TAG, attrList, XmlWriterSupport.CLOSE);
      }

      if (imageMap != null)
      {
        ImageMapWriter.writeImageMap(xmlWriter, imageMap, RenderUtility.getNormalizationScale(metaData));
      }
      result = true;
    }
  }

  private StyleBuilder produceClipStyle(final RenderableReplacedContentBox rc)
  {
    styleBuilder.clear(); // cuts down on object creation

    final long nodeWidth = rc.getWidth();
    final long nodeHeight = rc.getHeight();
    final NumberFormat pointConverter = styleBuilder.getPointConverter();
    styleBuilder.append(DefaultStyleBuilder.CSSKeys.OVERFLOW, "hidden"); //NON-NLS
    styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, pointConverter.format//NON-NLS
        (HtmlPrinter.fixLengthForSafari(StrictGeomUtility.toExternalValue(nodeWidth), safariLengthFix)), PT_UNIT);
    styleBuilder.append(DefaultStyleBuilder.CSSKeys.HEIGHT, pointConverter.format//NON-NLS
        (HtmlPrinter.fixLengthForSafari(StrictGeomUtility.toExternalValue(nodeHeight), safariLengthFix)), PT_UNIT);
    return styleBuilder;
  }

  /**
   * Populates the style builder with the style information for the image based on the RenderableReplacedContent
   *
   * @param rc th renderable content node.
   * @return the style-builder with the image style or null, if the image must be clipped.
   */
  private StyleBuilder produceImageStyle(final RenderableReplacedContentBox rc)
  {
    styleBuilder.clear(); // cuts down on object creation

    final NumberFormat pointConverter = styleBuilder.getPointConverter();
    final RenderableReplacedContent content = rc.getContent();
    final long contentWidth = content.getContentWidth();
    final long nodeWidth = rc.getWidth();
    final long contentHeight = content.getContentHeight();
    final long nodeHeight = rc.getHeight();
    final double scale = RenderUtility.getNormalizationScale(metaData);

    final StyleSheet styleSheet = rc.getStyleSheet();
    if (styleSheet.getBooleanStyleProperty(ElementStyleKeys.SCALE))
    {
      if (styleSheet.getBooleanStyleProperty(ElementStyleKeys.KEEP_ASPECT_RATIO) &&
          (contentWidth > 0 && contentHeight > 0))
      {
        final double scaleFactor = Math.min(nodeWidth / (double) contentWidth, nodeHeight / (double) contentHeight);

        styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, pointConverter.format
            (HtmlPrinter.fixLengthForSafari(StrictGeomUtility.toExternalValue((long) (contentWidth * scaleFactor * scale)),
                safariLengthFix)), PX_UNIT);
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.HEIGHT, pointConverter.format
            (HtmlPrinter.fixLengthForSafari(StrictGeomUtility.toExternalValue((long) (contentHeight * scaleFactor * scale)),
                safariLengthFix)), PX_UNIT);
      }
      else
      {
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, pointConverter.format
            (HtmlPrinter.fixLengthForSafari(StrictGeomUtility.toExternalValue((long) (nodeWidth * scale)),
                safariLengthFix)), PX_UNIT);
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.HEIGHT, pointConverter.format
            (HtmlPrinter.fixLengthForSafari(StrictGeomUtility.toExternalValue((long) (nodeHeight * scale)),
                safariLengthFix)), PX_UNIT);
      }
    }
    else
    {
      // for plain drawable content, there is no intrinsic-width or height, so we have to use the computed
      // width and height instead.
      if (contentWidth > nodeWidth || contentHeight > nodeHeight)
      {
        // There is clipping involved. The img-element does *not* receive a width or height property.
        // the width and height is applied to an external DIV element instead.
        return null;
      }

      if (contentWidth == 0 && contentHeight == 0)
      {
        // Drawable content has no intrinsic height or width, therefore we must not use the content size at all.
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, pointConverter.format(HtmlPrinter.fixLengthForSafari
            (StrictGeomUtility.toExternalValue((long) (nodeWidth * scale)), safariLengthFix)), PX_UNIT);
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.HEIGHT, pointConverter.format(HtmlPrinter.fixLengthForSafari
            (StrictGeomUtility.toExternalValue((long) (nodeHeight * scale)), safariLengthFix)), PX_UNIT);
      }
      else
      {
        final long width = Math.min(nodeWidth, contentWidth);
        final long height = Math.min(nodeHeight, contentHeight);
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, pointConverter.format(HtmlPrinter.fixLengthForSafari
            (StrictGeomUtility.toExternalValue((long) (width * scale)), safariLengthFix)), PX_UNIT);
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.HEIGHT, pointConverter.format(HtmlPrinter.fixLengthForSafari
            (StrictGeomUtility.toExternalValue((long) (height * scale)), safariLengthFix)), PX_UNIT);
      }
    }
    return styleBuilder;
  }

  protected void drawText(final RenderableText renderableText, final long contentX2)
  {
    try
    {

      if (renderableText.getLength() == 0)
      {
        // This text is empty.
        return;
      }
      if (renderableText.isNodeVisible(getParagraphBounds(), isOverflowX(), isOverflowY()) == false)
      {
        return;
      }

      final String text;
      final GlyphList gs = renderableText.getGlyphs();
      final int maxLength = renderableText.computeMaximumTextSize(contentX2);
      text = gs.getText(renderableText.getOffset(), maxLength, getCodePointBuffer());

      if (text.length() > 0)
      {
        xmlWriter.writeText(characterEntityParser.encodeEntities(text));
        if (text.trim().length() > 0)
        {
          result = true;
        }
        clearText();
      }
    }
    catch (IOException ioe)
    {
      throw new InvalidReportStateException("Failed to write text", ioe);
    }

  }
  protected void drawComplexText(final RenderableComplexText renderableComplexText)
  {
    try
    {

      if (renderableComplexText.getRawText().length() == 0)
      {
        // This text is empty.
        return;
      }
      if (renderableComplexText.isNodeVisible(getParagraphBounds(), isOverflowX(), isOverflowY()) == false)
      {
        return;
      }

      // check if we have to process inline text elements
      if(renderableComplexText.getRichText().getStyleChunks().size() > 1) {
        // iterate through all inline elements
        for(RichTextSpec.StyledChunk styledChunk: renderableComplexText.getRichText().getStyleChunks()) {
          final AttributeList attrList = new AttributeList();
          HtmlPrinter.applyHtmlAttributes(styledChunk.getOriginalAttributes(), attrList);

          // build the style for the current inline element
          final StyleBuilder style = HtmlPrinter.produceTextStyleFromStyleSheet
              (styleBuilder, styledChunk.getStyleSheet(), true, safariLengthFix,
                  useWhitespacePreWrap, processStack.getStyle());
          styleManager.updateStyle(style, attrList);

          String text = styledChunk.getText();

          if (text.length() > 0)
          {
            if (attrList.isEmpty() == false) {
              xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, SPAN_TAG, attrList, XmlWriterSupport.OPEN);
              xmlWriter.writeText(characterEntityParser.encodeEntities(text));
              xmlWriter.writeCloseTag();
            }
            else {
              xmlWriter.writeText(characterEntityParser.encodeEntities(text));
            }

            if (text.trim().length() > 0)
            {
              result = true;
            }
            clearText();
          }
        }
      }
      else {
        // regular processing (no online elements)
        final String text;
        TextLayout textLayout = renderableComplexText.getTextLayout();
        String debugInfo = textLayout.toString();
        String startPos = debugInfo.substring(debugInfo.indexOf("[start:"), debugInfo.indexOf(", len:")).replace("[start:","");
        int startPosIntValue = -1;

        try {
          startPosIntValue = Integer.parseInt(startPos);
        }
        catch (NumberFormatException e) {
          // do nothing
        }

        // workaround for line breaking (since the text cannot be extracted directly from textLayout as stream or String)
        // in order to avoid duplicates of same source raw text on multiple lines
        if((renderableComplexText.getRawText().length() > textLayout.getCharacterCount()) && startPosIntValue >= 0) {
          text = renderableComplexText.getRawText().substring(startPosIntValue, textLayout.getCharacterCount() + startPosIntValue);
        }
        else {
          text = renderableComplexText.getRawText();
        }

        if (text.length() > 0)
        {
          xmlWriter.writeText(characterEntityParser.encodeEntities(text));
          if (text.trim().length() > 0)
          {
            result = true;
          }
          clearText();
        }
      }
    }
    catch (IOException ioe)
    {
      throw new InvalidReportStateException("Failed to write text", ioe);
    }

  }
}
