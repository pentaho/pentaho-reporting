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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfTextArray;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.LocalImageContainer;
import org.pentaho.reporting.engine.classic.core.URLImageContainer;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMapEntry;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.layout.text.Glyph;
import org.pentaho.reporting.engine.classic.core.layout.text.GlyphList;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.LogicalPageDrawable;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.itext.BaseFontFontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontNativeContext;
import org.pentaho.reporting.libraries.fonts.text.Spacing;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;
import org.pentaho.reporting.libraries.xmlns.LibXmlInfo;

/**
 * Creation-Date: 17.07.2007, 18:41:46
 *
 * @author Thomas Morgner
 */
public class PdfLogicalPageDrawable extends LogicalPageDrawable
{
  private static final Log logger = LogFactory.getLog(PdfLogicalPageDrawable.class);

  protected static class PdfTextSpec extends TextSpec
  {
    private BaseFontFontMetrics fontMetrics;
    private PdfContentByte contentByte;

    protected PdfTextSpec(final StyleSheet layoutContext,
                          final PdfOutputProcessorMetaData metaData,
                          final PdfGraphics2D g2,
                          final BaseFontFontMetrics fontMetrics,
                          final PdfContentByte cb)
    {
      super(layoutContext, metaData, g2);
      if (fontMetrics == null)
      {
        throw new NullPointerException();
      }
      if (cb == null)
      {
        throw new NullPointerException();
      }
      this.fontMetrics = fontMetrics;
      this.contentByte = cb;
    }

    public BaseFontFontMetrics getFontMetrics()
    {
      return fontMetrics;
    }

    public PdfContentByte getContentByte()
    {
      return contentByte;
    }

    public void close()
    {
      contentByte.endText();
      //super.close(); // we do not dispose the graphics as we are working on the original object
    }
  }

  private static final float ITALIC_ANGLE = 0.21256f;

  private PdfWriter writer;
  private float globalHeight;
  private boolean globalEmbed;
  private LFUMap<ResourceKey,com.lowagie.text.Image> imageCache;
  private char version;

  public PdfLogicalPageDrawable(final PdfWriter writer,
                                final LFUMap<ResourceKey,com.lowagie.text.Image> imageCache,
                                final char version)
  {
    if (writer == null)
    {
      throw new NullPointerException();
    }
    if (imageCache == null)
    {
      throw new NullPointerException();
    }
    this.writer = writer;
    this.imageCache = imageCache;
    this.version = version;
  }

  public void init(final LogicalPageBox rootBox,
                   final OutputProcessorMetaData metaData,
                   final ResourceManager resourceManager)
  {
    throw new UnsupportedOperationException();
  }

  public void init(final LogicalPageBox rootBox,
                   final PdfOutputProcessorMetaData metaData,
                   final ResourceManager resourceManager,
                   final PhysicalPageBox page)
  {
    super.init(rootBox, metaData, resourceManager);

    if (page != null)
    {
      this.globalHeight = (float)
          StrictGeomUtility.toExternalValue(page.getHeight() - page.getImageableY() + page.getGlobalY());
    }
    else
    {
      this.globalHeight = rootBox.getPageHeight();
    }
    this.globalEmbed = getMetaData().isFeatureSupported(OutputProcessorFeature.EMBED_ALL_FONTS);
  }

  public PdfOutputProcessorMetaData getMetaData()
  {
    return (PdfOutputProcessorMetaData) super.getMetaData();
  }

  /**
   * Draws the object.
   *
   * @param graphics the graphics device.
   * @param area     the area inside which the object should be drawn.
   */
  public void draw(final Graphics2D graphics, final Rectangle2D area)
  {
    super.draw(graphics, area);
  }

  protected void processLinksAndAnchors(final RenderNode box)
  {
    final StyleSheet styleSheet = box.getStyleSheet();
    if (drawPdfScript(box) == false)
    {
      final String target = (String) styleSheet.getStyleProperty(ElementStyleKeys.HREF_TARGET);
      final String title = (String) styleSheet.getStyleProperty(ElementStyleKeys.HREF_TITLE);
      if (target != null || title != null)
      {
        final String window = (String) styleSheet.getStyleProperty(ElementStyleKeys.HREF_WINDOW);
        drawHyperlink(box, target, window, title);
      }
    }

    final String anchor = (String) styleSheet.getStyleProperty(ElementStyleKeys.ANCHOR_NAME);
    if (anchor != null)
    {
      drawAnchor(box);
    }

    final String bookmark = (String) styleSheet.getStyleProperty(BandStyleKeys.BOOKMARK);
    if (bookmark != null)
    {
      drawBookmark(box, bookmark);
    }
  }

