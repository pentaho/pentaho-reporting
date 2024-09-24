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
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;

/**
 * This class requires access to the system-properties and the local filesystem. I do not believe that this code can be
 * safely executed under a restrictive security manager rule.
 *
 * @author Thomas Morgner
 */
public class KettleTransFromFileProducer extends AbstractKettleTransformationProducer {
  private static final long serialVersionUID = -7222333765312572612L;

  private static Log logger = LogFactory.getLog( KettleTransFromFileProducer.class );

  private String transformationFile;

  @Deprecated
  public KettleTransFromFileProducer( final String repositoryName,
                                      final String transformationFile,
                                      final String stepName,
                                      final String username,
                                      final String password,
                                      final String[] definedArgumentNames,
                                      final ParameterMapping[] definedVariableNames ) {
    super( repositoryName, stepName, username, password, definedArgumentNames, definedVariableNames );
    this.transformationFile = transformationFile;
  }

  public KettleTransFromFileProducer( final String repositoryName,
                                      final String transformationFile,
                                      final String stepName,
                                      final String username,
                                      final String password,
                                      final FormulaArgument[] definedArgumentNames,
                                      final FormulaParameter[] definedVariableNames ) {
    super( repositoryName, stepName, username, password, definedArgumentNames, definedVariableNames );
    this.transformationFile = transformationFile;
  }

  @Deprecated
  public KettleTransFromFileProducer( final String transformationFile,
                                      final String stepName,
                                      final String[] definedArgumentNames,
                                      final ParameterMapping[] definedVariableNames ) {
    this( "", transformationFile, stepName, null, null, definedArgumentNames, definedVariableNames );
  }

  public KettleTransFromFileProducer( final String transformationFile,
                                      final String stepName,
                                      final FormulaArgument[] definedArgumentNames,
                                      final FormulaParameter[] definedVariableNames ) {
    this( "", transformationFile, stepName, null, null, definedArgumentNames, definedVariableNames );
  }

  public KettleTransFromFileProducer( final String transformationFile,
                                      final String stepName ) {
    this( "", transformationFile, stepName, null, null, new FormulaArgument[ 0 ], new FormulaParameter[ 0 ] );
  }

  public String getTransformationFile() {
    return transformationFile;
  }

  private ResourceKey createKey( final ResourceManager resourceManager,
                                 final ResourceKey contextKey ) throws ResourceKeyCreationException {
    try {
      return resourceManager.deriveKey( contextKey, transformationFile );
    } catch ( ResourceKeyCreationException e ) {
      // failure is expected ..
    }

    return resourceManager.createKey( new File( transformationFile ) );
  }

  protected TransMeta loadTransformation( final Repository repository,
                                          final ResourceManager resourceManager,
                                          final ResourceKey contextKey )
    throws ReportDataFactoryException, KettleException {
    if ( transformationFile == null ) {
      throw new ReportDataFactoryException( "No Transformation file given" );
    }

    if ( resourceManager == null || contextKey == null ) {
      return new TransMeta( transformationFile, repository );
    }

    try {
      final ResourceKey resourceKey = createKey( resourceManager, contextKey );
      final Resource resource = resourceManager.create( resourceKey, contextKey, Document.class );
      final Document document = (Document) resource.getResource();
      final Node node = XMLHandler.getSubNode( document, TransMeta.XML_TAG );
      final TransMeta meta = new TransMeta();
      meta.loadXML( node, repository, true, null, null );
      final String filename = computeFullFilename( resourceKey );
      if ( filename != null ) {
        logger.debug( "Computed Transformation Location: " + filename );
        meta.setFilename( filename );
      } else {
        logger.debug( "No Computed Transformation Location, using raw name: " + transformationFile );
        meta.setFilename( transformationFile );
      }
      return meta;
    } catch ( ResourceException re ) {
      throw new ReportDataFactoryException( "Unable to load Kettle-Transformation", re );
    }
  }

  public Object getQueryHash( final ResourceManager resourceManager, final ResourceKey resourceKey ) {
    final ArrayList<Object> retval = internalGetQueryHash();
    final String fullName = computeFullFilename( resourceKey );
    if ( fullName != null ) {
      retval.add( fullName );
    } else {
      retval.add( resourceKey );
    }
    // transformation file is a unique identifier already ...
    retval.add( transformationFile );
    return retval;
  }
}
