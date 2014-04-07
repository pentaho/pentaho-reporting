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

import java.awt.Color;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineInfo;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.LocalImageContainer;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.URLImageContainer;
import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackgroundProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellMarker;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.DefaultStyleBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.FilterStyleBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.GlobalStyleManager;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.InlineStyleManager;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.StyleBuilder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.StyleManager;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.util.HtmlColors;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.util.HtmlEncoderUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.encoder.UnsupportedEncoderException;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.MemoryStringReader;
import org.pentaho.reporting.libraries.base.util.MemoryStringWriter;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;
import org.pentaho.reporting.libraries.repository.ContentCreationException;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.LibRepositoryBoot;
import org.pentaho.reporting.libraries.repository.NameGenerator;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

/**
 * This class is the actual HTML-emitter.
 *
 * @author Thomas Morgner
 * @noinspection HardCodedStringLiteral
 */
public abstract class HtmlPrinter implements HtmlContentGenerator
{

  private static class ImageData
  {
    private byte[] imageData;
    private String mimeType;
    private String originalFileName;

    private ImageData(final byte[] imageData, final String mimeType, final String originalFileName)
    {
      if (imageData == null)
      {
        throw new NullPointerException();
      }
      if (mimeType == null)
      {
        throw new NullPointerException();
      }
      if (originalFileName == null)
      {
        throw new NullPointerException();
      }

      this.imageData = imageData;
      this.mimeType = mimeType;
      this.originalFileName = originalFileName;
    }

    public byte[] getImageData()
    {
      return imageData;
    }

    public String getMimeType()
    {
      return mimeType;
    }

    public String getOriginalFileName()
    {
      return originalFileName;
    }
  }

  private static class RowBackgroundStruct
  {
    protected Color color;
    protected BorderEdge topEdge;
    protected BorderEdge bottomEdge;
    protected boolean failed;
  }

  private static final Log logger = LogFactory.getLog(HtmlPrinter.class);

  private static final String GENERATOR = ClassicEngineInfo.getInstance().getName() + " version "
      + ClassicEngineInfo.getInstance().getVersion();

  public static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";


  private static final String[] XHTML_HEADER = {
      "<!DOCTYPE html",
      "     PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"",
      "     \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"};

  private Configuration configuration;
  //private OutputProcessorMetaData metaData;

  private XmlWriter xmlWriter;
  private boolean assumeZeroMargins;
  private boolean assumeZeroBorders;
  private boolean assumeZeroPaddings;

  private ContentLocation contentLocation;
  private NameGenerator contentNameGenerator;
  private ContentLocation dataLocation;
  private NameGenerator dataNameGenerator;

  private ResourceManager resourceManager;
  private HashMap<ResourceKey, String> knownResources;
  private HashMap<String, String> knownImages;
  private HashSet<String> validRawTypes;

  private URLRewriter urlRewriter;
  private ContentItem documentContentItem;
  private StyleManager styleManager;
  private boolean allowRawLinkTargets;
  private boolean copyExternalImages;
  private StyleBuilder styleBuilder;
  private static final DefaultStyleBuilder.CSSKeys[] EMPTY_CELL_ATTRNAMES = new DefaultStyleBuilder.CSSKeys[]{DefaultStyleBuilder.CSSKeys.FONT_SIZE};
  private static final String[] EMPTY_CELL_ATTRVALS = new String[]{"1pt"};

  private MemoryStringWriter bufferWriter;
  private BufferedWriter writer;
  private ContentItem styleFile;
  private String styleFileUrl;
  private HtmlTextExtractor textExtractor;
  private CellBackgroundProducer cellBackgroundProducer;
  private boolean safariLengthFix;
  private boolean useWhitespacePreWrap;
  private boolean enableRoundBorderCorner;

