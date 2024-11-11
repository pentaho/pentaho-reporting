/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.docbundle.metadata.writer;

import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.metadata.BundleManifest;
import org.pentaho.reporting.libraries.docbundle.metadata.BundleMetaData;

import java.io.IOException;
import java.io.OutputStream;

public class DocumentMetaDataWriter {
  private static class BundleManifestWrapper implements BundleManifest {
    private DocumentMetaData documentMetaData;

    private BundleManifestWrapper( final DocumentMetaData documentMetaData ) {
      if ( documentMetaData == null ) {
        throw new NullPointerException();
      }
      this.documentMetaData = documentMetaData;
    }

    public String getMimeType( final String entry ) {
      if ( entry == null ) {
        throw new NullPointerException();
      }
      return documentMetaData.getEntryMimeType( entry );
    }

    public String[] getEntries() {
      return documentMetaData.getManifestEntryNames();
    }

    public String getAttribute( final String entryName, final String attributeName ) {
      return documentMetaData.getEntryAttribute( entryName, attributeName );
    }

    public String[] getAttributeNames( final String entryName ) {
      return documentMetaData.getEntryAttributeNames( entryName );
    }
  }

  private static class BundleMetaDataWrapper implements BundleMetaData {
    private DocumentMetaData documentMetaData;

    private BundleMetaDataWrapper( final DocumentMetaData documentMetaData ) {
      if ( documentMetaData == null ) {
        throw new NullPointerException();
      }
      this.documentMetaData = documentMetaData;
    }

    public Object getBundleAttribute( final String namespace, final String attributeName ) {
      if ( namespace == null ) {
        throw new NullPointerException();
      }
      if ( attributeName == null ) {
        throw new NullPointerException();
      }
      return documentMetaData.getBundleAttribute( namespace, attributeName );
    }

    public String[] getNamespaces() {
      return documentMetaData.getMetaDataNamespaces();
    }

    public String[] getNames( final String namespace ) {
      if ( namespace == null ) {
        throw new NullPointerException();
      }
      return documentMetaData.getMetaDataNames( namespace );
    }

    public Object clone() throws CloneNotSupportedException {
      throw new UnsupportedOperationException();
    }
  }

  private BundleManifestWrapper manifestWrapper;
  private BundleMetaDataWrapper metaDataWrapper;

  public DocumentMetaDataWriter( final DocumentMetaData documentMetaData ) {
    if ( documentMetaData == null ) {
      throw new NullPointerException();
    }

    this.manifestWrapper = new BundleManifestWrapper( documentMetaData );
    this.metaDataWrapper = new BundleMetaDataWrapper( documentMetaData );
  }

  public void writeManifest( final OutputStream out ) throws IOException {
    final BundleManifestXmlWriter xmlWriter = new BundleManifestXmlWriter( manifestWrapper );
    xmlWriter.write( out );
  }

  public void writeMetaData( final OutputStream out ) throws IOException {
    final BundleMetaDataXmlWriter xmlWriter = new BundleMetaDataXmlWriter( metaDataWrapper );
    xmlWriter.write( out );
  }
}
