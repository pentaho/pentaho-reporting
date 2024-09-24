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

package org.pentaho.reporting.engine.classic.extensions.modules.java14print;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.print.PrintUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaName;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

/**
 * Creation-Date: 05.09.2005, 19:11:47
 *
 * @author Thomas Morgner
 */
public class Java14PrintUtil {
  private static final Log logger = LogFactory.getLog( Java14PrintUtil.class );

  public static final int CONFIGURATION_VALID = 0;
  public static final int CONFIGURATION_REPAGINATE = 1;
  public static final int CONFIGURATION_SHOW_DIALOG = 2;
  private static final double POINTS_PER_INCH = 72.0;

  private Java14PrintUtil() {
  }

  /**
   * This tests, whether the given attribute set defines the same page properties as the given JFreeReport object.
   * <p/>
   * While showing the print dialog, the user has the chance to alter the page format of the print job. When that
   * happens, we have to repaginate the whole report, which may render the users page range input invalid. In that case,
   * we will have to redisplay the dialog.
   *
   * @param attributes
   * @param report
   * @return
   */
  public static int isValidConfiguration( final PrintRequestAttributeSet attributes, final MasterReport report ) {
    final PrintRequestAttributeSet reportAttributes = copyConfiguration( null, report );
    // now, compare that minimal set with the given attribute collection.

    final Attribute[] printAttribs = reportAttributes.toArray();
    boolean invalidConfig = false;
    for ( int i = 0; i < printAttribs.length; i++ ) {
      final Attribute attrib = printAttribs[i];
      if ( attributes.containsValue( attrib ) == false ) {
        invalidConfig = true;
        break;
      }
    }

    if ( invalidConfig == false ) {
      return CONFIGURATION_VALID;
    }
    if ( attributes.containsKey( PageRanges.class ) ) {
      return CONFIGURATION_SHOW_DIALOG;
    }
    return CONFIGURATION_REPAGINATE;
  }

  /**
   * This method replaces the media definition from the given attribute set with the one found in the report itself.
   * <p/>
   * If no JobName is set, a default jobname will be assigned.
   *
   * @param attributes
   * @param report
   * @return
   */
  public static PrintRequestAttributeSet copyConfiguration( PrintRequestAttributeSet attributes,
      final MasterReport report ) {
    if ( attributes == null ) {
      attributes = new HashPrintRequestAttributeSet();
    }

    // for now, be lazy, assume that the first page is the reference
    final PageDefinition pdef = report.getPageDefinition();
    final PageFormat format = pdef.getPageFormat( 0 );
    final Paper paper = format.getPaper();

    final Media media =
        MediaSize.findMedia( (float) ( paper.getWidth() / POINTS_PER_INCH ),
            (float) ( paper.getHeight() / POINTS_PER_INCH ), Size2DSyntax.INCH );
    attributes.add( media );

    final MediaPrintableArea printableArea =
        new MediaPrintableArea( (float) ( paper.getImageableX() / POINTS_PER_INCH ),
            (float) ( paper.getImageableY() / POINTS_PER_INCH ),
            (float) ( paper.getImageableWidth() / POINTS_PER_INCH ),
            (float) ( paper.getImageableHeight() / POINTS_PER_INCH ), Size2DSyntax.INCH );

    attributes.add( printableArea );
    attributes.add( mapOrientation( format.getOrientation() ) );

    return attributes;
  }

  public static PrintRequestAttributeSet copyAuxillaryAttributes( PrintRequestAttributeSet attributes,
      final MasterReport report ) {
    if ( attributes == null ) {
      attributes = new HashPrintRequestAttributeSet();
    }

    if ( attributes.containsKey( JobName.class ) == false ) {
      final String jobName =
          report.getReportConfiguration().getConfigProperty( PrintUtil.PRINTER_JOB_NAME_KEY, report.getTitle() );
      if ( jobName != null ) {
        attributes.add( new JobName( jobName, null ) );
      }
    }
    if ( attributes.containsKey( Copies.class ) == false ) {
      final int numberOfCopies = PrintUtil.getNumberOfCopies( report.getReportConfiguration() );
      attributes.add( new Copies( numberOfCopies ) );
    }

    return attributes;
  }

  public static PageFormat extractPageFormat( final PrintRequestAttributeSet attributeSet ) {
    final Media media = (Media) attributeSet.get( Media.class );
    final MediaPrintableArea printableArea = (MediaPrintableArea) attributeSet.get( MediaPrintableArea.class );
    final OrientationRequested orientationRequested =
        (OrientationRequested) attributeSet.get( OrientationRequested.class );

    final MediaSize mediaSize = lookupMediaSize( media );
    if ( mediaSize == null ) {
      logger.warn( "Unknown media encountered, unable to compute page sizes." );
    }

    final PageFormat pageFormat = new PageFormat();
    pageFormat.setPaper( createPaper( mediaSize, printableArea ) );
    if ( OrientationRequested.PORTRAIT.equals( orientationRequested ) ) {
      pageFormat.setOrientation( PageFormat.PORTRAIT );
    } else if ( OrientationRequested.LANDSCAPE.equals( orientationRequested ) ) {
      pageFormat.setOrientation( PageFormat.LANDSCAPE );
    } else if ( OrientationRequested.REVERSE_LANDSCAPE.equals( orientationRequested ) ) {
      pageFormat.setOrientation( PageFormat.REVERSE_LANDSCAPE );
    } else if ( OrientationRequested.REVERSE_PORTRAIT.equals( orientationRequested ) ) {
      pageFormat.setOrientation( PageFormat.PORTRAIT );
    }
    return pageFormat;
  }

