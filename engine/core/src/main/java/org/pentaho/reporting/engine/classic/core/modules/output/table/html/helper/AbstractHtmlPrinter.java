/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2023 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.NumberFormat;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineInfo;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SlimSheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlTableModule;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.URLRewriteException;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.MemoryStringReader;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

@SuppressWarnings( "HardCodedStringLiteral" )
public abstract class AbstractHtmlPrinter {
  public static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";
  protected static final String[] XHTML_HEADER = { "<!DOCTYPE html",
    "     PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"",
    "     \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" };

  protected static final StyleBuilder.CSSKeys[] EMPTY_CELL_ATTRNAMES =
      new StyleBuilder.CSSKeys[] { StyleBuilder.CSSKeys.FONT_SIZE };
  protected static final String[] EMPTY_CELL_ATTRVALS = new String[] { "1pt" };
  private static final String GENERATOR = ClassicEngineInfo.getInstance().getName() + " version "
      + ClassicEngineInfo.getInstance().getVersion();

  private DefaultStyleBuilderFactory styleBuilderFactory;
  private DefaultHtmlContentGenerator contentGenerator;
  private Configuration configuration;
  private ContentItem styleFile;
  private String styleFileUrl;
  private HtmlTagHelper tagHelper;
  private boolean allowRawLinkTargets;

  public AbstractHtmlPrinter( ResourceManager resourceManager ) {
    if ( resourceManager == null ) {
      throw new NullPointerException( "A resource-manager must be given." );
    }

    contentGenerator = new DefaultHtmlContentGenerator( resourceManager );
  }

  protected void initialize( Configuration configuration ) {
    this.configuration = configuration;

    this.contentGenerator.setCopyExternalImages( "true".equals( configuration
        .getConfigProperty( HtmlTableModule.COPY_EXTERNAL_IMAGES ) ) );
    this.allowRawLinkTargets =
        "true".equals( configuration.getConfigProperty( HtmlTableModule.ALLOW_RAW_LINK_TARGETS ) );

    styleBuilderFactory = new DefaultStyleBuilderFactory();
    styleBuilderFactory.configure( ClassicEngineBoot.getInstance().getGlobalConfig() );

    this.tagHelper = new HtmlTagHelper( configuration, styleBuilderFactory );
  }

  public boolean isAllowRawLinkTargets() {
    return allowRawLinkTargets;
  }

  public StyleManager getStyleManager() {
    return this.tagHelper.getStyleManager();
  }

  public void setStyleManager( final StyleManager styleManager ) {
    this.tagHelper.setStyleManager( styleManager );
  }

