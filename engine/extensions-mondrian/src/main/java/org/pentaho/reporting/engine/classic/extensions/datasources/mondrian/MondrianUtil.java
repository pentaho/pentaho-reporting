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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import mondrian.olap.Dimension;
import mondrian.olap.Hierarchy;
import mondrian.olap.Member;
import mondrian.olap.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.util.XMLParserFactoryProducer;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.LoggingErrorHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParserEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class MondrianUtil {
  private static final Log logger = LogFactory.getLog( MondrianUtil.class );

  private MondrianUtil() {
  }

  public static String parseSchemaName( final ResourceManager resourceManager,
                                        final ResourceKey contextKey,
                                        final String designTimeFile ) {
    try {
      final CubeFileProvider cubeFileProvider =
        ClassicEngineBoot.getInstance().getObjectFactory().get( CubeFileProvider.class );
      cubeFileProvider.setDesignTimeFile( designTimeFile );
      final InputStream inputStream =
        Util.readVirtualFile( cubeFileProvider.getCubeFile( resourceManager, contextKey ) );
      try {
        return parseXmlDocument( inputStream );
      } finally {
        inputStream.close();
      }
    } catch ( Exception e ) {
      logger.debug( "Failed to parse mondrian schema file at " + designTimeFile, e );
    }
    return null;
  }

  private static String parseXmlDocument( final InputStream stream ) throws ResourceCreationException,
      ParserConfigurationException {
    final DocumentBuilderFactory dbf = XMLParserFactoryProducer.createSecureDocBuilderFactory();
    dbf.setNamespaceAware( true );
    dbf.setValidating( false );

    try {
      final DocumentBuilder db = dbf.newDocumentBuilder();
      db.setEntityResolver( ParserEntityResolver.getDefaultResolver() );
      db.setErrorHandler( new LoggingErrorHandler() );
      final InputSource input = new InputSource( stream );
      final Document document = db.parse( input );
      final Element documentElement = document.getDocumentElement();
      if ( "Schema".equals( documentElement.getTagName() ) ) { // NON-NLS
        return documentElement.getAttribute( "name" );
      }
      return null;
    } catch ( ParserConfigurationException e ) {
      throw new ResourceCreationException( "Unable to initialize the XML-Parser", e );
    } catch ( SAXException e ) {
      throw new ResourceCreationException( "Unable to parse the document.", e );
    } catch ( IOException e ) {
      throw new ResourceCreationException( "Unable to parse the document.", e );
    }
  }

  public static String getUniqueMemberName( Member member ) {
    String memberValue = Util.quoteMdxIdentifier( member.getName() );
    while ( member.getParentMember() != null ) {
      memberValue = Util.quoteMdxIdentifier( member.getParentMember().getName() ) + "." + memberValue;
      member = member.getParentMember();
    }
    final Hierarchy hierarchy = member.getHierarchy();
    final Dimension dimension = hierarchy.getDimension();
    if ( hierarchy.getName().equals( dimension.getName() ) ) {
      return Util.quoteMdxIdentifier( hierarchy.getName() ) + "." + memberValue;
    } else {
      return Util.quoteMdxIdentifier( dimension.getName() ) + "." + Util.quoteMdxIdentifier( hierarchy.getName() ) + "."
        +
        memberValue;
    }
  }
}
