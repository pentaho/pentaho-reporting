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

package org.pentaho.reporting.engine.classic.core.style.css;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceDefinition;
import org.pentaho.reporting.engine.classic.core.style.css.selector.CSSSelector;
import org.pentaho.reporting.engine.classic.core.style.css.selector.SelectorWeight;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * A stateless implementation of the style rule matching. This implementation is stateless within the current layout
 * process.
 *
 * @author Thomas Morgner
 */
public class SimpleStyleRuleMatcher implements StyleRuleMatcher {
  private ResourceManager resourceManager;
  private ElementStyleRule[] activeStyleRules;
  private ElementStyleRule[] activePseudoStyleRules;
  private NamespaceCollection namespaces;
  private DocumentContext context;

  public SimpleStyleRuleMatcher() {
  }

  public void initialize( final DocumentContext layoutProcess ) {
    if ( layoutProcess == null ) {
      throw new NullPointerException();
    }
    this.context = layoutProcess;
    this.resourceManager = layoutProcess.getResourceManager();

    final ArrayList<CSSCounterRule> counterRules = new ArrayList<CSSCounterRule>();
    final ArrayList<ElementStyleRule> styleRules = new ArrayList<ElementStyleRule>();
    final DocumentContext dc = this.context;

    namespaces = dc.getNamespaces();

    if ( dc.getStyleResource() != null ) {
      handleLinkNode( dc.getStyleResource(), styleRules, counterRules );
    }
    if ( dc.getStyleDefinition() != null ) {
      handleStyleNode( dc.getStyleDefinition(), styleRules, counterRules );
    }

    activeStyleRules = styleRules.toArray( new ElementStyleRule[styleRules.size()] );

    styleRules.clear();
    for ( int i = 0; i < activeStyleRules.length; i++ ) {
      final ElementStyleRule activeStyleRule = activeStyleRules[i];
      if ( isPseudoElementRule( activeStyleRule ) == false ) {
        continue;
      }
      styleRules.add( activeStyleRule );
    }
    activePseudoStyleRules = styleRules.toArray( new ElementStyleRule[styleRules.size()] );

  }

  private void handleLinkNode( final Object styleResource, final ArrayList<ElementStyleRule> styleRules,
      final ArrayList<CSSCounterRule> counterRules ) {
    // do some external parsing
    // (Same as the <link> element of HTML)
    try {
      final String href = (String) styleResource;
      final ResourceKey baseKey = context.getContextKey();

      final ResourceKey derivedKey;
      if ( baseKey == null ) {
        derivedKey = resourceManager.createKey( href );
      } else {
        derivedKey = resourceManager.deriveKey( baseKey, String.valueOf( href ) );
      }

      final ElementStyleDefinition styleSheet = parseStyleSheet( derivedKey, null );
      if ( styleSheet == null ) {
        return;
      }
      addStyleRules( styleSheet, styleRules );
      addCounterRules( styleSheet, counterRules );
    } catch ( ResourceKeyCreationException e ) {
      e.printStackTrace();
    }
  }

  private void handleStyleNode( final ElementStyleDefinition node, final ArrayList<ElementStyleRule> styleRules,
      final ArrayList<CSSCounterRule> counterRules ) {
    addStyleRules( node, styleRules );
    addCounterRules( node, counterRules );
  }

  private void addCounterRules( final ElementStyleDefinition styleSheet, final ArrayList<CSSCounterRule> rules ) {
    final int sc = styleSheet.getStyleSheetCount();
    for ( int i = 0; i < sc; i++ ) {
      addCounterRules( styleSheet.getStyleSheet( i ), rules );
    }

    final int rc = styleSheet.getRuleCount();
    for ( int i = 0; i < rc; i++ ) {
      final ElementStyleSheet rule = styleSheet.getRule( i );
      if ( rule instanceof CSSCounterRule ) {
        final CSSCounterRule drule = (CSSCounterRule) rule;
        rules.add( drule );
      }
    }
  }

