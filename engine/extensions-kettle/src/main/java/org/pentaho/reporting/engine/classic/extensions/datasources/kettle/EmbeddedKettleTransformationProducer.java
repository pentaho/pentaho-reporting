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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class EmbeddedKettleTransformationProducer extends AbstractKettleTransformationProducer {
  private static final long serialVersionUID = 1900310938438244134L;
  private static final Log logger = LogFactory.getLog( EmbeddedKettleTransformationProducer.class );

  private String pluginId;
  private byte[] rawBytes;

  public EmbeddedKettleTransformationProducer( final FormulaArgument[] definedArgumentNames,
                                               final FormulaParameter[] definedVariableNames,
                                               final String pluginId,
                                               final byte[] transformationRaw ) {
    super( "", EmbeddedKettleDataFactoryMetaData.DATA_RETRIEVAL_STEP,
      null, null, definedArgumentNames, definedVariableNames );

    this.pluginId = pluginId;
    if ( transformationRaw == null ) {
      final EmbeddedKettleDataFactoryMetaData md = (EmbeddedKettleDataFactoryMetaData)
        DataFactoryRegistry.getInstance().getMetaData( pluginId );
      rawBytes = md.getBytes().clone();
    } else {
      rawBytes = transformationRaw.clone();
    }
  }

  public String getPluginId() {
    return pluginId;
  }

  public byte[] getTransformationRaw() {
    return rawBytes.clone();
  }

  protected TransMeta loadTransformation( final Repository repository, final ResourceManager resourceManager,
                                          final ResourceKey contextKey )
    throws ReportDataFactoryException, KettleException {
    return loadTransformation( contextKey );
  }

  private TransMeta loadTransformation( final ResourceKey contextKey ) throws KettleMissingPluginsException,
    KettlePluginException, KettleXMLException {
    final Document document = DocumentHelper.loadDocumentFromBytes( getTransformationRaw() );
    final Node node = XMLHandler.getSubNode( document, TransMeta.XML_TAG );
    final TransMeta meta = new TransMeta();
    meta.loadXML( node, null, true, null, null );
    final String filename = computeFullFilename( contextKey );
    if ( filename != null ) {
      logger.debug( "Computed Transformation Location: " + filename );
      meta.setFilename( filename );
    }
    return meta;
  }

  public Object getQueryHash( final ResourceManager resourceManager, final ResourceKey resourceKey ) {
    final ArrayList<Object> retval = internalGetQueryHash();
    retval.add( pluginId );
    if ( rawBytes != null ) {
      retval.add( DigestUtils.sha256Hex( rawBytes ) );
    } else {
      retval.add( Boolean.FALSE );
    }
    return retval;
  }

  @Override
  public String getTransformationFile() {
    return null;
  }

}
