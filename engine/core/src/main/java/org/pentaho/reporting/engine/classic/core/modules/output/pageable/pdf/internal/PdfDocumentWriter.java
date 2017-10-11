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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineInfo;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.PhysicalPageDrawable;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfPageableModule;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

@SuppressWarnings( "HardCodedStringLiteral" )
public class PdfDocumentWriter {
  private static final Log logger = LogFactory.getLog( PdfDocumentWriter.class );

  /**
   * A useful constant for specifying the PDF creator.
   */
  private static final String CREATOR = ClassicEngineInfo.getInstance().getName() + " version "
      + ClassicEngineInfo.getInstance().getVersion();

  /**
   * A bytearray containing an empty password. iText replaces the owner password with random values, but Adobe allows to
   * have encryption without an owner password set. Copied from iText
   */
  private static final byte[] PDF_PASSWORD_PAD = { (byte) 0x28, (byte) 0xBF, (byte) 0x4E, (byte) 0x5E, (byte) 0x4E,
    (byte) 0x75, (byte) 0x8A, (byte) 0x41, (byte) 0x64, (byte) 0x00, (byte) 0x4E, (byte) 0x56, (byte) 0xFF,
    (byte) 0xFA, (byte) 0x01, (byte) 0x08, (byte) 0x2E, (byte) 0x2E, (byte) 0x00, (byte) 0xB6, (byte) 0xD0,
    (byte) 0x68, (byte) 0x3E, (byte) 0x80, (byte) 0x2F, (byte) 0x0C, (byte) 0xA9, (byte) 0xFE, (byte) 0x64,
    (byte) 0x53, (byte) 0x69, (byte) 0x7A };

  private Document document;
  private PdfOutputProcessorMetaData metaData;
  private OutputStream out;
  private PdfWriter writer;
  private boolean awaitOpenDocument;
  private Configuration config;
  private ResourceManager resourceManager;
  private LFUMap<ResourceKey, com.lowagie.text.Image> imageCache;
  private char version;

  public PdfDocumentWriter( final PdfOutputProcessorMetaData metaData, final OutputStream out,
      final ResourceManager resourceManager ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    if ( out == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }

    this.imageCache = new LFUMap<ResourceKey, com.lowagie.text.Image>( 50 );
    this.resourceManager = resourceManager;
    this.metaData = metaData;
    this.out = out;
    this.config = metaData.getConfiguration();
  }

  private Document getDocument() {
    return document;
  }

