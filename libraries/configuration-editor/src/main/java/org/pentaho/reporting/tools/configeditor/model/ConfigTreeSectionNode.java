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


package org.pentaho.reporting.tools.configeditor.model;

/**
 * The section node contains the modules for the given section. There are only two known sections, the global section,
 * which contains all boot-time keys and the local section, which contains all report-local configuration keys.
 *
 * @author Thomas Morgner
 */
public class ConfigTreeSectionNode extends AbstractConfigTreeNode {
  /**
   * Creates a new section node with the specified name.
   *
   * @param name the name of the node.
   */
  public ConfigTreeSectionNode( final String name ) {
    super( name );
  }

  /**
   * Removes all childs from this node.
   */
  public void reset() {
    super.reset();
  }
}
