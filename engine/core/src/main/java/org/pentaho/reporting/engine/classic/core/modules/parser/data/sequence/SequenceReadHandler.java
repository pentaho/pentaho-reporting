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


package org.pentaho.reporting.engine.classic.core.modules.parser.data.sequence;

import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.Sequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDescription;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class SequenceReadHandler extends AbstractXmlReadHandler {
  private String name;
  private Sequence data;
  private ArrayList<PropertyReadHandler> properties;

  public SequenceReadHandler() {
    properties = new ArrayList<PropertyReadHandler>();
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    name = attrs.getValue( getUri(), "name" );
    if ( name == null ) {
      throw new ParseException( "Required attribute 'name' is not defined.", getLocator() );
    }

    final String sequenceClass = attrs.getValue( getUri(), "class" );
    final Sequence sequence =
        ObjectUtilities.loadAndInstantiate( sequenceClass, SequenceReadHandler.class, Sequence.class );
    if ( sequence == null ) {
      throw new ParseException( "Required attribute 'class' is invalid.", getLocator() );
    }
    this.data = sequence;
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
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "property".equals( tagName ) ) {
      final PropertyReadHandler definitionReadHandler = new PropertyReadHandler();
      properties.add( definitionReadHandler );
      return definitionReadHandler;
    }

    return null;
  }

  public Sequence getData() {
    return data;
  }

  public String getName() {
    return name;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final SequenceDescription sequenceDescription = data.getSequenceDescription();
    for ( final PropertyReadHandler propertyReadHandler : properties ) {
      final String propertyName = propertyReadHandler.getName();
      final String propertyValue = propertyReadHandler.getResult();
      final int pos = getPropertyLocation( sequenceDescription, propertyName );
      if ( pos == -1 ) {
        throw new ParseException( "Unable to set property " + propertyName + ". There is no such property.",
            getLocator() );
      }
      final Class ptype = sequenceDescription.getParameterType( pos );
      try {
        final Object o = ConverterRegistry.toPropertyValue( propertyValue, ptype );
        data.setParameter( propertyName, o );
      } catch ( BeanException e ) {
        throw new ParseException( "Unable to set property " + propertyName + ". Conversion error.", e, getLocator() );
      }
    }
  }

  private int getPropertyLocation( final SequenceDescription sequenceDescription, final String name ) {
    final int parameterCount = sequenceDescription.getParameterCount();
    for ( int i = 0; i < parameterCount; i++ ) {
      if ( name.equals( sequenceDescription.getParameterName( i ) ) ) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return data;
  }
}
