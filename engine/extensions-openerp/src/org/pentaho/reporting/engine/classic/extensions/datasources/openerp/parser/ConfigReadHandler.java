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
* Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
* Copyright (c) 2011 - 2012 De Bortoli Wines Pty Limited (Australia). All Rights Reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.openerp.parser;

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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 * Copyright (c) 2011 - 2012 De Bortoli Wines Pty Limited (Australia). All Rights Reserved.
 */

import com.debortoliwines.openerp.reporting.di.OpenERPConfiguration;
import com.debortoliwines.openerp.reporting.di.OpenERPConfiguration.DataSource;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Creation-Date: 07.04.2006, 18:35:57
 *
 * @author Thomas Morgner, Pieter van der Merwe
 */
public class ConfigReadHandler extends AbstractXmlReadHandler {

  private final OpenERPConfiguration config = new OpenERPConfiguration();
  private String queryName;

  public ConfigReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    queryName = attrs.getValue( getUri(), "queryName" );

    config.setHostName( attrs.getValue( getUri(), "hostName" ) );
    config.setPortNumber( Integer.parseInt( attrs.getValue( getUri(), "portNumber" ) ) );
    config.setDatabaseName( attrs.getValue( getUri(), "databaseName" ) );
    config.setUserName( attrs.getValue( getUri(), "userName" ) );
    config.setPassword(
      PasswordEncryptionService.getInstance().decrypt( getRootHandler(), attrs.getValue( getUri(), "password" ) ) );
    config.setModelName( attrs.getValue( getUri(), "modelName" ) );

    String dataSourceStr = attrs.getValue( getUri(), "dataSource" );
    if ( dataSourceStr != null ) {
      config.setDataSource( DataSource.valueOf( dataSourceStr ) );
    }

    config.setCustomFunctionName( attrs.getValue( getUri(), "customFunctionName" ) );

  }


  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if there is a parsing error.
   */
  public Object getObject() throws SAXException {
    return null;
  }

  public String getQueryName() {
    return queryName;
  }

  public OpenERPConfiguration getConfig() {
    return config;
  }
}
