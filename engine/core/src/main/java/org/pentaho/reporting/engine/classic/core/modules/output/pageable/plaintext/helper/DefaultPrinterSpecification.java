/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper;

import java.util.HashMap;
import java.util.HashSet;

public class DefaultPrinterSpecification implements PrinterSpecification, Cloneable {
  private String name;
  private String displayName;
  private HashMap encodings;
  private HashSet operations;
  private PrinterEncoding[] encodingsCached;

  public DefaultPrinterSpecification( final String name, final String displayName ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( displayName == null ) {
      throw new NullPointerException();
    }
    this.encodings = new HashMap();
    this.operations = new HashSet();
    this.name = name;
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  /**
   * Returns the name of the encoding mapping. This is usually the same as the printer model name.
   *
   * @return the printer model.
   */
  public String getName() {
    return name;
  }

  public boolean isEncodingSupported( final String encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException();
    }
    return encodings.containsKey( encoding.toLowerCase() );
  }

  public boolean contains( final PrinterEncoding encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException();
    }
    return encodings.containsValue( encoding );
  }

  /**
   * Returns the encoding definition for the given java encoding.
   *
   * @param encoding
   *          the java encoding that should be mapped into a printer specific encoding.
   * @return the printer specific encoding.
   * @throws IllegalArgumentException
   *           if the given encoding is not supported.
   */
  public PrinterEncoding getEncoding( final String encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException();
    }
    final PrinterEncoding enc = (PrinterEncoding) encodings.get( encoding.toLowerCase() );
    if ( enc == null ) {
      throw new IllegalArgumentException( "Encoding is not supported." );
    }
    return enc;
  }

  public void addEncoding( final PrinterEncoding encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException();
    }
    if ( encodings.containsKey( encoding.getEncoding() ) == false ) {
      encodings.put( encoding.getEncoding().toLowerCase(), encoding );
      encodingsCached = null;
    }
  }

  public void removeEncoding( final PrinterEncoding encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException();
    }
    if ( encodings.remove( encoding.getEncoding().toLowerCase() ) != null ) {
      encodingsCached = null;
    }
  }

  public PrinterEncoding[] getSupportedEncodings() {
    if ( encodingsCached == null ) {
      final PrinterEncoding[] encodingArray = new PrinterEncoding[encodings.size()];
      encodingsCached = (PrinterEncoding[]) encodings.values().toArray( encodingArray );
    }
    return encodingsCached;
  }

  public Object clone() throws CloneNotSupportedException {
    final DefaultPrinterSpecification spec = (DefaultPrinterSpecification) super.clone();
    spec.encodings = (HashMap) encodings.clone();
    return spec;
  }

  /**
   * Returns true, if a given operation is supported, false otherwise.
   *
   * @param operationName
   *          the operation, that should be performed
   * @return true, if the printer will be able to perform that operation, false otherwise.
   */
  public boolean isFeatureAvailable( final String operationName ) {
    return operations.contains( operationName );
  }
}
