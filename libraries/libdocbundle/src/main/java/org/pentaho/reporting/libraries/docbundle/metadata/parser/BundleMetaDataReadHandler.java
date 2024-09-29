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


package org.pentaho.reporting.libraries.docbundle.metadata.parser;

import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.docbundle.metadata.DefaultBundleMetaData;
import org.pentaho.reporting.libraries.docbundle.metadata.UserDefinedAttribute;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * @author Thomas Morgner
 */
public class BundleMetaDataReadHandler extends AbstractXmlReadHandler {
  private ArrayList<BundleMetaDataEntryReadHandler> entries;
  private DefaultBundleMetaData metaData;

  public BundleMetaDataReadHandler() {
    this.entries = new ArrayList<BundleMetaDataEntryReadHandler>();
  }

  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    final BundleMetaDataEntryReadHandlerFactory handlerFactory = BundleMetaDataEntryReadHandlerFactory.getInstance();
    final BundleMetaDataEntryReadHandler readHandler =
      (BundleMetaDataEntryReadHandler) handlerFactory.getHandler( uri, tagName );

    if ( readHandler != null ) {
      entries.add( readHandler );
      return readHandler;
    }
    return null;
  }

  protected void doneParsing() throws SAXException {
    final ArrayList<UserDefinedAttribute> userAttributes = new ArrayList<UserDefinedAttribute>();

    metaData = new DefaultBundleMetaData();
    for ( int i = 0; i < entries.size(); i++ ) {
      final BundleMetaDataEntryReadHandler handler = entries.get( i );
      final Object value = handler.getObject();
      final String namespace = handler.getMetaDataNameSpace();
      final String tagName = handler.getMetaDataName();

      if ( value instanceof UserDefinedAttribute ) {
        userAttributes.add( (UserDefinedAttribute) value );
      } else {
        metaData.putBundleAttribute( namespace, tagName, value );
      }
    }

    metaData.putBundleAttribute( ODFMetaAttributeNames.Meta.NAMESPACE,
      ODFMetaAttributeNames.Meta.USER_DEFINED,
      userAttributes.toArray( new UserDefinedAttribute[ userAttributes.size() ] ) );

  }

  public Object getObject() throws SAXException {
    return metaData;
  }
}
