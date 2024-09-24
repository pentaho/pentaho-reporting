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

package org.pentaho.reporting.engine.classic.core.wizard;

public class ProxyDataSchemaDefinition implements DataSchemaDefinition {
  private DataSchemaDefinition rootDefinition;
  private DataSchemaDefinition overlayDefinition;

  public ProxyDataSchemaDefinition( final DataSchemaDefinition overlay, final DataSchemaDefinition root ) {
    this.overlayDefinition = overlay;
    this.rootDefinition = root;
  }

  public GlobalRule[] getGlobalRules() {
    final GlobalRule[] overlayRules = overlayDefinition.getGlobalRules();
    final GlobalRule[] rootRules = rootDefinition.getGlobalRules();

    final GlobalRule[] allRules = new GlobalRule[overlayRules.length + rootRules.length];
    System.arraycopy( overlayRules, 0, allRules, 0, overlayRules.length );
    System.arraycopy( rootRules, 0, allRules, overlayRules.length, rootRules.length );
    return allRules;
  }

  public MetaSelectorRule[] getIndirectRules() {
    final MetaSelectorRule[] overlayRules = overlayDefinition.getIndirectRules();
    final MetaSelectorRule[] rootRules = rootDefinition.getIndirectRules();

    final MetaSelectorRule[] allRules = new MetaSelectorRule[rootRules.length + overlayRules.length];
    System.arraycopy( overlayRules, 0, allRules, 0, overlayRules.length );
    System.arraycopy( rootRules, 0, allRules, overlayRules.length, rootRules.length );
    return allRules;
  }

  public DirectFieldSelectorRule[] getDirectRules() {
    final DirectFieldSelectorRule[] overlayRules = overlayDefinition.getDirectRules();
    final DirectFieldSelectorRule[] rootRules = rootDefinition.getDirectRules();

    final DirectFieldSelectorRule[] allRules = new DirectFieldSelectorRule[rootRules.length + overlayRules.length];
    System.arraycopy( overlayRules, 0, allRules, 0, overlayRules.length );
    System.arraycopy( rootRules, 0, allRules, overlayRules.length, rootRules.length );
    return allRules;
  }

  public Object clone() {
    try {
      final ProxyDataSchemaDefinition dataSchemaDefinition = (ProxyDataSchemaDefinition) super.clone();
      dataSchemaDefinition.overlayDefinition = (DataSchemaDefinition) overlayDefinition.clone();
      dataSchemaDefinition.rootDefinition = (DataSchemaDefinition) rootDefinition.clone();
      return dataSchemaDefinition;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( "Clone should always be supported in this class" );
    }
  }
}
