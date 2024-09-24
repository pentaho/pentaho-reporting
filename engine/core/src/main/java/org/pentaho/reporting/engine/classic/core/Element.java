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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.designtime.AttributeChange;
import org.pentaho.reporting.engine.classic.core.designtime.AttributeExpressionChange;
import org.pentaho.reporting.engine.classic.core.designtime.StyleChange;
import org.pentaho.reporting.engine.classic.core.designtime.StyleExpressionChange;
import org.pentaho.reporting.engine.classic.core.dom.ReportStructureMatcher;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.filter.DataTarget;
import org.pentaho.reporting.engine.classic.core.filter.EmptyDataSource;
import org.pentaho.reporting.engine.classic.core.filter.types.LegacyType;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.serializer.SerializerHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Base class for all report elements (displays items that can appear within a report band).
 * <p/>
 * Elements can either be Bands (which are container classes that group elements) or content-elements. All elements can
 * have a name and have a private style sheet defined. The style sheet is used to store and access all element
 * properties that can be used to control the layout of the element or affect the elements appearance in the generated
 * content.
 * <p/>
 * Elements can inherit all style information from its parent. A style value is inherited whenever the element's
 * stylesheet does not define an own value for the corresponding key. Some style-keys cannot be inherited at all, in
 * that case the default-style-sheets value is used as fall-back value. In addition to the bands stylesheet, elements
 * may also inherit from stylesheets assigned to the current report definition's StyleSheetCollection. Foreign
 * stylesheet will be lost after the local cloning is complete.
 * <p/>
 * All Style-computation is done outside of the element using one of the style-resolver implementations.
 *
 * @author David Gilbert
 * @author Thomas Morgner
 * @noinspection ClassReferencesSubclass
 */
public class Element implements DataTarget, ReportElement {
  private static final String[] EMPTY_NAMES = new String[0];
  private static final Log logger = LogFactory.getLog( Element.class );

  /**
   * An private implementation of a stylesheet.
   * <p/>
   * Using that stylesheet outside the element class will not work, cloning an element's private stylesheet without
   * cloning the element will produce <code>IllegalStateException</code>s later.
   */
  private static class InternalElementStyleSheet extends ElementStyleSheet {
    /**
     * The element that contains this stylesheet.
     */
    private Element element;

    /**
     * Creates a new internal stylesheet for the given element.
     *
     * @param element
     *          the element
     * @throws NullPointerException
     *           if the element given is null.
     */
    protected InternalElementStyleSheet( final Element element ) {
      this.element = element;
    }

    /**
     * Returns the element for this stylesheet.
     *
     * @return the element.
     */
    public Element getElement() {
      return element;
    }

    /**
     * Updates the reference to the element after the cloning.
     *
     * @param e
     *          the element that contains this stylesheet.
     */
    protected void updateElementReference( final Element e ) {
      if ( e == null ) {
        throw new NullPointerException( "Invalid implementation: Self reference cannot be null after cloning." );
      }
      this.element = e;
    }

    public void setStyleProperty( final StyleKey key, final Object value ) {
      final long l = getChangeTracker();
      final Object oldValue;
      if ( super.isLocalKey( key ) ) {
        oldValue = super.getStyleProperty( key );
      } else {
        oldValue = null;
      }

      super.setStyleProperty( key, value );
      if ( l != getChangeTracker() ) {
        element.notifyNodePropertiesChanged( new StyleChange( key, oldValue, value ) );
      }
    }
  }

  /**
   * The internal constant to mark anonymous element names.
   */
  public static final String ANONYMOUS_ELEMENT_PREFIX = "anonymousElement@";

  /**
   * A null datasource. This class is immutable and shared across all elements.
   */
  private static final DataSource NULL_DATASOURCE = new EmptyDataSource();

  private DataSource datasource;

  /**
   * The stylesheet defines global appearance for elements.
   */
  private InternalElementStyleSheet style;

  /**
   * the parent for the element (the band where the element is contained in).
   */
  private Section parent;

  /**
   * The tree lock to identify the element. This object is shared among all clones and can be used to identify elements
   * with the same anchestor.
   */
  private InstanceID treeLock;

  /**
   * The map of style-expressions keyed by the style-key.
   */
  private HashMap<StyleKey, Expression> styleExpressions;

  private transient ReportAttributeMap<Object> attributes;
  private transient boolean copyOnWrite;
  private ReportAttributeMap<Expression> attributeExpressions;
  private transient ReportAttributeMap<Object> cachedAttributes;
  private transient long changeTracker;
  private transient Object elementContext;
  private transient ElementType elementType;

