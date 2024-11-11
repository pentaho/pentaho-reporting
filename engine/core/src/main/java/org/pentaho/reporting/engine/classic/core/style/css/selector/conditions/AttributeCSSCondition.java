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


package org.pentaho.reporting.engine.classic.core.style.css.selector.conditions;

import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceDefinition;
import org.w3c.css.sac.AttributeCondition;

public class AttributeCSSCondition implements AttributeCondition, CSSCondition {
  private String name;
  private String namespace;
  private boolean specified;
  private String value;

  public AttributeCSSCondition( final String name, final String namespace, final boolean specified, final String value ) {
    this.name = name;
    this.namespace = namespace;
    this.specified = specified;
    this.value = value;
  }

  /**
   * An integer indicating the type of <code>Condition</code>.
   */
  public short getConditionType() {
    return SAC_ATTRIBUTE_CONDITION;
  }

  /**
   * Returns the <a href="http://www.w3.org/TR/REC-xml-names/#dt-NSName">namespace URI</a> of this attribute condition.
   * <p>
   * <code>NULL</code> if :
   * <ul>
   * <li>this attribute condition can match any namespace.
   * <li>this attribute is an id attribute.
   * </ul>
   */
  public String getNamespaceURI() {
    return namespace;
  }

  /**
   * Returns the <a href="http://www.w3.org/TR/REC-xml-names/#NT-LocalPart">local part</a> of the <a
   * href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">qualified name</a> of this attribute.
   * <p>
   * <code>NULL</code> if :
   * <ul>
   * <li>
   * <p>
   * this attribute condition can match any attribute.
   * <li>
   * <p>
   * this attribute is a class attribute.
   * <li>
   * <p>
   * this attribute is an id attribute.
   * <li>
   * <p>
   * this attribute is a pseudo-class attribute.
   * </ul>
   */
  public String getLocalName() {
    return name;
  }

  /**
   * Returns <code>true</code> if the attribute must have an explicit value in the original document, <code>false</code>
   * otherwise.
   */
  public final boolean getSpecified() {
    return isSpecified();
  }

  public boolean isSpecified() {
    return specified;
  }

  public String getValue() {
    return value;
  }

  public String print( final NamespaceCollection namespaces ) {
    StringBuilder b = new StringBuilder();
    b.append( "[" );
    if ( namespace != null ) {
      if ( "*".equals( namespace ) ) {
        b.append( "*|" );
      } else if ( "".equals( namespace ) ) {
        b.append( "|" );
      } else {
        NamespaceDefinition definition = namespaces.getDefinition( namespace );
        if ( definition == null ) {
          b.append( "\"" );
          b.append( namespace );
          b.append( "\"" );
          b.append( "|" );
        } else {
          b.append( definition.getPrefix() );
          b.append( "|" );
        }
      }
    }
    b.append( name );
    if ( value != null ) {
      b.append( getSelectorIndicator() );
      b.append( quoteValue( value ) );
    }
    b.append( "]" );
    return b.toString();
  }

  private String quoteValue( final String raw ) {
    final StringBuilder b = new StringBuilder();
    b.append( '"' );
    final char[] chars = raw.toCharArray();
    for ( int i = 0; i < chars.length; i++ ) {
      final char c = chars[i];
      if ( c == '\n' ) {
        b.append( '\\' );
        b.append( 'n' );
        continue;
      }
      if ( c == '\r' ) {
        b.append( '\\' );
        b.append( 'r' );
        continue;
      }
      if ( c == '\t' ) {
        b.append( '\\' );
        b.append( 't' );
        continue;
      }
      if ( c == '"' ) {
        b.append( '\\' );
      }
      if ( c == '\\' ) {
        b.append( '\\' );
      }
      b.append( c );
    }
    b.append( '"' );
    return b.toString();
  }

  protected String getSelectorIndicator() {
    return "=";
  }
}
