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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.docbundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.docbundle.metadata.writer.DocumentMetaDataWriter;
import org.pentaho.reporting.libraries.repository.ContentEntity;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.LibRepositoryBoot;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.repository.file.FileRepository;
import org.pentaho.reporting.libraries.repository.zipwriter.ZipRepository;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

public class BundleUtilities {
  private static final Log logger = LogFactory.getLog( BundleUtilities.class );
  public static final String STICKY_FLAG = "sticky";
  public static final String HIDDEN_FLAG = "hidden";

  private BundleUtilities() {
  }

  public static void copyInto( final WriteableDocumentBundle bundle,
                               final String targetPath,
                               final ResourceKey dataKey,
                               final ResourceManager resourceManager )
    throws IOException, ResourceLoadingException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( targetPath == null ) {
      throw new NullPointerException();
    }
    if ( resourceManager == null ) {
      throw new NullPointerException();
    }
    if ( dataKey == null ) {
      throw new NullPointerException();
    }

    final ResourceData resourceData = resourceManager.load( dataKey );
    String contentType = (String) resourceData.getAttribute( ResourceData.CONTENT_TYPE );
    if ( contentType == null ) {
      contentType = "application/octet-stream";
    }

    final InputStream stream = resourceData.getResourceAsStream( resourceManager );
    try {
      final OutputStream outStream = bundle.createEntry( targetPath, contentType );
      try {
        IOUtils.getInstance().copyStreams( stream, outStream );
      } finally {
        outStream.close();
      }
    } finally {
      stream.close();
    }
  }

  public static String getBundleType( final Repository repository ) {
    if ( repository == null ) {
      throw new NullPointerException();
    }
    try {
      final ContentEntity mimeTypeContentEntity = repository.getRoot().getEntry( "mimetype" );
      if ( mimeTypeContentEntity instanceof ContentItem ) {
        final ContentItem mimeTypeItem = (ContentItem) mimeTypeContentEntity;
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final InputStream in = mimeTypeItem.getInputStream();
        try {
          IOUtils.getInstance().copyStreams( in, bout );
        } finally {
          in.close();
        }
        return bout.toString( "ASCII" );
      }
      return null;
    } catch ( Exception e ) {
      return null;
    }
  }

  public static String getBundleMapping( final String bundleType ) {
    final String defaultType = LibDocBundleBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.libraries.docbundle.bundleloader.mapping" );
    if ( bundleType == null ) {
      return defaultType;
    }

    return LibDocBundleBoot.getInstance().getGlobalConfig().getConfigProperty
      ( "org.pentaho.reporting.libraries.docbundle.bundleloader.mapping." + bundleType, defaultType );

  }

  public static void writeAsZip( final File target, final DocumentBundle bundle )
    throws IOException, ContentIOException {
    if ( target == null ) {
      throw new NullPointerException();
    }
    if ( bundle == null ) {
      throw new NullPointerException();
    }

    final FileOutputStream fout = new FileOutputStream( target );
    final BufferedOutputStream bout = new BufferedOutputStream( fout );
    try {
      writeAsZip( bout, bundle );
    } finally {
      bout.close();
    }

  }

  public static void writeAsZip( final OutputStream targetStream, final DocumentBundle bundle )
    throws ContentIOException, IOException {
    if ( targetStream == null ) {
      throw new NullPointerException();
    }
    if ( bundle == null ) {
      throw new NullPointerException();
    }

    final ZipRepository repository = new ZipRepository( targetStream );
    writeToRepository( repository, bundle );
    repository.close();
  }

  public static void writeToDirectory( final File target, final DocumentBundle bundle )
    throws ContentIOException, IOException {
    if ( target == null ) {
      throw new NullPointerException();
    }
    if ( bundle == null ) {
      throw new NullPointerException();
    }

    final FileRepository repository = new FileRepository( target );
    writeToRepository( repository, bundle );
  }

  public static void writeToRepository( final Repository repository, final DocumentBundle bundle )
    throws ContentIOException, IOException {
    if ( repository == null ) {
      throw new NullPointerException();
    }
    if ( bundle == null ) {
      throw new NullPointerException();
    }

    final String bundleType = bundle.getEntryMimeType( "/" );
    if ( bundleType == null ) {
      logger.warn( "Document-Bundle has no bundle-type declared." );
    } else {
      final ContentItem contentItem = RepositoryUtilities.createItem
        ( repository, RepositoryUtilities.splitPath( "mimetype", "/" ) );
      contentItem.setAttribute
        ( LibRepositoryBoot.ZIP_DOMAIN, LibRepositoryBoot.ZIP_METHOD_ATTRIBUTE, LibRepositoryBoot.ZIP_METHOD_STORED );
      final byte[] rawData = bundleType.getBytes( "ASCII" );
      final OutputStream outputStream = contentItem.getOutputStream();
      try {
        outputStream.write( rawData );
      } finally {
        outputStream.close();
      }
    }

    final DocumentMetaDataWriter metaDataWriter = new DocumentMetaDataWriter( bundle.getMetaData() );
    final ContentItem manifestItem = RepositoryUtilities.createItem
      ( repository, RepositoryUtilities.splitPath( "META-INF/manifest.xml", "/" ) );
    final OutputStream manifestStream = manifestItem.getOutputStream();
    try {
      metaDataWriter.writeManifest( manifestStream );
    } finally {
      manifestStream.close();
    }

    final ContentItem metaDataItem = RepositoryUtilities.createItem
      ( repository, RepositoryUtilities.splitPath( "meta.xml", "/" ) );
    final OutputStream metaDataStream = metaDataItem.getOutputStream();
    try {
      metaDataWriter.writeMetaData( metaDataStream );
    } finally {
      metaDataStream.close();
    }

    final DocumentMetaData bundleMetaData = bundle.getMetaData();
    final String[] entryNames = bundleMetaData.getManifestEntryNames();
    Arrays.sort( entryNames );
    for ( int i = 0; i < entryNames.length; i++ ) {
      final String entryName = entryNames[ i ];
      if ( "/".equals( entryName ) ) {
        continue;
      }
      if ( "mimetype".equals( entryName ) ) {
        continue;
      }
      if ( "META-DATA/manifest.xml".equals( entryName ) ) {
        continue;
      }
      if ( "meta.xml".equals( entryName ) ) {
        continue;
      }

      logger.debug( "Processing " + entryName );

      final String[] entityNameArray = RepositoryUtilities.splitPath( entryName, "/" );
      if ( entryName.length() > 0 && entryName.charAt( entryName.length() - 1 ) == '/' ) {
        if ( RepositoryUtilities.isExistsEntity( repository, entityNameArray ) ) {
          continue;
        }
        // Skip, it is a directory-entry.
        RepositoryUtilities.createLocation( repository, entityNameArray );
        continue;
      }

      final ContentItem dataItem = RepositoryUtilities.createItem
        ( repository, entityNameArray );
      final OutputStream dataStream = dataItem.getOutputStream();
      try {
        final InputStream inStream = bundle.getEntryAsStream( entryName );
        try {
          IOUtils.getInstance().copyStreams( inStream, dataStream );
        } finally {
          inStream.close();
        }
      } finally {
        dataStream.close();
      }
    }
  }

  public static void copyInto( final WriteableDocumentBundle targetBundle,
                               final DocumentBundle sourceBundle ) throws IOException {
    if ( targetBundle == null ) {
      throw new NullPointerException();
    }
    if ( sourceBundle == null ) {
      throw new NullPointerException();
    }

    final WriteableDocumentMetaData targetBundleMetaData = targetBundle.getWriteableDocumentMetaData();
    final DocumentMetaData bundleMetaData = sourceBundle.getMetaData();
    targetBundleMetaData.setBundleType( bundleMetaData.getBundleType() );
    // copy the meta-data
    final String[] namespaces = bundleMetaData.getMetaDataNamespaces();
    for ( int namespaceIdx = 0; namespaceIdx < namespaces.length; namespaceIdx++ ) {
      final String namespace = namespaces[ namespaceIdx ];
      final String[] dataNames = bundleMetaData.getMetaDataNames( namespace );
      for ( int dataNameIdx = 0; dataNameIdx < dataNames.length; dataNameIdx++ ) {
        final String dataName = dataNames[ dataNameIdx ];
        final Object value = bundleMetaData.getBundleAttribute( namespace, dataName );
        targetBundleMetaData.setBundleAttribute( namespace, dataName, value );
      }
    }

    // copy the entries ...
    final String[] entryNames = bundleMetaData.getManifestEntryNames();
    for ( int i = 0; i < entryNames.length; i++ ) {
      final String entryName = entryNames[ i ];
      if ( "/".equals( entryName ) ) {
        continue;
      }
      if ( "mimetype".equals( entryName ) ) {
        continue;
      }
      if ( "META-DATA/manifest.xml".equals( entryName ) ) {
        continue;
      }
      if ( "meta.xml".equals( entryName ) ) {
        continue;
      }

      logger.debug( "Processing " + entryName );


      final String entryMimeType = bundleMetaData.getEntryMimeType( entryName );
      if ( entryMimeType == null ) {
        throw new IllegalStateException( "Found an entry with an invalid mime-type: " + entryName );
      }
      if ( entryName.length() > 0 && entryName.charAt( entryName.length() - 1 ) == '/' ) {
        targetBundle.createDirectoryEntry( entryName, entryMimeType );
      } else {
        final OutputStream dataStream = targetBundle.createEntry( entryName, entryMimeType );
        try {
          final InputStream inStream = sourceBundle.getEntryAsStream( entryName );
          try {
            IOUtils.getInstance().copyStreams( inStream, dataStream );
          } finally {
            inStream.close();
          }
        } finally {
          dataStream.close();
        }
      }

      final DocumentMetaData sourceMetaData = sourceBundle.getMetaData();
      final String[] attributeNames = sourceMetaData.getEntryAttributeNames( entryName );
      for ( int j = 0; j < attributeNames.length; j++ ) {
        final String attributeName = attributeNames[ j ];
        targetBundle.getWriteableDocumentMetaData().setEntryAttribute
          ( entryName, attributeName, sourceMetaData.getEntryAttribute( entryName, attributeName ) );
      }
    }
  }

  public static void copyStickyInto( final WriteableDocumentBundle targetBundle,
                                     final DocumentBundle sourceBundle ) throws IOException {
    if ( targetBundle == null ) {
      throw new NullPointerException();
    }
    if ( sourceBundle == null ) {
      throw new NullPointerException();
    }

    final WriteableDocumentMetaData targetBundleMetaData = targetBundle.getWriteableDocumentMetaData();
    final DocumentMetaData bundleMetaData = sourceBundle.getMetaData();
    targetBundleMetaData.setBundleType( bundleMetaData.getBundleType() );
    // copy the meta-data
    final String[] namespaces = bundleMetaData.getMetaDataNamespaces();
    for ( int namespaceIdx = 0; namespaceIdx < namespaces.length; namespaceIdx++ ) {
      final String namespace = namespaces[ namespaceIdx ];
      final String[] dataNames = bundleMetaData.getMetaDataNames( namespace );
      for ( int dataNameIdx = 0; dataNameIdx < dataNames.length; dataNameIdx++ ) {
        final String dataName = dataNames[ dataNameIdx ];
        final Object value = bundleMetaData.getBundleAttribute( namespace, dataName );
        targetBundleMetaData.setBundleAttribute( namespace, dataName, value );
      }
    }

    // copy the entries ...
    final String[] entryNames = bundleMetaData.getManifestEntryNames();
    for ( int i = 0; i < entryNames.length; i++ ) {
      final String entryName = entryNames[ i ];
      if ( "/".equals( entryName ) ) {
        continue;
      }
      if ( "mimetype".equals( entryName ) ) {
        continue;
      }
      if ( "META-DATA/manifest.xml".equals( entryName ) ) {
        continue;
      }
      if ( "meta.xml".equals( entryName ) ) {
        continue;
      }
      if ( "true".equals( bundleMetaData.getEntryAttribute( entryName, STICKY_FLAG ) ) == false ) {
        continue;
      }

      logger.debug( "Processing " + entryName );


      final String entryMimeType = bundleMetaData.getEntryMimeType( entryName );
      if ( entryMimeType == null ) {
        bundleMetaData.getEntryMimeType( entryName );
        throw new IllegalStateException( "Found an entry with an invalid mime-type: " + entryName );
      }
      if ( entryName.length() > 0 && entryName.charAt( entryName.length() - 1 ) == '/' ) {
        targetBundle.createDirectoryEntry( entryName, entryMimeType );
        continue;
      } else {
        final OutputStream dataStream = targetBundle.createEntry( entryName, entryMimeType );
        try {
          final InputStream inStream = sourceBundle.getEntryAsStream( entryName );
          try {
            IOUtils.getInstance().copyStreams( inStream, dataStream );
          } finally {
            inStream.close();
          }
        } finally {
          dataStream.close();
        }
      }

      final DocumentMetaData sourceMetaData = sourceBundle.getMetaData();
      final String[] attributeNames = sourceMetaData.getEntryAttributeNames( entryName );
      for ( int j = 0; j < attributeNames.length; j++ ) {
        final String attributeName = attributeNames[ j ];
        targetBundle.getWriteableDocumentMetaData().setEntryAttribute
          ( entryName, attributeName, sourceMetaData.getEntryAttribute( entryName, attributeName ) );
      }
    }
  }


  public static void copyInto( final WriteableDocumentBundle targetBundle,
                               final DocumentBundle sourceBundle,
                               final String[] files ) throws IOException {
    copyInto( targetBundle, sourceBundle, files, false );
  }

  public static void copyInto( final WriteableDocumentBundle targetBundle,
                               final DocumentBundle sourceBundle,
                               final String[] files,
                               final boolean ignoreSticky ) throws IOException {
    if ( targetBundle == null ) {
      throw new NullPointerException();
    }
    if ( sourceBundle == null ) {
      throw new NullPointerException();
    }
    if ( files == null ) {
      throw new NullPointerException();
    }

    final HashSet<String> fileSet = new HashSet<String>( Arrays.asList( files ) );

    final WriteableDocumentMetaData targetBundleMetaData = targetBundle.getWriteableDocumentMetaData();
    final DocumentMetaData bundleMetaData = sourceBundle.getMetaData();
    targetBundleMetaData.setBundleType( bundleMetaData.getBundleType() );
    // copy the meta-data
    final String[] namespaces = bundleMetaData.getMetaDataNamespaces();
    for ( int namespaceIdx = 0; namespaceIdx < namespaces.length; namespaceIdx++ ) {
      final String namespace = namespaces[ namespaceIdx ];
      final String[] dataNames = bundleMetaData.getMetaDataNames( namespace );
      for ( int dataNameIdx = 0; dataNameIdx < dataNames.length; dataNameIdx++ ) {
        final String dataName = dataNames[ dataNameIdx ];
        final Object value = bundleMetaData.getBundleAttribute( namespace, dataName );
        targetBundleMetaData.setBundleAttribute( namespace, dataName, value );
      }
    }

    // copy the entries ...
    final String[] entryNames = bundleMetaData.getManifestEntryNames();
    for ( int i = 0; i < entryNames.length; i++ ) {
      final String entryName = entryNames[ i ];
      if ( "/".equals( entryName ) ) {
        continue;
      }
      if ( "mimetype".equals( entryName ) ) {
        continue;
      }
      if ( "META-DATA/manifest.xml".equals( entryName ) ) {
        continue;
      }
      if ( "meta.xml".equals( entryName ) ) {
        continue;
      }
      if ( fileSet.contains( entryName ) == false ) {
        continue;
      }
      if ( ignoreSticky && "true".equals( bundleMetaData.getEntryAttribute( entryName, STICKY_FLAG ) ) ) {
        continue;
      }

      logger.debug( "Processing " + entryName );


      final String entryMimeType = bundleMetaData.getEntryMimeType( entryName );
      if ( entryMimeType == null ) {
        bundleMetaData.getEntryMimeType( entryName );
        throw new IllegalStateException( "Found an entry with an invalid mime-type: " + entryName );
      }
      if ( entryName.length() > 0 && entryName.charAt( entryName.length() - 1 ) == '/' ) {
        targetBundle.createDirectoryEntry( entryName, entryMimeType );
        continue;
      } else {
        final OutputStream dataStream = targetBundle.createEntry( entryName, entryMimeType );
        try {
          final InputStream inStream = sourceBundle.getEntryAsStream( entryName );
          try {
            IOUtils.getInstance().copyStreams( inStream, dataStream );
          } finally {
            inStream.close();
          }
        } finally {
          dataStream.close();
        }
      }

      final DocumentMetaData sourceMetaData = sourceBundle.getMetaData();
      final String[] attributeNames = sourceMetaData.getEntryAttributeNames( entryName );
      for ( int j = 0; j < attributeNames.length; j++ ) {
        final String attributeName = attributeNames[ j ];
        targetBundle.getWriteableDocumentMetaData().setEntryAttribute
          ( entryName, attributeName, sourceMetaData.getEntryAttribute( entryName, attributeName ) );
      }
    }
  }

  /**
   * Returns an unique name for the given pattern, producing a file relative to the parent file name. The returned path
   * will be an <b>absolute</b> path starting from the root of the bundle. When linking to this path via href-references
   * that imply relative paths, use {@link org.pentaho.reporting.libraries.base.util.IOUtils#createRelativePath(java
   * .lang.String,
   * java.lang.String)} to transform the absolute path returned here into a path relative to your current context.
   *
   * @param bundle  the document bundle for which we seek a new unique file name.
   * @param parent  the parent path to which the pattern is relative to.
   * @param pattern the file name pattern. We expect one parameter only.
   * @return the unique file name, never null.
   * @throws IllegalStateException if the first 2 million entries we test do not yield a unique name we can use.
   */
  public static String getUniqueName( final DocumentBundle bundle, final String parent, final String pattern ) {
    final String fullPattern = IOUtils.getInstance().getAbsolutePath( pattern, parent );
    return getUniqueName( bundle, fullPattern );
  }

  public static String getUniqueName( final DocumentBundle bundle, final String pattern ) {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( pattern == null ) {
      throw new NullPointerException();
    }

    final MessageFormat message = new MessageFormat( pattern );
    final Object[] objects = { "" };
    final String plain = message.format( objects );
    if ( bundle.isEntryExists( plain ) == false ) {
      return plain;
    }

    final Format[] formats = message.getFormats();
    if ( formats.length == 0 ) {
      // there is no variation in this name.
      return null;
    }

    int count = 1;
    while ( count < 2000000 ) {
      objects[ 0 ] = String.valueOf( count );
      final String testFile = message.format( objects );
      if ( bundle.isEntryExists( testFile ) == false ) {
        return testFile;
      }
      count += 1;
    }

    // If you have more than 2 million entries, you would hate me to test for the two billion entries, wont you?
    throw new IllegalStateException();
  }


  public static boolean isSameBundle( final ResourceKey elementSource, final ResourceKey attributeValue ) {
    if ( attributeValue == null ) {
      throw new NullPointerException();
    }
    if ( elementSource == null ) {
      return false;
    }
    if ( elementSource.getParent() != null && attributeValue.getParent() != null ) {
      // Check whether both keys are part of the same bundle.
      return ( ObjectUtilities.equal( elementSource.getParent(), attributeValue.getParent() ) );
    }

    // Not bundle keys? Check whether both keys at least refer to the same schema ..
    //    if (ObjectUtilities.equal(elementSource.getSchema(), attributeValue.getSchema()))
    //    {
    //      return true;
    //    }

    return false;
  }

  public static DocumentBundle getBundle( final File file ) throws ResourceException {
    if ( file == null ) {
      throw new NullPointerException();
    }

    final ResourceManager resManager = new ResourceManager();
    final Resource directly = resManager.createDirectly( file, DocumentBundle.class );
    return (DocumentBundle) directly.getResource();
  }

  private static final String[] DATEFORMATS = new String[] {
    "yyyy-MM-dd'T'hh:mm:ss.SSS z",
    "yyyy-MM-dd'T'hh:mm:ss z",
    "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'",
    "yyyy-MM-dd'T'hh:mm:ss'Z'",
    "yyyy-MM-dd'T'hh:mm:ss.SSS",
    "yyyy-MM-dd'T'hh:mm:ss",
    "yyyy-MM-dd zzz",
    "yyyy-MM-dd'Z'",
    "yyyy-MM-dd"
  };

  private static final long SECONDS = 1000;
  private static final long MINUTES = 60 * SECONDS;
  private static final long HOURS = 60 * MINUTES;

  public static Date parseDate( final String date ) {
    if ( date.startsWith( "PT" ) ) {
      return parseDuration( date );
    }
    final SimpleDateFormat dateFormat = new SimpleDateFormat();
    dateFormat.setLenient( false );
    for ( int i = 0; i < DATEFORMATS.length; i++ ) {
      try {
        final String dateformat = DATEFORMATS[ i ];
        dateFormat.applyPattern( dateformat );
        return dateFormat.parse( date );
      } catch ( ParseException e ) {
        // ignore
      }
    }
    return null;
  }

  public static Date parseDuration( final String duration ) {
    if ( duration.startsWith( "PT" ) == false ) {
      return null;
    }

    double div = 1;
    long date = 0;
    int item = 0;
    final char[] chars = duration.toCharArray();
    for ( int i = 1; i < chars.length; i++ ) {
      final char c = chars[ i ];

      if ( c == 'T' ) {
        item = 0;
        div = 1;
        continue;
      }
      if ( Character.isDigit( c ) ) {
        div *= 10;
        item = item * 10 + ( (int) c - '0' );
      } else if ( c == 'H' ) {
        date += item * HOURS;
        item = 0;
        div = 1;
      } else if ( c == 'M' ) {
        date += item * MINUTES;
        item = 0;
        div = 1;
      } else if ( c == 'S' ) {
        date += item * SECONDS;
        item = 0;
        div = 1;
      } else if ( c == '.' ) {
        div = 1;
      } else {
        return null;
      }
    }
    date += ( item / div ) * 1000;
    return new Date( date );
  }

  public static void copyMetaData( final MemoryDocumentBundle memoryDocumentBundle, final DocumentBundle bundle ) {
    final WriteableDocumentMetaData memMeta = memoryDocumentBundle.getWriteableDocumentMetaData();
    final DocumentMetaData metaData = bundle.getMetaData();
    memMeta.setBundleType( metaData.getBundleType() );
    final String[] metaNamespaces = metaData.getMetaDataNamespaces();
    for ( int i = 0; i < metaNamespaces.length; i++ ) {
      final String metaNamespace = metaNamespaces[ i ];
      final String[] metaDataNames = metaData.getMetaDataNames( metaNamespace );
      for ( int j = 0; j < metaDataNames.length; j++ ) {
        final String metaDataName = metaDataNames[ j ];
        final Object value = metaData.getBundleAttribute( metaNamespace, metaDataName );
        memMeta.setBundleAttribute( metaNamespace, metaDataName, value );
      }
    }
  }
}