  protected boolean drawPdfScript(final RenderNode box)
  {
    final Object attribute = box.getAttributes().getAttribute
        (AttributeNames.Pdf.NAMESPACE, AttributeNames.Pdf.SCRIPT_ACTION);
    if (attribute == null)
    {
      return false;
    }
    
    final String attributeText = String.valueOf(attribute);
    final PdfAction action = PdfAction.javaScript(attributeText, writer, false);

    final AffineTransform affineTransform = getGraphics().getTransform();
    final float translateX = (float) affineTransform.getTranslateX();

    final float leftX = translateX + (float) (StrictGeomUtility.toExternalValue(box.getX()));
    final float rightX = translateX + (float) (StrictGeomUtility.toExternalValue(box.getX() + box.getWidth()));
    final float lowerY = (float) (globalHeight - StrictGeomUtility.toExternalValue(box.getY() + box.getHeight()));
    final float upperY = (float) (globalHeight - StrictGeomUtility.toExternalValue(box.getY()));
    final PdfAnnotation annotation = new PdfAnnotation(writer, leftX, lowerY, rightX, upperY, action);
    writer.addAnnotation(annotation);
    return true;
  }
  
  protected void drawAnchor(final RenderNode content)
  {
    if (content.isNodeVisible(getDrawArea()) == false)
    {
      return;
    }
    final String anchorName = (String) content.getStyleSheet().getStyleProperty(ElementStyleKeys.ANCHOR_NAME);
    if (anchorName == null)
    {
      return;
    }
    final AffineTransform affineTransform = getGraphics().getTransform();
    final float translateX = (float) affineTransform.getTranslateX();

    final float upperY = translateX + (float) (globalHeight - StrictGeomUtility.toExternalValue(content.getY()));
    final float leftX = (float) (StrictGeomUtility.toExternalValue(content.getX()));
    final PdfDestination dest = new PdfDestination(PdfDestination.FIT, leftX, upperY, 0);
    writer.getDirectContent().localDestination(anchorName, dest);
  }

  protected void drawBookmark(final RenderNode box, final String bookmark)
  {
    if (box.isNodeVisible(getDrawArea()) == false)
    {
      return;
    }
    final PdfOutline root = writer.getDirectContent().getRootOutline();

    final AffineTransform affineTransform = getGraphics().getTransform();
    final float translateX = (float) affineTransform.getTranslateX();

    final float upperY = translateX + (float) (globalHeight - StrictGeomUtility.toExternalValue(box.getY()));
    final float leftX = (float) (StrictGeomUtility.toExternalValue(box.getX()));
    final PdfDestination dest = new PdfDestination(PdfDestination.FIT, leftX, upperY, 0);
    new PdfOutline(root, dest, bookmark);
    // destination will always point to the 'current' page
    // todo: Make this a hierarchy ..
  }

  protected void drawHyperlink(final RenderNode box, final String target, final String window, final String title)
  {
    if (box.isNodeVisible(getDrawArea()) == false)
    {
      return;
    }

    final PdfAction action = createActionForLink(target);

    final AffineTransform affineTransform = getGraphics().getTransform();
    final float translateX = (float) affineTransform.getTranslateX();

    final float leftX = translateX + (float) (StrictGeomUtility.toExternalValue(box.getX()));
    final float rightX = translateX + (float) (StrictGeomUtility.toExternalValue(box.getX() + box.getWidth()));
    final float lowerY = (float) (globalHeight - StrictGeomUtility.toExternalValue(box.getY() + box.getHeight()));
    final float upperY = (float) (globalHeight - StrictGeomUtility.toExternalValue(box.getY()));
    
    if (action != null)
    {
      final PdfAnnotation annotation = new PdfAnnotation(writer, leftX, lowerY, rightX, upperY, action);
      writer.addAnnotation(annotation);
    }
    else if (StringUtils.isEmpty(title) == false)
    {
      final Rectangle rect = new Rectangle(leftX, lowerY, rightX, upperY);
      final PdfAnnotation commentAnnotation = PdfAnnotation.createText(writer, rect, "Tooltip", title, false, null);
      commentAnnotation.setAppearance(PdfAnnotation.APPEARANCE_NORMAL,
          writer.getDirectContent().createAppearance(rect.getWidth(), rect.getHeight()));
      writer.addAnnotation(commentAnnotation);
    }
  }