  public HtmlTagHelper getTagHelper() {
    return tagHelper;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public void setDataWriter( final ContentLocation dataLocation, final NameGenerator dataNameGenerator ) {
    this.contentGenerator.setDataWriter( dataLocation, dataNameGenerator, getContentReWriteService() );
  }

  protected abstract ContentUrlReWriteService getContentReWriteService();

  public StyleBuilder getStyleBuilder() {
    return tagHelper.getStyleBuilder();
  }

  public DefaultStyleBuilderFactory getStyleBuilderFactory() {
    return styleBuilderFactory;
  }

  public DefaultHtmlContentGenerator getContentGenerator() {
    return contentGenerator;
  }

  protected ResourceManager getResourceManager() {
    return contentGenerator.getResourceManager();
  }

  protected boolean isProportionalColumnWidths() {
    return "true".equals( getConfiguration().getConfigProperty( HtmlTableModule.PROPORTIONAL_COLUMN_WIDTHS, "false" ) );
  }

  protected void writeColumnDeclaration( final SlimSheetLayout sheetLayout, final XmlWriter xmlWriter )
    throws IOException {
    StyleBuilder styleBuilder = getStyleBuilder();
    DefaultStyleBuilderFactory styleBuilderFactory = getStyleBuilderFactory();
    if ( sheetLayout == null ) {
      throw new NullPointerException();
    }

    final int colCount = sheetLayout.getColumnCount();
    final int fullWidth = (int) StrictGeomUtility.toExternalValue( sheetLayout.getMaxWidth() );
    final String[] colWidths = new String[colCount];
    final boolean proportionalColumnWidths = isProportionalColumnWidths();
    final NumberFormat pointConverter = styleBuilder.getPointConverter();
    final String unit;

    if ( proportionalColumnWidths ) {
      unit = "%";

      double totalWidth = 0;
      for ( int col = 0; col < colCount; col++ ) {
        final int width = (int) StrictGeomUtility.toExternalValue( sheetLayout.getCellWidth( col, col + 1 ) );
        final double colWidth = styleBuilderFactory.fixLengthForSafari( Math.max( 1, width * 100.0d / fullWidth ) );
        if ( col == colCount - 1 ) {
          colWidths[col] = pointConverter.format( 100 - totalWidth );
        } else {
          totalWidth += colWidth;
          colWidths[col] = pointConverter.format( colWidth );
        }
      }
    } else {
      unit = "pt";

      double totalWidth = 0;
      for ( int col = 0; col < colCount; col++ ) {
        final int width = (int) StrictGeomUtility.toExternalValue( sheetLayout.getCellWidth( col, col + 1 ) );
        final double colWidth = styleBuilderFactory.fixLengthForSafari( Math.max( 1, width ) );
        if ( col == colCount - 1 ) {
          colWidths[col] = pointConverter.format( fullWidth - totalWidth );
        } else {
          totalWidth += colWidth;
          colWidths[col] = pointConverter.format( colWidth );
        }
      }
    }

    for ( int col = 0; col < colCount; col++ ) {
      // Print the table.
      styleBuilder.clear();
      styleBuilder.append( DefaultStyleBuilder.CSSKeys.WIDTH, colWidths[col], unit );
      xmlWriter.writeTag( null, "col", "style", styleBuilder.toString(), XmlWriterSupport.CLOSE );
    }
  }

  protected void writeCompleteHeader( final XmlWriter docWriter, final String sheetName,
      final ReportAttributeMap attributes, final String styleSheetUrl, final StyleManager inlineStyleSheet )
    throws IOException {
    Configuration configuration = getConfiguration();
    final String encoding =
        configuration.getConfigProperty( HtmlTableModule.ENCODING, EncodingRegistry.getPlatformDefaultEncoding() );

    docWriter.writeXmlDeclaration( encoding );
    for ( int i = 0; i < XHTML_HEADER.length; i++ ) {
      docWriter.writeText( XHTML_HEADER[i] );
      docWriter.writeNewLine();
    }
    docWriter.writeTag( XHTML_NAMESPACE, "html", XmlWriterSupport.OPEN );
    docWriter.writeTag( XHTML_NAMESPACE, "head", XmlWriterSupport.OPEN );

    final String title = configuration.getConfigProperty( HtmlTableModule.TITLE );
    if ( title != null ) {
      docWriter.writeTag( XHTML_NAMESPACE, "title", XmlWriterSupport.OPEN );
      docWriter.writeTextNormalized( title, false );
      docWriter.writeCloseTag();
    } else if ( sheetName != null ) { // if no single title defined, use the sheetname function previously computed
      docWriter.writeTag( XHTML_NAMESPACE, "title", XmlWriterSupport.OPEN );
      docWriter.writeTextNormalized( sheetName, true );
      docWriter.writeCloseTag();
    } else {
      docWriter.writeTag( XHTML_NAMESPACE, "title", XmlWriterSupport.OPEN );
      docWriter.writeText( " " );
      docWriter.writeCloseTag();
    }

    writeMeta( docWriter, "subject", configuration.getConfigProperty( HtmlTableModule.SUBJECT ) );
    writeMeta( docWriter, "author", configuration.getConfigProperty( HtmlTableModule.AUTHOR ) );
    writeMeta( docWriter, "keywords", configuration.getConfigProperty( HtmlTableModule.KEYWORDS ) );
    writeMeta( docWriter, "generator", GENERATOR );

    final AttributeList metaAttrs = new AttributeList();
    metaAttrs.setAttribute( XHTML_NAMESPACE, "http-equiv", "content-type" );
    metaAttrs.setAttribute( XHTML_NAMESPACE, "content", "text/html; charset=" + encoding );
    docWriter.writeTag( XHTML_NAMESPACE, "meta", metaAttrs, XmlWriterSupport.CLOSE );

    if ( styleSheetUrl != null ) {
      final AttributeList attrList = new AttributeList();
      attrList.setAttribute( XHTML_NAMESPACE, "type", "text/css" );
      attrList.setAttribute( XHTML_NAMESPACE, "rel", "stylesheet" );
      attrList.setAttribute( XHTML_NAMESPACE, "href", styleSheetUrl );

      docWriter.writeTag( XHTML_NAMESPACE, "link", attrList, XmlWriterSupport.CLOSE );
    } else if ( inlineStyleSheet != null ) {
      docWriter.writeTag( XHTML_NAMESPACE, "style", "type", "text/css", XmlWriterSupport.OPEN );
      StringWriter writer = new StringWriter();
      inlineStyleSheet.write( writer );
      docWriter.writeText( writer.toString() );
      docWriter.writeCloseTag();
    }

    final Object rawHeaderContent =
        attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.EXTRA_RAW_HEADER_CONTENT );
    if ( rawHeaderContent != null ) {
      // Warning: This text is not escaped or processed in any way. it is *RAW* content.
      docWriter.writeText( String.valueOf( rawHeaderContent ) );
    }
    docWriter.writeCloseTag();
  }

