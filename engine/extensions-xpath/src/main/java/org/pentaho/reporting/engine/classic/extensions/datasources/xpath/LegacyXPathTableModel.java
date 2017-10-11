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

package org.pentaho.reporting.engine.classic.extensions.datasources.xpath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.GenericObjectTable;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.table.AbstractTableModel;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class LegacyXPathTableModel extends AbstractTableModel {
  private static final Log logger = LogFactory.getLog( LegacyXPathTableModel.class );
  public static final String DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
  public static final String XPATH_ENABLE_DTDS = "org.pentaho.reporting.engine.classic.extensions.datasources.xpath.EnableDTDs";

  private static class InternalXPathVariableResolver implements XPathVariableResolver {
    private final DataRow parameters;

    private InternalXPathVariableResolver( final DataRow parameters ) {
      this.parameters = parameters;
    }

    public Object resolveVariable( final QName variableName ) {
      if ( parameters != null ) {
        final String var = variableName.getLocalPart();
        return parameters.get( var );
      }
      return null;
    }
  }

  private static final Map<String, Class> SUPPORTED_TYPES;

  static {
    final HashMap<String, Class> types = new HashMap<String, Class>();
    types.put( "java.lang.String", String.class );
    types.put( "java.sql.Date", Date.class );
    types.put( "java.math.BigDecimal", BigDecimal.class );
    types.put( "java.sql.Timestamp", Timestamp.class );
    types.put( "java.lang.Integer", Integer.class );
    types.put( "java.lang.Double", Double.class );
    types.put( "java.lang.Long", Long.class );

    SUPPORTED_TYPES = Collections.unmodifiableMap( types );
  }

  private GenericObjectTable data;
  private ArrayList<Class> columnTypes;
  private ArrayList<String> columnNames;

  public LegacyXPathTableModel( final ResourceData xmlResource,
                                final ResourceManager resourceManager,
                                final String xPathExpression,
                                final DataRow parameters,
                                final int maxRowsToProcess )
    throws ReportDataFactoryException {
    try {
      columnTypes = new ArrayList<Class>();
      columnNames = new ArrayList<String>();

      DocumentBuilderFactory dbf = calculateDocumentBuilderFactory( ClassicEngineBoot.getInstance().getGlobalConfig() );

      final XPath xPath = XPathFactory.newInstance().newXPath();
      xPath.setXPathVariableResolver( new InternalXPathVariableResolver( parameters ) );

      // load metadata (number of rows, row names, row types)

      final String nodeValue = computeColDeclaration( xmlResource, resourceManager, xPath, dbf.newDocumentBuilder() );
      if ( nodeValue != null ) {
        final StringTokenizer stringTokenizer = new StringTokenizer( nodeValue, "," );
        while ( stringTokenizer.hasMoreTokens() ) {
          final String className = stringTokenizer.nextToken();
          if ( SUPPORTED_TYPES.containsKey( className ) ) {
            columnTypes.add( SUPPORTED_TYPES.get( className ) );
          } else {
            columnTypes.add( String.class );
          }
        }
      }

      // try to find all valid column names
      // visit all entries and add the names as we find them
      final NodeList rows = evaluateNodeList( xPath, xPathExpression, xmlResource, resourceManager );
      final HashMap<String, Integer> columnNamesToPositionMap = new HashMap<String, Integer>();
      final int rowCount = rows.getLength();
      data = new GenericObjectTable( Math.max( 1, rowCount ), Math.max( 1, columnTypes.size() ) );
      logger.debug( "Processing " + rowCount + " rows" );

      for ( int row = 0; row < rowCount; row++ ) {

        if ( maxRowsToProcess >= 0 ) {
          // query at least one row, so that we get the column names ...
          final int count = data.getRowCount();
          if ( count > 0 && count >= maxRowsToProcess ) {
            break;
          }
        }

        final Node node = rows.item( row );
        if ( node.getNodeType() != Node.ELEMENT_NODE ) {
          continue;
        }

        logger.debug( "Processing row " + row );
        final NodeList childNodes = node.getChildNodes();
        for ( int column = 0; column < childNodes.getLength(); column++ ) {
          final Node child = childNodes.item( column );
          if ( child.getNodeType() != Node.ELEMENT_NODE ) {
            continue;
          }

          final String columnName = child.getNodeName();
          final String textContent = extractText( child );

          final int columnPosition;
          final Integer rawPos = columnNamesToPositionMap.get( columnName );
          if ( rawPos == null ) {
            // a new one
            columnPosition = columnNames.size();
            columnNames.add( columnName );
            columnNamesToPositionMap.put( columnName, IntegerCache.getInteger( columnPosition ) );
          } else {
            columnPosition = rawPos.intValue();
          }
          logger.debug( "Processing column " + columnPosition + " Name=" + columnName + " value=" + textContent );

          final Class columnClass;
          if ( columnPosition < columnTypes.size() ) {
            columnClass = columnTypes.get( columnPosition );
          } else {
            columnClass = String.class;
          }

          if ( String.class.equals( columnClass ) ) {
            data.setObject( row, columnPosition, textContent );
            continue;
          }

          if ( columnClass == Date.class ) {
            data.setObject( row, columnPosition, new Date( Long.parseLong( textContent ) ) );
          } else if ( columnClass == BigDecimal.class ) {
            data.setObject( row, columnPosition, new BigDecimal( textContent ) );
          } else if ( columnClass == Timestamp.class ) {
            data.setObject( row, columnPosition, new Timestamp( Long.parseLong( textContent ) ) );
          } else if ( columnClass == Integer.class ) {
            data.setObject( row, columnPosition, Integer.valueOf( textContent ) );
          } else if ( columnClass == Double.class ) {
            data.setObject( row, columnPosition, Double.valueOf( textContent ) );
          } else if ( columnClass == Long.class ) {
            data.setObject( row, columnPosition, Long.valueOf( textContent ) );
          } else {
            data.setObject( row, columnPosition, textContent );
          }
        }
      }
    } catch ( Exception e ) {
      throw new ReportDataFactoryException( "Failed to query XPath datasource", e );
    }
  }

  private DocumentBuilderFactory calculateDocumentBuilderFactory( final Configuration configuration ) throws ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setXIncludeAware( false );
    if ( !"true".equals( configuration
            .getConfigProperty( XPATH_ENABLE_DTDS ) ) ) {
      dbf.setFeature( DISALLOW_DOCTYPE_DECL, true );
    }
    return dbf;
  }

  private String computeColDeclaration( final ResourceData xmlResource,
                                        final ResourceManager resourceManager,
                                        final XPath xPath, final DocumentBuilder builder )
          throws XPathExpressionException, ResourceLoadingException, IOException, ParserConfigurationException, SAXException {




    final Node pi = evaluateNode( xPath.compile( "/processing-instruction('pentaho-dataset')" ), xmlResource, resourceManager, builder );
    if ( pi != null ) {
      final String text = pi.getNodeValue();
      if ( text.length() > 0 ) {
        return text;
      }
    }
    final Node types = evaluateNode( xPath.compile( "/comment()" ), xmlResource, resourceManager, builder );
    if ( types != null ) {
      final String text = types.getNodeValue();
      if ( text.length() > 0 ) {
        return text;
      }
    }

    final Node resultsetComment = evaluateNode( xPath.compile( "/result-set/comment()" ), xmlResource, resourceManager, builder );
    if ( resultsetComment != null ) {
      final String text = resultsetComment.getNodeValue();
      if ( text.length() > 0 ) {
        return text;
      }
    }

    return null;
  }

  private String extractText( final Node child ) {
    final NodeList contentNodes = child.getChildNodes();
    final StringBuilder textContent = new StringBuilder( 32 );
    for ( int k = 0; k < contentNodes.getLength(); k++ ) {
      final Node t = contentNodes.item( k );
      if ( t.getNodeType() == Node.TEXT_NODE ) {
        textContent.append( t.getNodeValue() );
      }
    }
    return textContent.toString();
  }

  private NodeList evaluateNodeList( final XPath xpath, final String xpathQuery,
                                     final ResourceData xmlResourceData, final ResourceManager resourceManager )
    throws XPathExpressionException, ResourceLoadingException, IOException {
    final InputStream stream = xmlResourceData.getResourceAsStream( resourceManager );
    try {
      return (NodeList) xpath.evaluate( xpathQuery, new InputSource( stream ), XPathConstants.NODESET );
    } finally {
      stream.close();
    }
  }

  private Node evaluateNode( final XPathExpression xPathExpression,
                            final ResourceData xmlResourceData, final ResourceManager resourceManager, final DocumentBuilder builder )
          throws XPathExpressionException, ResourceLoadingException, IOException, SAXException {
    final InputStream stream = xmlResourceData.getResourceAsStream( resourceManager );
    try {
      return (Node) xPathExpression.evaluate( builder.parse( new InputSource( stream ) ), XPathConstants.NODE );

    } finally {
      stream.close();
    }
  }

  public Class getColumnClass( final int columnIndex ) {
    if ( columnIndex < columnTypes.size() ) {
      return columnTypes.get( columnIndex );
    }
    return String.class;
  }

  public int getRowCount() {
    return data.getRowCount();
  }


  public int getColumnCount() {
    return data.getColumnCount();
  }

  public String getColumnName( final int column ) {
    return columnNames.get( column );
  }


  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return data.getObject( rowIndex, columnIndex );
  }
}