  private PdfAction createActionForLink(final String target)
  {
    if (StringUtils.isEmpty(target))
    {
      return null;
    }
    final PdfAction action = new PdfAction();
    if (target.startsWith("#"))
    {
      // its a local link ..
      action.put(PdfName.S, PdfName.GOTO);
      action.put(PdfName.D, new PdfString(target.substring(1)));
    }
    else
    {
      action.put(PdfName.S, PdfName.URI);
      action.put(PdfName.URI, new PdfString(target));
    }
    return action;
  }

  protected void drawText(final RenderableText renderableText, final long contentX2)
  {
    if (renderableText.getLength() == 0)
    {
      return;
    }

    final long posX = renderableText.getX();
    final long posY = renderableText.getY();
    final float x1 = (float) (StrictGeomUtility.toExternalValue(posX));

    final PdfContentByte cb;
    PdfTextSpec textSpec = (PdfTextSpec) getTextSpec();
    if (textSpec == null)
    {
      final StyleSheet layoutContext = renderableText.getStyleSheet();

      // The code below may be weird, but at least it is predictable weird.
      final String fontName = getMetaData().getNormalizedFontFamilyName
          ((String) layoutContext.getStyleProperty(TextStyleKeys.FONT));
      final String encoding = (String) layoutContext.getStyleProperty(TextStyleKeys.FONTENCODING);
      final float fontSize = (float) layoutContext.getDoubleStyleProperty(TextStyleKeys.FONTSIZE, 10);

      final boolean embed = globalEmbed || layoutContext.getBooleanStyleProperty(TextStyleKeys.EMBEDDED_FONT);
      final boolean bold = layoutContext.getBooleanStyleProperty(TextStyleKeys.BOLD);
      final boolean italics = layoutContext.getBooleanStyleProperty(TextStyleKeys.ITALIC);

      final BaseFontFontMetrics fontMetrics = getMetaData().getBaseFontFontMetrics
          (fontName, fontSize, bold, italics, encoding, embed, false);

      final PdfGraphics2D g2 = (PdfGraphics2D) getGraphics();
      final Color cssColor = (Color) layoutContext.getStyleProperty(ElementStyleKeys.PAINT);
      g2.setPaint(cssColor);
      g2.setFillPaint();
      g2.setStrokePaint();
      //final float translateY = (float) affineTransform.getTranslateY();

      cb = g2.getRawContentByte();

      textSpec = new PdfTextSpec(layoutContext, getMetaData(), g2, fontMetrics, cb);
      setTextSpec(textSpec);

      cb.beginText();
      cb.setFontAndSize(fontMetrics.getBaseFont(), fontSize);
    }
    else
    {
      cb = textSpec.getContentByte();
    }

    final BaseFontFontMetrics baseFontRecord = textSpec.getFontMetrics();
    final BaseFont baseFont = baseFontRecord.getBaseFont();
    final float ascent = baseFont.getFontDescriptor(BaseFont.BBOXURY, textSpec.getFontSize());
    final float y2 = (float) (StrictGeomUtility.toExternalValue(posY) + ascent);
    final float y = globalHeight - y2;

    final AffineTransform affineTransform = textSpec.getGraphics().getTransform();
    final float translateX = (float) affineTransform.getTranslateX();

    final FontNativeContext nativeContext = baseFontRecord.getNativeContext();
    if (baseFontRecord.isTrueTypeFont() && textSpec.isBold() && nativeContext.isNativeBold() == false)
    {
      final float strokeWidth = textSpec.getFontSize() / 30.0f; // right from iText ...
      if (strokeWidth == 1)
      {
        cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
      }
      else
      {
        cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE);
        cb.setLineWidth(strokeWidth);
      }
    }
    else
    {
      cb.setTextRenderingMode(PdfContentByte.TEXT_RENDER_MODE_FILL);
    }

