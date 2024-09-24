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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

/**
 * @author Gretchen Moran
 */
public class TransformationDatasourceMetadata {
  private static final String DATASOURCE_DIRECTORY = "datasources";

  private static class DirectoryFileFilter implements FileFilter {
    private DirectoryFileFilter() {
    }

    public boolean accept( final File file ) {
      if ( file.getName().length() > 0 && file.getName().charAt( 0 ) == '.' ) {
        return false;
      }
      return file.isDirectory();
    }
  }

  private static final Log logger = LogFactory.getLog( TransformationDatasourceMetadata.class );
  private static final DirectoryFileFilter DIRECTORY_FILTER = new DirectoryFileFilter();
  private static final FilesystemFilter TEMPLATE_FILES_FILTER =
    new FilesystemFilter( new String[] { ".ktr" }, "", false );// NON-NLS

  public static void registerDatasources() throws ReportDataFactoryException {

    final ResourceManager resourceManager = new ResourceManager();
    final URL templateLocation =
      TransformationDatasourceMetadata.class.getClassLoader().getResource( DATASOURCE_DIRECTORY );
    if ( templateLocation != null ) {
      try {
        final File templateDir = new File( templateLocation.toURI() );
        if ( templateDir.exists() && templateDir.isDirectory() ) {
          processDirectory( templateDir, resourceManager );
        }

      } catch ( URISyntaxException e ) {
        logger
          .error( "Not able to access location of datasource templates. Some datasources may not be available.", e );
        throw new ReportDataFactoryException( "Templated datasource not available.", e );
      }

    } else {
      // Not a big deal; maybe this install doesn't have templated datasources
      logger.debug( "No datasource template directory found. No templated datasources will be available." );
    }
  }

  /**
   * Creates a list of datasources from the templates located in the /datasources directory.
   */
  private static void processDirectory( final File dir,
                                        final ResourceManager resourceManager ) {
    try {
      final File[] dirs = dir.listFiles( DIRECTORY_FILTER );
      if ( dirs == null ) {
        return;
      }
      for ( final File f : dirs ) {
        processDirectory( f, resourceManager );
      }

      final File[] templatesArray = dir.listFiles( TEMPLATE_FILES_FILTER );
      if ( templatesArray == null ) {
        return;
      }

      Arrays.sort( templatesArray );
      for ( final File f : templatesArray ) {
        final String absolutePath = f.getAbsolutePath();
        try {
          // TODO: the plugin ID should be preserved even if the template name changes.. not the case today.
          // Ta;kl with Instaview team and Matt C. regarding best place to impose a template ID
          final InputStream in = new BufferedInputStream( new FileInputStream( f ) );
          try {
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            IOUtils.getInstance().copyStreams( in, bout );

            final String possiblePluginId = absolutePath.substring(
              absolutePath.indexOf( DATASOURCE_DIRECTORY ),
              absolutePath.length() );

            DataFactoryRegistry.getInstance().register( new EmbeddedKettleDataFactoryMetaData
              ( possiblePluginId, f.getName().replace( ".ktr", "" ), bout.toByteArray() ) );

            if ( logger.isDebugEnabled() ) {
              logger.debug( "Datasource metadata successfully registered: ".concat( absolutePath ) );
            }
          } finally {
            in.close();
          }
        } catch ( IOException ioe ) {
          logger.warn( "Error reading template file: " + absolutePath, ioe );
        }
      }

    } catch ( Exception se ) {
      logger.error( "Cannot access datasource template directory", se );// NON-NLS
    }
  }

}