  private void writeMeta( final XmlWriter writer, final String name, final String value ) throws IOException {
    if ( value == null ) {
      return;
    }
    final AttributeList attrList = new AttributeList();
    attrList.setAttribute( XHTML_NAMESPACE, "name", name );
    attrList.setAttribute( XHTML_NAMESPACE, "content", value );
    writer.writeTag( XHTML_NAMESPACE, "meta", attrList, XmlWriterSupport.CLOSE );
  }

  private void addStickyHeaderStyle(GlobalStyleManager styleManager) {
    StyleBuilder styleBuilder = getStyleBuilder();
    AttributeList attrs = new AttributeList();

    attrs.setAttribute( HtmlPrinter.XHTML_NAMESPACE, AttributeNames.Html.STYLE_CLASS, "sticky-header" );
    styleBuilder.append( StyleBuilder.CSSKeys.POSITION, "sticky" );
    styleBuilder.append( StyleBuilder.CSSKeys.TOP, "0", "px" );

    styleManager.updateStyleForcedStyleName( styleBuilder, attrs, "sticky-header" );
  }

  protected StyleManager createStyleManager() {
    if ( isCreateBodyFragment() == false && isInlineStylesRequested() == false ) {
      GlobalStyleManager styleManager = new GlobalStyleManager();
      addStickyHeaderStyle( styleManager );
      return styleManager;
    }

    return new InlineStyleManager();
  }

  protected boolean isCreateBodyFragment() {
    return "true".equals( getConfiguration().getConfigProperty( HtmlTableModule.BODY_FRAGMENT, "false" ) );
  }

  protected boolean isInlineStylesRequested() {
    return "true".equals( getConfiguration().getConfigProperty( HtmlTableModule.INLINE_STYLE ) );
  }

