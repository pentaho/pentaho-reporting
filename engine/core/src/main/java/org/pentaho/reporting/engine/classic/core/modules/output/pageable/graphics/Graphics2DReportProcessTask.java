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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

import org.pentaho.reporting.engine.classic.core.AbstractReportProcessTask;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.GraphicsContentInterceptor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.GraphicsOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.StreamGraphicsOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.encoder.ImageEncoder;
import org.pentaho.reporting.libraries.base.encoder.ImageEncoderRegistry;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

public class Graphics2DReportProcessTask extends AbstractReportProcessTask {
  private static class ImageGeneratorInterceptor implements GraphicsContentInterceptor {
    private BufferedImage image;

    private ImageGeneratorInterceptor() {
    }

    public void processLogicalPage( final LogicalPageKey key, final PageDrawable page ) {
      final Dimension preferredSize = page.getPreferredSize();
      image = new BufferedImage( preferredSize.width, preferredSize.height, BufferedImage.TYPE_INT_ARGB );
      final Graphics2D g2 = image.createGraphics();
      page.draw( g2, new Rectangle2D.Double( 0, 0, preferredSize.width, preferredSize.height ) );
      g2.dispose();
    }

    public void processPhysicalPage( final PhysicalPageKey key, final PageDrawable page ) {
      // intentionally empty
    }

    public boolean isPhysicalPageAccepted( final PhysicalPageKey key ) {
      return false;
    }

    public boolean isLogicalPageAccepted( final LogicalPageKey key ) {
      return true;
    }

    public BufferedImage getImage() {
      return image;
    }
  }

  public Graphics2DReportProcessTask() {
  }

  /**
   * @noinspection ThrowableInstanceNeverThrown
   */
  public void run() {
    if ( isValid() == false ) {
      setError( new ReportProcessingException( "Error: The task is not configured properly." ) );
      return;
    }

    setError( null );
    try {
      final MasterReport masterReport = getReport();
      final Configuration configuration = masterReport.getConfiguration();
      final boolean alphaChannel =
          "true"
              .equals( configuration
                  .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.AlphaChannel" ) );
      final String mimeType = computeMimeType( configuration );
      final float quality =
          ParserUtil
              .parseFloat(
                  configuration
                      .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.Quality" ),
                  0.9f );

      final GraphicsOutputProcessor outputProcessor =
          new GraphicsOutputProcessor( new StreamGraphicsOutputProcessorMetaData(), masterReport.getResourceManager() );
      final StreamReportProcessor streamReportProcessor = new StreamReportProcessor( masterReport, outputProcessor );
      final ReportProgressListener[] progressListeners = getReportProgressListeners();
      for ( int i = 0; i < progressListeners.length; i++ ) {
        final ReportProgressListener listener = progressListeners[i];
        streamReportProcessor.addReportProgressListener( listener );
      }

      final ImageGeneratorInterceptor interceptor = new ImageGeneratorInterceptor();
      outputProcessor.setInterceptor( interceptor );
      streamReportProcessor.processReport();
      streamReportProcessor.close();

      final ContentLocation contentLocation = getBodyContentLocation();
      final NameGenerator nameGenerator = getBodyNameGenerator();
      final ContentItem contentItem = contentLocation.createItem( nameGenerator.generateName( null, mimeType ) );
      final BufferedImage image = interceptor.getImage();
      final ImageEncoder imageEncoder = ImageEncoderRegistry.getInstance().createEncoder( mimeType );
      final OutputStream outputStream = contentItem.getOutputStream();
      imageEncoder.encodeImage( image, outputStream, quality, alphaChannel );
      outputStream.close();
    } catch ( Throwable e ) {
      setError( e );
    }
  }

  protected String computeMimeType( final Configuration configuration ) {
    return configuration.getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.EncoderMime", "image/png" );
  }

  public String getReportMimeType() {
    final MasterReport masterReport = getReport();
    final Configuration configuration = masterReport.getConfiguration();
    return computeMimeType( configuration );
  }
}
