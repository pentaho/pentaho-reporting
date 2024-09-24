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

package org.pentaho.reporting.engine.classic.core.layout;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 26.04.2007, 20:23:40
 *
 * @author Thomas Morgner
 */
public class TextCache {
  public static class Result {
    private RenderNode[] text;
    private RenderNode[] finish;
    private StyleSheet styleSheet;
    private ReportAttributeMap attributeMap;

    protected Result( final RenderNode[] text, final RenderNode[] finish, final StyleSheet styleSheet,
        final ReportAttributeMap attributeMap ) {
      this.styleSheet = styleSheet;
      this.attributeMap = attributeMap;
      this.text = text.clone();
      this.finish = finish.clone();
    }

    public ReportAttributeMap getAttributeMap() {
      return attributeMap;
    }

    public StyleSheet getStyleSheet() {
      return styleSheet;
    }

    public RenderNode[] getText() {
      final RenderNode[] nodes = text.clone();
      final int nodeCount = nodes.length;
      for ( int i = 0; i < nodeCount; i++ ) {
        final RenderNode node = nodes[i];
        nodes[i] = node.derive( true );
      }
      return nodes;
    }

    public RenderNode[] getFinish() {
      final RenderNode[] nodes = finish.clone();
      final int nodeCount = nodes.length;
      for ( int i = 0; i < nodeCount; i++ ) {
        final RenderNode node = nodes[i];
        nodes[i] = node.derive( true );
      }
      return nodes;
    }
  }

  private static class InternalResult extends Result {
    private long changeTracker;
    private long attrChangeTracker;
    private String originalText;

    protected InternalResult( final RenderNode[] text, final RenderNode[] finish, final StyleSheet styleSheet,
        final long styleChangeTracker, final ReportAttributeMap attributeMap, final long attrChangeTracker,
        final String originalText ) {
      super( text, finish, styleSheet, attributeMap );
      this.changeTracker = styleChangeTracker;
      this.attrChangeTracker = attrChangeTracker;
      this.originalText = originalText;
    }

    public boolean isValid( final long changeTracker, final long attrsChangeTracker, final String text ) {
      if ( changeTracker != this.changeTracker ) {
        return false;
      }
      if ( attrsChangeTracker != this.attrChangeTracker ) {
        return false;
      }
      return ObjectUtilities.equal( text, originalText );
    }
  }

  private LFUMap<InstanceID, InternalResult> cache;

  public TextCache( final int maxEntries ) {
    cache = new LFUMap<InstanceID, InternalResult>( maxEntries );
  }

  public void store( final InstanceID instanceID, final long styleChangeTracker, final long attrChangeTracker,
      final String originalText, final StyleSheet styleSheet, final ReportAttributeMap attributeMap,
      final RenderNode[] text, final RenderNode[] finish ) {
    cache.put( instanceID, new InternalResult( text, finish, styleSheet, styleChangeTracker, attributeMap,
        attrChangeTracker, originalText ) );
  }

  public Result get( final InstanceID instanceID, final long styleChangeTracker, final long attributeChangeTracker,
      final String originalText ) {
    final InternalResult o = cache.get( instanceID );
    if ( o == null ) {
      return null;
    }
    if ( o.isValid( styleChangeTracker, attributeChangeTracker, originalText ) == false ) {
      cache.remove( instanceID );
      return null;
    }
    return o;
  }

}
