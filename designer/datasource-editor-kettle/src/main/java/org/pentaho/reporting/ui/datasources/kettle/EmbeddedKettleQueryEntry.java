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

package org.pentaho.reporting.ui.datasources.kettle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.AbstractKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.DocumentHelper;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.ui.datasources.kettle.embedded.KettleParameterInfo;
import org.pentaho.reporting.ui.datasources.kettle.embedded.XulDialogHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;

public class EmbeddedKettleQueryEntry extends KettleQueryEntry {
  private class ValidatePropertyBinding implements ChangeListener {
    private boolean recursionPrevention;

    public void stateChanged( final ChangeEvent e ) {
      if ( recursionPrevention ) {
        return;
      }
      try {
        recursionPrevention = true;
        setValidated( dialogHelper.validate() );
      } finally {
        recursionPrevention = false;
      }
    }
  }

  private static final Log logger = LogFactory.getLog( EmbeddedKettleQueryEntry.class );
  private static final String AUTO_GENERATED_PARAMETER = "AUTO.GENERATED.PARAMETER";

  private String pluginId;
  private XulDialogHelper dialogHelper;

  private EmbeddedKettleQueryEntry( String name, String pluginId,
                                    XulDialogHelper dialogHelper ) throws KettleException {
    super( name );

    this.pluginId = pluginId;
    this.dialogHelper = dialogHelper;
    this.dialogHelper.addChangeListener( new ValidatePropertyBinding() );
  }

  public static EmbeddedKettleQueryEntry createFromExisting( String name,
                                                             EmbeddedKettleTransformationProducer producer,
                                                             DataFactoryContext dataFactoryContext )
    throws KettleException, ReportDataFactoryException {
    XulDialogHelper dialogHelper = new XulDialogHelper( producer.loadTransformation( dataFactoryContext ) );
    EmbeddedKettleQueryEntry entry = new EmbeddedKettleQueryEntry( name, producer.getPluginId(), dialogHelper );
    entry.setArguments( producer.getArguments() );
    entry.setParameters( producer.getParameter() );
    entry.setStopOnErrors( producer.isStopOnError() );
    return entry;
  }

  public static EmbeddedKettleQueryEntry createFromTemplate( String name,
                                                             String pluginId )
    throws KettleException {

    XulDialogHelper dialogHelper = new XulDialogHelper( loadTemplate( pluginId ) );
    return new EmbeddedKettleQueryEntry( name, pluginId, dialogHelper );
  }

  private static TransMeta loadTemplate( String pluginId ) throws KettleException {
    final Document document = DocumentHelper.loadDocumentFromPlugin( pluginId );
    final Node node = XMLHandler.getSubNode( document, TransMeta.XML_TAG );
    final TransMeta meta = new TransMeta();
    meta.loadXML( node, null, true, null, null );
    return meta;
  }

  @Override
  public boolean validate() {
    return dialogHelper.validate();
  }

  @Override
  public KettleTransformationProducer createProducer()
    throws KettleException {
    EmbeddedKettleTransformationProducer p =
      new EmbeddedKettleTransformationProducer( getArguments(), getParameters(), pluginId, dialogHelper.getRawData() );
    p.setStopOnError( isStopOnErrors() );
    return p;
  }

  public JComponent createUI() throws ReportDataFactoryException {
    return dialogHelper.createEditor();
  }

  public void clear() {
    dialogHelper.clear();
  }

  protected AbstractKettleTransformationProducer loadTransformation( final DataFactoryContext context )
    throws KettleException {
    return new EmbeddedKettleTransformationProducer( getArguments(), getParameters(), pluginId,
      dialogHelper.getRawData() );
  }

  public KettleParameterInfo[] getDeclaredParameters( final DataFactoryContext context )
    throws KettleException, ReportDataFactoryException {
    KettleParameterInfo[] declaredParameters = super.getDeclaredParameters( context );
    ArrayList<KettleParameterInfo> infos = new ArrayList<KettleParameterInfo>();
    for ( KettleParameterInfo declaredParameter : declaredParameters ) {
      if ( AUTO_GENERATED_PARAMETER.equalsIgnoreCase( declaredParameter.getDescription() ) ) {
        infos.add( declaredParameter );
      }
    }

    return infos.toArray( new KettleParameterInfo[ infos.size() ] );
  }
}
