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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ClassicEngineFactoryParameters;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundleUtils;
import org.pentaho.reporting.libraries.repository.DefaultMimeRegistry;
import org.pentaho.reporting.libraries.repository.MimeRegistry;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.loader.raw.RawResourceLoader;

/**
 * A handler that writes resources into the document bundle based on the resource keys found inside the report
 * definition.
 *
 * @author David Kincade
 */
public class ResourceWriter implements BundleWriterHandler {
  // The logger used in this class
  private static final Log log = LogFactory.getLog( ResourceWriter.class );

  public ResourceWriter() {
  }

  /**
   * Returns a relatively low processing order indicating this BundleWriterHandler should be one of the first processed.
   * This is due to the fact that any Resource which is writen into the DocumentBundle will cause the report's
   * definition to be modified with a new Resource Key.
   *
   * @return the relative processing order for this BundleWriterHandler
   */
  public int getProcessingOrder() {
    return 0;
  }

  /**
   * Writes a certain aspect into a own file. The name of file inside the bundle is returned as string. The file name
   * returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler did not
   * generate a file on its own, it should return null.
   *
   * @param bundle
   *          the bundle where to write to.
   * @param state
   *          the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws IOException
   *           if any error occured
   * @throws BundleWriterException
   *           if a bundle-management error occured.
   */
  public String writeReport( final WriteableDocumentBundle bundle, final BundleWriterState state ) throws IOException,
    BundleWriterException {
    BundleUtilities.copyStickyInto( bundle, state.getGlobalBundle() );

    // Process all nodes starting at the top
    processSection( bundle, state.getMasterReport(), state.getMasterReport() );

    // Don't return anything ... we may have created none-or-more files
    return null;
  }

