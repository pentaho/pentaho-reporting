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


package org.pentaho.reporting.libraries.repository.dummy;

import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * A dummy content item, that does not provide any input and that does swallow all content fed into it.
 *
 * @author Thomas Morgner
 */
public class DummyContentItem implements ContentItem, Serializable {
  private ContentLocation parent;
  private String name;
  private static final byte[] EMPTY_BUFFER = new byte[ 0 ];

  /**
   * Creates a new dummy item for the given parent and having the given name.
   *
   * @param parent the parent.
   * @param name   the name of the new item.
   */
  public DummyContentItem( final ContentLocation parent, final String name ) {
    if ( parent == null ) {
      throw new NullPointerException( "Parent must not be null" );
    }
    this.parent = parent;
    this.name = name;
  }

  /**
   * Returns the mime type for the content entity. If the repository does not store mimetypes, this call usually uses
   * the repositories MimeRegistry to resolve the mimetype.
   *
   * @return the mime type.
   * @throws ContentIOException if an error occured.
   */
  public String getMimeType() throws ContentIOException {
    return getRepository().getMimeRegistry().getMimeType( this );
  }

  /**
   * Returns a NullOutputStream that ignores all content given to it.
   *
   * @return the output stream.
   */
  public OutputStream getOutputStream() {
    return new NullOutputStream();
  }

  /**
   * Returns an new empty input stream that does not allow to read a single byte from it.
   *
   * @return the input stream.
   */
  public InputStream getInputStream() {
    return new ByteArrayInputStream( EMPTY_BUFFER );
  }

  /**
   * Claims that the item is readable.
   *
   * @return true.
   */
  public boolean isReadable() {
    return true;
  }

  /**
   * Claims that the item is writable.
   *
   * @return true.
   */
  public boolean isWriteable() {
    return true;
  }

  /**
   * Returns the name of the entry.
   *
   * @return the name, never null.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the full pathname of the location.
   *
   * @return the full pathname.
   */
  public Object getContentId() {
    return RepositoryUtilities.buildName( this, "/" );
  }

  /**
   * Dummy locations do not have attributes, therefore this method always returns null.
   *
   * @param domain the attribute domain.
   * @param key    the name of the attribute.
   * @return the value or null, if the content-entity does not have a value for this attribute.
   */
  public Object getAttribute( final String domain, final String key ) {
    return null;
  }

  /**
   * Dummy locations do not allow to set attributes, therefore this method always returns false.
   *
   * @param domain the attribute domain.
   * @param key    the attribute name
   * @param value  the new attribute value.
   * @return false.
   */
  public boolean setAttribute( final String domain, final String key, final Object value ) {
    return false;
  }

  /**
   * Returns the parent, if there is any.
   *
   * @return the parent.
   */
  public ContentLocation getParent() {
    return parent;
  }

  /**
   * Returns the parent repository for this location.
   *
   * @return the repository.
   */
  public Repository getRepository() {
    return parent.getRepository();
  }

  /**
   * A dummy location does not have content and therefore does not support the delete command.
   *
   * @return always false.
   */
  public boolean delete() {
    return false;
  }
}
