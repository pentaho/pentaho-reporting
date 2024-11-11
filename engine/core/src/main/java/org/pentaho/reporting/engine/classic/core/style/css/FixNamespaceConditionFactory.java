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


package org.pentaho.reporting.engine.classic.core.style.css;

import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.PositionalCondition;

public class FixNamespaceConditionFactory implements ConditionFactory {
  private ConditionFactory parent;
  private StyleSheetParserUtil styleSheetParserUtil;
  private NamespaceCollection namespaceCollection;

  public FixNamespaceConditionFactory( final ConditionFactory parent, final NamespaceCollection namespaceCollection ) {
    this.namespaceCollection = namespaceCollection;
    if ( parent == null ) {
      throw new NullPointerException();
    }
    this.parent = parent;
    this.styleSheetParserUtil = StyleSheetParserUtil.getInstance();
  }

  public CombinatorCondition createAndCondition( final Condition first, final Condition second ) throws CSSException {
    return parent.createAndCondition( first, second );
  }

  public CombinatorCondition createOrCondition( final Condition first, final Condition second ) throws CSSException {
    return parent.createOrCondition( first, second );
  }

  public NegativeCondition createNegativeCondition( final Condition condition ) throws CSSException {
    return parent.createNegativeCondition( condition );
  }

  public PositionalCondition createPositionalCondition( final int position, final boolean typeNode, final boolean type )
    throws CSSException {
    return parent.createPositionalCondition( position, typeNode, type );
  }

  public AttributeCondition createAttributeCondition( final String localName, final String namespaceURI,
      final boolean specified, final String value ) throws CSSException {
    if ( namespaceURI != null ) {
      return parent.createAttributeCondition( localName, namespaceURI, specified, value );
    } else {
      styleSheetParserUtil = StyleSheetParserUtil.getInstance();
      final String[] ns = styleSheetParserUtil.parseNamespaceIdent( localName, namespaceCollection );
      return parent.createAttributeCondition( ns[1], ns[0], specified, value );
    }
  }

  public AttributeCondition createIdCondition( final String value ) throws CSSException {
    return parent.createIdCondition( value );
  }

  public LangCondition createLangCondition( final String lang ) throws CSSException {
    return parent.createLangCondition( lang );
  }

  public AttributeCondition createOneOfAttributeCondition( final String localName, final String namespaceURI,
      final boolean specified, final String value ) throws CSSException {
    if ( namespaceURI != null ) {
      return parent.createOneOfAttributeCondition( localName, namespaceURI, specified, value );
    } else {
      final String[] ns = styleSheetParserUtil.parseNamespaceIdent( localName, namespaceCollection );
      return parent.createOneOfAttributeCondition( ns[1], ns[0], specified, value );
    }
  }

  public AttributeCondition createBeginHyphenAttributeCondition( final String localName, final String namespaceURI,
      final boolean specified, final String value ) throws CSSException {
    if ( namespaceURI != null ) {
      return parent.createBeginHyphenAttributeCondition( localName, namespaceURI, specified, value );
    } else {
      final String[] ns = styleSheetParserUtil.parseNamespaceIdent( localName, namespaceCollection );
      return parent.createBeginHyphenAttributeCondition( ns[1], ns[0], specified, value );
    }
  }

  public AttributeCondition createClassCondition( final String namespaceURI, final String value ) throws CSSException {
    return parent.createClassCondition( namespaceURI, value );
  }

  public AttributeCondition createPseudoClassCondition( final String namespaceURI, final String value )
    throws CSSException {
    if ( namespaceURI != null ) {
      return parent.createPseudoClassCondition( namespaceURI, value );
    } else {
      final String[] ns = styleSheetParserUtil.parseNamespaceIdent( value, namespaceCollection );
      return parent.createPseudoClassCondition( ns[0], ns[1] );
    }
  }

  public Condition createOnlyChildCondition() throws CSSException {
    return parent.createOnlyChildCondition();
  }

  public Condition createOnlyTypeCondition() throws CSSException {
    return parent.createOnlyTypeCondition();
  }

  public ContentCondition createContentCondition( final String data ) throws CSSException {
    return parent.createContentCondition( data );
  }
}