    // if the font does not declare to be italics already, emulate it ..
    if (baseFontRecord.isTrueTypeFont() && textSpec.isItalics() && nativeContext.isNativeItalics() == false)
    {
      final float italicAngle =
          baseFont.getFontDescriptor(BaseFont.ITALICANGLE, textSpec.getFontSize());
      if (italicAngle == 0)
      {
        // italics requested, but the font itself does not supply italics gylphs.
        cb.setTextMatrix(1, 0, PdfLogicalPageDrawable.ITALIC_ANGLE, 1, x1 + translateX, y);
      }
      else
      {
        cb.setTextMatrix(x1 + translateX, y);
      }
    }
    else
    {
      cb.setTextMatrix(x1 + translateX, y);
    }

    final OutputProcessorMetaData metaData = getMetaData();
    final GlyphList gs = renderableText.getGlyphs();
    final int offset = renderableText.getOffset();

    final CodePointBuffer codePointBuffer = getCodePointBuffer();
    if (metaData.isFeatureSupported(OutputProcessorFeature.FAST_FONTRENDERING) &&
        isNormalTextSpacing(renderableText))
    {
      final int maxLength = renderableText.computeMaximumTextSize(contentX2);
      final String text = gs.getText(renderableText.getOffset(), maxLength, codePointBuffer);

      cb.showText(text);
    }
    else
    {
      final PdfTextArray textArray = new PdfTextArray();
      final StringBuilder buffer = new StringBuilder(gs.getSize());
      final int maxPos = offset + renderableText.computeMaximumTextSize(contentX2);

      for (int i = offset; i < maxPos; i++)
      {
        final Glyph g = gs.getGlyph(i);
        final Spacing spacing = g.getSpacing();
        if (i != offset)
        {
          final float optimum = (float) StrictGeomUtility.toFontMetricsValue(spacing.getMinimum());
          if (optimum != 0)
          {
            textArray.add(buffer.toString());
            textArray.add(-optimum / textSpec.getFontSize());
            buffer.setLength(0);
          }
        }

        final String text = gs.getGlyphAsString(i, codePointBuffer);
        buffer.append(text);
      }
      if (buffer.length() > 0)
      {
        textArray.add(buffer.toString());
      }
      cb.showText(textArray);
    }
  }
