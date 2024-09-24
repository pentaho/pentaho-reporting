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