  private void addStyleRules( final ElementStyleDefinition styleSheet,
      final ArrayList<ElementStyleRule> activeStyleRules ) {
    final int sc = styleSheet.getStyleSheetCount();
    for ( int i = 0; i < sc; i++ ) {
      addStyleRules( styleSheet.getStyleSheet( i ), activeStyleRules );
    }

    final int rc = styleSheet.getRuleCount();
    for ( int i = 0; i < rc; i++ ) {
      final ElementStyleSheet rule = styleSheet.getRule( i );
      if ( rule instanceof ElementStyleRule ) {
        final ElementStyleRule drule = (ElementStyleRule) rule;
        activeStyleRules.add( drule );
      }
    }
  }

  private ElementStyleDefinition parseStyleSheet( final ResourceKey key, final ResourceKey context ) {
    try {
      final Resource resource = resourceManager.create( key, context, ElementStyleDefinition.class );
      return (ElementStyleDefinition) resource.getResource();
    } catch ( ResourceException e ) {
      // Log.info("Unable to parse StyleSheet: " + e.getLocalizedMessage());
    }
    return null;
  }

  private boolean isPseudoElementRule( final ElementStyleRule rule ) {
    final List<CSSSelector> selectorList = rule.getSelectorList();
    for ( int i = 0; i < selectorList.size(); i += 1 ) {
      final CSSSelector selector = selectorList.get( i );
      if ( selector == null ) {
        continue;
      }

      if ( selector.getSelectorType() != Selector.SAC_CONDITIONAL_SELECTOR ) {
        continue;
      }

      final ConditionalSelector cs = (ConditionalSelector) selector;
      final Condition condition = cs.getCondition();
      if ( condition.getConditionType() != Condition.SAC_PSEUDO_CLASS_CONDITION ) {
        continue;
      }
      return true;
    }
    return false;
  }

