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
 * Copyright (c) 2001 - 2017 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.repository.file.FileRepository;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;
import org.pentaho.reporting.libraries.repository.zipwriter.ZipRepository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Utility class to provide an easy to use default implementation of html exports.
 *
 * @author Thomas Morgner
 */
public final class HtmlReportUtil {
  private static final Log logger = LogFactory.getLog( HtmlReportUtil.class );

  /**
   * DefaultConstructor.
   */
  private HtmlReportUtil() {
  }

  /**
   * Saves a report into a single HTML format.
   *
   * @param report
   *          the report.
   * @param filename
   *          target file name.
   * @throws ReportProcessingException
   *           if the report processing failed.
   * @throws java.io.IOException
   *           if there was an IOerror while processing the report.
   */
  public static void createStreamHTML( final MasterReport report, final String filename ) throws IOException,
    ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( filename == null ) {
      throw new NullPointerException();
    }
    final File file = new File( filename );
    final OutputStream fout = new BufferedOutputStream( new FileOutputStream( file ) );
    try {
      createStreamHTML( report, fout );
    } finally {
      fout.close();
    }
  }

  public static void createStreamHTML( final MasterReport report, final OutputStream outputStream )
    throws ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }
    final StreamRepository targetRepository = new StreamRepository( outputStream );
    final ContentLocation targetRoot = targetRepository.getRoot();

    final HtmlOutputProcessor outputProcessor = new StreamHtmlOutputProcessor( report.getConfiguration() );
    final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
    printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, "index", "html" ) );
    printer.setDataWriter( null, null );
    printer.setUrlRewriter( new FileSystemURLRewriter() );
    outputProcessor.setPrinter( printer );

    final StreamReportProcessor sp = new StreamReportProcessor( report, outputProcessor );
    sp.processReport();
    sp.close();
  }

  /**
   * Saves a report to HTML. The HTML file is stored in a directory; all other content goes into the same directory as
   * the specified html file. The parent directories for both the TargetFilename and the DataDirectoryName will be
   * created if necessary.
   * <p/>
   * When exporting a report with manual pagebreaks, the directory of the target-filename will contain more than one
   * result-HTML files after the export is complete.
   *
   * @param report
   *          the report.
   * @param targetFileName
   *          target file name.
   * @throws ReportProcessingException
   *           if the report processing failed.
   * @throws IOException
   *           if there was an IOerror while processing the report.
   */
  public static void createDirectoryHTML( final MasterReport report, final String targetFileName ) throws IOException,
    ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( targetFileName == null ) {
      throw new NullPointerException();
    }
    try {
      boolean isCreateParentFolder;
      final String createParentFolder =
        report.getConfiguration().getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.paged.CreateParentFolder" ); //$NON-NLS-1$
      if ( createParentFolder == null ) {
        isCreateParentFolder = false;
      } else {
        isCreateParentFolder = Boolean.parseBoolean( createParentFolder );
      }

      final File targetFile = new File( targetFileName ).getCanonicalFile();

      final File targetDirectory = targetFile.getParentFile();
      if ( isCreateParentFolder ) {
        if ( targetFile.exists() ) {
          // try to delete it ..
          if ( targetFile.delete() == false ) {
            throw new IOException( "Unable to remove the already existing target-file." );
          }
        }
        if ( targetDirectory.exists() == false ) {
          if ( targetDirectory.mkdirs() == false ) {
            throw new IOException( "Unable to create the target-directory." );
          }
        }
      }
      final FileRepository targetRepository = new FileRepository( targetDirectory );
      final ContentLocation targetRoot = targetRepository.getRoot();

      final String suffix = getSuffix( targetFileName );
      final String filename = IOUtils.getInstance().stripFileExtension( targetFile.getName() );

      final FlowHtmlOutputProcessor outputProcessor = new FlowHtmlOutputProcessor();

      final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
      printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, filename, suffix ) );
      printer.setDataWriter( targetRoot, new DefaultNameGenerator( targetRoot, "content" ) );
      printer.setUrlRewriter( new FileSystemURLRewriter() );
      outputProcessor.setPrinter( printer );

      final FlowReportProcessor sp = new FlowReportProcessor( report, outputProcessor );
      sp.processReport();
      sp.close();
    } catch ( ContentIOException e ) {
      throw new IOException( "Failed to get or create the repository-root." );
    }
  }

  /**
   * Saves a report to HTML. The HTML file is stored in a directory; all other content goes into the same directory as
   * the specified html file. The parent directories for both the TargetFilename and the DataDirectoryName will be
   * created if necessary.
   * <p/>
   * When exporting a report with manual pagebreaks, the directory of the target-filename will contain more than one
   * result-HTML files after the export is complete.
   *
   * @param report
   *          the report.
   * @param targetFileName
   *          target file name.
   * @throws ReportProcessingException
   *           if the report processing failed.
   * @throws IOException
   *           if there was an IOerror while processing the report.
   */
  public static void createDirectoryHTML( final MasterReport report, final String targetFileName,
      final String dataDirectoryName ) throws IOException, ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( targetFileName == null ) {
      throw new NullPointerException();
    }
    if ( dataDirectoryName == null ) {
      throw new NullPointerException();
    }
    try {
      final File targetFile = new File( targetFileName );
      if ( targetFile.exists() ) {
        // try to delete it ..
        if ( targetFile.delete() == false ) {
          throw new IOException( "Unable to remove the already existing target-file." );
        }
      }

      final File targetDirectory = targetFile.getParentFile().getCanonicalFile();
      if ( targetDirectory.exists() == false ) {
        if ( targetDirectory.mkdirs() == false ) {
          throw new IOException( "Unable to create the target-directory." );
        }
      }

      final File tempDataDir = new File( dataDirectoryName ).getCanonicalFile();
      File dataDirectory;
      if ( tempDataDir.isAbsolute() ) {
        dataDirectory = tempDataDir;
      } else {
        dataDirectory = new File( targetDirectory, dataDirectoryName ).getCanonicalFile();
      }
      if ( dataDirectory.exists() && dataDirectory.isDirectory() == false ) {
        dataDirectory = dataDirectory.getParentFile();
        if ( dataDirectory.isDirectory() == false ) {
          throw new ReportProcessingException( "DataDirectory is invalid: " + dataDirectory );
        }
      } else if ( dataDirectory.exists() == false ) {
        if ( dataDirectory.mkdirs() == false ) {
          throw new IOException( "Unable to create the data-directory." );
        }
      }

      final FileRepository targetRepository = new FileRepository( targetDirectory );
      final ContentLocation targetRoot = targetRepository.getRoot();

      final FileRepository dataRepository = new FileRepository( dataDirectory );
      final ContentLocation dataRoot = dataRepository.getRoot();

      final String suffix = getSuffix( targetFileName );
      final String filename = IOUtils.getInstance().stripFileExtension( targetFile.getName() );

      final FlowHtmlOutputProcessor outputProcessor = new FlowHtmlOutputProcessor();

      final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
      printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, filename, suffix ) );
      printer.setDataWriter( dataRoot, new DefaultNameGenerator( dataRoot, "content" ) );
      printer.setUrlRewriter( new FileSystemURLRewriter() );
      outputProcessor.setPrinter( printer );

      final FlowReportProcessor sp = new FlowReportProcessor( report, outputProcessor );
      sp.processReport();
      sp.close();
    } catch ( ContentIOException e ) {
      throw new IOException( "Failed to get repository-root." );
    }
  }

  private static String getSuffix( final String filename ) {
    final String suffix = IOUtils.getInstance().getFileExtension( filename );
    if ( suffix.length() == 0 ) {
      return "";
    }
    return suffix.substring( 1 );
  }

  /**
   * Saves a report in a ZIP file. The zip file contains a HTML document. The directory that contains the specified
   * filename must be created before this method is called.
   *
   * @param report
   *          the report.
   * @param filename
   *          target file name.
   * @throws ReportProcessingException
   *           if the report processing failed.
   * @throws IOException
   *           if there was an IOerror while processing the report.
   */
  public static void createZIPHTML( final MasterReport report, final String filename ) throws IOException,
    ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( filename == null ) {
      throw new NullPointerException();
    }

    OutputStream out = null;
    try {
      out = new BufferedOutputStream( new FileOutputStream( filename ) );
      createZIPHTML( report, out, "report" );
      out.close();
      out = null;
    } catch ( IOException ioe ) {
      throw ioe;
    } catch ( ReportProcessingException re ) {
      throw re;
    } catch ( Exception re ) {
      throw new ReportProcessingException( "Failed to process the report", re );
    } finally {
      try {
        if ( out != null ) {
          out.close();
        }
      } catch ( Exception e ) {
        logger.error( "Unable to close the output stream.", e );
      }
    }
  }

  /**
   * Saves a report in a ZIP file. The zip file contains a HTML document. The directory that contains the specified
   * filename must be created before this method is called.
   *
   * @param report
   *          the report.
   * @param filename
   *          target file name.
   * @throws ReportProcessingException
   *           if the report processing failed.
   * @throws IOException
   *           if there was an IOerror while processing the report.
   */
  public static void createZIPHTML( final MasterReport report, final OutputStream out, final String filename )
    throws IOException, ReportProcessingException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( filename == null ) {
      throw new NullPointerException();
    }
    if ( out == null ) {
      throw new NullPointerException();
    }

    try {
      final ZipRepository zipRepository = new ZipRepository( out );
      final ContentLocation root = zipRepository.getRoot();
      final ContentLocation data =
          RepositoryUtilities.createLocation( zipRepository, RepositoryUtilities.splitPath( "data", "/" ) );

      final FlowHtmlOutputProcessor outputProcessor = new FlowHtmlOutputProcessor();

      final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
      printer.setContentWriter( root, new DefaultNameGenerator( root, filename ) );
      printer.setDataWriter( data, new DefaultNameGenerator( data, "content" ) );
      printer.setUrlRewriter( new SingleRepositoryURLRewriter() );
      outputProcessor.setPrinter( printer );

      final FlowReportProcessor sp = new FlowReportProcessor( report, outputProcessor );
      sp.processReport();
      sp.close();
      zipRepository.close();
    } catch ( IOException ioe ) {
      throw ioe;
    } catch ( ReportProcessingException re ) {
      throw re;
    } catch ( Exception re ) {
      throw new ReportProcessingException( "Failed to process the report", re );
    }
  }

}