  protected HtmlPrinter(final ResourceManager resourceManager)
  {
    if (resourceManager == null)
    {
      throw new NullPointerException("A resource-manager must be given.");
    }

    this.resourceManager = resourceManager;
    this.knownResources = new HashMap<ResourceKey, String>();
    this.knownImages = new HashMap<String, String>();
    this.styleBuilder = new DefaultStyleBuilder();

    this.validRawTypes = new HashSet<String>();
    this.validRawTypes.add("image/gif");
    this.validRawTypes.add("image/x-xbitmap");
    this.validRawTypes.add("image/gi_");
    this.validRawTypes.add("image/jpeg");
    this.validRawTypes.add("image/jpg");
    this.validRawTypes.add("image/jp_");
    this.validRawTypes.add("application/jpg");
    this.validRawTypes.add("application/x-jpg");
    this.validRawTypes.add("image/pjpeg");
    this.validRawTypes.add("image/pipeg");
    this.validRawTypes.add("image/vnd.swiftview-jpeg");
    this.validRawTypes.add("image/x-xbitmap");
    this.validRawTypes.add("image/png");
    this.validRawTypes.add("application/png");
    this.validRawTypes.add("application/x-png");

    assumeZeroMargins = true;
    assumeZeroBorders = true;
    assumeZeroPaddings = true;

    // this primitive implementation assumes that the both repositories are
    // the same ..
    urlRewriter = new FileSystemURLRewriter();

    safariLengthFix = ("true".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.html.SafariLengthHack")));
    useWhitespacePreWrap = ("true".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.html.UseWhitespacePreWrap")));
    enableRoundBorderCorner = ("true".equals(ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.table.html.EnableRoundBorderCorner")));
  }

  protected boolean isAllowRawLinkTargets()
  {
    return allowRawLinkTargets;
  }

  protected Configuration getConfiguration()
  {
    return configuration;
  }

  protected boolean isAssumeZeroMargins()
  {
    return assumeZeroMargins;
  }

  protected void setAssumeZeroMargins(final boolean assumeZeroMargins)
  {
    this.assumeZeroMargins = assumeZeroMargins;
  }

  protected boolean isAssumeZeroBorders()
  {
    return assumeZeroBorders;
  }

  protected void setAssumeZeroBorders(final boolean assumeZeroBorders)
  {
    this.assumeZeroBorders = assumeZeroBorders;
  }

  protected boolean isAssumeZeroPaddings()
  {
    return assumeZeroPaddings;
  }

  protected void setAssumeZeroPaddings(final boolean assumeZeroPaddings)
  {
    this.assumeZeroPaddings = assumeZeroPaddings;
  }

  public ContentLocation getContentLocation()
  {
    return contentLocation;
  }

  public NameGenerator getContentNameGenerator()
  {
    return contentNameGenerator;
  }

  public ContentLocation getDataLocation()
  {
    return dataLocation;
  }

  public NameGenerator getDataNameGenerator()
  {
    return dataNameGenerator;
  }

  public void setDataWriter(final ContentLocation dataLocation,
                            final NameGenerator dataNameGenerator)
  {
    this.dataNameGenerator = dataNameGenerator;
    this.dataLocation = dataLocation;
  }

  public void setContentWriter(final ContentLocation contentLocation,
                               final NameGenerator contentNameGenerator)
  {
    this.contentNameGenerator = contentNameGenerator;
    this.contentLocation = contentLocation;
  }

  public ResourceManager getResourceManager()
  {
    return resourceManager;
  }

  public URLRewriter getUrlRewriter()
  {
    return urlRewriter;
  }

  public void setUrlRewriter(final URLRewriter urlRewriter)
  {
    if (urlRewriter == null)
    {
      throw new NullPointerException();
    }
    this.urlRewriter = urlRewriter;
  }

  public ContentItem getDocumentContentItem()
  {
    return documentContentItem;
  }

  protected void setDocumentContentItem(final ContentItem documentContentItem)
  {
    this.documentContentItem = documentContentItem;
  }

  public String writeRaw(final ResourceKey source) throws IOException
  {
    if (source == null)
    {
      throw new NullPointerException();
    }

    if (copyExternalImages == false)
    {
      final Object identifier = source.getIdentifier();
      if (identifier instanceof URL)
      {
        final URL url = (URL) identifier;
        final String protocol = url.getProtocol();
        if ("http".equalsIgnoreCase(protocol) ||
            "https".equalsIgnoreCase(protocol) ||
            "ftp".equalsIgnoreCase(protocol))
        {
          return url.toExternalForm();
        }
      }
    }

    if (dataLocation == null)
    {
      return null;
    }

    try
    {
      final ResourceData resourceData = resourceManager.load(source);
      final String mimeType = queryMimeType(resourceData);
      if (isValidImage(mimeType))
      {
        // lets do some voodo ..
        final ContentItem item = dataLocation.createItem
            (dataNameGenerator.generateName(extractFilename(resourceData), mimeType));
        if (item.isWriteable())
        {
          item.setAttribute(LibRepositoryBoot.REPOSITORY_DOMAIN, LibRepositoryBoot.CONTENT_TYPE, mimeType);

          // write it out ..
          final InputStream stream = new BufferedInputStream(resourceData.getResourceAsStream(resourceManager));
          try
          {
            final OutputStream outputStream = new BufferedOutputStream(item.getOutputStream());
            try
            {
              IOUtils.getInstance().copyStreams(stream, outputStream);
            }
            finally
            {
              outputStream.close();
            }
          }
          finally
          {
            stream.close();
          }

          return urlRewriter.rewrite(documentContentItem, item);
        }
      }
    }
    catch (ResourceLoadingException e)
    {
      // Ok, loading the resource failed. Not a problem, so we will
      // recode the raw-object instead ..
    }
    catch (ContentIOException e)
    {
      // ignore it ..
    }
    catch (URLRewriteException e)
    {
      HtmlPrinter.logger.warn("Rewriting the URL failed.", e);
      throw new RuntimeException("Failed", e);
    }
    return null;
  }

  /**
   * Tests, whether the given URL points to a supported file format for common browsers. Returns true if the URL
   * references a JPEG, PNG or GIF image, false otherwise.
   * <p/>
   * The checked filetypes are the ones recommended by the W3C.
   *
   * @param key the url that should be tested.
   * @return true, if the content type is supported by the browsers, false otherwise.
   */
  protected boolean isSupportedImageFormat(final ResourceKey key)
  {
    final URL url = resourceManager.toURL(key);
    if (url == null)
    {
      return false;
    }

    final String file = url.getFile();
    if (StringUtils.endsWithIgnoreCase(file, ".jpg"))
    {
      return true;
    }
    if (StringUtils.endsWithIgnoreCase(file, ".jpeg"))
    {
      return true;
    }
    if (StringUtils.endsWithIgnoreCase(file, ".png"))
    {
      return true;
    }
    if (StringUtils.endsWithIgnoreCase(file, ".gif"))
    {
      return true;
    }
    return false;
  }

  private ImageData getImageData(final ImageContainer image,
                                 final String encoderType,
                                 final float quality,
                                 final boolean alpha) throws IOException, UnsupportedEncoderException
  {
    ResourceKey url = null;
    // The image has an assigned URL ...
    if (image instanceof URLImageContainer)
    {
      final URLImageContainer urlImage = (URLImageContainer) image;

      url = urlImage.getResourceKey();
      // if we have an source to load the image data from ..
      if (url != null)
      {
        if (urlImage.isLoadable() && isSupportedImageFormat(url))
        {
          try
          {
            final ResourceData data = resourceManager.load(url);
            final byte[] imageData = data.getResource(resourceManager);
            final String mimeType = queryMimeType(imageData);
            final URL maybeRealURL = resourceManager.toURL(url);
            if (maybeRealURL != null)
            {
              final String originalFileName = IOUtils.getInstance().getFileName(maybeRealURL);
              return new ImageData(imageData, mimeType, originalFileName);
            }
            else
            {
              return new ImageData(imageData, mimeType, "picture");
            }
          }
          catch (ResourceException re)
          {
            // ok, try as local ...
            HtmlPrinter.logger.debug("Failed to process image as raw-data, trying as processed data next", re);
          }
        }
      }
    }

    if (image instanceof LocalImageContainer)
    {
      // Check, whether the imagereference contains an AWT image.
      // if so, then we can use that image instance for the recoding
      final LocalImageContainer li = (LocalImageContainer) image;
      Image awtImage = li.getImage();
      if (awtImage == null)
      {
        if (url != null)
        {
          try
          {
            final Resource resource = resourceManager.createDirectly(url, Image.class);
            awtImage = (Image) resource.getResource();
          }
          catch (ResourceException e)
          {
            // ignore.
          }
        }
      }
      if (awtImage != null)
      {
        // now encode the image. We don't need to create digest data
        // for the image contents, as the image is perfectly identifyable
        // by its URL
        final byte[] imageData = RenderUtility.encodeImage(awtImage, encoderType, quality, alpha);
        final String originalFileName;
        if (url != null)
        {
          final URL maybeRealURL = resourceManager.toURL(url);
          if (maybeRealURL != null)
          {
            originalFileName = IOUtils.getInstance().getFileName(maybeRealURL);
          }
          else
          {
            // we just need the picture part, the file-extension will be replaced by one that matches
            // the mime-type.
            originalFileName = "picture";
          }
        }
        else
        {
          // we just need the picture part, the file-extension will be replaced by one that matches
          // the mime-type.
          originalFileName = "picture";
        }
        return new ImageData(imageData, encoderType, originalFileName);
      }
    }
    return null;
  }

  public String writeImage(final ImageContainer image,
                           final String encoderType,
                           final float quality,
                           final boolean alpha)
      throws ContentIOException, IOException
  {
    if (image == null)
    {
      throw new NullPointerException();
    }

    if (dataLocation == null)
    {
      return null;
    }

    final String cacheKey;
    if (image instanceof URLImageContainer)
    {
      final URLImageContainer uic = (URLImageContainer) image;
      cacheKey = uic.getSourceURLString();
      final String retval = knownImages.get(cacheKey);
      if (retval != null)
      {
        return retval;
      }

      final String sourceURLString = uic.getSourceURLString();
      if (uic.isLoadable() == false && sourceURLString != null)
      {
        knownImages.put(cacheKey, sourceURLString);
        return sourceURLString;
      }
    }
    else
    {
      cacheKey = null;
    }

    try
    {
      final ImageData data = getImageData(image, encoderType, quality, alpha);
      if (data == null)
      {
        return null;
      }
      // write the encoded picture ...
      final String filename = IOUtils.getInstance().stripFileExtension(data.getOriginalFileName());
      final ContentItem dataFile = dataLocation.createItem
          (dataNameGenerator.generateName(filename, data.getMimeType()));
      final String contentURL = urlRewriter.rewrite(documentContentItem, dataFile);

      // a png encoder is included in JCommon ...
      final OutputStream out = new BufferedOutputStream(dataFile.getOutputStream());
      try
      {
        out.write(data.getImageData());
        out.flush();
      }
      finally
      {
        out.close();
      }
      if (cacheKey != null)
      {
        knownImages.put(cacheKey, contentURL);
      }

      return contentURL;
    }
    catch (ContentCreationException cce)
    {
      // Can't create the content
      HtmlPrinter.logger.warn("Failed to create the content image: Reason given was: " + cce.getMessage());
      return null;
    }
    catch (URLRewriteException re)
    {
      // cannot handle this ..
      HtmlPrinter.logger.warn("Failed to write the URL: Reason given was: " + re.getMessage());
      return null;
    }
    catch (UnsupportedEncoderException e)
    {
      HtmlPrinter.logger.warn("Failed to write the URL: Reason given was: " + e.getMessage());
      return null;
    }
  }

  private String extractFilename(final ResourceData resourceData)
  {
    final String filename = (String) resourceData.getAttribute(ResourceData.FILENAME);
    if (filename == null)
    {
      return "image";
    }

    final String pureFileName = IOUtils.getInstance().getFileName(filename);
    return IOUtils.getInstance().stripFileExtension(pureFileName);
  }

  private String queryMimeType(final ResourceData resourceData)
      throws ResourceLoadingException, IOException
  {
    final Object contentType = resourceData.getAttribute(ResourceData.CONTENT_TYPE);
    if (contentType instanceof String)
    {
      return (String) contentType;
    }

    // now we are getting very primitive .. (Kids, dont do this at home)
    final byte[] data = new byte[12];
    resourceData.getResource(resourceManager, data, 0, data.length);
    return queryMimeType(data);
  }

  private String queryMimeType(final byte[] data) throws IOException
  {
    final ByteArrayInputStream stream = new ByteArrayInputStream(data);
    if (isGIF(stream))
    {
      return "image/gif";
    }
    stream.reset();
    if (isJPEG(stream))
    {
      return "image/jpeg";
    }
    stream.reset();
    if (isPNG(stream))
    {
      return "image/png";
    }
    return null;
  }

  private boolean isPNG(final ByteArrayInputStream data)
  {
    final int[] PNF_FINGERPRINT = {137, 80, 78, 71, 13, 10, 26, 10};
    for (int i = 0; i < PNF_FINGERPRINT.length; i++)
    {
      if (PNF_FINGERPRINT[i] != data.read())
      {
        return false;
      }
    }
    return true;
  }

  private boolean isJPEG(final InputStream data) throws IOException
  {
    final int[] JPG_FINGERPRINT_1 = {0xFF, 0xD8, 0xFF, 0xE0};
    for (int i = 0; i < JPG_FINGERPRINT_1.length; i++)
    {
      if (JPG_FINGERPRINT_1[i] != data.read())
      {
        return false;
      }
    }
    // then skip two bytes ..
    if (data.read() == -1)
    {
      return false;
    }
    if (data.read() == -1)
    {
      return false;
    }

    final int[] JPG_FINGERPRINT_2 = {0x4A, 0x46, 0x49, 0x46, 0x00};
    for (int i = 0; i < JPG_FINGERPRINT_2.length; i++)
    {
      if (JPG_FINGERPRINT_2[i] != data.read())
      {
        return false;
      }
    }
    return true;
  }

  private boolean isGIF(final InputStream data) throws IOException
  {
    final int[] GIF_FINGERPRINT = {'G', 'I', 'F', '8'};
    for (int i = 0; i < GIF_FINGERPRINT.length; i++)
    {
      if (GIF_FINGERPRINT[i] != data.read())
      {
        return false;
      }
    }
    return true;
  }

  private boolean isValidImage(final String mimeType)
  {
    return validRawTypes.contains(mimeType);
  }

  private boolean isCreateBodyFragment()
  {
    return "true".equals(getConfiguration().getConfigProperty(HtmlTableModule.BODY_FRAGMENT, "false"));
  }

  private boolean isEmptyCellsUseCSS()
  {
    return "true".equals(getConfiguration().getConfigProperty(HtmlTableModule.EMPTY_CELLS_USE_CSS, "false"));
  }

  private boolean isUseTableLayoutFixed()
  {
    return "true".equals(getConfiguration().getConfigProperty(HtmlTableModule.USE_TABLE_LAYOUT_FIXED, "true"));
  }

  private boolean isTableRowBorderDefinition()
  {
    return "true".equals(getConfiguration().getConfigProperty(HtmlTableModule.TABLE_ROW_BORDER_DEFINITION, "false"));
  }

  private boolean isProportionalColumnWidths()
  {
    return "true".equals(getConfiguration().getConfigProperty(HtmlTableModule.PROPORTIONAL_COLUMN_WIDTHS, "false"));
  }

  private StyleBuilder createCellStyle(final RenderBox content,
                                       final CellBackground background,
                                       final DefaultStyleBuilder.CSSKeys[] extraStyleKeys,
                                       final String[] extraStyleValues)
  {
    if (content == null)
    {
      styleBuilder.clear();
    }
    else
    {
      styleBuilder = HtmlPrinter.produceTextStyle
          (styleBuilder, content, true, safariLengthFix, useWhitespacePreWrap, enableRoundBorderCorner,
              null);
    }

    // Add the extra styles
    if (extraStyleKeys != null
        && extraStyleValues != null
        && extraStyleKeys.length == extraStyleValues.length)
    {
      for (int i = 0; i < extraStyleKeys.length; ++i)
      {
        styleBuilder.append(extraStyleKeys[i], extraStyleValues[i], false);
      }
    }

    if (background != null)
    {
      final Color colorValue = (background.getBackgroundColor());
      if (colorValue != null)
      {
        styleBuilder.append(DefaultStyleBuilder.CSSKeys.BACKGROUND_COLOR, HtmlColors.getColorString(colorValue));
      }


      final BorderEdge topEdge = background.getTop();
      final BorderEdge leftEdge = background.getLeft();
      final BorderEdge bottomEdge = background.getBottom();
      final BorderEdge rightEdge = background.getRight();
      if (topEdge.equals(leftEdge) &&
          topEdge.equals(rightEdge) &&
          topEdge.equals(bottomEdge))
      {
        if (BorderEdge.EMPTY.equals(topEdge) == false)
        {
          styleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.BORDER, styleBuilder.printEdgeAsCSS(topEdge));
        }
      }
      else
      {
        if (BorderEdge.EMPTY.equals(topEdge) == false)
        {
          styleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.BORDER_TOP, styleBuilder.printEdgeAsCSS(topEdge));
        }
        if (BorderEdge.EMPTY.equals(leftEdge) == false)
        {
          styleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.BORDER_LEFT, styleBuilder.printEdgeAsCSS(leftEdge));
        }
        if (BorderEdge.EMPTY.equals(bottomEdge) == false)
        {
          styleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM, styleBuilder.printEdgeAsCSS(bottomEdge));
        }
        if (BorderEdge.EMPTY.equals(rightEdge) == false)
        {
          styleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.BORDER_RIGHT, styleBuilder.printEdgeAsCSS(rightEdge));
        }
      }

      if (enableRoundBorderCorner)
      {
        final BorderCorner topLeft = background.getTopLeft();
        if (BorderCorner.EMPTY.equals(topLeft) == false)
        {
          styleBuilder.append(DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_TOP_LEFT, styleBuilder.printCornerAsCSS(topLeft));
          styleBuilder.append(DefaultStyleBuilder.CSSKeys.BORDER_TOP_LEFT_RADIUS, styleBuilder.printCornerAsCSS(topLeft));
        }

        final BorderCorner topRight = background.getTopRight();
        if (BorderCorner.EMPTY.equals(topRight) == false)
        {
          styleBuilder.append(DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_TOP_RIGHT, styleBuilder.printCornerAsCSS(topRight));
          styleBuilder.append(DefaultStyleBuilder.CSSKeys.BORDER_TOP_RIGHT_RADIUS, styleBuilder.printCornerAsCSS(topRight));
        }

        final BorderCorner bottomLeft = background.getBottomLeft();
        if (BorderCorner.EMPTY.equals(bottomLeft) == false)
        {
          styleBuilder.append(DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_BOTTOM_LEFT, styleBuilder.printCornerAsCSS(bottomLeft));
          styleBuilder.append(DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM_LEFT_RADIUS, styleBuilder.printCornerAsCSS(bottomLeft));
        }

        final BorderCorner bottomRight = background.getBottomRight();
        if (BorderCorner.EMPTY.equals(bottomRight) == false)
        {
          styleBuilder.append(DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_BOTTOM_RIGHT, styleBuilder.printCornerAsCSS(bottomRight));
          styleBuilder.append(DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM_RIGHT_RADIUS, styleBuilder.printCornerAsCSS(bottomRight));
        }
      }
    }
    return styleBuilder;
  }

  private AttributeList createCellAttributes(final int colSpan,
                                             final int rowSpan,
                                             final RenderBox content,
                                             final CellBackground background,
                                             final StyleBuilder styleBuilder)
  {

    final AttributeList attrList = new AttributeList();
    if (content != null)
    {
      // ignore for now ..
      if (rowSpan > 1)
      {
        attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "rowspan", String.valueOf(rowSpan));
      }
      if (colSpan > 1)
      {
        attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "colspan", String.valueOf(colSpan));
      }

      final ElementAlignment verticalAlignment = content.getNodeLayoutProperties().getVerticalAlignment();
      attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "valign", translateVerticalAlignment(verticalAlignment));
    }

    if (background != null && content != null)
    {
      final ReportAttributeMap attrs = new ReportAttributeMap(background.getAttributes());
      attrs.putAll(content.getAttributes());
      HtmlPrinter.applyHtmlAttributes(attrs, attrList);
    }
    else if (background != null)
    {
      final ReportAttributeMap attrs = background.getAttributes();
      HtmlPrinter.applyHtmlAttributes(attrs, attrList);
    }
    else if (content != null)
    {
      HtmlPrinter.applyHtmlAttributes(content.getAttributes(), attrList);
    }
    styleManager.updateStyle(styleBuilder, attrList);
    return attrList;
  }


  /**
   * Translates the JFreeReport horizontal element alignment into a HTML alignment constant.
   *
   * @param ea the element alignment
   * @return the translated alignment name.
   */
  private String translateVerticalAlignment(final ElementAlignment ea)
  {
    if (ElementAlignment.BOTTOM.equals(ea))
    {
      return "bottom";
    }
    if (ElementAlignment.MIDDLE.equals(ea))
    {
      return "middle";
    }
    return "top";
  }


  private AttributeList createRowAttributes(final LogicalPageBox logicalPageBox,
                                            final SheetLayout sheetLayout, final int row,
                                            final TableContentProducer tableContentProducer)
  {
    final AttributeList attrList = new AttributeList();
    final int rowHeight = (int) StrictGeomUtility.toExternalValue(sheetLayout.getRowHeight(row));

    if (isTableRowBorderDefinition())
    {
      styleBuilder.clear();

      final RowBackgroundStruct struct = getCommonBackground(logicalPageBox, sheetLayout, row, tableContentProducer);
      if (struct.failed == false)
      {
        final Color commonBackgroundColor = struct.color;
        final BorderEdge top = struct.topEdge;
        final BorderEdge bottom = struct.bottomEdge;
        if (commonBackgroundColor != null)
        {
          styleBuilder.append(DefaultStyleBuilder.CSSKeys.BACKGROUND_COLOR, HtmlColors.getColorString(commonBackgroundColor));
        }
        if (BorderEdge.EMPTY.equals(top) == false)
        {
          styleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.BORDER_TOP, styleBuilder.printEdgeAsCSS(top));
        }
        if (BorderEdge.EMPTY.equals(bottom) == false)
        {
          styleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM, styleBuilder.printEdgeAsCSS(bottom));
        }
      }
      styleBuilder.append(DefaultStyleBuilder.CSSKeys.HEIGHT, styleBuilder.getPointConverter().format(fixLengthForSafari(rowHeight)), "pt");
      styleManager.updateStyle(styleBuilder, attrList);
    }
    else
    {
      // equally expensive and makes text more readable (helps with debugging)
      attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE,
          "style", "height: " + styleBuilder.getPointConverter().format(fixLengthForSafari(rowHeight)) + "pt");
    }
    return attrList;
  }

  private RowBackgroundStruct getCommonBackground(final LogicalPageBox logicalPageBox,
                                                  final SheetLayout sheetLayout, final int row,
                                                  final TableContentProducer tableContentProducer)
  {
    final RowBackgroundStruct bg = new RowBackgroundStruct();
    bg.topEdge = BorderEdge.EMPTY;
    bg.bottomEdge = BorderEdge.EMPTY;

    final int columnCount = sheetLayout.getColumnCount();
    for (int col = 0; col < columnCount; col += 1)
    {
      final CellMarker.SectionType sectionType = tableContentProducer.getSectionType(row, col);
      final RenderBox content = tableContentProducer.getContent(row, col);
      final CellBackground backgroundAt;
      if (content == null)
      {
        final RenderBox background = tableContentProducer.getBackground(row, col);
        if (background != null)
        {
          backgroundAt = cellBackgroundProducer.getBackgroundForBox
              (logicalPageBox, sheetLayout, col, row, 1, 1, false, sectionType, background);
        }
        else
        {
          backgroundAt = cellBackgroundProducer.getBackgroundAt(logicalPageBox, sheetLayout, col, row, false, sectionType);
        }
      }
      else
      {
        final long contentOffset = tableContentProducer.getContentOffset(row, col);
        final int colSpan = sheetLayout.getColSpan(col, content.getX() + content.getWidth());
        final int rowSpan = sheetLayout.getRowSpan(row, content.getY() + content.getHeight() + contentOffset);
        backgroundAt = cellBackgroundProducer.getBackgroundForBox
            (logicalPageBox, sheetLayout, col, row, colSpan, rowSpan, false, sectionType, content);
      }
      if (backgroundAt == null)
      {
        bg.failed = true;
        bg.color = null;
        bg.topEdge = BorderEdge.EMPTY;
        bg.bottomEdge = BorderEdge.EMPTY;
        return bg;
      }

      boolean fail = false;
      if (col == 0)
      {
        bg.color = backgroundAt.getBackgroundColor();
        bg.topEdge = backgroundAt.getTop();
        bg.bottomEdge = backgroundAt.getBottom();
      }
      else
      {
        if (ObjectUtilities.equal(bg.color, backgroundAt.getBackgroundColor()) == false)
        {
          fail = true;
        }
        if (ObjectUtilities.equal(bg.topEdge, backgroundAt.getTop()) == false)
        {
          fail = true;
        }
        if (ObjectUtilities.equal(bg.bottomEdge, backgroundAt.getBottom()) == false)
        {
          fail = true;
        }
      }

      if (BorderCorner.EMPTY.equals(backgroundAt.getBottomLeft()) == false)
      {
        fail = true;
      }
      if (BorderCorner.EMPTY.equals(backgroundAt.getBottomRight()) == false)
      {
        fail = true;
      }
      if (BorderCorner.EMPTY.equals(backgroundAt.getTopLeft()) == false)
      {
        fail = true;
      }
      if (BorderCorner.EMPTY.equals(backgroundAt.getTopRight()) == false)
      {
        fail = true;
      }
      if (fail)
      {
        bg.failed = true;
        bg.color = null;
        bg.topEdge = BorderEdge.EMPTY;
        bg.bottomEdge = BorderEdge.EMPTY;
        break;
      }

    }
    return bg;
  }

  private AttributeList createSheetNameAttributes()
  {
    final AttributeList tableAttrList = new AttributeList();

    final String additionalStyleClass =
        getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.html.SheetNameClass");
    if (additionalStyleClass != null)
    {
      tableAttrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "class", additionalStyleClass);
    }

    return tableAttrList;
  }

  private AttributeList createTableAttributes(final SheetLayout sheetLayout,
                                              final LogicalPageBox logicalPageBox)
  {
    final int noc = sheetLayout.getColumnCount();
    styleBuilder.clear();
    if ((noc > 0) && (isProportionalColumnWidths() == false))
    {
      final int width = (int) StrictGeomUtility.toExternalValue(sheetLayout.getCellWidth(0, noc));
      styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, width + "pt");
    }
    else
    {
      // Consume the complete width for proportional column widths
      styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, "100%");
    }

    // style += "table-layout: fixed;";
    if (isTableRowBorderDefinition())
    {
      styleBuilder.append(DefaultStyleBuilder.CSSKeys.BORDER_COLLAPSE, "collapse");
    }
    if (isEmptyCellsUseCSS())
    {
      styleBuilder.append(DefaultStyleBuilder.CSSKeys.EMPTY_CELLS, "show");
    }
    if (isUseTableLayoutFixed())
    {
      styleBuilder.append(DefaultStyleBuilder.CSSKeys.TABLE_LAYOUT, "fixed");
    }

    final String additionalStyleClass =
        getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.html.StyleClass");

    final AttributeList tableAttrList = new AttributeList();
    if (additionalStyleClass != null)
    {
      tableAttrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "class", additionalStyleClass);
    }
    tableAttrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "cellspacing", "0");
    tableAttrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "cellpadding", "0");

    HtmlPrinter.applyHtmlAttributes(logicalPageBox.getAttributes(), tableAttrList);

    styleManager.updateStyle(styleBuilder, tableAttrList);
    return tableAttrList;
  }

  public static void applyHtmlAttributes(final ReportAttributeMap attributes, final AttributeList attrList)
  {
    if (attributes == null)
    {
      throw new NullPointerException("Attributes must not be null");
    }
    if (attrList == null)
    {
      throw new NullPointerException();
    }

    final Object name = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.NAME);
    if (name != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "name", String.valueOf(name));
    }
    final Object id = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.XML_ID);
    if (id != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "id", String.valueOf(id));
    }
    final Object styleClass = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.STYLE_CLASS);
    if (styleClass != null)
    {
      final String styleClassAttr = attrList.getAttribute(XHTML_NAMESPACE, "class");
      if (styleClassAttr == null)
      {
        attrList.setAttribute(XHTML_NAMESPACE, "class", String.valueOf(styleClass));
      }
      else
      {
        attrList.setAttribute(XHTML_NAMESPACE, "class", styleClassAttr + ' ' + String.valueOf(styleClass));
      }
    }
    final Object onClick = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONCLICK);
    if (onClick != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "onclick", String.valueOf(onClick));
    }
    final Object onDblClick = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONDBLCLICK);
    if (onDblClick != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "ondblclick", String.valueOf(onDblClick));
    }
    final Object onKeyDown = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONKEYDOWN);
    if (onKeyDown != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "onkeydown", String.valueOf(onKeyDown));
    }
    final Object onKeyPressed = attributes.getAttribute(AttributeNames.Html.NAMESPACE,
        AttributeNames.Html.ONKEYPRESSED);
    if (onKeyPressed != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "onkeypressed", String.valueOf(onKeyPressed));
    }
    final Object onKeyUp = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONKEYUP);
    if (onKeyUp != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "onkeyup", String.valueOf(onKeyUp));
    }
    final Object onMouseDown = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONMOUSEDOWN);
    if (onMouseDown != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "onmousedown", String.valueOf(onMouseDown));
    }
    final Object onMouseMove = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONMOUSEMOVE);
    if (onMouseMove != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "onmousemove", String.valueOf(onMouseMove));
    }
    final Object onMouseOver = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONMOUSEOVER);
    if (onMouseOver != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "onmouseover", String.valueOf(onMouseOver));
    }
    final Object onMouseUp = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONMOUSEUP);
    if (onMouseUp != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "onmouseup", String.valueOf(onMouseUp));
    }
    final Object onMouseOut = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONMOUSEOUT);
    if (onMouseOut != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "onmouseout", String.valueOf(onMouseOut));
    }
    final Object onMouseEnter = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.ONMOUSEENTER);
    if (onMouseEnter != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "onmouseenter", String.valueOf(onMouseEnter));
    }
    final Object title = attributes.getAttribute(AttributeNames.Html.NAMESPACE, AttributeNames.Html.TITLE);
    if (title != null)
    {
      attrList.setAttribute(XHTML_NAMESPACE, "title", String.valueOf(title));
    }
  }

  private void writeColumnDeclaration(final SheetLayout sheetLayout)
      throws IOException
  {
    if (sheetLayout == null)
    {
      throw new NullPointerException();
    }

    final int colCount = sheetLayout.getColumnCount();
    final int fullWidth = (int) StrictGeomUtility.toExternalValue(sheetLayout.getMaxWidth());
    final String[] colWidths = new String[colCount];
    final boolean proportionalColumnWidths = isProportionalColumnWidths();
    final NumberFormat pointConverter = styleBuilder.getPointConverter();
    final String unit;

    if (proportionalColumnWidths)
    {
      unit = "%";

      double totalWidth = 0;
      for (int col = 0; col < colCount; col++)
      {
        final int width = (int) StrictGeomUtility.toExternalValue(sheetLayout.getCellWidth(col, col + 1));
        final double colWidth = fixLengthForSafari(Math.max(1, width * 100.0d / fullWidth));
        if (col == colCount - 1)
        {
          colWidths[col] = pointConverter.format(100 - totalWidth);
        }
        else
        {
          totalWidth += colWidth;
          colWidths[col] = pointConverter.format(colWidth);
        }
      }
    }
    else
    {
      unit = "pt";

      double totalWidth = 0;
      for (int col = 0; col < colCount; col++)
      {
        final int width = (int) StrictGeomUtility.toExternalValue(sheetLayout.getCellWidth(col, col + 1));
        final double colWidth = fixLengthForSafari(Math.max(1, width));
        if (col == colCount - 1)
        {
          colWidths[col] = pointConverter.format(fullWidth - totalWidth);
        }
        else
        {
          totalWidth += colWidth;
          colWidths[col] = pointConverter.format(colWidth);
        }
      }
    }

    for (int col = 0; col < colCount; col++)
    {
      // Print the table.
      styleBuilder.clear();
      styleBuilder.append(DefaultStyleBuilder.CSSKeys.WIDTH, colWidths[col], unit);
      xmlWriter.writeTag(null, "col", "style", styleBuilder.toString(), XmlWriterSupport.CLOSE);
    }
  }

  public void print(final LogicalPageKey logicalPageKey,
                    final LogicalPageBox logicalPage,
                    final TableContentProducer contentProducer,
                    final OutputProcessorMetaData metaData,
                    final boolean incremental)
      throws ContentProcessingException
  {
    try
    {
      final SheetLayout sheetLayout = contentProducer.getSheetLayout();
      final int startRow = contentProducer.getFinishedRows();
      final int finishRow = contentProducer.getFilledRows();
      if (incremental && startRow == finishRow)
      {
        return;
      }

      if (documentContentItem == null)
      {
        this.cellBackgroundProducer = new CellBackgroundProducer
            (metaData.isFeatureSupported(AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE),
                metaData.isFeatureSupported(OutputProcessorFeature.UNALIGNED_PAGEBANDS));

        this.configuration = metaData.getConfiguration();
        this.allowRawLinkTargets = "true".equals
            (configuration.getConfigProperty(HtmlTableModule.ALLOW_RAW_LINK_TARGETS));
        this.copyExternalImages = "true".equals
            (configuration.getConfigProperty(HtmlTableModule.COPY_EXTERNAL_IMAGES));

        documentContentItem = contentLocation.createItem
            (contentNameGenerator.generateName(null, "text/html"));

        final OutputStream out = documentContentItem.getOutputStream();
        final String encoding = configuration.getConfigProperty
            (HtmlTableModule.ENCODING, EncodingRegistry.getPlatformDefaultEncoding());
        writer = new BufferedWriter(new OutputStreamWriter(out, encoding));

        final DefaultTagDescription td = new DefaultTagDescription();
        td.setDefaultNamespace(XHTML_NAMESPACE);
        td.setNamespaceHasCData(XHTML_NAMESPACE, true);
        td.setNamespaceHasCData(XHTML_NAMESPACE, true);
        td.setElementHasCData(XHTML_NAMESPACE, "body", false);
        td.setElementHasCData(XHTML_NAMESPACE, "br", true);
        td.setElementHasCData(XHTML_NAMESPACE, "col", false);
        td.setElementHasCData(XHTML_NAMESPACE, "colgroup", false);
        td.setElementHasCData(XHTML_NAMESPACE, "div", true);
        td.setElementHasCData(XHTML_NAMESPACE, "head", false);
        td.setElementHasCData(XHTML_NAMESPACE, "html", false);
        td.setElementHasCData(XHTML_NAMESPACE, "img", true);
        td.setElementHasCData(XHTML_NAMESPACE, "input", true);
        td.setElementHasCData(XHTML_NAMESPACE, "meta", true);
        td.setElementHasCData(XHTML_NAMESPACE, "p", true);
        td.setElementHasCData(XHTML_NAMESPACE, "pre", true);
        td.setElementHasCData(XHTML_NAMESPACE, "span", true);
        td.setElementHasCData(XHTML_NAMESPACE, "style", false);
        td.setElementHasCData(XHTML_NAMESPACE, "table", false);
        td.setElementHasCData(XHTML_NAMESPACE, "tbody", false);
        td.setElementHasCData(XHTML_NAMESPACE, "td", true);
        td.setElementHasCData(XHTML_NAMESPACE, "tfoot", false);
        td.setElementHasCData(XHTML_NAMESPACE, "th", false);
        td.setElementHasCData(XHTML_NAMESPACE, "thead", false);
        td.setElementHasCData(XHTML_NAMESPACE, "title", true);
        td.setElementHasCData(XHTML_NAMESPACE, "tr", false);

        if (isCreateBodyFragment() == false)
        {
          if (isInlineStylesRequested())
          {
            this.styleManager = new InlineStyleManager();
            this.xmlWriter = new XmlWriter(writer, td);
            this.xmlWriter.addImpliedNamespace(HtmlPrinter.XHTML_NAMESPACE, "");
            this.xmlWriter.setHtmlCompatiblityMode(true);
            writeCompleteHeader(xmlWriter, writer, contentProducer, logicalPage, null, null);
          }
          else
          {
            if (isExternalStyleSheetRequested())
            {
              this.styleFile = dataLocation.createItem(dataNameGenerator.generateName("style", "text/css"));
              this.styleFileUrl = urlRewriter.rewrite(documentContentItem, styleFile);
            }

            this.styleManager = new GlobalStyleManager();
            if (isForceBufferedWriting() == false && styleFile != null)
            {
              this.xmlWriter = new XmlWriter(writer, td);
              this.xmlWriter.addImpliedNamespace(HtmlPrinter.XHTML_NAMESPACE, "");
              this.xmlWriter.setHtmlCompatiblityMode(true);
              writeCompleteHeader(xmlWriter, writer, contentProducer, logicalPage, styleFileUrl, null);
            }
            else
            {
              this.bufferWriter = new MemoryStringWriter(1024 * 512);
              this.xmlWriter = new XmlWriter(bufferWriter, td);
              this.xmlWriter.setAdditionalIndent(1);
              this.xmlWriter.addImpliedNamespace(HtmlPrinter.XHTML_NAMESPACE, "");
              this.xmlWriter.setHtmlCompatiblityMode(true);
            }
          }

          this.xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "body", XmlWriterSupport.OPEN);
        }
        else
        {
          this.styleManager = new InlineStyleManager();
          this.xmlWriter = new XmlWriter(writer, td);
          this.xmlWriter.addImpliedNamespace(HtmlPrinter.XHTML_NAMESPACE, "");
          this.xmlWriter.setHtmlCompatiblityMode(true);
        }

        final ReportAttributeMap map = logicalPage.getAttributes();
        final Object rawContent = map.getAttribute(AttributeNames.Html.NAMESPACE,
            AttributeNames.Html.EXTRA_RAW_CONTENT);
        if (rawContent != null)
        {
          xmlWriter.writeText(String.valueOf(rawContent));
        }

        // table name
        if ("true".equals(metaData.getConfiguration().getConfigProperty
            ("org.pentaho.reporting.engine.classic.core.modules.output.table.html.EnableSheetNameProcessing")))
        {
          final String sheetName = contentProducer.getSheetName();
          if (sheetName != null)
          {
            xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "h1", createSheetNameAttributes(), XmlWriterSupport.OPEN);
            xmlWriter.writeTextNormalized(sheetName, true);
            xmlWriter.writeCloseTag();
          }
        }

        // table
        xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "table", createTableAttributes(sheetLayout, logicalPage),
            XmlWriterSupport.OPEN);
        writeColumnDeclaration(sheetLayout);
      }

      final int colCount = sheetLayout.getColumnCount();
      final boolean emptyCellsUseCSS = isEmptyCellsUseCSS();

      if (textExtractor == null)
      {
        textExtractor = new HtmlTextExtractor(metaData, xmlWriter, styleManager, this);
      }

      for (int row = startRow; row < finishRow; row++)
      {
        xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "tr",
            createRowAttributes(logicalPage, sheetLayout, row, contentProducer), XmlWriterSupport.OPEN);
        for (int col = 0; col < colCount; col++)
        {
          final RenderBox content = contentProducer.getContent(row, col);
          final CellMarker.SectionType sectionType = contentProducer.getSectionType(row, col);
          if (content == null)
          {
            final RenderBox backgroundBox = contentProducer.getBackground(row, col);
            final CellBackground background;
            if (backgroundBox != null)
            {
              background = cellBackgroundProducer.getBackgroundForBox
                  (logicalPage, sheetLayout, col, row, 1, 1, true, sectionType, backgroundBox);
            }
            else
            {
              background = cellBackgroundProducer.getBackgroundAt(logicalPage, sheetLayout, col, row, true, sectionType);
            }
            if (background == null)
            {
              if (emptyCellsUseCSS)
              {
                xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "td", XmlWriterSupport.CLOSE);
              }
              else
              {
                final AttributeList attrs = new AttributeList();
                attrs.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "style", "font-size: 1pt");
                xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "td", attrs, XmlWriterSupport.OPEN);
                xmlWriter.writeText("&nbsp;");
                xmlWriter.writeCloseTag();
              }
              continue;
            }

            // Background cannot be null at this point ..
            final String[] anchor = background.getAnchors();
            if (anchor.length == 0 && emptyCellsUseCSS)
            {
              final StyleBuilder cellStyle = createCellStyle(null, background, null, null);
              final AttributeList cellAttributes = createCellAttributes(1, 1, null, background, cellStyle);
              xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "td", cellAttributes, XmlWriterSupport.CLOSE);
            }
            else
            {
              final StyleBuilder cellStyle = createCellStyle(null, background, HtmlPrinter.EMPTY_CELL_ATTRNAMES,
                  HtmlPrinter.EMPTY_CELL_ATTRVALS);
              final AttributeList cellAttributes = createCellAttributes(1, 1, null, background, cellStyle);
              xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "td", cellAttributes, XmlWriterSupport.OPEN);
              for (int i = 0; i < anchor.length; i++)
              {
                xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "a", "name", anchor[i], XmlWriterSupport.CLOSE);
              }
              xmlWriter.writeText("&nbsp;");
              xmlWriter.writeCloseTag();

            }
            continue;
          }

          if (content.isCommited() == false)
          {
            throw new InvalidReportStateException(
                "Uncommited content encountered: " + row + ", " + col + ' ' + content);
          }

          final long contentOffset = contentProducer.getContentOffset(row, col);

          final long colPos = sheetLayout.getXPosition(col);
          final long rowPos = sheetLayout.getYPosition(row);
          if (content.getX() != colPos || (content.getY() + contentOffset) != rowPos)
          {
            // A spanned cell ..
            if (content.isFinishedTable())
            {
              continue;
            }
          }

          final int colSpan = sheetLayout.getColSpan(col, content.getX() + content.getWidth());
          final int rowSpan = sheetLayout.getRowSpan(row, content.getY() + content.getHeight() + contentOffset);

          final CellBackground realBackground = cellBackgroundProducer.getBackgroundForBox
              (logicalPage, sheetLayout, col, row, colSpan, rowSpan, true, sectionType, content);

          final StyleBuilder cellStyle = createCellStyle(content, realBackground, null, null);
          final AttributeList cellAttributes =
              createCellAttributes(colSpan, rowSpan, content, realBackground, cellStyle);
          xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "td", cellAttributes, XmlWriterSupport.OPEN);

          final Object rawContent = content.getAttributes().getAttribute(AttributeNames.Html.NAMESPACE,
              AttributeNames.Html.EXTRA_RAW_CONTENT);
          if (rawContent != null)
          {
            xmlWriter.writeText(String.valueOf(rawContent));
          }

          if (realBackground != null)
          {
            final String[] anchors = realBackground.getAnchors();
            for (int i = 0; i < anchors.length; i++)
            {
              final String anchor = anchors[i];
              xmlWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "a", "name", anchor, XmlWriterSupport.CLOSE);
            }
          }

          if (Boolean.TRUE.equals(content.getAttributes().getAttribute(AttributeNames.Html.NAMESPACE,
              AttributeNames.Html.SUPPRESS_CONTENT)) == false)
          {
            // the style of the content-box itself is already contained in the <td> tag. So there is no need
            // to duplicate the style here
            if (textExtractor.performOutput(content, cellStyle.toArray()) == false)
            {
              if (emptyCellsUseCSS == false)
              {
                xmlWriter.writeText("&nbsp;");
              }
            }
          }

          final Object rawFooterContent = content.getAttributes().getAttribute(AttributeNames.Html.NAMESPACE,
              AttributeNames.Html.EXTRA_RAW_FOOTER_CONTENT);
          if (rawFooterContent != null)
          {
            xmlWriter.writeText(String.valueOf(rawFooterContent));
          }

          xmlWriter.writeCloseTag();
          content.setFinishedTable(true);
        }
        xmlWriter.writeCloseTag();
      }


      if (incremental == false)
      {
        performCloseFile(contentProducer, logicalPage);

        xmlWriter = null;
        try
        {
          writer.close();
        }
        catch (IOException e)
        {
          // ignored ..
          logger.error("Failed to close writer instance", e);
        }
        textExtractor = null;
        writer = null;
        bufferWriter = null;
        documentContentItem = null;
      }
    }
    catch (IOException ioe)
    {
      xmlWriter = null;
      try
      {
        if (writer != null)
        {
          writer.close();
        }
      }
      catch (IOException e)
      {
        // ignored ..
      }
      writer = null;
      bufferWriter = null;
      documentContentItem = null;
      styleFile = null;
      textExtractor = null;

      // ignore for now ..
      throw new ContentProcessingException("IOError while creating content", ioe);
    }
    catch (ContentIOException e)
    {
      xmlWriter = null;
      try
      {
        if (writer != null)
        {
          writer.close();
        }
      }
      catch (IOException ex)
      {
        // ignored ..
      }
      writer = null;
      bufferWriter = null;
      documentContentItem = null;
      styleFile = null;
      textExtractor = null;

      throw new ContentProcessingException("Content-IOError while creating content", e);
    }
    catch (URLRewriteException e)
    {
      try
      {
        if (writer != null)
        {
          writer.close();
        }
      }
      catch (IOException ex)
      {
        // ignored ..
      }
      xmlWriter = null;
      writer = null;
      bufferWriter = null;
      documentContentItem = null;
      styleFile = null;
      textExtractor = null;

      throw new ContentProcessingException("Cannot create URL for external stylesheet", e);
    }
  }

  private void writeCompleteHeader(final XmlWriter docWriter,
                                   final Writer writer,
                                   final TableContentProducer contentProducer,
                                   final LogicalPageBox logicalPage,
                                   final String url,
                                   final StyleManager inlineStyleSheet) throws IOException
  {
    final String encoding = configuration.getConfigProperty
        (HtmlTableModule.ENCODING, EncodingRegistry.getPlatformDefaultEncoding());

    docWriter.writeXmlDeclaration(encoding);
    for (int i = 0; i < HtmlPrinter.XHTML_HEADER.length; i++)
    {
      docWriter.writeText(HtmlPrinter.XHTML_HEADER[i]);
      docWriter.writeNewLine();
    }
    docWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "html", XmlWriterSupport.OPEN);
    docWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "head", XmlWriterSupport.OPEN);

    final String title = configuration.getConfigProperty(HtmlTableModule.TITLE);
    if (title != null)
    {
      docWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "title", XmlWriterSupport.OPEN);
      docWriter.writeTextNormalized(title, false);
      docWriter.writeCloseTag();
    }
    // if no single title defined, use the sheetname function previously computed
    else if (contentProducer.getSheetName() != null)
    {
      docWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "title", XmlWriterSupport.OPEN);
      docWriter.writeTextNormalized(contentProducer.getSheetName(), true);
      docWriter.writeCloseTag();
    }
    else
    {
      docWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "title", XmlWriterSupport.OPEN);
      docWriter.writeText(" ");
      docWriter.writeCloseTag();
    }

    writeMeta(docWriter, "subject",
        configuration.getConfigProperty(HtmlTableModule.SUBJECT));
    writeMeta(docWriter, "author",
        configuration.getConfigProperty(HtmlTableModule.AUTHOR));
    writeMeta(docWriter, "keywords",
        configuration.getConfigProperty(HtmlTableModule.KEYWORDS));
    writeMeta(docWriter, "generator", HtmlPrinter.GENERATOR);

    final AttributeList metaAttrs = new AttributeList();
    metaAttrs.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "http-equiv", "content-type");
    metaAttrs.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "content", "text/html; charset=" + encoding);
    docWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "meta", metaAttrs, XmlWriterSupport.CLOSE);

    if (url != null)
    {
      final AttributeList attrList = new AttributeList();
      attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "type", "text/css");
      attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "rel", "stylesheet");
      attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "href", url);

      docWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "link", attrList, XmlWriterSupport.CLOSE);
    }
    else if (inlineStyleSheet != null)
    {
      docWriter.writeTag(HtmlPrinter.XHTML_NAMESPACE, "style", "type", "text/css", XmlWriterSupport.OPEN);
      inlineStyleSheet.write(writer);
      docWriter.writeCloseTag();
    }

    final ReportAttributeMap attributes = logicalPage.getAttributes();
    final Object rawHeaderContent = attributes.getAttribute
        (AttributeNames.Html.NAMESPACE, AttributeNames.Html.EXTRA_RAW_HEADER_CONTENT);
    if (rawHeaderContent != null)
    {
      // Warning: This text is not escaped or processed in any way. it is *RAW* content.
      docWriter.writeText(String.valueOf(rawHeaderContent));
    }
    docWriter.writeCloseTag();
  }

  private void performCloseFile(final TableContentProducer contentProducer,
                                final LogicalPageBox logicalPageBox)
      throws IOException, ContentIOException
  {
    xmlWriter.writeCloseTag(); // for the opening table ..

    final Object rawFooterContent = logicalPageBox.getAttributes().getAttribute(AttributeNames.Html.NAMESPACE,
        AttributeNames.Html.EXTRA_RAW_FOOTER_CONTENT);
    if (rawFooterContent != null)
    {
      xmlWriter.writeText(String.valueOf(rawFooterContent));
    }

    if (isCreateBodyFragment())
    {
      xmlWriter.close();
      return;
    }

    if (styleFile != null)
    {
      final String encoding = configuration.getConfigProperty
          (HtmlTableModule.ENCODING, EncodingRegistry.getPlatformDefaultEncoding());
      final Writer styleOut = new OutputStreamWriter
          (new BufferedOutputStream(styleFile.getOutputStream()), encoding);
      styleManager.write(styleOut);
      styleOut.flush();
      styleOut.close();

      if (isForceBufferedWriting() == false)
      {
        // A complete header had been written when the processing started ..
        this.xmlWriter.writeCloseTag(); // for the body tag
        this.xmlWriter.writeCloseTag(); // for the HTML tag
        this.xmlWriter.close();
        return;
      }
    }
    if (isInlineStylesRequested())
    {
      this.xmlWriter.writeCloseTag(); // for the body tag
      this.xmlWriter.writeCloseTag(); // for the HTML tag
      this.xmlWriter.close();
      return;
    }


    final XmlWriter docWriter = new XmlWriter(writer, xmlWriter.getTagDescription());
    docWriter.addImpliedNamespace(HtmlPrinter.XHTML_NAMESPACE, "");
    docWriter.setHtmlCompatiblityMode(true);

    if (styleFile != null)
    {
      // now its time to write the header with the link to the style-sheet-file
      writeCompleteHeader(docWriter, writer, contentProducer, logicalPageBox, styleFileUrl, null);
    }
    else
    {
      writeCompleteHeader(docWriter, writer, contentProducer, logicalPageBox, null, styleManager);
    }

    xmlWriter.writeCloseTag(); // for the body ..
    xmlWriter.flush();

    // no need to check for IOExceptions here, as we know the implementation does not create such things
    final MemoryStringReader stringReader = bufferWriter.createReader();
    docWriter.writeStream(stringReader);
    stringReader.close();

    docWriter.writeCloseTag(); // for the html ..
    docWriter.close();
  }

  private boolean isForceBufferedWriting()
  {
    return "true".equals(configuration.getConfigProperty
        (HtmlTableModule.FORCE_BUFFER_WRITING));
  }

  private void writeMeta(final XmlWriter writer, final String name, final String value) throws IOException
  {
    if (value == null)
    {
      return;
    }
    final AttributeList attrList = new AttributeList();
    attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "name", name);
    attrList.setAttribute(HtmlPrinter.XHTML_NAMESPACE, "content", value);
    writer.writeTag(HtmlPrinter.XHTML_NAMESPACE, "meta", attrList, XmlWriterSupport.CLOSE);
  }

  private boolean isInlineStylesRequested()
  {
    return "true".equals(configuration.getConfigProperty(HtmlTableModule.INLINE_STYLE));
  }

  private boolean isExternalStyleSheetRequested()
  {
    if (isCreateBodyFragment())
    {
      // body-fragments have no header ..
      return false;
    }

    // We will add the style-declarations directly to the HTML elements ..
    if (isInlineStylesRequested())
    {
      return false;
    }

    // Without the ability to create external files, we cannot create external stylesheet.
    if (dataLocation == null)
    {
      return false;
    }

    // User explicitly requested internal styles by disabling the external-style property.
    return "true".equals(configuration.getConfigProperty(HtmlTableModule.EXTERNALIZE_STYLE, "true"));

  }


  public static StyleBuilder produceTextStyle(StyleBuilder styleBuilder,
                                              final RenderBox box,
                                              final boolean includeBorder,
                                              final boolean fixLength,
                                              final boolean useWhitespacePreWrap,
                                              final boolean enableRoundBorderCorner,
                                              final DefaultStyleBuilder.StyleCarrier[] parentElementStyle)
  {
    if (box == null)
    {
      throw new NullPointerException();
    }
    if (styleBuilder == null)
    {
      styleBuilder = new DefaultStyleBuilder();
    }
    styleBuilder.clear();

    final FilterStyleBuilder filterStyleBuilder = new FilterStyleBuilder(styleBuilder, parentElementStyle);
    final NumberFormat pointConverter = filterStyleBuilder.getPointConverter();
    final StyleSheet styleSheet = box.getStyleSheet();
    final Color textColor = (Color) styleSheet.getStyleProperty(ElementStyleKeys.PAINT);
    final Color backgroundColor = (Color) styleSheet.getStyleProperty(ElementStyleKeys.BACKGROUND_COLOR);

    if (includeBorder)
    {
      if (backgroundColor != null)
      {
        filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.BACKGROUND_COLOR, HtmlColors.getColorString(backgroundColor));
      }

      final BoxDefinition boxDefinition = box.getBoxDefinition();
      final Border border = boxDefinition.getBorder();
      final BorderEdge top = border.getTop();
      final BorderEdge left = border.getLeft();
      final BorderEdge bottom = border.getBottom();
      final BorderEdge right = border.getRight();
      if (top.equals(left) &&
          top.equals(right) &&
          top.equals(bottom))
      {
        if (BorderEdge.EMPTY.equals(top) == false)
        {
          styleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.BORDER, styleBuilder.printEdgeAsCSS(top));
        }
      }
      else
      {
        if (top != null && BorderEdge.EMPTY.equals(top) == false)
        {
          filterStyleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.BORDER_TOP, filterStyleBuilder.printEdgeAsCSS(top));
        }
        if (left != null && BorderEdge.EMPTY.equals(left) == false)
        {
          filterStyleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.BORDER_LEFT, filterStyleBuilder.printEdgeAsCSS(left));
        }
        if (bottom != null && BorderEdge.EMPTY.equals(bottom) == false)
        {
          filterStyleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM, filterStyleBuilder.printEdgeAsCSS(bottom));
        }
        if (right != null && BorderEdge.EMPTY.equals(right) == false)
        {
          filterStyleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.BORDER_RIGHT, filterStyleBuilder.printEdgeAsCSS(right));
        }
      }
      if (enableRoundBorderCorner)
      {
        final double blW = Math.max(0, styleSheet.getDoubleStyleProperty(ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, 0));
        final double blH = Math.max(0, styleSheet.getDoubleStyleProperty(ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, 0));
        if (blW > 0 && blH > 0)
        {
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_BOTTOM_LEFT,
              pointConverter.format(fixLengthForSafari(blW, fixLength)) + "pt " +
                  pointConverter.format(fixLengthForSafari(blH, fixLength)) + "pt");
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM_LEFT_RADIUS,
              pointConverter.format(fixLengthForSafari(blW, fixLength)) + "pt " +
                  pointConverter.format(fixLengthForSafari(blH, fixLength)) + "pt");
        }

        final double brW = Math.max(0, styleSheet.getDoubleStyleProperty(ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, 0));
        final double brH = Math.max(0, styleSheet.getDoubleStyleProperty(ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, 0));
        if (brW > 0 && brH > 0)
        {
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_BOTTOM_RIGHT,
              pointConverter.format(fixLengthForSafari(brW, fixLength)) + "pt " +
                  pointConverter.format(fixLengthForSafari(brH, fixLength)) + "pt");
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM_RIGHT_RADIUS,
              pointConverter.format(fixLengthForSafari(brW, fixLength)) + "pt " +
                  pointConverter.format(fixLengthForSafari(brH, fixLength)) + "pt");
        }

        final double tlW = Math.max(0, styleSheet.getDoubleStyleProperty(ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, 0));
        final double tlH = Math.max(0, styleSheet.getDoubleStyleProperty(ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, 0));
        if (tlW > 0 && tlH > 0)
        {
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_TOP_LEFT,
              pointConverter.format(fixLengthForSafari(tlW, fixLength)) + "pt " +
                  pointConverter.format(fixLengthForSafari(tlH, fixLength)) + "pt");
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.BORDER_TOP_LEFT_RADIUS,
              pointConverter.format(fixLengthForSafari(tlW, fixLength)) + "pt " +
                  pointConverter.format(fixLengthForSafari(tlH, fixLength)) + "pt");
        }

        final double trW = Math.max(0, styleSheet.getDoubleStyleProperty(ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, 0));
        final double trH = Math.max(0, styleSheet.getDoubleStyleProperty(ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, 0));
        if (trW > 0 && trH > 0)
        {
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_TOP_RIGHT,
              pointConverter.format(fixLengthForSafari(trW, fixLength)) + "pt " +
                  pointConverter.format(fixLengthForSafari(trH, fixLength)) + "pt");
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.BORDER_TOP_RIGHT_RADIUS,
              pointConverter.format(fixLengthForSafari(trW, fixLength)) + "pt " +
                  pointConverter.format(fixLengthForSafari(trH, fixLength)) + "pt");
        }
      }

      final long paddingTop = boxDefinition.getPaddingTop();
      final long paddingLeft = boxDefinition.getPaddingLeft();
      final long paddingBottom = boxDefinition.getPaddingBottom();
      final long paddingRight = boxDefinition.getPaddingRight();
      if (paddingTop == paddingLeft && paddingTop == paddingRight && paddingTop == paddingBottom)
      {
        if (paddingTop > 0)
        {
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.PADDING, pointConverter.format
              (fixLengthForSafari(StrictGeomUtility.toExternalValue(paddingTop), fixLength)), "pt");
        }
      }
      else
      {
        if (paddingTop > 0)
        {
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.PADDING_TOP, pointConverter.format
              (fixLengthForSafari(StrictGeomUtility.toExternalValue(paddingTop), fixLength)), "pt");
        }
        if (paddingLeft > 0)
        {
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.PADDING_LEFT, pointConverter.format
              (fixLengthForSafari(StrictGeomUtility.toExternalValue(paddingLeft), fixLength)), "pt");
        }
        if (paddingBottom > 0)
        {
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.PADDING_BOTTOM, pointConverter.format
              (fixLengthForSafari(StrictGeomUtility.toExternalValue(paddingBottom), fixLength)), "pt");
        }
        if (paddingRight > 0)
        {
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.PADDING_RIGHT, pointConverter.format
              (fixLengthForSafari(StrictGeomUtility.toExternalValue(paddingRight), fixLength)), "pt");
        }
      }
    }
    if (textColor != null)
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.COLOR, HtmlColors.getColorString(textColor));
    }
    filterStyleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.FONT_FAMILY, translateFontFamily(box));
    filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.FONT_SIZE, pointConverter.format
        (fixLengthForSafari(styleSheet.getDoubleStyleProperty(TextStyleKeys.FONTSIZE, 0), fixLength)), "pt");
    if (styleSheet.getBooleanStyleProperty(TextStyleKeys.BOLD))
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.FONT_WEIGHT, "bold");
    }
    else
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.FONT_WEIGHT, "normal");
    }

    if (styleSheet.getBooleanStyleProperty(TextStyleKeys.ITALIC))
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.FONT_STYLE, "italic");
    }
    else
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.FONT_STYLE, "normal");
    }

    final boolean underlined = styleSheet.getBooleanStyleProperty(TextStyleKeys.UNDERLINED);
    final boolean strikeThrough = styleSheet.getBooleanStyleProperty(TextStyleKeys.STRIKETHROUGH);
    if (underlined && strikeThrough)
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.TEXT_DECORATION, "underline line-through");
    }
    else if (strikeThrough)
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.TEXT_DECORATION, "line-through");
    }
    if (underlined)
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.TEXT_DECORATION, "underline");
    }
    else
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.TEXT_DECORATION, "none");
    }

    final ElementAlignment align = (ElementAlignment) styleSheet.getStyleProperty(ElementStyleKeys.ALIGNMENT);
    filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.TEXT_ALIGN, translateHorizontalAlignment(align));

    final double wordSpacing = styleSheet.getDoubleStyleProperty(TextStyleKeys.WORD_SPACING, 0);
    filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.WORD_SPACING, pointConverter.format
        (fixLengthForSafari(wordSpacing, fixLength)), "pt");

    final double minLetterSpacing = styleSheet.getDoubleStyleProperty(TextStyleKeys.X_MIN_LETTER_SPACING, 0);
    final double maxLetterSpacing = styleSheet.getDoubleStyleProperty(TextStyleKeys.X_MAX_LETTER_SPACING, 0);
    filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.LETTER_SPACING, pointConverter.format
        (fixLengthForSafari(Math.min(minLetterSpacing, maxLetterSpacing), fixLength)), "pt");

    if (true)
    {
      final WhitespaceCollapse wsCollapse = (WhitespaceCollapse)
          styleSheet.getStyleProperty(TextStyleKeys.WHITE_SPACE_COLLAPSE);
      if (WhitespaceCollapse.PRESERVE.equals(wsCollapse))
      {
        if (useWhitespacePreWrap)
        {
          // this style does not work for IE6 and IE7, but heck, in that case they just behave as if normal mode is
          // selected. In that case multiple spaces are collapsed into a single space.
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.WHITE_SPACE, "pre-wrap");
        }
        else
        {
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.WHITE_SPACE, "pre");
        }
      }
      else if (WhitespaceCollapse.PRESERVE_BREAKS.equals(wsCollapse))
      {
        filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.WHITE_SPACE, "nowrap");
      }
      else
      {
        // discard is handled on the layouter level already;
        // collapse is the normal way of handling whitespaces in the engine.
        filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.WHITE_SPACE, "normal");
      }
    }

    return styleBuilder;
  }

  /**
   * Similar with produceTextStyle, but excluding the RenderBox processing
   * @param styleBuilder
   * @param styleSheet
   * @param includeBorder
   * @param fixLength
   * @param useWhitespacePreWrap
   * @param parentElementStyle
   * @return
   */
  public static StyleBuilder produceTextStyleFromStyleSheet(StyleBuilder styleBuilder,
                                                            final StyleSheet styleSheet,
                                                            final boolean includeBorder,
                                                            final boolean fixLength,
                                                            final boolean useWhitespacePreWrap,
                                                            final DefaultStyleBuilder.StyleCarrier[] parentElementStyle)
  {
    if (styleBuilder == null)
    {
      styleBuilder = new DefaultStyleBuilder();
    }
    styleBuilder.clear();

    final FilterStyleBuilder filterStyleBuilder = new FilterStyleBuilder(styleBuilder, parentElementStyle);
    final NumberFormat pointConverter = filterStyleBuilder.getPointConverter();
    final Color textColor = (Color) styleSheet.getStyleProperty(ElementStyleKeys.PAINT);
    final Color backgroundColor = (Color) styleSheet.getStyleProperty(ElementStyleKeys.BACKGROUND_COLOR);

    if (includeBorder)
    {
      if (backgroundColor != null)
      {
        filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.BACKGROUND_COLOR, HtmlColors.getColorString(backgroundColor));
      }
    }
    if (textColor != null)
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.COLOR, HtmlColors.getColorString(textColor));
    }
