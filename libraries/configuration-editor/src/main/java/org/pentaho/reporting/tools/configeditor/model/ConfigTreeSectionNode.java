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
