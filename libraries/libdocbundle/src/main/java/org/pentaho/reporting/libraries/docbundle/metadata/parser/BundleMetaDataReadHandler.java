/*
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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