//    filterStyleBuilder.appendRaw(DefaultStyleBuilder.CSSKeys.FONT_FAMILY, translateFontFamily(box));
    filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.FONT_SIZE, pointConverter.format
        (fixLengthForSafari(styleSheet.getDoubleStyleProperty(TextStyleKeys.FONTSIZE, 0), fixLength)), "pt");
    if (styleSheet.getBooleanStyleProperty(TextStyleKeys.BOLD))
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.FONT_WEIGHT, "bold");
    }
    else
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.FONT_WEIGHT, "normal");
    }

    if (styleSheet.getBooleanStyleProperty(TextStyleKeys.ITALIC))
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.FONT_STYLE, "italic");
    }
    else
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.FONT_STYLE, "normal");
    }

    final boolean underlined = styleSheet.getBooleanStyleProperty(TextStyleKeys.UNDERLINED);
    final boolean strikeThrough = styleSheet.getBooleanStyleProperty(TextStyleKeys.STRIKETHROUGH);
    if (underlined && strikeThrough)
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.TEXT_DECORATION, "underline line-through");
    }
    else if (strikeThrough)
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.TEXT_DECORATION, "line-through");
    }
    if (underlined)
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.TEXT_DECORATION, "underline");
    }
    else
    {
      filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.TEXT_DECORATION, "none");
    }

    final ElementAlignment align = (ElementAlignment) styleSheet.getStyleProperty(ElementStyleKeys.ALIGNMENT);
    filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.TEXT_ALIGN, translateHorizontalAlignment(align));

    final double wordSpacing = styleSheet.getDoubleStyleProperty(TextStyleKeys.WORD_SPACING, 0);
    filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.WORD_SPACING, pointConverter.format
        (fixLengthForSafari(wordSpacing, fixLength)), "pt");

    final double minLetterSpacing = styleSheet.getDoubleStyleProperty(TextStyleKeys.X_MIN_LETTER_SPACING, 0);
    final double maxLetterSpacing = styleSheet.getDoubleStyleProperty(TextStyleKeys.X_MAX_LETTER_SPACING, 0);
    filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.LETTER_SPACING, pointConverter.format
        (fixLengthForSafari(Math.min(minLetterSpacing, maxLetterSpacing), fixLength)), "pt");

    if (true)
    {
      final WhitespaceCollapse wsCollapse = (WhitespaceCollapse)
          styleSheet.getStyleProperty(TextStyleKeys.WHITE_SPACE_COLLAPSE);
      if (WhitespaceCollapse.PRESERVE.equals(wsCollapse))
      {
        if (useWhitespacePreWrap)
        {
          // this style does not work for IE6 and IE7, but heck, in that case they just behave as if normal mode is
          // selected. In that case multiple spaces are collapsed into a single space.
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.WHITE_SPACE, "pre-wrap");
        }
        else
        {
          filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.WHITE_SPACE, "pre");
        }
      }
      else if (WhitespaceCollapse.PRESERVE_BREAKS.equals(wsCollapse))
      {
        filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.WHITE_SPACE, "nowrap");
      }
      else
      {
        // discard is handled on the layouter level already;
        // collapse is the normal way of handling whitespaces in the engine.
        filterStyleBuilder.append(DefaultStyleBuilder.CSSKeys.WHITE_SPACE, "normal");
      }
    }

    return styleBuilder;
  }

  private static String translateFontFamily(final RenderBox box)
  {
    final String family = box.getStaticBoxLayoutProperties().getFontFamily();
    if ("Serif".equalsIgnoreCase(family))
    {
      return "serif";
    }
    else if ("Sans-serif".equalsIgnoreCase(family) || "SanSerif".equalsIgnoreCase(
        family) || "SansSerif".equalsIgnoreCase(family) ||
        "Dialog".equalsIgnoreCase(family) || "DialogInput".equalsIgnoreCase(family))
    {
      return "sans-serif";
    }
    else if ("Monospaced".equalsIgnoreCase(family))
    {
      return "monospace";
    }
    else
    {
      return '\"' + HtmlEncoderUtil.encodeCSS(family) + '\"';
    }
  }

  /**
   * Translates the JFreeReport horizontal element alignment into a HTML alignment constant.
   *
   * @param ea the element alignment
   * @return the translated alignment name.
   */
  public static String translateHorizontalAlignment(final ElementAlignment ea)
  {
    if (ElementAlignment.JUSTIFY.equals(ea))
    {
      return "justify";
    }
    if (ElementAlignment.RIGHT.equals(ea))
    {
      return "right";
    }
    if (ElementAlignment.CENTER.equals(ea))
    {
      return "center";
    }
    return "left";
  }


  public void registerFailure(final ResourceKey source)
  {
    knownResources.put(source, null);
  }

  public void registerContent(final ResourceKey source, final String name)
  {
    knownResources.put(source, name);
  }

  public boolean isRegistered(final ResourceKey source)
  {
    return knownResources.containsKey(source);
  }

  public String getRegisteredName(final ResourceKey source)
  {
    final Object o = knownResources.get(source);
    if (o instanceof String)
    {
      return (String) o;
    }
    return null;
  }


  private double fixLengthForSafari(final double border)
  {
    return fixLengthForSafari(border, safariLengthFix);
  }

  public static double fixLengthForSafari(final double border, final boolean safariLengthFix)
  {
    if (safariLengthFix == false)
    {
      return border;
    }
    if (border == 0)
    {
      return 0;
    }
    return Math.max(1, Math.round(border));
  }
}