/*
  protected void drawComplexText(final RenderableComplexText renderableComplexText, final Graphics2D g2)
  {
    RichTextSpec text = renderableComplexText.getRichText();
    Phrase p = new Phrase();
    for (RichTextSpec.StyledChunk c: text.getStyleChunks())
    {
      // add chunks
      BaseFont baseFont = null;// from framework
      Font font = new Font(baseFont, size, style, color);
      Chunk chunk = new Chunk(c.getText(), font);
      p.add(chunk);
    }

    // TODO
    super.drawComplexText(renderableComplexText, g2);
  }
*/
  protected boolean startInlineBox(final InlineRenderBox box)
  {
    if (box.getStaticBoxLayoutProperties().isVisible() == false)
    {
      return false;
    }

    if (box.isBoxVisible(getDrawArea()) == false)
    {
      return false;
    }

    return super.startInlineBox(box);
  }

  protected void finishInlineBox(final InlineRenderBox box)
  {
    if (box.getStaticBoxLayoutProperties().isVisible() == false)
    {
      return;
    }

    if (box.isBoxVisible(getDrawArea()) == false)
    {
      return;
    }

    super.finishInlineBox(box);
  }

  protected void drawReplacedContent(final RenderableReplacedContentBox content)
  {
    final Graphics2D g2 = getGraphics();
    final Object o = content.getContent().getRawObject();
    if (o instanceof DrawableWrapper)
    {
      final DrawableWrapper drawableWrapper = (DrawableWrapper) o;
      if (drawDrawable(content, g2, drawableWrapper))
      {
        drawImageMap(content);
      }
      return;
    }
    if (o instanceof Image)
    {
      if (drawImage(content, (Image) o))
      {
        drawImageMap(content);
      }
      return;
    }

    if (o instanceof URLImageContainer)
    {
      final URLImageContainer imageContainer = (URLImageContainer) o;
      if (imageContainer.isLoadable() == false)
      {
        PdfLogicalPageDrawable.logger.info
            ("URL-image cannot be rendered, as it was declared to be not loadable: " +
                imageContainer.getSourceURLString());
      }
      else
      {
        final ResourceKey resource = imageContainer.getResourceKey();
        if (resource == null)
        {
          PdfLogicalPageDrawable.logger.info("URL-image cannot be rendered, as it did not return a valid URL.");
        }
        else
        {
          try
          {
            final ResourceManager resourceManager = getResourceManager();
            final com.lowagie.text.Image instance;
            final com.lowagie.text.Image maybeImage = imageCache.get(resource);
            if (maybeImage != null)
            {
              instance = maybeImage;
            }
            else
            {
              final ResourceData data = resourceManager.load(resource);
              instance = com.lowagie.text.Image.getInstance(data.getResource(resourceManager));
              imageCache.put(resource, instance);
            }

            final Resource imageWrapped = resourceManager.create(resource, null, Image.class);
            final Image image = (Image) imageWrapped.getResource();

            if (drawImage(content, image, instance))
            {
              drawImageMap(content);
            }
            return;
          }
          catch (InvalidReportStateException re)
          {
            throw re;
          }
          catch (Exception e)
          {
            PdfLogicalPageDrawable.logger.info("URL-image cannot be rendered, as the image was not loadable.", e);
          }
        }
      }
    }

    if (o instanceof LocalImageContainer)
    {
      final LocalImageContainer imageContainer = (LocalImageContainer) o;
      final Image image = imageContainer.getImage();
      if (drawImage(content, image))
      {
        drawImageMap(content);
      }
    }
    else
    {
      PdfLogicalPageDrawable.logger.debug("Unable to handle " + o);
    }
  }

  protected void drawImageMap(final RenderableReplacedContentBox content)
  {
    if (version < '6')
    {
      return;
    }

    final ImageMap imageMap = RenderUtility.extractImageMap(content);
    // only generate a image map, if the user does not specify their own onw via the override.
    // Of course, they would have to provide the map by other means as well.

    if (imageMap == null)
    {
      return;
    }

    final ImageMapEntry[] imageMapEntries = imageMap.getMapEntries();
    for (int i = 0; i < imageMapEntries.length; i++)
    {
      final ImageMapEntry imageMapEntry = imageMapEntries[i];
      final String link = imageMapEntry.getAttribute(LibXmlInfo.XHTML_NAMESPACE, "href");
      final String tooltip = imageMapEntry.getAttribute(LibXmlInfo.XHTML_NAMESPACE, "title");
      if (StringUtils.isEmpty(tooltip))
      {
        continue;
      }

      final AffineTransform affineTransform = getGraphics().getTransform();
      final float translateX = (float) affineTransform.getTranslateX();
      final int x = (int) (translateX + StrictGeomUtility.toExternalValue(content.getX()));
      final int y = (int) StrictGeomUtility.toExternalValue(content.getY());
      final float[] translatedCoords = translateCoordinates(imageMapEntry.getAreaCoordinates(), x, y);

      final PolygonAnnotation polygonAnnotation = new PolygonAnnotation(writer, translatedCoords);
      polygonAnnotation.put(PdfName.CONTENTS, new PdfString(tooltip, PdfObject.TEXT_UNICODE));
      writer.addAnnotation(polygonAnnotation);
    }
  }


  private float[] translateCoordinates(final float[] coords, final float dx, final float dy)
  {
    final float[] floats = coords.clone();
    if (floats.length % 2 != 0)
    {
      throw new IllegalArgumentException("Corrdinates are not x/y pairs");
    }
    for (int i = 0; i < floats.length; i += 2)
    {
      floats[i] += dx;
      floats[i + 1] = globalHeight - floats[i + 1] + dy;
    }
    return floats;
  }

  protected boolean drawImage(final RenderableReplacedContentBox content,
                              final Image image,
                              final com.lowagie.text.Image itextImage)
  {
    final StyleSheet layoutContext = content.getStyleSheet();
    final boolean shouldScale = layoutContext.getBooleanStyleProperty(ElementStyleKeys.SCALE);

    final int x = (int) StrictGeomUtility.toExternalValue(content.getX());
    final int y = (int) StrictGeomUtility.toExternalValue(content.getY());
    final int width = (int) StrictGeomUtility.toExternalValue(content.getWidth());
    final int height = (int) StrictGeomUtility.toExternalValue(content.getHeight());

    if (width == 0 || height == 0)
    {
      PdfLogicalPageDrawable.logger.debug("Error: Image area is empty: " + content);
      return false;
    }

    final WaitingImageObserver obs = new WaitingImageObserver(image);
    obs.waitImageLoaded();
    final int imageWidth = image.getWidth(obs);
    final int imageHeight = image.getHeight(obs);
    if (imageWidth < 1 || imageHeight < 1)
    {
      return false;
    }

    final Rectangle2D.Double drawAreaBounds = new Rectangle2D.Double(x, y, width, height);
    final AffineTransform scaleTransform;

    final Graphics2D g2;
    if (shouldScale == false)
    {
      double deviceScaleFactor = 1;
      final double devResolution = getMetaData().getNumericFeatureValue(OutputProcessorFeature.DEVICE_RESOLUTION);
      if (getMetaData().isFeatureSupported(OutputProcessorFeature.IMAGE_RESOLUTION_MAPPING))
      {
        if (devResolution != 72.0 && devResolution > 0)
        {
          // Need to scale the device to its native resolution before attempting to draw the image..
          deviceScaleFactor = (72.0 / devResolution);
        }
      }

      final int clipWidth = Math.min(width, (int) Math.ceil(deviceScaleFactor * imageWidth));
      final int clipHeight = Math.min(height, (int) Math.ceil(deviceScaleFactor * imageHeight));
      final ElementAlignment horizontalAlignment =
          (ElementAlignment) layoutContext.getStyleProperty(ElementStyleKeys.ALIGNMENT);
      final ElementAlignment verticalAlignment =
          (ElementAlignment) layoutContext.getStyleProperty(ElementStyleKeys.VALIGNMENT);
      final int alignmentX = (int) RenderUtility.computeHorizontalAlignment(horizontalAlignment, width, clipWidth);
      final int alignmentY = (int) RenderUtility.computeVerticalAlignment(verticalAlignment, height, clipHeight);

      g2 = (Graphics2D) getGraphics().create();
      g2.clip(drawAreaBounds);
      g2.translate(x, y);
      g2.translate(alignmentX, alignmentY);
      g2.clip(new Rectangle2D.Float(0, 0, clipWidth, clipHeight));
      g2.scale(deviceScaleFactor, deviceScaleFactor);

      scaleTransform = null;
    }
    else
    {
      g2 = (Graphics2D) getGraphics().create();
      g2.clip(drawAreaBounds);
      g2.translate(x, y);
      g2.clip(new Rectangle2D.Float(0, 0, width, height));

      final double scaleX;
      final double scaleY;

      final boolean keepAspectRatio = layoutContext.getBooleanStyleProperty(ElementStyleKeys.KEEP_ASPECT_RATIO);
      if (keepAspectRatio)
      {
        final double scaleFactor = Math.min(width / (double) imageWidth, height / (double) imageHeight);
        scaleX = scaleFactor;
        scaleY = scaleFactor;
      }
      else
      {
        scaleX = width / (double) imageWidth;
        scaleY = height / (double) imageHeight;
      }

      final int clipWidth = (int) (scaleX * imageWidth);
      final int clipHeight = (int) (scaleY * imageHeight);

      final ElementAlignment horizontalAlignment =
          (ElementAlignment) layoutContext.getStyleProperty(ElementStyleKeys.ALIGNMENT);
      final ElementAlignment verticalAlignment =
          (ElementAlignment) layoutContext.getStyleProperty(ElementStyleKeys.VALIGNMENT);
      final int alignmentX = (int) RenderUtility.computeHorizontalAlignment(horizontalAlignment, width, clipWidth);
      final int alignmentY = (int) RenderUtility.computeVerticalAlignment(verticalAlignment, height, clipHeight);

      g2.translate(alignmentX, alignmentY);
      scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
    }

    final PdfGraphics2D pdfGraphics2D = (PdfGraphics2D) g2;
    pdfGraphics2D.drawPdfImage(itextImage, image, scaleTransform, null);
    g2.dispose();
    return true;
  }

}