  private static Paper createPaper( final MediaSize mediaSize, final MediaPrintableArea printableArea ) {
    final Paper paper = new Paper();
    if ( mediaSize != null ) {
      paper.setSize( mediaSize.getX( Size2DSyntax.INCH ) * POINTS_PER_INCH, mediaSize.getY( Size2DSyntax.INCH )
          * POINTS_PER_INCH );
    }
    if ( printableArea != null ) {
      paper.setImageableArea( printableArea.getX( Size2DSyntax.INCH ) * POINTS_PER_INCH, printableArea
          .getY( Size2DSyntax.INCH )
          * POINTS_PER_INCH, printableArea.getWidth( Size2DSyntax.INCH ) * POINTS_PER_INCH, printableArea
          .getHeight( Size2DSyntax.INCH )
          * POINTS_PER_INCH );
    }
    return paper;
  }

  private static MediaSize lookupMediaSize( final Media media ) {

    if ( media instanceof MediaSizeName ) {
      return MediaSize.getMediaSizeForName( (MediaSizeName) media );
    } else if ( media instanceof MediaName ) {
      if ( media.equals( MediaName.ISO_A4_TRANSPARENT ) || media.equals( MediaName.ISO_A4_WHITE ) ) {
        return MediaSize.getMediaSizeForName( MediaSizeName.ISO_A4 );
      } else if ( media.equals( MediaName.NA_LETTER_TRANSPARENT ) || media.equals( MediaName.NA_LETTER_WHITE ) ) {
        return MediaSize.getMediaSizeForName( MediaSizeName.NA_LETTER );
      }
    }
    return null;
  }

  private static OrientationRequested mapOrientation( final int orientation ) {
    switch ( orientation ) {
      case PageFormat.LANDSCAPE:
        return OrientationRequested.LANDSCAPE;
      case PageFormat.REVERSE_LANDSCAPE:
        return OrientationRequested.REVERSE_LANDSCAPE;
      case PageFormat.PORTRAIT:
        return OrientationRequested.PORTRAIT;
      default:
        throw new IllegalArgumentException( "The given value is no valid PageFormat orientation." );
    }
  }

  public static void printDirectly( final MasterReport report, PrintService printService ) throws PrintException,
    ReportProcessingException {
    // with that method we do not use the PrintService UI ..
    // it is up to the user to supply a valid print service that
    // supports the Pageable printing.
    if ( printService == null ) {
      printService = lookupPrintService();
    } else {
      if ( printService.isDocFlavorSupported( DocFlavor.SERVICE_FORMATTED.PAGEABLE ) == false ) {
        throw new PrintException( "The print service implementation does not support the Pageable Flavor." );
      }
    }

    PrintRequestAttributeSet attributes = Java14PrintUtil.copyConfiguration( null, report );
    attributes = Java14PrintUtil.copyAuxillaryAttributes( attributes, report );

    final PrintReportProcessor reportPane = new PrintReportProcessor( report );
    final DocPrintJob job = printService.createPrintJob();
    final SimpleDoc document = new SimpleDoc( reportPane, DocFlavor.SERVICE_FORMATTED.PAGEABLE, null );

    try {
      job.print( document, attributes );
    } finally {
      reportPane.close();
    }

  }

  private static PrintService lookupPrintService() throws PrintException {
    final PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
    if ( defaultService != null && defaultService.isDocFlavorSupported( DocFlavor.SERVICE_FORMATTED.PAGEABLE ) ) {
      return defaultService;
    }

    final PrintService printService;
    final PrintService[] services = PrintServiceLookup.lookupPrintServices( DocFlavor.SERVICE_FORMATTED.PAGEABLE, null );
    if ( services.length == 0 ) {
      throw new PrintException( "Unable to find a matching print service implementation." );
    }
    printService = services[0];
    return printService;
  }

  public static boolean print( final MasterReport report ) throws PrintException, ReportProcessingException {
    return print( report, null );
  }

  public static boolean print( final MasterReport report, final ReportProgressListener progressListener )
    throws PrintException, ReportProcessingException {
    final PrintService[] services = PrintServiceLookup.lookupPrintServices( DocFlavor.SERVICE_FORMATTED.PAGEABLE, null );
    if ( services.length == 0 ) {
      throw new PrintException( "Unable to find a matching print service implementation." );
    }
    PrintRequestAttributeSet attributes = Java14PrintUtil.copyConfiguration( null, report );
    attributes = Java14PrintUtil.copyAuxillaryAttributes( attributes, report );

    final PrintService service =
        ServiceUI.printDialog( null, 50, 50, services, lookupPrintService(), DocFlavor.SERVICE_FORMATTED.PAGEABLE,
            attributes );
    if ( service == null ) {
      return false;
    }

    final PrintReportProcessor reportPane = new PrintReportProcessor( report );
    if ( progressListener != null ) {
      reportPane.addReportProgressListener( progressListener );
    }

    try {
      reportPane.fireProcessingStarted();

      final DocPrintJob job = service.createPrintJob();
      final SimpleDoc document = new SimpleDoc( reportPane, DocFlavor.SERVICE_FORMATTED.PAGEABLE, null );

      job.print( document, attributes );
    } finally {
      reportPane.fireProcessingFinished();
      reportPane.close();

      if ( progressListener != null ) {
        reportPane.removeReportProgressListener( progressListener );
      }
    }
    return true;
  }
}