  public boolean isMatchingPseudoElement( final ReportElement element, final String pseudo ) {
    for ( int i = 0; i < activePseudoStyleRules.length; i++ ) {
      final ElementStyleRule activeStyleRule = activePseudoStyleRules[i];
      final List<CSSSelector> selectorList = activeStyleRule.getSelectorList();
      for ( int x = 0; x < selectorList.size(); x += 1 ) {
        final CSSSelector selector = selectorList.get( x );
        if ( selector instanceof ConditionalSelector == false ) {
          continue;
        }

        final ConditionalSelector cs = (ConditionalSelector) selector;
        final Condition condition = cs.getCondition();

        final AttributeCondition ac = (AttributeCondition) condition;
        if ( ObjectUtilities.equal( ac.getValue(), pseudo ) == false ) {
          continue;
        }

        final SimpleSelector simpleSelector = cs.getSimpleSelector();
        if ( isMatch( element, simpleSelector ) ) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Creates an independent copy of this style rule matcher.
   *
   * @return this instance, as this implementation is stateless
   */
  public StyleRuleMatcher deriveInstance() {
    return this;
  }

  /**
   * Returns all matching rules for the given element. Each matched rule must carry the weight of the matching selector.
   *
   * @param element
   * @return
   */
  public MatcherResult[] getMatchingRules( final ReportElement element ) {
    final ArrayList<MatcherResult> retvals = new ArrayList<MatcherResult>();
    for ( int i = 0; i < activeStyleRules.length; i++ ) {
      final ElementStyleRule activeStyleRule = activeStyleRules[i];
      final List<CSSSelector> selectorList = activeStyleRule.getSelectorList();
      SelectorWeight weight = null;

      for ( int x = 0; x < selectorList.size(); x += 1 ) {
        final CSSSelector selector = selectorList.get( x );
        if ( selector == null ) {
          continue;
        }

        if ( isMatch( element, selector ) ) {
          if ( weight == null ) {
            weight = selector.getWeight();
          } else {
            if ( weight.compareTo( selector.getWeight() ) < 0 ) {
              weight = selector.getWeight();
            }
          }
        }
      }
      if ( weight != null ) {
        retvals.add( new MatcherResult( weight, activeStyleRule ) );
      }
    }

    // Log.debug ("Got " + retvals.size() + " matching rules for " +
    // layoutContext.getTagName() + ":" +
    // layoutContext.getPseudoElement());

    return retvals.toArray( new MatcherResult[retvals.size()] );
  }

  private boolean isMatch( final ReportElement node, final Selector selector ) {
    final short selectorType = selector.getSelectorType();
    switch ( selectorType ) {
      case Selector.SAC_ANY_NODE_SELECTOR:
        return true;
      case Selector.SAC_ROOT_NODE_SELECTOR:
        return node.getParentSection() == null;
      case Selector.SAC_NEGATIVE_SELECTOR: {
        final NegativeSelector negativeSelector = (NegativeSelector) selector;
        return isMatch( node, negativeSelector ) == false;
      }
      case Selector.SAC_DIRECT_ADJACENT_SELECTOR: {
        final SiblingSelector silbSelect = (SiblingSelector) selector;
        return isSilblingMatch( node, silbSelect );
      }
      case Selector.SAC_PSEUDO_ELEMENT_SELECTOR: {
        return false;
      }
      case Selector.SAC_ELEMENT_NODE_SELECTOR: {
        final ElementSelector es = (ElementSelector) selector;
        final String localName = es.getLocalName();
        if ( localName != null ) {
          if ( localName.equals( getTagName( node ) ) == false ) {
            return false;
          }
        }
        final String namespaceURI = es.getNamespaceURI();
        if ( namespaceURI != null ) {
          final String namespace = getNamespace( node );
          if ( namespaceURI.equals( namespace ) == false ) {
            return false;
          }
        }
        return true;
      }
      case Selector.SAC_CHILD_SELECTOR: {
        final DescendantSelector ds = (DescendantSelector) selector;
        if ( isMatch( node, ds.getSimpleSelector() ) == false ) {
          return false;
        }
        final ReportElement parent = node.getParentSection();
        return ( isMatch( parent, ds.getAncestorSelector() ) );
      }
      case Selector.SAC_DESCENDANT_SELECTOR: {
        final DescendantSelector ds = (DescendantSelector) selector;
        if ( isMatch( node, ds.getSimpleSelector() ) == false ) {
          return false;
        }
        return ( isDescendantMatch( node, ds.getAncestorSelector() ) );
      }
      case Selector.SAC_CONDITIONAL_SELECTOR: {
        final ConditionalSelector cs = (ConditionalSelector) selector;
        if ( evaluateCondition( node, cs.getCondition() ) == false ) {
          return false;
        }
        if ( isMatch( node, cs.getSimpleSelector() ) == false ) {
          return false;
        }
        return true;
      }
      default:
        return false;
    }
  }

  private boolean evaluateCondition( final ReportElement node, final Condition condition ) {
    switch ( condition.getConditionType() ) {
      case Condition.SAC_AND_CONDITION: {
        final CombinatorCondition cc = (CombinatorCondition) condition;
        return ( evaluateCondition( node, cc.getFirstCondition() ) && evaluateCondition( node, cc.getSecondCondition() ) );
      }
      case Condition.SAC_OR_CONDITION: {
        final CombinatorCondition cc = (CombinatorCondition) condition;
        return ( evaluateCondition( node, cc.getFirstCondition() ) || evaluateCondition( node, cc.getSecondCondition() ) );
      }
      case Condition.SAC_ATTRIBUTE_CONDITION: {
        final AttributeCondition ac = (AttributeCondition) condition;
        final Object attr = queryAttribute( node, ac );

        if ( ac.getValue() == null ) {
          // dont care what's inside, as long as there is a value ..
          return attr != null;
        } else {
          return ObjectUtilities.equal( attr, ac.getValue() );
        }
      }
      case Condition.SAC_CLASS_CONDITION: {
        final AttributeCondition ac = (AttributeCondition) condition;
        String namespace = getNamespace( node );
        if ( namespace == null ) {
          namespace = namespaces.getDefaultNamespaceURI();
        }
        if ( namespace == null ) {
          return false;
        }
        final NamespaceDefinition ndef = namespaces.getDefinition( namespace );
        if ( ndef == null ) {
          return false;
        }
        final String[] classAttribute = ndef.getClassAttribute( getTagName( node ) );
        for ( int i = 0; i < classAttribute.length; i++ ) {
          final String attr = classAttribute[i];
          final String htmlAttr = (String) node.getAttribute( namespace, attr );
          if ( isOneOfAttributes( htmlAttr, ac.getValue() ) ) {
            return true;
          }
        }
        return false;
      }
      case Condition.SAC_ID_CONDITION: {
        final AttributeCondition ac = (AttributeCondition) condition;
        final Object id = node.getAttribute( AttributeNames.Xml.NAMESPACE, AttributeNames.Xml.ID );
        return ObjectUtilities.equal( ac.getValue(), id );
      }
      case Condition.SAC_LANG_CONDITION: {
        final AttributeCondition ac = (AttributeCondition) condition;
        final Locale locale = getLanguage( node );
        final String lang = locale.getLanguage();
        return isBeginHyphenAttribute( lang, ac.getValue() );
      }
      case Condition.SAC_NEGATIVE_CONDITION: {
        final NegativeCondition nc = (NegativeCondition) condition;
        return evaluateCondition( node, nc.getCondition() ) == false;
      }
      case Condition.SAC_ONE_OF_ATTRIBUTE_CONDITION: {
        final AttributeCondition ac = (AttributeCondition) condition;
        final Object o = queryAttribute( node, ac );
        if ( o == null ) {
          return false;
        }

        try {
          final String attr = ConverterRegistry.toAttributeValue( o );
          return isOneOfAttributes( attr, ac.getValue() );
        } catch ( BeanException e ) {
          return false;
        }
      }
      case Condition.SAC_PSEUDO_CLASS_CONDITION: {
        final AttributeCondition ac = (AttributeCondition) condition;
        final String pseudoClass = getPseudoElement( node );
        if ( pseudoClass == null ) {
          return false;
        }
        if ( pseudoClass.equals( ac.getValue() ) ) {
          return true;
        }
        return false;
      }
      case Condition.SAC_ONLY_CHILD_CONDITION:
      case Condition.SAC_ONLY_TYPE_CONDITION:
      case Condition.SAC_POSITIONAL_CONDITION:
      case Condition.SAC_CONTENT_CONDITION:
      default: {
        // any of these conditionals are not yet implemented. They are defined as part of the CSS standard.
        return false;
      }
    }
  }

  private Object queryAttribute( final ReportElement node, final AttributeCondition ac ) {
    final String namespaceURI = ac.getNamespaceURI();
    final Object attr;
    if ( namespaceURI == null ) {
      attr = node.getFirstAttribute( ac.getLocalName() );
    } else {
      attr = node.getAttribute( namespaceURI, ac.getLocalName() );
    }
    return attr;
  }

  private String getPseudoElement( final ReportElement node ) {
    // at the moment we do not support pseudo-elements.
    return null;
  }

  private String getNamespace( final ReportElement node ) {
    return AttributeNames.Core.NAMESPACE;
  }

  private String getTagName( final ReportElement node ) {
    return node.getElementType().getMetaData().getName();
  }

  private Locale getLanguage( final ReportElement node ) {
    return null;
  }

  private boolean isOneOfAttributes( final String attrValue, final String value ) {
    if ( attrValue == null ) {
      return false;
    }
    if ( attrValue.equals( value ) ) {
      return true;
    }

    final StringTokenizer strTok = new StringTokenizer( attrValue );
    while ( strTok.hasMoreTokens() ) {
      final String token = strTok.nextToken();
      if ( token.equals( value ) ) {
        return true;
      }
    }
    return false;
  }

  private boolean isBeginHyphenAttribute( final String attrValue, final String value ) {
    if ( attrValue == null ) {
      return false;
    }
    if ( value == null ) {
      return false;
    }
    return ( attrValue.startsWith( value ) );

  }

  private boolean isDescendantMatch( final ReportElement node, final Selector selector ) {
    ReportElement parent = node.getParentSection();
    while ( parent != null ) {
      if ( isMatch( parent, selector ) ) {
        return true;
      }
      parent = parent.getParentSection();
    }
    return false;
  }

  private boolean isSilblingMatch( final ReportElement node, final SiblingSelector select ) {
    ReportElement pred = getPreviousReportElement( node );
    while ( pred != null ) {
      if ( isMatch( pred, select ) ) {
        return true;
      }
      pred = getPreviousReportElement( pred );
    }
    return false;
  }

  private ReportElement getPreviousReportElement( final ReportElement e ) {
    final Section parentSection = e.getParentSection();
    if ( parentSection == null ) {
      return null;
    }

    final int count = parentSection.getElementCount();
    for ( int i = 0; i < count; i += 1 ) {
      final Element element = parentSection.getElement( i );
      if ( e == element ) {
        if ( i == 0 ) {
          return null;
        } else {
          return parentSection.getElement( i - 1 );
        }
      }
    }
    return null;
  }
}
