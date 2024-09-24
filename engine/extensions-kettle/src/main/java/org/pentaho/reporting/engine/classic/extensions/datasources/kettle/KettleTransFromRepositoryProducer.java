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

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.ArrayList;

/**
 * This class requires access to the system-properties and the local filesystem. I do not believe that this code can be
 * safely executed under a restrictive security manager rule.
 *
 * @author Thomas Morgner
 */
public class KettleTransFromRepositoryProducer extends AbstractKettleTransformationProducer {
  private static final long serialVersionUID = -2029785159214228120L;

  private String directoryName;
  private String transformationName;

  public KettleTransFromRepositoryProducer( final String repositoryName,
                                            final String directoryName,
                                            final String transformationName,
                                            final String stepName,
                                            final String username,
                                            final String password,
                                            final FormulaArgument[] definedArgumentNames,
                                            final FormulaParameter[] definedVariableNames ) {
    super( repositoryName, stepName, username, password, definedArgumentNames, definedVariableNames );
    if ( directoryName == null ) {
      throw new NullPointerException();
    }
    if ( transformationName == null ) {
      throw new NullPointerException();
    }

    this.directoryName = directoryName;
    this.transformationName = transformationName;
  }

  public String getDirectoryName() {
    return directoryName;
  }

  public String getTransformationName() {
    return transformationName;
  }

  protected TransMeta loadTransformation( final Repository repository,
                                          final ResourceManager resourceManager,
                                          final ResourceKey contextKey )
    throws ReportDataFactoryException, KettleException {
    // Find the directory specified.
    final RepositoryDirectoryInterface repositoryDirectory =
      repository.loadRepositoryDirectoryTree().findDirectory( directoryName );
    if ( repositoryDirectory == null ) {
      throw new ReportDataFactoryException( "No such directory in repository: " + directoryName );
    }
    return repository.loadTransformation( transformationName, repositoryDirectory, null, true, null );
  }

  public Object getQueryHash( final ResourceManager resourceManager, final ResourceKey resourceKey ) {
    final ArrayList<Object> retval = internalGetQueryHash();
    retval.add( getDirectoryName() );
    retval.add( getTransformationName() );
    return retval;
  }

  @Override
  public String getTransformationFile() {
    // intentional... not applicable here
    return null;
  }
}
