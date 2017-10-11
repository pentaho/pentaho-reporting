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
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.table.AbstractTableModel;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class XPathTableModel extends AbstractTableModel {
  private static final Log logger = LogFactory.getLog( XPathTableModel.class );

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

  private ArrayList<Class> columnTypes;
  private TypedTableModel backend;
  private int dataLimit;
  private static final String PROCESSING_INSTRUCTION_PENTAHO_DATASET = "/processing-instruction('pentaho-dataset')";
  private static final String COMMENT_XPATH = "/comment()";
  private static final String RESULT_SET_COMMENT_XPATH = "/result-set/comment()";

  public XPathTableModel( final ResourceData xmlResource,
                          final ResourceManager resourceManager,
                          final String xPathExpression,
                          final DataRow parameters,
                          final int maxRowsToProcess )
    throws ReportDataFactoryException {
    try {
      columnTypes = new ArrayList<Class>();

      final XPath xPath = XPathFactory.newInstance().newXPath();
      xPath.setXPathVariableResolver( new InternalXPathVariableResolver( parameters ) );

      // load metadata (number of rows, row names, row types)

      final String nodeValue = computeColDeclaration( xmlResource, resourceManager, xPath );
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

      if ( maxRowsToProcess == -1 ) {
        dataLimit = Integer.MAX_VALUE;
      } else {
        this.dataLimit = Math.min( 1, maxRowsToProcess );
      }

      backend = new TypedTableModel();
      final LinkedHashMap<String, String> results = new LinkedHashMap<String, String>();
      // try to find all valid column names
      // visit all entries and add the names as we find them
      final NodeList rows = evaluateNodeList( xPath, xPathExpression, xmlResource, resourceManager );
      for ( int r = 0; r < rows.getLength(); r++ ) {
        // Get the next value from the result sequence
        final Node rowValue = rows.item( r );

        final short nodeType = rowValue.getNodeType();
        // Print this value
        if ( nodeType == Node.ELEMENT_NODE ) {
          // explodes into columns ..
          if ( processNode( rowValue, results, backend ) == false ) {
            return;
          }
        } else {
          final String columnName = rowValue.getNodeValue();
          results.put( columnName, rowValue.toString() );
          if ( addRow( results, backend ) == false ) {
            return;
          }
          results.clear();
        }
        //     System.out.println("NodeType: " + nodeType + "\n" + value.toString());
      }
    } catch ( Exception e ) {
      throw new ReportDataFactoryException( "Failed to query XPath datasource", e );
    }
  }

  private Object convertFromString( final int columnPosition, final String textContent )
    throws ReportDataFactoryException {
    if ( textContent == null ) {
      return null;
    }

    try {
      final Class columnClass;
      if ( columnPosition < columnTypes.size() ) {
        columnClass = columnTypes.get( columnPosition );
      } else {
        columnClass = String.class;
      }

      if ( String.class.equals( columnClass ) ) {
        return textContent;
      }

      if ( columnClass == Date.class ) {
        return ( new Date( Long.parseLong( textContent ) ) );
      } else if ( columnClass == BigDecimal.class ||
        columnClass == Number.class ) {
        return ( new BigDecimal( textContent ) );
      } else if ( columnClass == BigInteger.class ) {
        return ( new BigInteger( textContent ) );
      } else if ( columnClass == Timestamp.class ) {
        return ( new Timestamp( Long.parseLong( textContent ) ) );
      } else if ( columnClass == Integer.class ) {
        return ( Integer.valueOf( textContent ) );
      } else if ( columnClass == Double.class ) {
        return ( Double.valueOf( textContent ) );
      } else if ( columnClass == Long.class ) {
        return ( Long.valueOf( textContent ) );
      } else {
        return ( textContent );
      }
    } catch ( Exception e ) {
      throw new ReportDataFactoryException( "Unable to convert data to the declared type", e );
    }
  }

  private boolean addRow( final HashMap<String, String> results, final TypedTableModel tableModel )
    throws ReportDataFactoryException {
    final int row = tableModel.getRowCount();
    if ( row >= dataLimit ) {
      return false;
    }

    final Set<Map.Entry<String, String>> entries = results.entrySet();
    for ( final Map.Entry<String, String> entry : entries ) {
      final String colName = entry.getKey();
      final int colIdx = tableModel.findColumn( colName );
      if ( colIdx == -1 ) {
        tableModel.addColumn( colName, Object.class );
        final int colum = tableModel.getColumnCount() - 1;
        final Object value = convertFromString( colum, entry.getValue() );
        tableModel.setValueAt( value, row, colum );
      } else {
        final Object value = convertFromString( colIdx, entry.getValue() );
        tableModel.setValueAt( value, row, colIdx );
      }
    }
    return true;
  }


  private boolean processNode( final Node node,
                               final LinkedHashMap<String, String> results,
                               final TypedTableModel typedTableModel ) throws ReportDataFactoryException {
    final LinkedHashMap<String, String> innerResults = new LinkedHashMap<String, String>( results );

    boolean isLeaf = true;
    //    System.out.println("<" + node.getQName() + ">");
    final NodeList childList = node.getChildNodes();
    for ( int i = 0; i < childList.getLength(); i++ ) {
      final Node nodeIf = childList.item( i );
      final short type = nodeIf.getNodeType();
      if ( type == Node.COMMENT_NODE ) {
        continue;
      }

      if ( type == Node.ELEMENT_NODE ) {
        final NodeList anIf = nodeIf.getChildNodes();
        final int size = anIf.getLength();
        // check if either a empty node or a
        if ( size == 0 ) {
          // a empty node ...
          innerResults.put( nodeIf.getNodeName(), null );
        } else if ( size == 1 ) {
          final Node subNode = anIf.item( 0 );
          if ( subNode.getNodeType() == Node.TEXT_NODE || subNode.getNodeType() == Node.CDATA_SECTION_NODE ) {
            // a single text node ..
            innerResults.put( nodeIf.getNodeName(), nodeIf.getTextContent() );
          } else if ( subNode.getNodeType() == Node.ELEMENT_NODE ) {
            isLeaf = false;
          } else {
            innerResults.put( nodeIf.getNodeName(), nodeIf.getTextContent() );
          }
        } else {
          isLeaf = false;
        }
      } else {
        final String content = nodeIf.getTextContent();
        if ( StringUtils.isEmpty( content, true ) == false ) {
          innerResults.put( nodeIf.getNodeName(), content );
        }
      }
    }

    if ( isLeaf == false ) {
      for ( int i = 0; i < childList.getLength(); i++ ) {
        final Node deepNode = childList.item( i );
        if ( deepNode.getNodeType() == Node.ELEMENT_NODE ) {
          final NodeList childNodes = deepNode.getChildNodes();
          if ( childNodes.getLength() > 1 ||
            ( childNodes.getLength() == 1 &&
              childNodes.item( 0 ).getNodeType() == Node.ELEMENT_NODE ) ) {
            if ( processNode( deepNode, innerResults, typedTableModel ) == false ) {
              return false;
            }
          }
        }
      }
      return true;
    } else {
      return addRow( innerResults, typedTableModel );
    }
  }


  private String computeColDeclaration( final ResourceData xmlResource,
                                        final ResourceManager resourceManager,
                                        final XPath xPath )
    throws XPathExpressionException, ResourceLoadingException, IOException {
    final Node pi = evaluateNode( xPath, PROCESSING_INSTRUCTION_PENTAHO_DATASET, xmlResource, resourceManager );
    if ( pi != null ) {
      final String text = pi.getNodeValue();
      if ( text.length() > 0 ) {
        return text;
      }
    }
    final Node types = evaluateNode( xPath, COMMENT_XPATH, xmlResource, resourceManager );
    if ( types != null ) {
      final String text = types.getNodeValue();
      if ( text.length() > 0 ) {
        return text;
      }
    }

    final Node resultsetComment = evaluateNode( xPath, RESULT_SET_COMMENT_XPATH, xmlResource, resourceManager );
    if ( resultsetComment != null ) {
      final String text = resultsetComment.getNodeValue();
      if ( text.length() > 0 ) {
        return text;
      }
    }

    return null;
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

  private Node evaluateNode( final XPath xpath, final String xpathQuery,
                             final ResourceData xmlResourceData, final ResourceManager resourceManager )
    throws XPathExpressionException, ResourceLoadingException, IOException {
    final InputStream stream = xmlResourceData.getResourceAsStream( resourceManager );
    try {
      return (Node) xpath.evaluate( xpathQuery, new InputSource( stream ), XPathConstants.NODE );
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
    return backend.getRowCount();
  }


  public int getColumnCount() {
    return backend.getColumnCount();
  }

  public String getColumnName( final int column ) {
    return backend.getColumnName( column );
  }


  public Object getValueAt( final int rowIndex, final int columnIndex ) {
    return backend.getValueAt( rowIndex, columnIndex );
  }
}
