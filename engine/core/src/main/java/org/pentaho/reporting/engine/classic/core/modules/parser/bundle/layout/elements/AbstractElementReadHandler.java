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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import java.beans.PropertyEditor;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ClassicEngineFactoryParameters;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.StyleExpressionHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.ElementReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.ElementStyleReadHandler;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.util.beans.ValueConverter;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class AbstractElementReadHandler extends AbstractXmlReadHandler implements ElementReadHandler {
  private static final Log logger = LogFactory.getLog( AbstractElementReadHandler.class );

  private Element element;
  private ElementMetaData metaData;
  private ArrayList<StyleExpressionHandler> styleExpressions;
  private ArrayList<AttributeExpressionReadHandler> attributeExpressions;
  private ArrayList<BulkAttributeReadHandler> bulkattributes;
  private ArrayList<BulkExpressionReadHandler> bulkexpressions;

  protected AbstractElementReadHandler() {
    styleExpressions = new ArrayList<StyleExpressionHandler>();
    attributeExpressions = new ArrayList<AttributeExpressionReadHandler>();
    bulkattributes = new ArrayList<BulkAttributeReadHandler>();
    bulkexpressions = new ArrayList<BulkExpressionReadHandler>();
  }

  protected AbstractElementReadHandler( final ElementType elementType ) throws ParseException {
    this();
    initialize( elementType );
  }

  protected void autoInit() throws ParseException {
    String tagName = getTagName();
    String uri = getUri();
    ElementMetaData elementType = ElementTypeRegistry.getInstance().getElementType( tagName );
    if ( ObjectUtilities.equal( uri, elementType.getNamespace() ) == false ) {
      throw new ParseException( "Metadata not registered, and auto-registration does not match namespace" );
    }
    this.metaData = elementType;
    this.element = createElement();
  }

  protected void initialize( final ElementType elementType ) throws ParseException {
    metaData = elementType.getMetaData();
    element = createElement();
  }

  protected Element createElement() throws ParseException {
    try {
      final ElementType elementTypeObj = metaData.create();
      return (Element) elementTypeObj.create();
    } catch ( InstantiationException e ) {
      // This should not happen at this point, as there is no way to instantiate the class if the
      // element is not there. But it could happen if the element is not registered, which indicates
      // a user error (Engine not booted).
      throw new ParseException( "Unable to instantiate element for type '" + metaData.getName() + '"' );
    }
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final ReportElement element = getElement();
    if ( element == null ) {
      throw new IllegalStateException( "Failed at " + getClass() );
    }

    final int length = attrs.getLength();
    for ( int i = 0; i < length; i++ ) {
      if ( "xmlns".equals( attrs.getQName( i ) ) || attrs.getQName( i ).startsWith( "xmlns:" ) ) {
        // workaround for buggy parsers
        continue;
      }
      final String name = attrs.getLocalName( i );
      if ( name.indexOf( ':' ) > -1 ) {
        // attribute with ':' are not valid and indicate a namespace definition or so
        continue;
      }
      final String namespace = attrs.getURI( i );
      final String attributeValue = attrs.getValue( i );

      setAttributeValue( element, namespace, name, attributeValue, ReportAttributeMap.EMPTY_MAP );
    }
  }

  private void setAttributeValue( final ReportElement element, final String namespace, final String name,
      final String attributeValue, final ReportAttributeMap attributes ) throws ParseException {
    final AttributeMetaData attributeMetaData = metaData.getAttributeDescription( namespace, name );
    if ( attributeMetaData == null || attributeValue == null ) {
      element.setAttribute( namespace, name, attributeValue );
      return;
    }

    if ( attributeMetaData.isTransient() ) {
      return;
    }

    if ( isFiltered( attributeMetaData ) ) {
      return;
    }

    if ( ElementMetaData.VALUEROLE_RESOURCE.equals( attributeMetaData.getValueRole() ) ) {
      try {
        final Object type = attributes.getAttribute( AttributeNames.Core.NAMESPACE, "resource-type" );
        if ( "url".equals( type ) ) {
          element.setAttribute( namespace, name, new URL( attributeValue ) );
          return;
        }
        if ( "file".equals( type ) ) {
          element.setAttribute( namespace, name, new File( attributeValue ) );
          return;
        }
        if ( "local-ref".equals( type ) ) {
          element.setAttribute( namespace, name, attributeValue );
          return;
        }
        if ( "resource-key".equals( type ) ) {
          final ResourceManager resourceManager = getRootHandler().getResourceManager();
          final ResourceKey key = getRootHandler().getContext();
          final ResourceKey parent = key.getParent();
          final ResourceKey valueKey = resourceManager.deserialize( parent, attributeValue );

          // make local ..
          final ResourceKey resourceKey = localizeKey( resourceManager, valueKey );
          element.setAttribute( namespace, name, resourceKey );
          return;
        }
        element.setAttribute( namespace, name, attributeValue );
        return;
      } catch ( MalformedURLException e ) {
        throw new ParseException( "Failed to parse URL value", e );
      } catch ( ResourceKeyCreationException e ) {
        throw new ParseException( "Failed to parse resource-key value", e );
      }
    }

    final Class type = attributeMetaData.getTargetType();
    if ( String.class.equals( type ) ) {
      element.setAttribute( namespace, name, attributeValue );
    } else {
      try {
        final PropertyEditor propertyEditor = attributeMetaData.getEditor();
        if ( propertyEditor != null ) {
          propertyEditor.setAsText( attributeValue );
          element.setAttribute( namespace, name, propertyEditor.getValue() );
        } else {
          final ConverterRegistry instance = ConverterRegistry.getInstance();
          final ValueConverter valueConverter = instance.getValueConverter( type );
          if ( valueConverter != null ) {
            final Object o = ConverterRegistry.toPropertyValue( attributeValue, type );
            element.setAttribute( namespace, name, o );
          } else if ( String.class.isAssignableFrom( type ) ) {
            // the attribute would allow raw-string values, so copy the element ..
            element.setAttribute( namespace, name, attributeValue );
          }
        }

      } catch ( BeanException e ) {
        // ignore.
        AbstractElementReadHandler.logger.warn( "Attribute '" + namespace + '|' + name
            + "' is not convertible with the bean-methods " + getLocator() );
      }
    }
  }

  protected boolean isFiltered( final AttributeMetaData attributeMetaData ) {
    if ( AttributeNames.Core.NAMESPACE.equals( attributeMetaData.getNameSpace() ) ) {
      if ( AttributeNames.Core.ELEMENT_TYPE.equals( attributeMetaData.getName() ) ) {
        return true;
      }
    }
    return false;
  }

  private ResourceKey localizeKey( final ResourceManager resourceManager, final ResourceKey valueKey ) {
    final Object object = valueKey.getFactoryParameters().get( ClassicEngineFactoryParameters.EMBED );
    if ( "false".equals( object ) ) {
      return valueKey;
    }
    if ( "org.pentaho.reporting.libraries.docbundle.bundleloader.RepositoryResourceBundleLoader".equals( valueKey
        .getSchema() ) == false
        && object == null ) {
      return valueKey;
    }

    try {
      final ResourceData resourceData = resourceManager.load( valueKey );
      final byte[] resource = resourceData.getResource( resourceManager );
      return resourceManager.createKey( resource, valueKey.getFactoryParameters() );
    } catch ( ResourceException e ) {
      if ( logger.isDebugEnabled() ) {
        logger.info( "Unable to normalize embedded resource-key, using ordinary key-object instead.", e );
      } else {
        logger.info( "Unable to normalize embedded resource-key, using ordinary key-object instead." );
      }
    }
    return valueKey;
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( BundleNamespaces.LAYOUT.equals( uri ) ) {
      if ( "attribute-expression".equals( tagName ) ) {
        final AttributeExpressionReadHandler readHandler = new AttributeExpressionReadHandler();
        attributeExpressions.add( readHandler );
        return readHandler;
      } else if ( "style-expression".equals( tagName ) ) {
        final StyleExpressionHandler readHandler = new StyleExpressionHandler();
        styleExpressions.add( readHandler );
        return readHandler;
      } else if ( "expression".equals( tagName ) ) {
        final BulkExpressionReadHandler readHandler = new BulkExpressionReadHandler();
        bulkexpressions.add( readHandler );
        return readHandler;
      } else if ( "attribute".equals( tagName ) ) {
        String namespace = atts.getValue( getUri(), "namespace" );
        String attrName = atts.getValue( getUri(), "name" );

        final BulkAttributeReadHandler readHandler = new BulkAttributeReadHandler( namespace, attrName );
        bulkattributes.add( readHandler );
        return readHandler;
      }
    }
    if ( BundleNamespaces.STYLE.equals( uri ) ) {
      if ( "element-style".equals( tagName ) ) {
        return new ElementStyleReadHandler( getElement().getStyle() );
      }
    }

    if ( metaData.getAttributeDescription( uri, tagName ) != null ) {
      final BulkAttributeReadHandler readHandler = new BulkAttributeReadHandler( uri, tagName );
      bulkattributes.add( readHandler );
      return readHandler;
    }

    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    for ( int i = 0; i < styleExpressions.size(); i++ ) {
      final StyleExpressionHandler handler = styleExpressions.get( i );
      final StyleKey key = handler.getKey();
      if ( handler.getKey() != null ) {
        final Expression expression = handler.getExpression();
        element.setStyleExpression( key, expression );
      }
    }

    for ( int i = 0; i < attributeExpressions.size(); i++ ) {
      final AttributeExpressionReadHandler handler = attributeExpressions.get( i );
      final Expression expression = handler.getExpression();
      element.setAttributeExpression( handler.getNamespace(), handler.getName(), expression );
    }

    for ( int i = 0; i < bulkattributes.size(); i++ ) {
      final BulkAttributeReadHandler attributeReadHandler = bulkattributes.get( i );
      setAttributeValue( element, attributeReadHandler.getNamespace(), attributeReadHandler.getName(),
          attributeReadHandler.getResult(), attributeReadHandler.getAttributes() );
    }
    for ( int i = 0; i < bulkexpressions.size(); i++ ) {
      final BulkExpressionReadHandler expressionReadHandler = bulkexpressions.get( i );
      element.setAttribute( expressionReadHandler.getAttributeNameSpace(), expressionReadHandler.getAttributeName(),
          expressionReadHandler.getObject() );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return getElement();
  }

  public Element getElement() {
    return element;
  }
}