  protected void generateHeaderOnOpen( final ReportAttributeMap attributeMap, final String sheetName,
      final XmlWriter xmlWriter ) throws IOException {
    if ( isCreateBodyFragment() == false ) {
      if ( isInlineStylesRequested() ) {
        writeCompleteHeader( xmlWriter, sheetName, attributeMap, null, null );
      } else {
        if ( isExternalStyleSheetRequested() ) {
          if ( isForceBufferedWriting() == false ) {
            writeCompleteHeader( xmlWriter, sheetName, attributeMap, styleFileUrl, null );
          }
        }
      }
      xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "body", XmlWriterSupport.OPEN );
    }
  }

  protected void generateExternalStylePlaceHolder() throws ContentIOException, URLRewriteException {
    if ( isExternalStyleSheetRequested() == false ) {
      return;
    }

    this.styleFile = getContentGenerator().createItem( "style", "text/css" );
    this.styleFileUrl = getContentReWriteService().rewriteContentDataItem( styleFile );
  }

  public ContentItem getStyleFile() {
    return styleFile;
  }

  public String getStyleFileUrl() {
    return styleFileUrl;
  }

  protected WriterService createWriterService( final OutputStream out ) throws UnsupportedEncodingException {
    final String encoding =
        configuration.getConfigProperty( HtmlTableModule.ENCODING, EncodingRegistry.getPlatformDefaultEncoding() );

    if ( isCreateBodyFragment() == false ) {
      if ( isInlineStylesRequested() ) {
        return WriterService.createPassThroughService( out, encoding );
      } else {
        if ( isExternalStyleSheetRequested() && isForceBufferedWriting() == false ) {
          return WriterService.createPassThroughService( out, encoding );
        } else {
          return WriterService.createBufferedService( out, encoding );
        }
      }
    } else {
      return WriterService.createPassThroughService( out, encoding );
    }
  }

  protected boolean isForceBufferedWriting() {
    return "true".equals( getConfiguration().getConfigProperty( HtmlTableModule.FORCE_BUFFER_WRITING ) );
  }

  protected boolean isExternalStyleSheetRequested() {
    if ( isCreateBodyFragment() ) {
      // body-fragments have no header ..
      return false;
    }

    // We will add the style-declarations directly to the HTML elements ..
    if ( isInlineStylesRequested() ) {
      return false;
    }

    // Without the ability to create external files, we cannot create external stylesheet.
    if ( getContentGenerator().isExternalContentAvailable() == false ) {
      return false;
    }

    // User explicitly requested internal styles by disabling the external-style property.
    return "true".equals( getConfiguration().getConfigProperty( HtmlTableModule.EXTERNALIZE_STYLE, "true" ) );

  }

  protected void performCloseFile( final String sheetName, final ReportAttributeMap logicalPageBox,
      final WriterService writer ) throws IOException, ContentIOException {
    XmlWriter xmlWriter = writer.getXmlWriter();
    xmlWriter.writeCloseTag(); // for the opening table ..

    final Object rawFooterContent =
        logicalPageBox.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.EXTRA_RAW_FOOTER_CONTENT );
    if ( rawFooterContent != null ) {
      xmlWriter.writeText( String.valueOf( rawFooterContent ) );
    }

    if ( isCreateBodyFragment() ) {
      xmlWriter.close();
      return;
    }

    ContentItem styleFile = getStyleFile();
    if ( styleFile != null ) {
      final String encoding =
          getConfiguration()
              .getConfigProperty( HtmlTableModule.ENCODING, EncodingRegistry.getPlatformDefaultEncoding() );
      final Writer styleOut =
          new OutputStreamWriter( new BufferedOutputStream( styleFile.getOutputStream() ), encoding );
      getStyleManager().write( styleOut );
      styleOut.flush();
      styleOut.close();

      if ( isForceBufferedWriting() == false ) {
        // A complete header had been written when the processing started ..
        xmlWriter.writeCloseTag(); // for the body tag
        xmlWriter.writeCloseTag(); // for the HTML tag
        xmlWriter.close();
        return;
      }
    }
    if ( isInlineStylesRequested() ) {
      xmlWriter.writeCloseTag(); // for the body tag
      xmlWriter.writeCloseTag(); // for the HTML tag
      xmlWriter.close();
      return;
    }

    // handle external stylesheets. They need to be injected into the header.

    // finish the body fragment
    xmlWriter.writeCloseTag(); // for the body ..
    xmlWriter.flush();

    final XmlWriter docWriter = writer.createHeaderXmlWriter();
    if ( styleFile != null ) {
      // now its time to write the header with the link to the style-sheet-file
      writeCompleteHeader( docWriter, sheetName, logicalPageBox, getStyleFileUrl(), null );
    } else {
      writeCompleteHeader( docWriter, sheetName, logicalPageBox, null, getStyleManager() );
    }

    // no need to check for IOExceptions here, as we know the implementation does not create such things
    final MemoryStringReader stringReader = writer.getBufferWriter().createReader();
    docWriter.writeStream( stringReader );
    stringReader.close();

    docWriter.writeCloseTag(); // for the html ..
    docWriter.close();
  }

  protected void openSheet( final ReportAttributeMap logicalPage, final String sheetName,
      final OutputProcessorMetaData metaData, final SlimSheetLayout sheetLayout, final XmlWriter xmlWriter )
    throws ContentIOException, URLRewriteException, IOException {
    setStyleManager( createStyleManager() );

    generateExternalStylePlaceHolder();
    generateHeaderOnOpen( logicalPage, sheetName, xmlWriter );

    final Object rawContent =
        logicalPage.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.EXTRA_RAW_CONTENT );
    if ( rawContent != null ) {
      xmlWriter.writeText( String.valueOf( rawContent ) );
    }

    // table name
    if ( "true".equals( metaData.getConfiguration().getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.table.html.EnableSheetNameProcessing" ) ) ) {
      if ( sheetName != null ) {
        xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "h1", getTagHelper().createSheetNameAttributes(),
            XmlWriterSupport.OPEN );
        xmlWriter.writeTextNormalized( sheetName, true );
        xmlWriter.writeCloseTag();
      }
    }

    // table
    xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "table", getTagHelper().createTableAttributes( sheetLayout,
        logicalPage ), XmlWriterSupport.OPEN );
    writeColumnDeclaration( sheetLayout, xmlWriter );
  }

  protected void writeBackgroundCell( CellBackground background, XmlWriter xmlWriter ) throws IOException {
    final boolean emptyCellsUseCSS = getTagHelper().isEmptyCellsUseCSS();
    if ( background == null ) {
      if ( emptyCellsUseCSS ) {
        xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "td", XmlWriterSupport.CLOSE );
      } else {
        final AttributeList attrs = new AttributeList();
        attrs.setAttribute( HtmlPrinter.XHTML_NAMESPACE, "style", "font-size: 1pt" );
        xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "td", attrs, XmlWriterSupport.OPEN );
        xmlWriter.writeText( "&nbsp;" );
        xmlWriter.writeCloseTag();
      }
      return;
    }

    StyleBuilder styleBuilder = getStyleBuilder();
    DefaultStyleBuilderFactory styleBuilderFactory = getStyleBuilderFactory();

    // Background cannot be null at this point ..
    final String[] anchor = background.getAnchors();
    if ( anchor.length == 0 && emptyCellsUseCSS ) {
      final StyleBuilder cellStyle =
          styleBuilderFactory.createCellStyle( styleBuilder, null, null, background, null, null );
      final AttributeList cellAttributes =
          getTagHelper().createCellAttributes( 1, 1, null, null, background, cellStyle );
      xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "td", cellAttributes, XmlWriterSupport.CLOSE );
    } else {
      final StyleBuilder cellStyle =
          styleBuilderFactory.createCellStyle( styleBuilder, null, null, background, HtmlPrinter.EMPTY_CELL_ATTRNAMES,
              HtmlPrinter.EMPTY_CELL_ATTRVALS );
      final AttributeList cellAttributes =
          getTagHelper().createCellAttributes( 1, 1, null, null, background, cellStyle );
      xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "td", cellAttributes, XmlWriterSupport.OPEN );
      for ( int i = 0; i < anchor.length; i++ ) {
        xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, "a", "name", anchor[i], XmlWriterSupport.CLOSE );
      }
      xmlWriter.writeText( "&nbsp;" );
      xmlWriter.writeCloseTag();

    }

  }

}
