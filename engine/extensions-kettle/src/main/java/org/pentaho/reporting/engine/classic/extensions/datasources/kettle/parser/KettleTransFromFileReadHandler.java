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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransFromFileProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class KettleTransFromFileReadHandler extends AbstractKettleTransformationProducerReadHandler {
  private static final Log logger = LogFactory.getLog( KettleTransFromFileReadHandler.class );
  private String fileName;

  public KettleTransFromFileReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    fileName = attrs.getValue( getUri(), "filename" );
    if ( fileName == null ) {
      logger.warn( "Required attribute 'filename' is not defined. This report may not execute correctly." );
    }
  }

  public String getFileName() {
    return fileName;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public KettleTransformationProducer getObject() {
    KettleTransFromFileProducer kettleTransFromFileProducer = new KettleTransFromFileProducer
      ( getRepositoryName(), fileName, getStepName(), getUsername(), getPassword(),
        getDefinedArgumentNames(), getDefinedVariableNames() );
    kettleTransFromFileProducer.setStopOnError( isStopOnError() );
    return kettleTransFromFileProducer;
  }
}
