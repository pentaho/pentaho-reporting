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


package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser;

import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromRepositoryProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class KettleTransFromRepositoryReadHandler extends AbstractKettleTransformationProducerReadHandler {
  private String transformation;
  private String directory;

  public KettleTransFromRepositoryReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    transformation = attrs.getValue( getUri(), "transformation" );
    if ( transformation == null ) {
      throw new ParseException( "Required attribute 'transformation' is not defined" );
    }

    directory = attrs.getValue( getUri(), "directory" );
    if ( directory == null ) {
      throw new ParseException( "Required attribute 'directory' is not defined" );
    }
  }

  public String getTransformation() {
    return transformation;
  }

  public String getDirectory() {
    return directory;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public KettleTransformationProducer getObject() {
    KettleTransFromRepositoryProducer kettleTransFromRepositoryProducer = new KettleTransFromRepositoryProducer
      ( getRepositoryName(), directory, transformation, getStepName(), getUsername(), getPassword(),
        getDefinedArgumentNames(), getDefinedVariableNames() );
    kettleTransFromRepositoryProducer.setStopOnError( isStopOnError() );
    return kettleTransFromRepositoryProducer;
  }
}
