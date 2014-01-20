package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import java.io.IOException;
import java.text.NumberFormat;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.URLImageContainer;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.imagemap.parser.ImageMapWriter;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlContentGenerator;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

public class HtmlTextExtractorHelper
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

  private HtmlTextExtractorState processStack;
  private InstanceID firstElement;
  private XmlWriter xmlWriter;
  private HtmlTagHelper tagHelper;
  private OutputProcessorMetaData metaData;
  private boolean enableInheritedLinkStyle;
  private HtmlContentGenerator contentGenerator;

  public HtmlTextExtractorHelper(final HtmlTagHelper tagHelper,
                                 final XmlWriter xmlWriter,
                                 final OutputProcessorMetaData metaData,
                                 final HtmlContentGenerator contentGenerator)
  {
    this.tagHelper = tagHelper;
    this.metaData = metaData;
    this.contentGenerator = contentGenerator;
    this.enableInheritedLinkStyle = ("true".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.html.LinksInheritStyle")));
    this.xmlWriter = xmlWriter;
  }

  public boolean isEnableInheritedLinkStyle()
  {
    return enableInheritedLinkStyle;
  }

  public void setFirstElement(final InstanceID firstElement)
  {
    this.firstElement = firstElement;
  }


  public boolean startBox(final InstanceID box,
                          final ReportAttributeMap attrs,
                          final StyleSheet styleSheet,
                          final BoxDefinition boxDefinition,
                          final boolean forceTag)
  {
    return startBox(box, attrs, styleSheet, boxDefinition, forceTag, DIV_TAG);
  }

  public boolean startInlineBox(final InstanceID box,
                                final ReportAttributeMap attrs,
                                final StyleSheet styleSheet,
                                final BoxDefinition boxDefinition)
  {
    return startBox(box, attrs, styleSheet, boxDefinition, true, SPAN_TAG);
  }

  private boolean startBox(final InstanceID box,
                           final ReportAttributeMap attrs,
                           final StyleSheet styleSheet,
                           final BoxDefinition boxDefinition,
                           final boolean forceTag,
                           final String tag)
  {
    try
    {
      if (firstElement != box)
      {
        final AttributeList attrList = new AttributeList();
        HtmlTagHelper.applyHtmlAttributes(attrs, attrList);
        final StyleBuilder styleBuilder = tagHelper.getStyleBuilder();
        final StyleBuilder style = tagHelper.getStyleBuilderFactory().produceTextStyle
            (styleBuilder, styleSheet, boxDefinition, true, processStack.getStyle());
        tagHelper.getStyleManager().updateStyle(style, attrList);

        if (forceTag || attrList.isEmpty() == false)
        {
          xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, DIV_TAG, attrList, XmlWriterSupport.OPEN);
          processStack = new HtmlTextExtractorState(processStack, true);
        }
        else
        {
          processStack = new HtmlTextExtractorState(processStack, true);
        }

        writeLocalAnchor(styleSheet);

        final Object rawContent = attrs.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.EXTRA_RAW_CONTENT);
        if (rawContent != null)
        {
          xmlWriter.writeText(String.valueOf(rawContent));
        }

      }
      else
      {
        processStack = new HtmlTextExtractorState(processStack, false);
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

  public void finishBox(final InstanceID box,
                        final ReportAttributeMap<Object> attributes)
  {
    try
    {
      if (processStack.isWrittenTag())
      {
        xmlWriter.writeCloseTag();
      }
      processStack = processStack.getParent();

      if (firstElement != box)
      {
        final Object rawFooterContent = attributes.getAttribute(AttributeNames.Html.NAMESPACE,
            AttributeNames.Html.EXTRA_RAW_FOOTER_CONTENT);
        if (rawFooterContent != null)
        {
          xmlWriter.writeText(String.valueOf(rawFooterContent));
        }
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

  public void writeLocalAnchor(final StyleSheet styleSheet) throws IOException
  {
    final String anchor = (String) styleSheet.getStyleProperty(ElementStyleKeys.ANCHOR_NAME);
    if (anchor != null)
    {
      xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, A_TAG, "name", anchor, XmlWriterSupport.CLOSE);
    }
  }

  public void handleLinkOnElement(final StyleSheet styleSheet, final String target) throws IOException
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
      StyleBuilder styleBuilder = createLinkStyle(tagHelper.getStyleBuilder());
      tagHelper.getStyleManager().updateStyle(styleBuilder, linkAttr);
    }
    xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, A_TAG, linkAttr, XmlWriterSupport.OPEN);
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

  private StyleBuilder createLinkStyle(StyleBuilder b)
  {
    if (b == null)
    {
      b = new DefaultStyleBuilder(tagHelper.getStyleBuilderFactory());
    }

    b.append(StyleBuilder.CSSKeys.FONT_STYLE, "inherit");
    b.append(StyleBuilder.CSSKeys.FONT_FAMILY, "inherit");
    b.append(StyleBuilder.CSSKeys.FONT_WEIGHT, "inherit");
    b.append(StyleBuilder.CSSKeys.FONT_SIZE, "inherit");
    b.append(StyleBuilder.CSSKeys.TEXT_DECORATION, "inherit");
    b.append(StyleBuilder.CSSKeys.COLOR, "inherit");
    return b;
  }


  public StyleBuilder produceClipStyle(final long nodeWidth, long nodeHeight)
  {
    StyleBuilder styleBuilder = tagHelper.getStyleBuilder();
    styleBuilder.clear(); // cuts down on object creation

    DefaultStyleBuilderFactory styleBuilderFactory = tagHelper.getStyleBuilderFactory();
    final NumberFormat pointConverter = styleBuilder.getPointConverter();
    styleBuilder.append(DefaultStyleBuilder.CSSKeys.OVERFLOW, "hidden"); //NON-NLS
    styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, pointConverter.format//NON-NLS
        (styleBuilderFactory.fixLengthForSafari(StrictGeomUtility.toExternalValue(nodeWidth))), PT_UNIT);
    styleBuilder.append(DefaultStyleBuilder.CSSKeys.HEIGHT, pointConverter.format//NON-NLS
        (styleBuilderFactory.fixLengthForSafari(StrictGeomUtility.toExternalValue(nodeHeight))), PT_UNIT);
    return styleBuilder;
  }

  /**
   * Populates the style builder with the style information for the image based on the RenderableReplacedContent
   *
   * @return the style-builder with the image style or null, if the image must be clipped.
   */
  public StyleBuilder produceImageStyle(final StyleSheet styleSheet,
                                         final long nodeWidth, long nodeHeight,
                                         final long contentWidth, final long contentHeight)
  {
    StyleBuilder styleBuilder = tagHelper.getStyleBuilder();
    styleBuilder.clear(); // cuts down on object creation
    DefaultStyleBuilderFactory styleBuilderFactory = tagHelper.getStyleBuilderFactory();
    final NumberFormat pointConverter = styleBuilder.getPointConverter();
    final double scale = RenderUtility.getNormalizationScale(metaData);

    if (styleSheet.getBooleanStyleProperty(ElementStyleKeys.SCALE))
    {
      if (styleSheet.getBooleanStyleProperty(ElementStyleKeys.KEEP_ASPECT_RATIO) &&
          (contentWidth > 0 && contentHeight > 0))
      {
        final double scaleFactor = Math.min(nodeWidth / (double) contentWidth, nodeHeight / (double) contentHeight);

        styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, pointConverter.format
            (styleBuilderFactory.fixLengthForSafari
                (StrictGeomUtility.toExternalValue((long) (contentWidth * scaleFactor * scale)))), PX_UNIT);
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.HEIGHT, pointConverter.format
            (styleBuilderFactory.fixLengthForSafari
                (StrictGeomUtility.toExternalValue((long) (contentHeight * scaleFactor * scale)))), PX_UNIT);
      }
      else
      {
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, pointConverter.format
            (styleBuilderFactory.fixLengthForSafari
                (StrictGeomUtility.toExternalValue((long) (nodeWidth * scale)))), PX_UNIT);
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.HEIGHT, pointConverter.format
            (styleBuilderFactory.fixLengthForSafari
                (StrictGeomUtility.toExternalValue((long) (nodeHeight * scale)))), PX_UNIT);
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
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, pointConverter.format(styleBuilderFactory.fixLengthForSafari
            (StrictGeomUtility.toExternalValue((long) (nodeWidth * scale)))), PX_UNIT);
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.HEIGHT, pointConverter.format(styleBuilderFactory.fixLengthForSafari
            (StrictGeomUtility.toExternalValue((long) (nodeHeight * scale)))), PX_UNIT);
      }
      else
      {
        final long width = Math.min(nodeWidth, contentWidth);
        final long height = Math.min(nodeHeight, contentHeight);
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, pointConverter.format(styleBuilderFactory.fixLengthForSafari
            (StrictGeomUtility.toExternalValue((long) (width * scale)))), PX_UNIT);
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.HEIGHT, pointConverter.format(styleBuilderFactory.fixLengthForSafari
            (StrictGeomUtility.toExternalValue((long) (height * scale)))), PX_UNIT);
      }
    }
    return styleBuilder;
  }


  public boolean processRenderableReplacedContent(final ReportAttributeMap attrs,
                                                   final StyleSheet styleSheet,
                                                   final long width,
                                                   final long height,
                                                   final long contentWidth,
                                                   final long contentHeight,
                                                   final Object rawObject) throws ContentIOException, IOException
  {
    // Fallback: (At the moment, we only support drawables and images.)
    if (rawObject instanceof ImageContainer)
    {
      if (rawObject instanceof URLImageContainer)
      {
        if (tryHandleUrlImage(styleSheet, width, height, contentWidth, contentHeight, (URLImageContainer) rawObject))
        {
          return true;
        }
      }

      if (tryHandleLocalImageContainer
          (styleSheet, attrs, width, height, contentWidth, contentHeight, (ImageContainer) rawObject))
      {
        return true;
      }
      return false;
    }

    if (rawObject instanceof DrawableWrapper)
    {
      return tryHandleDrawable(attrs, width, height, contentWidth, contentHeight, styleSheet, (DrawableWrapper) rawObject);
    }
    return false;
  }

  private boolean tryHandleDrawable(final ReportAttributeMap attrs,
                                 final long width,
                                 final long height,
                                 final long contentWidth,
                                 final long contentHeight,
                                 final StyleSheet styleSheet,
                                 final DrawableWrapper drawable) throws ContentIOException, IOException
  {
    // render it into an Buffered image and make it a PNG file.
    final StrictBounds cb = new StrictBounds(0, 0, width, height);
    final ImageContainer image =
        RenderUtility.createImageFromDrawable(drawable, cb, styleSheet, metaData);
    if (image == null)
    {
      //xmlWriter.writeComment("Drawable content [No image generated]:" + source);
      return false;
    }

    final String type = RenderUtility.getEncoderType(attrs);
    final float quality = RenderUtility.getEncoderQuality(attrs);

    final String name = contentGenerator.writeImage(image, type, quality, true);
    if (name == null)
    {
      //xmlWriter.writeComment("Drawable content [No image written]:" + source);
      return false;
    }

    //xmlWriter.writeComment("Drawable content:" + source);
    // Write image reference ..
    final AttributeList attrList = new AttributeList();
    attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, SRC_ATTR, name);
    attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "border", "0"); //NON-NLS

    final Object titleText = attrs.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.TITLE);
    if (titleText != null)
    {
      attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, TITLE_ATTR, String.valueOf(titleText));
    }

    final Object altText = attrs.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ALT);
    if (altText != null)
    {
      attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, ALT_ATTR, String.valueOf(altText));
    }
    // width and height and scaling and so on ..
    writeImageTag(styleSheet, width, height, contentWidth, contentHeight, attrList);

    final ImageMap imageMap = extractImageMap(attrs, drawable, width, height, name, attrList);
    if (imageMap != null)
    {
      ImageMapWriter.writeImageMap(xmlWriter, imageMap, RenderUtility.getNormalizationScale(metaData));
    }
    return true;
  }

  private ImageMap extractImageMap(final ReportAttributeMap attributes,
                                   final Object rawObject,
                                   final long width, final long height,
                                   final String name, final AttributeList attrList)
  {
    final ImageMap imageMap;
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
      imageMap = RenderUtility.extractImageMap(attributes, rawObject, width, height);

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
    return imageMap;
  }

  private boolean tryHandleLocalImageContainer(final StyleSheet styleSheet,
                                               final ReportAttributeMap attributes,
                                               final long width, final long height,
                                               final long contentWidth, final long contentHeight,
                                               final ImageContainer rawObject)
      throws ContentIOException, IOException
  {
    final String type = RenderUtility.getEncoderType(attributes);
    final float quality = RenderUtility.getEncoderQuality(attributes);

    // Make it a PNG file ..
    //xmlWriter.writeComment("Image content source:" + source);
    final String name = contentGenerator.writeImage(rawObject, type, quality, true);
    if (name == null)
    {
      return false;
    }

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
    writeImageTag(styleSheet, width, height, contentWidth, contentHeight, attrList);

    final ImageMap imageMap = extractImageMap(attributes, null, width, height, name, attrList);
    if (imageMap != null)
    {
      ImageMapWriter.writeImageMap(xmlWriter, imageMap, RenderUtility.getNormalizationScale(metaData));
    }

    return true;
  }

  private boolean tryHandleUrlImage(final StyleSheet styleSheet,
                                    final long width, final long height,
                                    final long contentWidth, final long contentHeight,
                                    final URLImageContainer urlImageContainer) throws ContentIOException, IOException
  {
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
          writeImageTag(styleSheet, width, height, contentWidth, contentHeight, attrList);

          contentGenerator.registerContent(source, name);
          return true;
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
          writeImageTag(styleSheet, width, height, contentWidth, contentHeight, attrList);
          return true;
        }
      }
    }
    return false;
  }

  private void writeImageTag(final StyleSheet styleSheet,
                             final long width, final long height,
                             final long contentWidth, final long contentHeight,
                             AttributeList attrList) throws IOException
  {
    final StyleBuilder imgStyle = produceImageStyle(styleSheet, width, height, contentWidth, contentHeight);
    if (imgStyle == null)
    {
      final AttributeList clipAttrList = new AttributeList();
      final StyleBuilder divStyle = produceClipStyle(width, height);
      tagHelper.getStyleManager().updateStyle(divStyle, clipAttrList);

      xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, DIV_TAG, clipAttrList, XmlWriterSupport.OPEN);
      xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, IMG_TAG, attrList, XmlWriterSupport.CLOSE);
      xmlWriter.writeCloseTag();
    }
    else
    {
      tagHelper.getStyleManager().updateStyle(imgStyle, attrList);
      xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, IMG_TAG, attrList, XmlWriterSupport.CLOSE);
    }
  }


}