  /**
   * Constructs an element.
   * <p/>
   * The element inherits the element's defined default ElementStyleSheet to provide reasonable default values for
   * common stylekeys. When the element is added to the band, the bands stylesheet is set as parent to the element's
   * stylesheet.
   * <p/>
   * A datasource is assigned with this element is set to a default source, which always returns null.
   */
  public Element() {
    treeLock = new InstanceID();
    datasource = Element.NULL_DATASOURCE;
    style = new InternalElementStyleSheet( this );
    attributes = new ReportAttributeMap<Object>();
    setElementType( LegacyType.INSTANCE );
  }

  protected Element( final InstanceID id ) {
    this();
    treeLock = id;
  }

  public SimpleStyleSheet getComputedStyle() {
    final SimpleStyleSheet computedStyle =
        (SimpleStyleSheet) this.attributes.getAttribute( AttributeNames.Internal.NAMESPACE,
            AttributeNames.Internal.COMPUTED_STYLE );
    if ( computedStyle == null ) {
      final int hc = System.identityHashCode( this );
      throw new InvalidReportStateException( "No computed style for (" + hc + ") - " + this );
    }
    return computedStyle;
  }

  public void setComputedStyle( final SimpleStyleSheet computedStyle ) {
    if ( computedStyle == null ) {
      throw new IllegalArgumentException();
    }
    setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMPUTED_STYLE, computedStyle, false );
  }

  public void setAttributeExpression( final String namespace, final String name, final Expression value ) {
    if ( attributeExpressions == null ) {
      attributeExpressions = new ReportAttributeMap<Expression>();
    }

    final Expression oldExpression = this.attributeExpressions.setAttribute( namespace, name, value );
    notifyNodePropertiesChanged( new AttributeExpressionChange( namespace, name, oldExpression, value ) );
  }

  public Expression getAttributeExpression( final String namespace, final String name ) {
    if ( attributeExpressions == null ) {
      return null;
    }
    return attributeExpressions.getAttribute( namespace, name );
  }

  public String[] getAttributeExpressionNamespaces() {
    if ( attributeExpressions == null ) {
      return Element.EMPTY_NAMES;
    }
    return attributeExpressions.getNameSpaces();
  }

  public String[] getAttributeExpressionNames( final String name ) {
    if ( attributeExpressions == null ) {
      return Element.EMPTY_NAMES;
    }
    return attributeExpressions.getNames( name );
  }

  public ReportAttributeMap<Expression> getAttributeExpressions() {
    if ( attributeExpressions == null ) {
      attributeExpressions = new ReportAttributeMap<Expression>();
    }
    return attributeExpressions;
  }

  public void setAttribute( final String namespace, final String name, final Object value ) {
    setAttribute( namespace, name, value, true );
  }

  public void setAttribute( final String namespace, final String name, final Object value, final boolean notifyChange ) {
    if ( copyOnWrite ) {
      this.attributes = attributes.clone();
      this.copyOnWrite = false;
    }

    final Object oldValue = attributes.setAttribute( namespace, name, value );
    if ( cachedAttributes != null ) {
      if ( cachedAttributes.getChangeTracker() != attributes.getChangeTracker() ) {
        cachedAttributes = null;
      }
    }
    if ( AttributeNames.Core.NAMESPACE.equals( namespace ) && AttributeNames.Core.ELEMENT_TYPE.equals( name ) ) {
      if ( value instanceof ElementType ) {
        this.elementType = (ElementType) value;
      } else {
        this.elementType = LegacyType.INSTANCE;
      }
    }

    if ( notifyChange ) {
      notifyNodePropertiesChanged( new AttributeChange( namespace, name, oldValue, value ) );
    }
  }

  public Object getAttribute( final String namespace, final String name ) {
    return attributes.getAttribute( namespace, name );
  }

  public Object getFirstAttribute( final String localName ) {
    return attributes.getFirstAttribute( localName );
  }

  public String[] getAttributeNamespaces() {
    return attributes.getNameSpaces();
  }

  public String[] getAttributeNames( final String namespace ) {
    return attributes.getNames( namespace );
  }

  /**
   * Returns the attributes of the element as unmodifable collection. The collection can be safely stored as it is
   * guaranteed to never change. (However, no assumptions are made about the contents inside the collection.)
   *
   * @return the unmodifiable attribute collection
   */
  public ReportAttributeMap<Object> getAttributes() {
    if ( cachedAttributes != null ) {
      if ( cachedAttributes.getChangeTracker() == attributes.getChangeTracker() ) {
        return cachedAttributes;
      }
    }

    cachedAttributes = attributes.createUnmodifiableMap();
    return cachedAttributes;
  }

  public <TS> TS getAttributeTyped( final String namespace, final String attribute, final Class<TS> filter ) {
    return attributes.getAttributeTyped( namespace, attribute, filter );
  }

  public void setElementType( final ElementType elementType ) {
    if ( elementType == null ) {
      throw new NullPointerException( "Element.setElementType(..): ElementType cannot be null" );
    }

    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE, elementType, true );
  }

  public ElementType getElementType() {
    if ( elementType == null ) {
      final Object maybeElementType = getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE );
      if ( maybeElementType instanceof ElementType ) {
        this.elementType = (ElementType) maybeElementType;
      } else {
        this.elementType = LegacyType.INSTANCE;
      }
    }
    return elementType;

  }

  public String getElementTypeName() {
    return getElementType().getMetaData().getName();
  }

  public final ElementMetaData getMetaData() {
    return getElementType().getMetaData();
  }

  /**
   * Return the parent of the Element. You can use this to explore the component tree.
   *
   * @return the parent of this element.
   */
  public final Band getParent() {
    if ( parent instanceof Band ) {
      return (Band) parent;
    }

    return null;
  }

  public final Section getParentSection() {
    return parent;
  }

  /**
   * Defines the parent of the Element.
   * <p/>
   * This method is public as a implementation side effect. Only a band or section implementation should call this
   * method. Calling this method manually will create a huge disaster.
   *
   * @param parent
   *          (null allowed).
   */
  protected final void setParent( final Section parent ) {
    this.parent = parent;
    this.notifyElement();
  }

  protected void notifyElement() {
  }

  /**
   * Defines the name for this Element. The name must not be empty, or a NullPointerException is thrown.
   * <p/>
   * Names can be used to lookup an element within a band. There is no requirement for element names to be unique.
   *
   * @param name
   *          the name of this element
   */
  public void setName( final String name ) {
    setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, name );
  }

  /**
   * Returns the name of the Element. The name of the Element is never null.
   *
   * @return the name.
   */
  public String getName() {
    final Object o = getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME );
    if ( o != null ) {
      return String.valueOf( o );
    }
    return "";
  }

  /**
   * Returns the datasource for this Element. You cannot override this function as the Element needs always to be the
   * last consumer in the chain of filters. This function must never return null.
   *
   * @return the assigned legacy datasource.
   * @deprecated Whereever possible use ElementType implementations instead. This method only exists to let old reports
   *             run.
   */
  public final DataSource getDataSource() {
    return datasource;
  }

  /**
   * Sets the data source for this Element. The data source is used to produce or query the element's display value.
   *
   * @param ds
   *          the datasource (<code>null</code> not permitted).
   * @throws NullPointerException
   *           if the given data source is null.
   * @deprecated The data-source should not be used anymore. Use ElementType implementations instead. This method only
   *             exists to let old reports run.
   */
  public void setDataSource( final DataSource ds ) {
    if ( ds == null ) {
      throw new NullPointerException( "Element.setDataSource(...) : null data source." );
    }
    this.datasource = ds;
    notifyNodePropertiesChanged();
  }

  /**
   * Defines whether this Element should be painted. The detailed implementation is up to the outputtarget.
   *
   * @return the current visiblity state.
   */
  public boolean isVisible() {
    return getStyle().getBooleanStyleProperty( ElementStyleKeys.VISIBLE, true );
  }

  /**
   * Defines, whether this Element should be visible in the output. The interpretation of this flag is up to the content
   * processor.
   *
   * @param b
   *          the new visibility state
   */
  public void setVisible( final boolean b ) {
    if ( b ) {
      getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, Boolean.TRUE );
    } else {
      getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, Boolean.FALSE );
    }
  }

  /**
   * Clones this Element, the datasource and the private stylesheet of this Element. The clone does no longer have a
   * parent, as the old parent would not recognize that new object anymore.
   *
   * @return a clone of this Element.
   */
  public Element clone() {
    try {
      final Element e = (Element) super.clone();
      e.style = (InternalElementStyleSheet) style.clone();
      e.datasource = datasource.clone();
      e.parent = null;
      e.style.updateElementReference( e );
      e.elementContext = null;

      if ( attributeExpressions != null ) {
        e.attributes = attributes.clone();
        e.attributeExpressions = attributeExpressions.clone();
        final String[] namespaces = e.attributeExpressions.getNameSpaces();
        for ( int i = 0; i < namespaces.length; i++ ) {
          final String namespace = namespaces[i];
          final Map<String, Expression> attrsNs = attributeExpressions.getAttributes( namespace );
          for ( final Map.Entry<String, Expression> entry : attrsNs.entrySet() ) {
            final Expression exp = entry.getValue();
            e.attributeExpressions.setAttribute( namespace, entry.getKey(), exp == null ? null : (Expression) exp.clone() );
          }
        }
      } else {
        if ( e.cachedAttributes != null ) {
          e.attributes = attributes;
          e.copyOnWrite = true;
          copyOnWrite = true;
        } else {
          e.copyOnWrite = false;
          e.attributes = attributes.clone();
        }
      }

      if ( styleExpressions != null ) {
        e.styleExpressions = (HashMap<StyleKey, Expression>) styleExpressions.clone();
        for ( final Map.Entry<StyleKey, Expression> entry : e.styleExpressions.entrySet() ) {
          final Expression exp = entry.getValue();
          entry.setValue( (Expression) exp.clone() );
        }
      }
      return e;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( cne );
    }
  }

  public final Element derive() {
    return derive( false );
  }

  /**
   * Creates a deep copy of this element and regenerates all instance-ids.
   *
   * @param preserveElementInstanceIds
   *          defines whether this call generates new instance-ids for the derived elements. Instance-IDs are used by
   *          the report processor to recognize reoccurring elements and must not changed within the report run. Outside
   *          of the report processors new instance ids should be generated at all times to separate instances and to
   *          make them uniquely identifiable.
   * @return the copy of the element.
   */
  public Element derive( final boolean preserveElementInstanceIds ) {
    try {
      final Element e = (Element) super.clone();
      e.elementContext = null;
      if ( preserveElementInstanceIds == false ) {
        e.treeLock = new InstanceID();
      }

      e.style = (InternalElementStyleSheet) style.derive( preserveElementInstanceIds );
      e.datasource = datasource.clone();
      e.parent = null;
      e.style.updateElementReference( e );
      e.attributes = attributes.clone();
      e.copyOnWrite = false;
      final ElementMetaData metaData = e.getMetaData();
      final String[] namespaces = e.attributes.getNameSpaces();
      for ( int i = 0; i < namespaces.length; i++ ) {
        final String namespace = namespaces[i];
        final Map attrsNs = attributes.getAttributes( namespace );
        final Iterator it = attrsNs.entrySet().iterator();
        while ( it.hasNext() ) {
          final Map.Entry entry = (Map.Entry) it.next();
          final Object value = entry.getValue();

          final String name = (String) entry.getKey();
          final AttributeMetaData data = metaData.getAttributeDescription( namespace, name );
          if ( data == null ) {
            if ( logger.isDebugEnabled() ) {
              logger.debug( getElementTypeName() + ": Attribute " + namespace + "|" + name
                  + " is not listed in the metadata." );
            }
          }
          if ( value instanceof Cloneable ) {
            e.attributes.setAttribute( namespace, name, ObjectUtilities.clone( value ) );
          } else if ( data == null || data.isComputed() == false || data.isDesignTimeValue() ) {
            e.attributes.setAttribute( namespace, name, value );
          } else {
            e.attributes.setAttribute( namespace, name, null );
          }
        }
      }
      if ( e.cachedAttributes != null && e.attributes.getChangeTracker() != e.cachedAttributes.getChangeTracker() ) {
        e.cachedAttributes = null;
      }

      if ( attributeExpressions != null ) {
        e.attributeExpressions = attributeExpressions.clone();
        final String[] attrExprNamespaces = e.attributeExpressions.getNameSpaces();
        for ( int i = 0; i < attrExprNamespaces.length; i++ ) {
          final String namespace = attrExprNamespaces[i];
          final Map attrsNs = attributeExpressions.getAttributes( namespace );
          final Iterator it = attrsNs.entrySet().iterator();
          while ( it.hasNext() ) {
            final Map.Entry entry = (Map.Entry) it.next();
            final Expression exp = (Expression) entry.getValue();
            e.attributeExpressions.setAttribute( namespace, (String) entry.getKey(), exp == null ? null : exp.getInstance() );
          }
        }
      }

      if ( styleExpressions != null ) {
        // noinspection unchecked
        e.styleExpressions = (HashMap<StyleKey, Expression>) styleExpressions.clone();
        final Iterator<Map.Entry<StyleKey, Expression>> styleExpressionsIt = e.styleExpressions.entrySet().iterator();
        while ( styleExpressionsIt.hasNext() ) {
          final Map.Entry<StyleKey, Expression> entry = styleExpressionsIt.next();
          final Expression exp = entry.getValue();
          entry.setValue( exp.getInstance() );
        }
      }
      return e;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( cne );
    }

  }

  /**
   * Returns this elements private stylesheet. This sheet can be used to override the default values set in one of the
   * parent-stylesheets.
   *
   * @return the Element's stylesheet
   */
  public ElementStyleSheet getStyle() {
    return style;
  }

  /**
   * Returns the tree lock object for the self tree. If the element is part of a content hierarchy, the parent's tree
   * lock is returned.
   *
   * @return the treelock object.
   */
  public final Object getTreeLock() {
    final Section parent = getParentSection();
    if ( parent != null ) {
      return parent.getTreeLock();
    }
    return treeLock;
  }

  /**
   * Returns the Xml-ID of this element. This ID is unique within the report-definition, but is not a internal
   * object-instance ID but a user-defined string.
   *
   * @return the element id.
   */
  public String getId() {
    return (String) getAttribute( AttributeNames.Xml.NAMESPACE, AttributeNames.Xml.ID );
  }

  /**
   * Defines the Xml-ID of this element. This ID is unique within the report-definition, but is not a internal
   * object-instance ID but a user-defined string.
   *
   * @param id
   *          the element id.
   */
  public void setId( final String id ) {
    setAttribute( AttributeNames.Xml.NAMESPACE, AttributeNames.Xml.ID, id );
  }

  /**
   * Returns a unique identifier for the given instance. The identifier can be used to recognize cloned instance which
   * have the same anchestor. The identifier is unique as long as the element remains in the JVM, it does not guarantee
   * uniqueness or the ability to recognize clones, after the element has been serialized.
   *
   * @return the object identifier.
   */
  public final InstanceID getObjectID() {
    return treeLock;
  }

  /**
   * Checks whether the layout of this element is dynamic and adjusts to the element's printable content. If set to
   * false, the element's minimum-size will be also used as maximum size.
   *
   * @return true, if the Element's layout is dynamic, false otherwise.
   */
  public boolean isDynamicContent() {
    return getStyle().getBooleanStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT );
  }

  /**
   * Defines the stylesheet property for the dynamic attribute. Calling this function with either parameter will
   * override any previously defined value for the dynamic attribute. The value can no longer be inherited from parent
   * stylesheets.
   *
   * @param dynamicContent
   *          the new state of the dynamic flag.
   */
  public void setDynamicContent( final boolean dynamicContent ) {
    getStyle().setBooleanStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, dynamicContent );
  }

  /**
   * Returns the currently assigned report definition.
   *
   * @return the report definition or null, if no report has been assigned.
   */
  public ReportDefinition getReportDefinition() {
    if ( parent != null ) {
      return parent.getReportDefinition();
    }
    return null;
  }

  /**
   * Returns the master-report element. This will be a MasterReport while outside of the report processing. Inside the
   * report processing (when called from a report-definition contained in a report-state), this will be a
   * ReportDefinitionImpl.
   *
   * @return the master report.
   */
  public ReportDefinition getMasterReport() {
    if ( parent != null ) {
      return parent.getMasterReport();
    }
    return null;
  }

  /**
   * Redefines the link target for this element.
   *
   * @param target
   *          the target
   */
  public void setHRefTarget( final String target ) {
    getStyle().setStyleProperty( ElementStyleKeys.HREF_TARGET, target );
  }

  /**
   * Returns the currently set link target for this element.
   *
   * @return the link target.
   */
  public String getHRefTarget() {
    return (String) getStyle().getStyleProperty( ElementStyleKeys.HREF_TARGET );
  }

  /**
   * Creates the global stylesheet for this element type. The global stylesheet is an immutable stylesheet that provides
   * reasonable default values for some of the style keys.
   * <p/>
   * The global default stylesheet is always the last stylesheet that will be queried for values.
   *
   * @return the global stylesheet.
   */
  public ElementStyleSheet getDefaultStyleSheet() {
    return ElementDefaultStyleSheet.getDefaultStyle();
  }

  /**
   * Adds a function to the report's collection of expressions.
   *
   * @param property
   *          the stylekey that will be modified by this element.
   * @param function
   *          the function.
   */
  public void setStyleExpression( final StyleKey property, final Expression function ) {
    if ( styleExpressions == null ) {
      if ( function == null ) {
        return;
      }

      styleExpressions = new HashMap<StyleKey, Expression>();
    }
    final Object oldValue;
    if ( function == null ) {
      oldValue = styleExpressions.remove( property );
    } else {
      oldValue = styleExpressions.put( property, function );
    }
    notifyNodePropertiesChanged( new StyleExpressionChange( property, (Expression) oldValue, function ) );
  }

  /**
   * Returns the expressions for the report.
   *
   * @param property
   *          the stylekey for which an style-expression is returned.
   * @return the expressions.
   */
  public Expression getStyleExpression( final StyleKey property ) {
    if ( styleExpressions == null ) {
      return null;
    }
    return styleExpressions.get( property );
  }

  /**
   * Returns a map of all style expressions attached to this element. The map is keyed by an StyleKey and contains
   * Expression instances.
   *
   * @return the expression.
   */
  public Map<StyleKey, Expression> getStyleExpressions() {
    if ( styleExpressions != null ) {
      return Collections.unmodifiableMap( styleExpressions );
    }
    return Collections.emptyMap();
  }

  /**
   * Returns the resource-key of the file that defined this element. This method may return null if the whole report was
   * created in memory.
   *
   * @return the the definition source.
   */
  public ResourceKey getDefinitionSource() {
    final Object o = getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE );
    if ( o instanceof ResourceKey ) {
      return (ResourceKey) o;
    }
    if ( parent != null ) {
      return parent.getDefinitionSource();
    }
    return null;
  }

  public ResourceKey getContentBase() {
    final Object o = getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.CONTENT_BASE );
    if ( o instanceof ResourceKey ) {
      return (ResourceKey) o;
    }
    if ( parent != null ) {
      return parent.getContentBase();
    }
    return null;
  }

  /**
   * Returns the element's change-tracker. The changetracker is a version indicator that tracks the number of changes
   * that have been made to an element and makes it easier to implement caching on top of elements or bands. Any change
   * will increase the change-tracker.
   *
   * @return the element's change tracking number.
   * @see Element#notifyNodePropertiesChanged()
   * @see Element#notifyNodeStructureChanged()
   */
  public long getChangeTracker() {
    return changeTracker;
  }

  /**
   * Notifies the element and any parent element that a property of this element has changed. This notification updates
   * the change tracker.
   */
  public void notifyNodePropertiesChanged() {
    updateChangedFlagInternal( this, ReportModelEvent.NODE_PROPERTIES_CHANGED, null );
  }

  public void notifyNodePropertiesChanged( final Object parameter ) {
    updateChangedFlagInternal( this, ReportModelEvent.NODE_PROPERTIES_CHANGED, parameter );
  }

  /**
   * Notifies the element and any parent element that a child node has been added. This notification updates the change
   * tracker.
   *
   * @param o
   *          the node that has been added.
   */
  public void notifyNodeChildAdded( final Object o ) {
    updateChangedFlagInternal( this, ReportModelEvent.NODE_ADDED, o );
  }

  /**
   * Notifies the element and any parent element that a child node has been removed. This notification updates the
   * change tracker.
   *
   * @param o
   *          the node that has been removed.
   */
  public void notifyNodeChildRemoved( final Object o ) {
    updateChangedFlagInternal( this, ReportModelEvent.NODE_REMOVED, o );
  }

  /**
   * Notifies the element and any parent element that the structure of this element has changed in some undisclosed way.
   * This notification updates the change tracker.
   */
  public void notifyNodeStructureChanged() {
    updateChangedFlagInternal( this, ReportModelEvent.NODE_STRUCTURE_CHANGED, null );
  }

  /**
   * Updates the change flag and notifies the parent, if this element has a parent.
   *
   * @param element
   *          the element that caused the notification.
   * @param type
   *          the notification type.
   * @param parameter
   *          the optional parameter further describing the event.
   */
  protected void updateChangedFlagInternal( final ReportElement element, final int type, final Object parameter ) {
    changeTracker += 1;
    if ( parent != null ) {
      parent.updateChangedFlagInternal( element, type, parameter );
    }
  }

  /**
   * Updates the internal change flag without notifying the parent. This is a internal method and unless you are calling
   * this method from a report-definition, you are probably doing something wrong.
   */
  protected final void updateInternalChangeFlag() {
    changeTracker += 1;
  }

  /**
   * This method is intended for subreport handling inside the process state. Messing with the change tracker in any
   * other way will break reports. You have been warned. This method is internal and may change or be renamed at any
   * time.
   *
   * @param changeTracker
   *          the new change tracker value
   */
  protected final void setChangeTracker( final long changeTracker ) {
    this.changeTracker = changeTracker;
  }

  /**
   * A helper method that serializes the element object.
   *
   * @param stream
   *          the stream to which the element should be serialized.
   * @throws IOException
   *           if an IO error occured or a property was not serializable.
   */
  private void writeObject( final ObjectOutputStream stream ) throws IOException {
    stream.defaultWriteObject();
    final ReportAttributeMap attributes = this.attributes;
    stream.writeLong( attributes.getChangeTracker() );
    final String[] nameSpaces = attributes.getNameSpaces();
    stream.writeObject( nameSpaces );
    for ( int i = 0; i < nameSpaces.length; i++ ) {
      final String nameSpace = nameSpaces[i];
      final String[] names = attributes.getNames( nameSpace );
      stream.writeObject( names );
      for ( int j = 0; j < names.length; j++ ) {
        final String name = names[j];
        final Object attribute = attributes.getAttribute( nameSpace, name );

        final AttributeMetaData data = getMetaData().getAttributeDescription( nameSpace, name );
        if ( data != null ) {
          if ( data.isTransient() ) {
            stream.writeByte( 1 );
            continue;
          }

          if ( attribute instanceof ResourceKey ) {
            final ResourceKey key = (ResourceKey) attribute;
            final ResourceKey parent = key.getParent();
            if ( AttributeNames.Core.NAMESPACE.equals( nameSpace )
                && ( AttributeNames.Core.CONTENT_BASE.equals( name ) || AttributeNames.Core.SOURCE.equals( name ) ) ) {
              if ( parent != null ) {
                // unwrap the content base attribute. After deserialization, the report assumes the bundle-location
                // as content base, as the bundle will be gone.
                if ( isKeySerializable( parent ) ) {
                  stream.writeByte( 0 );
                  SerializerHelper.getInstance().writeObject( parent, stream );
                } else {
                  stream.writeByte( 1 );
                }
              } else {
                // great, the report was never part of a bundle. That makes life easier and the key should be
                // safely serializable too.

                if ( isKeySerializable( key ) ) {
                  stream.writeByte( 0 );
                  SerializerHelper.getInstance().writeObject( key, stream );
                } else {
                  stream.writeByte( 1 );
                }
              }
            } else {
              if ( "Resource".equals( data.getValueRole() ) || parent != null ) {
                stream.writeByte( 0 );
                try {
                  final ResourceKey resourceKey =
                      ResourceKeyUtils.embedResourceInKey( locateResourceManager(), key, key.getFactoryParameters() );
                  SerializerHelper.getInstance().writeObject( resourceKey, stream );
                } catch ( ResourceException e ) {
                  throw new IOException( "Failed to convert resource-key into byte-array key: " + e );
                }
              } else {
                stream.writeByte( 0 );
                SerializerHelper.getInstance().writeObject( attribute, stream );
              }
            }
          } else if ( SerializerHelper.getInstance().isSerializable( attribute ) ) {
            stream.writeByte( 0 );
            SerializerHelper.getInstance().writeObject( attribute, stream );
          } else {
            stream.writeByte( 1 );
          }
        } else if ( attribute instanceof String ) {
          stream.writeByte( 0 );
          SerializerHelper.getInstance().writeObject( attribute, stream );
        } else {
          stream.writeByte( 1 );
        }
      }
    }
  }

  private boolean isKeySerializable( final ResourceKey key ) {
    try {
      final ObjectOutputStream oout = new ObjectOutputStream( new NullOutputStream() );
      oout.writeObject( key );
      oout.close();
      return true;
    } catch ( Exception e ) {
      return false;
    }
  }

  private ResourceManager locateResourceManager() {
    final ReportDefinition report = getMasterReport();
    if ( report instanceof MasterReport ) {
      MasterReport mr = (MasterReport) report;
      return mr.getResourceManager();
    }
    return new ResourceManager();
  }

  /**
   * A helper method that deserializes a object from the given stream.
   *
   * @param stream
   *          the stream from which to read the object data.
   * @throws IOException
   *           if an IO error occured.
   * @throws ClassNotFoundException
   *           if an referenced class cannot be found.
   */
  private void readObject( final ObjectInputStream stream ) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    this.attributes = new ReportAttributeMap<Object>( stream.readLong() );
    final String[] nameSpaces = (String[]) stream.readObject();
    for ( int i = 0; i < nameSpaces.length; i++ ) {
      final String nameSpace = nameSpaces[i];
      final String[] names = (String[]) stream.readObject();
      for ( int j = 0; j < names.length; j++ ) {
        final String name = names[j];
        final int nullHandler = stream.readByte();
        if ( nullHandler == 0 ) {
          final Object attribute = SerializerHelper.getInstance().readObject( stream );
          this.attributes.setAttribute( nameSpace, name, attribute );
        }
      }
    }
  }

  /**
   * Returns a string representation of the band, useful mainly for debugging purposes.
   *
   * @return a string representation of this band.
   */
  public String toString() {
    final StringBuilder b = new StringBuilder( 100 );
    b.append( this.getClass().getName() );
    b.append( "={name=\"" );
    b.append( getName() );
    b.append( "\", type=\"" );
    b.append( getElementTypeName() );
    b.append( "\"}" );
    return b.toString();
  }

  public ReportElement[] getChildElementsByType( final ElementType type ) {
    return ReportStructureMatcher.findElementsByType( this, type );
  }

  public ReportElement getChildElementByType( final ElementType type ) {
    return ReportStructureMatcher.findElementByType( this, type );
  }

  public ReportElement[] getChildElementsByName( final String name ) {
    return ReportStructureMatcher.findElementsByName( this, name );
  }

  public void copyInto( final Element target ) {
    final ElementMetaData metaData = getMetaData();
    final String[] attributeNamespaces = getAttributeNamespaces();
    for ( int i = 0; i < attributeNamespaces.length; i++ ) {
      final String namespace = attributeNamespaces[i];
      final String[] attributeNames = getAttributeNames( namespace );
      for ( int j = 0; j < attributeNames.length; j++ ) {
        final String name = attributeNames[j];
        final AttributeMetaData attributeDescription = metaData.getAttributeDescription( namespace, name );
        if ( attributeDescription == null ) {
          continue;
        }
        if ( attributeDescription.isTransient() ) {
          continue;
        }
        if ( attributeDescription.isComputed() ) {
          continue;
        }
        if ( AttributeNames.Core.ELEMENT_TYPE.equals( name ) && AttributeNames.Core.NAMESPACE.equals( namespace ) ) {
          continue;
        }
        target.setAttribute( namespace, name, getAttribute( namespace, name ), false );
      }
    }

    final String[] attrExprNamespaces = getAttributeExpressionNamespaces();
    for ( int i = 0; i < attrExprNamespaces.length; i++ ) {
      final String namespace = attrExprNamespaces[i];
      final String[] attributeNames = getAttributeExpressionNames( namespace );
      for ( int j = 0; j < attributeNames.length; j++ ) {
        final String name = attributeNames[j];

        final AttributeMetaData attributeDescription = metaData.getAttributeDescription( namespace, name );
        if ( attributeDescription == null ) {
          continue;
        }
        if ( attributeDescription.isTransient() ) {
          continue;
        }
        target.setAttributeExpression( namespace, name, getAttributeExpression( namespace, name ) );
      }
    }

    final ElementStyleSheet styleSheet = getStyle();
    final StyleKey[] styleKeys = styleSheet.getDefinedPropertyNamesArray();
    for ( int i = 0; i < styleKeys.length; i++ ) {
      final StyleKey styleKey = styleKeys[i];
      if ( styleKey != null ) {
        target.getStyle().setStyleProperty( styleKey, styleSheet.getStyleProperty( styleKey ) );
      }
    }

    final Set<Map.Entry<StyleKey, Expression>> styleExpressionEntries = getStyleExpressions().entrySet();
    for ( final Map.Entry<StyleKey, Expression> entry : styleExpressionEntries ) {
      target.setStyleExpression( entry.getKey(), entry.getValue() );
    }
  }

  public <T> T getElementContext( final Class<T> contextType ) {
    if ( contextType.isInstance( elementContext ) ) {
      return contextType.cast( elementContext );
    }

    try {
      final T elementContext = contextType.newInstance();
      this.elementContext = elementContext;
      return elementContext;
    } catch ( Exception e ) {
      throw new InvalidReportStateException( "Unable to create element context of " + contextType, e );
    }
  }

  public void copyAttributes( final ReportAttributeMap<Object> attributes ) {
    // noinspection unchecked
    this.attributes.putAll( attributes );
    this.cachedAttributes = null;

    final Object value = attributes.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE );
    if ( value instanceof ElementType ) {
      this.elementType = (ElementType) value;
    } else {
      this.elementType = LegacyType.INSTANCE;
    }
  }

}
