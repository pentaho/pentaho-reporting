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


package org.pentaho.reporting.engine.classic.core.style.css.selector;

import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

import java.io.Serializable;

/**
 * Creation-Date: 30.11.2005, 15:38:58
 *
 * @author Thomas Morgner
 */
public class CSSDescendantSelector extends AbstractSelector implements DescendantSelector, Serializable {
  private Selector anchestorSelector;
  private SimpleSelector simpleSelector;
  private boolean childRelation;

  public CSSDescendantSelector( final SimpleSelector simpleSelector, final Selector anchestorSelector,
      final boolean childRelation ) {
    this.simpleSelector = simpleSelector;
    this.anchestorSelector = anchestorSelector;
    this.childRelation = childRelation;
  }

  /**
   * Returns the parent selector.
   */
  public Selector getAncestorSelector() {
    return anchestorSelector;
  }

  protected SelectorWeight createWeight() {
    if ( anchestorSelector instanceof CSSSelector == false || simpleSelector instanceof CSSSelector == false ) {
      throw new ClassCastException( "Invalid selector implementation!" );
    }
    CSSSelector anchestor = (CSSSelector) anchestorSelector;
    CSSSelector simple = (CSSSelector) simpleSelector;
    return new SelectorWeight( anchestor.getWeight(), simple.getWeight() );
  }

  /*
   * Returns the simple selector.
   */
  public SimpleSelector getSimpleSelector() {
    return simpleSelector;
  }

  /**
   * An integer indicating the type of <code>Selector</code>
   */
  public short getSelectorType() {
    if ( childRelation ) {
      return Selector.SAC_CHILD_SELECTOR;
    } else {
      return Selector.SAC_DESCENDANT_SELECTOR;
    }
  }

  public String print( final NamespaceCollection namespaces ) {
    final CSSSelector anchestor = (CSSSelector) anchestorSelector;
    final CSSSelector simple = (CSSSelector) simpleSelector;
    if ( childRelation ) {
      return anchestor.print( namespaces ) + " > " + simple.print( namespaces );
    }
    return anchestor.print( namespaces ) + " " + simple.print( namespaces );
  }
}
