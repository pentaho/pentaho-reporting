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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pentaho.reporting.engine.classic.extensions.charting.runtime;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileReader;
import java.io.StringWriter;

/**
 * @author pdpi
 */
class BaseScope extends ImporterTopLevel {

  private static final Log logger = LogFactory.getLog( BaseScope.class );
  private boolean sealedStdLib = false;
  private boolean initialized;
  private String basePath;
  private String systemPath;

  public BaseScope() {
    super();
  }

  public void init( final Context cx ) {
    // Define some global functions particular to the shell. Note
    // that these functions are not part of ECMA.
    initStandardObjects( cx, sealedStdLib );
    final String[] names = { "print", "load", "lib", "_loadSvg", "_xmlToString" };
    defineFunctionProperties( names, BaseScope.class, ScriptableObject.DONTENUM );

    initialized = true;
  }

  public static Object print( final Context cx, final Scriptable thisObj,
                              final Object[] args, final Function funObj ) {

    for ( final Object arg : args ) {
      final String s = Context.toString( arg );
      logger.info( s );
    }
    return Context.getUndefinedValue();
  }

  public static Object load( final Context cx, final Scriptable thisObj,
                             final Object[] args, final Function funObj ) {

    final String file = args[ 0 ].toString();
    try {
      final BaseScope scope = (BaseScope) thisObj;
      cx.evaluateReader( scope, new FileReader( scope.basePath + "/" + file ), file, 1, null );
    } catch ( Exception e ) {
      logger.error( e );
      return Context.toBoolean( false );
    }
    return Context.toBoolean( true );
  }

  public static Object _loadSvg( final Context cx, final Scriptable thisObj,
                                 final Object[] args, final Function funObj ) {

    final String file = args[ 0 ].toString();
    try {
      final BaseScope scope = (BaseScope) thisObj;
      final String parser = "org.apache.xerces.parsers.SAXParser"; //XMLResourceDescriptor.getXMLParserClassName();
      final SAXSVGDocumentFactory f = new SAXSVGDocumentFactory( parser );
      final String uri = "file:" + scope.basePath + "/" + file;
      final SVGOMDocument doc = (SVGOMDocument) f.createDocument( uri );

      // Initialize the CSS Engine for the document
      final SVGDOMImplementation impl = (SVGDOMImplementation) SVGDOMImplementation.getDOMImplementation();
      final UserAgent userAgent = new UserAgentAdapter();
      final BridgeContext ctx = new BridgeContext( userAgent, new DocumentLoader( userAgent ) );
      doc.setCSSEngine( impl.createCSSEngine( doc, ctx ) );

      return Context.javaToJS( doc, scope );
    } catch ( Exception e ) {
      e.printStackTrace();
      logger.error( e );
      return Context.getUndefinedValue();
    }
  }

  public static Object lib( final Context cx, final Scriptable thisObj,
                            final Object[] args, final Function funObj ) {

    final String file = args[ 0 ].toString();
    try {
      final BaseScope scope = (BaseScope) thisObj;
      cx.evaluateReader( scope, new FileReader( scope.systemPath + "/" + file ), file, 1, null );
    } catch ( Exception e ) {
      logger.error( e );
      return Context.toBoolean( false );
    }
    return Context.toBoolean( true );
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath( final String basePath ) {
    this.basePath = basePath;
  }

  public void setSystemPath( final String systemPath ) {
    this.systemPath = systemPath;
  }

  public static Object _xmlToString( final Context cx, final Scriptable thisObj,
                                     final Object[] args, final Function funObj ) {
    final Node node = (Node) ( (NativeJavaObject) args[ 0 ] ).unwrap();
    try {
      final Source source = new DOMSource( node );
      final StringWriter stringWriter = new StringWriter();
      final Result result = new StreamResult( stringWriter );
      final TransformerFactory factory = TransformerFactory.newInstance();
      final Transformer transformer = factory.newTransformer();
      transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
      transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );
      transformer.transform( source, result );

      final BaseScope scope = (BaseScope) thisObj;
      return Context.javaToJS( stringWriter.getBuffer().toString(), scope );
    } catch ( TransformerConfigurationException e ) {
      e.printStackTrace();
    } catch ( TransformerException e ) {
      e.printStackTrace();
    }
    return null;
  }
}
