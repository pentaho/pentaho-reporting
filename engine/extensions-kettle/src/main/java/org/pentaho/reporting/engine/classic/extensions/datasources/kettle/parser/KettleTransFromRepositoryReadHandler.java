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
