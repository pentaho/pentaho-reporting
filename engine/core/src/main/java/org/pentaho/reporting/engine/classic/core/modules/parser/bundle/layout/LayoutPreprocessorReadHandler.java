/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.LayoutPreprocessorPropertyReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.beans.IntrospectionException;

/**
 * This is a bean-handler. The layout-preprocessor provides beanified getter and setter methods (like any function).
 *
 * @author Thomas Morgner
 */
public class LayoutPreprocessorReadHandler extends AbstractXmlReadHandler {
  private BeanUtility beanUtility;
  private ReportPreProcessor preProcessor;

  public LayoutPreprocessorReadHandler() {
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
    final String preprocessorClass = attrs.getValue( getUri(), "class" );
    if ( preprocessorClass == null ) {
      throw new ParseException( "Required attribute 'class' is missing" );
    }

    preProcessor =
        (ReportPreProcessor) ObjectUtilities.loadAndInstantiate( CompatibilityMapperUtil
            .mapClassName( preprocessorClass ), LayoutPreprocessorReadHandler.class, ReportPreProcessor.class );
    if ( preProcessor == null ) {
      throw new ParseException( "Failed to instantiate the specified preprocessor '" + preprocessorClass + '\'',
          getLocator() );
    }

    try {
      this.beanUtility = new BeanUtility( preProcessor );
    } catch ( IntrospectionException e ) {
      throw new ParseException( "Failed to introspect the specified preprocessor '" + preprocessorClass + '\'',
          getLocator() );
    }
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
    if ( isSameNamespace( uri ) && "property".equals( tagName ) ) {
      return new LayoutPreprocessorPropertyReadHandler( beanUtility );
    }
    return null;

  }

  public ReportPreProcessor getPreProcessor() {
    return preProcessor;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return preProcessor;
  }
}
