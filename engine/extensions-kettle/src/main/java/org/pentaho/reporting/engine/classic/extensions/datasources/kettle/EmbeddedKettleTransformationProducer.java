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
