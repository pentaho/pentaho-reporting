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

package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectFactoryException;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A style writer. This class will write a single stylesheet into the writer.
 *
 * @author Thomas Morgner.
 */
public class StyleWriter extends AbstractXMLDefinitionWriter {
  /**
   * The element style sheet.
   */
  private ElementStyleSheet elementStyleSheet;

  /**
   * Creates a new writer.
   *
   * @param reportWriter
   *          the report writer.
   * @param elementStyleSheet
   *          the element style sheet (never null).
   * @param writer
   *          the current indention level.
   */
  public StyleWriter( final ReportWriterContext reportWriter, final ElementStyleSheet elementStyleSheet,
      final XmlWriter writer ) {
    super( reportWriter, writer );
    if ( elementStyleSheet == null ) {
      throw new NullPointerException();
    }
    this.elementStyleSheet = elementStyleSheet;
  }

  /**
   * Writes the style sheet.
   *
   * @throws IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if there is a problem writing the report.
   */
  public void write() throws IOException, ReportWriterException {
    // now write all defined properties of the stylesheet ...
    // this will not write ihnerited values, only the ones defined in this instance.
    final StyleKey[] namesArray = elementStyleSheet.getDefinedPropertyNamesArray();
    for ( final StyleKey key : namesArray ) {
      if ( key == null ) {
        continue;
      }

      if ( key.isTransient() == false ) {
        final Object value = elementStyleSheet.getStyleProperty( key );
        if ( value != null ) {
          writeKeyValue( key, value );
        }
      }
    }
  }

  /**
   * Tries to find an object description suitable for the given stylekey type. If first tries to find an implementation
   * which matches the given object, if that fails, it tries to find a description for the key types. If this also
   * fails, the method starts to search for super class descriptions for the key and the object.
   *
   * @param key
   *          the stylekey.
   * @param o
   *          the stylekey value.
   * @return the found object description or null, if none was found.
   */
  private ObjectDescription findObjectDescription( final StyleKey key, final Object o ) {
    final ClassFactoryCollector cc = getReportWriter().getClassFactoryCollector();
    // search an direct definition for the given object class ...
    ObjectDescription od = cc.getDescriptionForClass( o.getClass() );
    if ( od != null ) {
      return od;
    }

    // now search an definition for the stylekey class ...
    od = cc.getDescriptionForClass( key.getValueType() );

    // and use this as best known result when searching for super class object
    // descriptions. ...

    // search the most suitable super class object description for the object
    // and the key ...
    if ( od == null ) {
      od = cc.getSuperClassObjectDescription( o.getClass(), od );
    }
    if ( od == null ) {
      od = cc.getSuperClassObjectDescription( key.getValueType(), od );
    }

    // if it is still null now, then we do not know anything about this object type.
    return od;
  }

  /**
   * Checks whether this key object would use the default object description for this key type. If this method returns
   * true, the object class can be omitted in the xml definition.
   *
   * @param key
   *          the style key that should be used as base
   * @param o
   *          the value object for this key type.
   * @return true, of the object can be described using the default object description, false otherwise.
   */
  private boolean isUseKeyObjectDescription( final StyleKey key, final Object o ) {
    final ClassFactoryCollector cc = getReportWriter().getClassFactoryCollector();
    ObjectDescription odObject = cc.getDescriptionForClass( o.getClass() );
    ObjectDescription odKey = cc.getDescriptionForClass( key.getValueType() );

    // search the most suitable super class object description ...
    if ( odObject == null ) {
      odObject = cc.getSuperClassObjectDescription( o.getClass(), odObject );
    }
    if ( odKey == null ) {
      odKey = cc.getSuperClassObjectDescription( key.getValueType(), odKey );
    }
    return ObjectUtilities.equal( odKey, odObject );
  }

  /**
   * Writes a stylekey.
   *
   * @param key
   *          the style key that should be written.
   * @param o
   *          the object that was stored at that key.
   * @throws IOException
   *           if there is an I/O problem.
   * @throws ReportWriterException
   *           if there is a problem writing the report.
   */
  private void writeKeyValue( final StyleKey key, final Object o ) throws IOException, ReportWriterException {
    final ObjectDescription od = findObjectDescription( key, o );
    if ( od == null ) {
      throw new ReportWriterException( "Unable to find object description for key: " + key.getName() );
    }

    try {
      od.setParameterFromObject( o );
    } catch ( ObjectFactoryException e ) {
      throw new ReportWriterException( "Unable to fill the parameters for key: " + key.getName(), e );
    }
    final StyleKey keyFromFactory = getReportWriter().getStyleKeyFactoryCollector().getStyleKey( key.getName() );
    if ( keyFromFactory == null ) {
      throw new ReportWriterException( "The stylekey " + key.getName() + " has no corresponding key description." );
    }

    final AttributeList p = new AttributeList();
    p.setAttribute( ExtParserModule.NAMESPACE, "name", key.getName() );
    if ( isUseKeyObjectDescription( key, o ) == false ) {
      p.setAttribute( ExtParserModule.NAMESPACE, "class", o.getClass().getName() );
    }

    final XmlWriter writer = getXmlWriter();
    final List parameterNames = getParameterNames( od );
    if ( isBasicKey( parameterNames, od ) ) {
      writer.writeTag( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.BASIC_KEY_TAG, p, XmlWriterSupport.OPEN );
      writer.writeTextNormalized( (String) od.getParameter( "value" ), false );
      writer.writeCloseTag();
    } else {
      writer.writeTag( ExtParserModule.NAMESPACE, AbstractXMLDefinitionWriter.COMPOUND_KEY_TAG, p,
          XmlWriterSupport.OPEN );
      final ObjectWriter objWriter = new ObjectWriter( getReportWriter(), o, od, writer );
      objWriter.write();
      writer.writeCloseTag();
    }
  }

  /**
   * Returns <code>true</code> if this is a basic key, and <code>false</code> otherwise.
   *
   * @param parameters
   *          the parameters.
   * @param od
   *          the object description.
   * @return A boolean.
   */
  private boolean isBasicKey( final List parameters, final ObjectDescription od ) {
    if ( parameters.size() == 1 ) {
      final String param = (String) parameters.get( 0 );
      if ( "value".equals( param ) ) {
        if ( od.getParameterDefinition( "value" ).equals( String.class ) ) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns a list of parameter names.
   *
   * @param d
   *          the object description.
   * @return The list.
   */
  private ArrayList getParameterNames( final ObjectDescription d ) {
    final ArrayList list = new ArrayList();
    final Iterator it = d.getParameterNames();
    while ( it.hasNext() ) {
      final String name = (String) it.next();
      list.add( name );
    }
    return list;
  }

  /**
   * Returns <code>true</code> if the style sheet is the default, and <code>false</code> otherwise.
   *
   * @param es
   *          the style sheet.
   * @return A boolean.
   */
  private boolean isDefaultStyleSheet( final ElementStyleSheet es ) {
    if ( es == BandDefaultStyleSheet.getBandDefaultStyle() ) {
      return true;
    }
    if ( es == ElementDefaultStyleSheet.getDefaultStyle() ) {
      return true;
    }
    if ( es == Watermark.getDefaultStyle() ) {
      return true;
    }
    return false;
  }
}