  public void open() throws DocumentException {
    this.document = new Document();
    // pageSize, marginLeft, marginRight, marginTop, marginBottom));

    writer = PdfWriter.getInstance( document, out );
    writer.setLinearPageMode();

    version = getVersion();
    writer.setPdfVersion( version );
    writer.setViewerPreferences( getViewerPreferences() );

    final String encrypt =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Encryption" );

    if ( encrypt != null ) {
      if ( encrypt.equals( PdfPageableModule.SECURITY_ENCRYPTION_128BIT ) == true
          || encrypt.equals( PdfPageableModule.SECURITY_ENCRYPTION_40BIT ) == true ) {
        final String userpassword =
            config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.UserPassword" );
        final String ownerpassword =
            config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.OwnerPassword" );
        // Log.debug ("UserPassword: " + userpassword + " - OwnerPassword: " + ownerpassword);
        final byte[] userpasswordbytes = DocWriter.getISOBytes( userpassword );
        byte[] ownerpasswordbytes = DocWriter.getISOBytes( ownerpassword );
        if ( ownerpasswordbytes == null ) {
          ownerpasswordbytes = PdfDocumentWriter.PDF_PASSWORD_PAD;
        }
        final int encryptionType;
        if ( encrypt.equals( PdfPageableModule.SECURITY_ENCRYPTION_128BIT ) ) {
          encryptionType = PdfWriter.STANDARD_ENCRYPTION_128;
        } else {
          encryptionType = PdfWriter.STANDARD_ENCRYPTION_40;
        }
        writer.setEncryption( userpasswordbytes, ownerpasswordbytes, getPermissions(), encryptionType );
      }
    }

    /**
     * MetaData can be set when the writer is registered to the document.
     */
    final String title =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Title", config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.metadata.Title" ) );
    final String subject =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Description",
            config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.metadata.Description" ) );
    final String author =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Author",
            config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.metadata.Author" ) );
    final String keyWords =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Keywords",
            config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.metadata.Keywords" ) );

    if ( title != null ) {
      document.addTitle( title );
    }
    if ( author != null ) {
      document.addAuthor( author );
    }
    if ( keyWords != null ) {
      document.addKeywords( keyWords );
    }
    if ( keyWords != null ) {
      document.addSubject( subject );
    }

    document.addCreator( PdfDocumentWriter.CREATOR );
    document.addCreationDate();

    // getDocument().open();
    awaitOpenDocument = true;
  }

  /**
   * Extracts the Page Layout and page mode settings for this PDF (ViewerPreferences). All preferences are defined as
   * properties which have to be set before the target is opened.
   *
   * @return the ViewerPreferences.
   */
  private int getViewerPreferences() {
    final String pageLayout =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PageLayout" );
    final String pageMode =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PageMode" );
    final String fullScreenMode =
        config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.FullScreenMode" );
    final boolean hideToolBar =
        "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.HideToolBar" ) );
    final boolean hideMenuBar =
        "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.HideMenuBar" ) );
    final boolean hideWindowUI =
        "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.HideWindowUI" ) );
    final boolean fitWindow =
        "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.FitWindow" ) );
    final boolean centerWindow =
        "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.CenterWindow" ) );
    final boolean displayDocTitle =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.DisplayDocTitle" ) );
    final boolean printScalingNone =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PrintScalingNone" ) );
    final String direction =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Direction" );

    int viewerPreferences = 0;
    if ( "PageLayoutOneColumn".equals( pageLayout ) ) {
      viewerPreferences = PdfWriter.PageLayoutOneColumn;
    } else if ( "PageLayoutSinglePage".equals( pageLayout ) ) {
      viewerPreferences = PdfWriter.PageLayoutSinglePage;
    } else if ( "PageLayoutTwoColumnLeft".equals( pageLayout ) ) {
      viewerPreferences = PdfWriter.PageLayoutTwoColumnLeft;
    } else if ( "PageLayoutTwoColumnRight".equals( pageLayout ) ) {
      viewerPreferences = PdfWriter.PageLayoutTwoColumnRight;
    } else if ( "PageLayoutTwoPageLeft".equals( pageLayout ) ) {
      viewerPreferences = PdfWriter.PageLayoutTwoPageLeft;
    } else if ( "PageLayoutTwoPageRight".equals( pageLayout ) ) {
      viewerPreferences = PdfWriter.PageLayoutTwoPageRight;
    }

    if ( "PageModeUseNone".equals( pageMode ) ) {
      viewerPreferences |= PdfWriter.PageModeUseNone;
    } else if ( "PageModeUseOutlines".equals( pageMode ) ) {
      viewerPreferences |= PdfWriter.PageModeUseOutlines;
    } else if ( "PageModeUseThumbs".equals( pageMode ) ) {
      viewerPreferences |= PdfWriter.PageModeUseThumbs;
    } else if ( "PageModeFullScreen".equals( pageMode ) ) {
      viewerPreferences |= PdfWriter.PageModeFullScreen;
      if ( "NonFullScreenPageModeUseNone".equals( fullScreenMode ) ) {
        viewerPreferences = PdfWriter.NonFullScreenPageModeUseNone;
      } else if ( "NonFullScreenPageModeUseOC".equals( fullScreenMode ) ) {
        viewerPreferences |= PdfWriter.NonFullScreenPageModeUseOC;
      } else if ( "NonFullScreenPageModeUseOutlines".equals( fullScreenMode ) ) {
        viewerPreferences |= PdfWriter.NonFullScreenPageModeUseOutlines;
      } else if ( "NonFullScreenPageModeUseThumbs".equals( fullScreenMode ) ) {
        viewerPreferences |= PdfWriter.NonFullScreenPageModeUseThumbs;
      }
    } else if ( "PageModeUseOC".equals( pageMode ) ) {
      viewerPreferences |= PdfWriter.PageModeUseOC;
    } else if ( "PageModeUseAttachments".equals( pageMode ) ) {
      viewerPreferences |= PdfWriter.PageModeUseAttachments;
    }

    if ( hideToolBar ) {
      viewerPreferences |= PdfWriter.HideToolbar;
    }

    if ( hideMenuBar ) {
      viewerPreferences |= PdfWriter.HideMenubar;
    }

    if ( hideWindowUI ) {
      viewerPreferences |= PdfWriter.HideWindowUI;
    }

    if ( fitWindow ) {
      viewerPreferences |= PdfWriter.FitWindow;
    }
    if ( centerWindow ) {
      viewerPreferences |= PdfWriter.CenterWindow;
    }
    if ( displayDocTitle ) {
      viewerPreferences |= PdfWriter.DisplayDocTitle;
    }
    if ( printScalingNone ) {
      viewerPreferences |= PdfWriter.PrintScalingNone;
    }

    if ( "DirectionL2R".equals( direction ) ) {
      viewerPreferences |= PdfWriter.DirectionL2R;
    } else if ( "DirectionR2L".equals( direction ) ) {
      viewerPreferences |= PdfWriter.DirectionR2L;
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug( "viewerPreferences = 0b" + Integer.toBinaryString( viewerPreferences ) );
    }
    return viewerPreferences;
  }

  public void processPhysicalPage( final PageGrid pageGrid, final LogicalPageBox logicalPage, final int row,
      final int col, final PhysicalPageKey pageKey ) throws DocumentException {
    final PhysicalPageBox page = pageGrid.getPage( row, col );
    if ( page == null ) {
      return;
    }

    final float width = (float) StrictGeomUtility.toExternalValue( page.getWidth() );
    final float height = (float) StrictGeomUtility.toExternalValue( page.getHeight() );

    final Rectangle pageSize = new Rectangle( width, height );

    final float marginLeft = (float) StrictGeomUtility.toExternalValue( page.getImageableX() );
    final float marginRight =
        (float) StrictGeomUtility.toExternalValue( page.getWidth() - page.getImageableWidth() - page.getImageableX() );
    final float marginTop = (float) StrictGeomUtility.toExternalValue( page.getImageableY() );
    final float marginBottom =
        (float) StrictGeomUtility.toExternalValue( page.getHeight() - page.getImageableHeight() - page.getImageableY() );

    final Document document = getDocument();
    document.setPageSize( pageSize );
    document.setMargins( marginLeft, marginRight, marginTop, marginBottom );

    if ( awaitOpenDocument ) {
      document.open();
      awaitOpenDocument = false;
    }

    final PdfContentByte directContent = writer.getDirectContent();
    final Graphics2D graphics = new PdfGraphics2D( directContent, width, height, metaData );
    final PdfLogicalPageDrawable logicalPageDrawable = createLogicalPageDrawable( logicalPage, page );
    final PhysicalPageDrawable drawable = createPhysicalPageDrawable( logicalPageDrawable, page );
    drawable.draw( graphics, new Rectangle2D.Double( 0, 0, width, height ) );

    graphics.dispose();

    document.newPage();
  }

  protected PhysicalPageDrawable createPhysicalPageDrawable( final PdfLogicalPageDrawable logicalPageDrawable,
      final PhysicalPageBox page ) {
    return new PhysicalPageDrawable( logicalPageDrawable, page );
  }

  public void processLogicalPage( final LogicalPageKey key, final LogicalPageBox logicalPage ) throws DocumentException {

    final float width = (float) StrictGeomUtility.toExternalValue( logicalPage.getPageWidth() );
    final float height = (float) StrictGeomUtility.toExternalValue( logicalPage.getPageHeight() );

    final Rectangle pageSize = new Rectangle( width, height );

    final Document document = getDocument();
    document.setPageSize( pageSize );
    document.setMargins( 0, 0, 0, 0 );

    if ( awaitOpenDocument ) {
      document.open();
      awaitOpenDocument = false;
    }

    final Graphics2D graphics = new PdfGraphics2D( writer.getDirectContent(), width, height, metaData );
    // and now process the box ..
    final PdfLogicalPageDrawable logicalPageDrawable = createLogicalPageDrawable( logicalPage, null );
    logicalPageDrawable.draw( graphics, new Rectangle2D.Double( 0, 0, width, height ) );

    graphics.dispose();

    document.newPage();
  }

  protected PdfOutputProcessorMetaData getMetaData() {
    return metaData;
  }

  protected PdfWriter getWriter() {
    return writer;
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }

  public LFUMap<ResourceKey, Image> getImageCache() {
    return imageCache;
  }

  protected PdfLogicalPageDrawable createLogicalPageDrawable( final LogicalPageBox logicalPage,
      final PhysicalPageBox page ) {
    final PdfLogicalPageDrawable drawable = new PdfLogicalPageDrawable( getWriter(), getImageCache(), getVersion() );
    drawable.init( logicalPage, getMetaData(), getResourceManager(), page );
    return drawable;
  }

  /**
   * Closes the document.
   */
  public void close() {
    this.getDocument().close();
    try {
      this.out.flush();
    } catch ( IOException e ) {
      PdfDocumentWriter.logger.info( "Flushing the PDF-Export-Stream failed." );
    }
    this.document = null;
    this.writer = null;
  }

  /**
   * Extracts the to be generated PDF version as iText parameter from the given property value. The value has the form
   * "1.x" where x is the extracted version.
   *
   * @return the itext character defining the version.
   */
  protected char getVersion() {
    final String version =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Version" );

    if ( version == null ) {
      return '5';
    }
    if ( version.length() < 3 ) {
      PdfDocumentWriter.logger.warn( "PDF version specification is invalid, using default version '1.4'." );
      return '5';
    }
    final char retval = version.charAt( 2 );
    if ( retval < '2' || retval > '9' ) {
      PdfDocumentWriter.logger.warn( "PDF version specification is invalid, using default version '1.4'." );
      return '5';
    }
    return retval;
  }

  /**
   * Extracts the permissions for this PDF. The permissions are returned as flags in the integer value. All permissions
   * are defined as properties which have to be set before the target is opened.
   *
   * @return the permissions.
   */
  private int getPermissions() {
    final String printLevel =
        config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PrintLevel" );

    final boolean allowPrinting = "none".equals( printLevel ) == false;
    final boolean allowDegradedPrinting = "degraded".equals( printLevel );

    final boolean allowModifyContents =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowModifyContents" ) );
    final boolean allowModifyAnn =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowModifyAnnotations" ) );

    final boolean allowCopy =
        "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowCopy" ) );
    final boolean allowFillIn =
        "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowFillIn" ) );
    final boolean allowScreenReaders =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowScreenReader" ) );
    final boolean allowAssembly =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AllowAssembly" ) );

    int permissions = 0;
    if ( allowPrinting ) {
      permissions |= PdfWriter.ALLOW_PRINTING;
    }
    if ( allowModifyContents ) {
      permissions |= PdfWriter.ALLOW_MODIFY_CONTENTS;
    }
    if ( allowModifyAnn ) {
      permissions |= PdfWriter.ALLOW_MODIFY_ANNOTATIONS;
    }
    if ( allowCopy ) {
      permissions |= PdfWriter.ALLOW_COPY;
    }
    if ( allowFillIn ) {
      permissions |= PdfWriter.ALLOW_FILL_IN;
    }
    if ( allowScreenReaders ) {
      permissions |= PdfWriter.ALLOW_SCREENREADERS;
    }
    if ( allowAssembly ) {
      permissions |= PdfWriter.ALLOW_ASSEMBLY;
    }
    if ( allowDegradedPrinting ) {
      permissions |= PdfWriter.ALLOW_DEGRADED_PRINTING;
    }
    return permissions;
  }

}