  private void processSection( final WriteableDocumentBundle documentBundle, final MasterReport report,
      final Section section ) throws BundleWriterException {
    final int count = section.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      final ReportElement element = section.getElement( i );
      if ( element instanceof Section ) {
        processSection( documentBundle, report, (Section) element );
      }

      if ( element instanceof RootLevelBand ) {
        final RootLevelBand rl = (RootLevelBand) element;
        final SubReport[] reports = rl.getSubReports();
        for ( int j = 0; j < reports.length; j++ ) {
          final SubReport subReport = reports[j];
          processSection( documentBundle, report, subReport );
        }
      }

      // Process the attributes if they are a resource key
      final ElementMetaData metaData = element.getMetaData();
      final AttributeMetaData[] attributeDescriptions = metaData.getAttributeDescriptions();
      for ( int j = 0; j < attributeDescriptions.length; j++ ) {
        final AttributeMetaData attributeDescription = attributeDescriptions[j];
        if ( "Resource".equals( attributeDescription.getValueRole() ) == false ) {
          continue;
        }

        final Object attribute =
            element.getAttribute( attributeDescription.getNameSpace(), attributeDescription.getName() );
        if ( attribute instanceof ResourceKey == false ) {
          continue;
        }

        final ResourceKey resourceKey = (ResourceKey) attribute;
        final ResourceKey replacementKey = processResourceKeyAttribute( documentBundle, report, resourceKey );
        if ( replacementKey != null ) {
          element.setAttribute( attributeDescription.getNameSpace(), attributeDescription.getName(), replacementKey );
        }
      }
    }
  }

  /**
   * Processes the resource key to see if it refers to a resource which should be embedded. If it should be embedded,
   * the data will be embedded and a replacement resource key will be generated.
   *
   * @param documentBundle
   *          the document bundle in which the resources will be embedded
   * @param resourceKey
   *          the resource key which may refer to a resource which should be embedded
   * @return a resource key which should replace the key passed in
   * @throws BundleWriterException
   *           indicates an error trying to embed the resource into the document bundle
   */
  private static ResourceKey processResourceKeyAttribute( final WriteableDocumentBundle documentBundle,
      final MasterReport report, final ResourceKey resourceKey ) throws BundleWriterException {
    // See if this key is already embedded
    if ( documentBundle.isEmbeddedKey( resourceKey ) ) {
      return null;
    }

    final boolean embedded = isEmbeddedKey( report, resourceKey );
    final DefaultMimeRegistry mimeRegistry = new DefaultMimeRegistry();

    // Determine if this key should be embedded
    final Map factoryParameters = resourceKey.getFactoryParameters();
    if ( embedded == false && "true".equals( factoryParameters.get( ClassicEngineFactoryParameters.EMBED ) ) == false
        && RawResourceLoader.SCHEMA_NAME.equals( resourceKey.getSchema() ) == false ) {
      return null;
    }

    try {
      // Embed the key into the document bundle
      String mimeType = (String) factoryParameters.get( ClassicEngineFactoryParameters.MIME_TYPE );
      final String originalValue = (String) factoryParameters.get( ClassicEngineFactoryParameters.ORIGINAL_VALUE );
      if ( mimeType == null ) {
        final ResourceData resourceData = report.getResourceManager().load( resourceKey );
        final Object originalMimeType = resourceData.getAttribute( ResourceData.CONTENT_TYPE );
        if ( originalMimeType instanceof String ) {
          mimeType = (String) originalMimeType;
        } else {
          mimeType = mimeRegistry.getMimeType( originalValue );
        }
      }

      String pattern = (String) factoryParameters.get( ClassicEngineFactoryParameters.PATTERN );
      if ( pattern == null ) {
        pattern = derivePatternFromPath( mimeRegistry, mimeType, resourceKey.getIdentifierAsString() );
      }

      log.debug( "Embedding resource : originalValue=[" + originalValue + "] pattern=[" + pattern + "] mimeType=["
          + mimeType + "]" );

      // Clean up the factory parameters - we are only keeping the Original Value parameter
      Map newFactoryParameters = null;
      if ( originalValue != null ) {
        newFactoryParameters = new HashMap();
        newFactoryParameters.put( ClassicEngineFactoryParameters.ORIGINAL_VALUE, originalValue );
      }

      // Embed the resource
      final ResourceKey newResourceKey =
          WriteableDocumentBundleUtils.embedResource( documentBundle, report.getResourceManager(), resourceKey,
              pattern, mimeType, newFactoryParameters );
      if ( log.isDebugEnabled() ) {
        log.debug( "Resouce Embedded: [" + newResourceKey.getIdentifierAsString() + "]" );
      }
      return newResourceKey;
    } catch ( Exception e ) {
      throw new BundleWriterException( "Error embedding the resource into the document bundle: " + e.getMessage(), e );
    }
  }

  public static boolean isEmbeddedKey( final MasterReport report, final ResourceKey resourceKey ) {
    final ResourceKey contentBase = report.getContentBase();
    if ( contentBase == null ) {
      return false;
    }

    ResourceKey bundleKey = contentBase.getParent();
    while ( bundleKey != null ) {
      if ( bundleKey.equals( resourceKey.getParent() ) ) {
        return true;
      }
      bundleKey = bundleKey.getParent();
    }

    return false;
  }

  public static String
    derivePatternFromPath( final MimeRegistry mimeRegistry, final String mimeType, final String path ) {
    if ( mimeType == null ) {
      throw new NullPointerException();
    }
    if ( path == null ) {
      if ( mimeType.startsWith( "image/" ) ) {
        return "resources/image{0}." + mimeRegistry.getSuffix( mimeType );
      } else {
        return "resources/data{0}." + mimeRegistry.getSuffix( mimeType );
      }
    }

    final String directory = IOUtils.getInstance().getPath( path );
    final String fileNameWExt = IOUtils.getInstance().getFileName( path );
    String fileExtension = IOUtils.getInstance().getFileExtension( fileNameWExt );
    if ( StringUtils.isEmpty( fileExtension ) ) {
      fileExtension = "." + mimeRegistry.getSuffix( mimeType );
    }
    final String fileName = IOUtils.getInstance().stripFileExtension( fileNameWExt );
    String pattern = stripTrailingNumbers( fileName );
    if ( pattern == null ) {
      if ( mimeType.startsWith( "image/" ) ) {
        pattern = "image{0}";
      } else {
        pattern = "data{0}";
      }
    } else {
      pattern = pattern + "{0}";
    }

    return directory + "/" + pattern + fileExtension;
  }

  private static String stripTrailingNumbers( final String path ) {
    for ( int i = path.length() - 1; i >= 0; i-- ) {
      if ( Character.isDigit( path.charAt( i ) ) == false ) {
        return path.substring( 0, i + 1 );
      }
    }
    // if its empty or all numbers, return null.
    return null;
  }
}
