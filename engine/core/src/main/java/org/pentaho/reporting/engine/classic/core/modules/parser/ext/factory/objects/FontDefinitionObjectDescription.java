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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.AbstractObjectDescription;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectFactoryException;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;

/**
 * An object-description for a {@link org.pentaho.reporting.engine.classic.core.style.FontDefinition} object.
 *
 * @author Thomas Morgner
 */
public class FontDefinitionObjectDescription extends AbstractObjectDescription {
  private static final Log logger = LogFactory.getLog( FontDefinitionObjectDescription.class );
  /**
   * The font encoding parameter name.
   */
  public static final String FONT_ENCODING = "fontEncoding";

  /**
   * The font name parameter name.
   */
  public static final String FONT_NAME = "fontName";

  /**
   * The font size parameter name.
   */
  public static final String FONT_SIZE = "fontSize";

  /**
   * The bold attribute text.
   */
  public static final String BOLD = "bold";

  /**
   * The embedded font attribute text.
   */
  public static final String EMBEDDED_FONT = "embeddedFont";

  /**
   * The italic attribute text.
   */
  public static final String ITALIC = "italic";

  /**
   * The strikethrough attribute text.
   */
  public static final String STRIKETHROUGH = "strikethrough";

  /**
   * The underline attribute text.
   */
  public static final String UNDERLINE = "underline";

  /**
   * Creates a new object description.
   */
  public FontDefinitionObjectDescription() {
    super( FontDefinition.class );
    setParameterDefinition( FontDefinitionObjectDescription.FONT_ENCODING, String.class );
    setParameterDefinition( FontDefinitionObjectDescription.FONT_NAME, String.class );
    setParameterDefinition( FontDefinitionObjectDescription.FONT_SIZE, Integer.class );
    setParameterDefinition( FontDefinitionObjectDescription.BOLD, Boolean.class );
    setParameterDefinition( FontDefinitionObjectDescription.EMBEDDED_FONT, Boolean.class );
    setParameterDefinition( FontDefinitionObjectDescription.ITALIC, Boolean.class );
    setParameterDefinition( FontDefinitionObjectDescription.STRIKETHROUGH, Boolean.class );
    setParameterDefinition( FontDefinitionObjectDescription.UNDERLINE, Boolean.class );
  }

  /**
   * Returns a parameter value as a boolean.
   *
   * @param name
   *          the parameter name.
   * @return A boolean.
   */
  private boolean getBooleanParameter( final String name ) {
    final Boolean bool = (Boolean) getParameter( name );
    if ( bool == null ) {
      return false;
    }
    return bool.booleanValue();
  }

  /**
   * Returns a parameter as an int.
   *
   * @param name
   *          the parameter name.
   * @return The parameter value.
   * @throws ObjectFactoryException
   *           if there is a problem while reading the properties of the given object.
   */
  private int getIntegerParameter( final String name ) throws ObjectFactoryException {
    final Integer i = (Integer) getParameter( name );
    if ( i == null ) {
      throw new ObjectFactoryException( "Parameter " + name + " is not set" );
    }
    return i.intValue();
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    try {
      final String fontEncoding = (String) getParameter( FontDefinitionObjectDescription.FONT_ENCODING );
      final String fontName = (String) getParameter( FontDefinitionObjectDescription.FONT_NAME );
      final int fontSize = getIntegerParameter( FontDefinitionObjectDescription.FONT_SIZE );
      final boolean bold = getBooleanParameter( FontDefinitionObjectDescription.BOLD );
      final boolean embedded = getBooleanParameter( FontDefinitionObjectDescription.EMBEDDED_FONT );
      final boolean italic = getBooleanParameter( FontDefinitionObjectDescription.ITALIC );
      final boolean strike = getBooleanParameter( FontDefinitionObjectDescription.STRIKETHROUGH );
      final boolean underline = getBooleanParameter( FontDefinitionObjectDescription.UNDERLINE );
      return new FontDefinition( fontName, fontSize, bold, italic, underline, strike, fontEncoding, embedded );
    } catch ( Exception e ) {
      FontDefinitionObjectDescription.logger.info( "Failed to create FontDefinition: ", e );
      return null;
    }
  }

  /**
   * Sets the parameters of this description object to match the supplied object.
   *
   * @param o
   *          the object (should be an instance of <code>FontDefinition</code>).
   * @throws ObjectFactoryException
   *           if the object is not an instance of <code>Float</code>.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( ( o instanceof FontDefinition ) == false ) {
      throw new ObjectFactoryException( "The given object is no FontDefinition." );
    }

    final FontDefinition fdef = (FontDefinition) o;
    setParameter( FontDefinitionObjectDescription.FONT_ENCODING, fdef.getFontEncoding( null ) );
    setParameter( FontDefinitionObjectDescription.FONT_NAME, fdef.getFontName() );
    setParameter( FontDefinitionObjectDescription.FONT_SIZE, new Integer( fdef.getFontSize() ) );
    setParameter( FontDefinitionObjectDescription.BOLD, getBoolean( fdef.isBold() ) );
    setParameter( FontDefinitionObjectDescription.EMBEDDED_FONT, getBoolean( fdef.isEmbeddedFont() ) );
    setParameter( FontDefinitionObjectDescription.ITALIC, getBoolean( fdef.isItalic() ) );
    setParameter( FontDefinitionObjectDescription.STRIKETHROUGH, getBoolean( fdef.isStrikeThrough() ) );
    setParameter( FontDefinitionObjectDescription.UNDERLINE, getBoolean( fdef.isUnderline() ) );
  }

  /**
   * Returns the correct Boolean object for the given primitive boolean variable.
   *
   * @param bool
   *          the primitive boolean.
   * @return the Boolean object.
   */
  private Boolean getBoolean( final boolean bool ) {
    if ( bool == true ) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
