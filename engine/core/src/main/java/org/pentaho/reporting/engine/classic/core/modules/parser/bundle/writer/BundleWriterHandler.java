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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;

import java.io.IOException;

/**
 * A handler that writes a certain aspect into a own file. The name of file inside the bundle is returned as string. The
 * file name returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler
 * did not generate a file on its own, it should return null.
 *
 * @author Thomas Morgner
 */
public interface BundleWriterHandler {
  /**
   * Writes a certain aspect into a own file. The name of file inside the bundle is returned as string. The file name
   * returned is always absolute and can be made relative by using the IOUtils of LibBase. If the writer-handler did not
   * generate a file on its own, it should return null.
   *
   * @param bundle
   *          the bundle where to write to.
   * @param state
   *          the writer state to hold the current processing information.
   * @return the name of the newly generated file or null if no file was created.
   * @throws IOException
   *           if any error occured
   * @throws BundleWriterException
   *           if a bundle-management error occured.
   */
  public String writeReport( final WriteableDocumentBundle bundle, final BundleWriterState state ) throws IOException,
    BundleWriterException;

  /**
   * Indicates an processing order for this BundleWriterHandler. The lower the order, the earlier in the writing process
   * this BundleWriterHandler will be executed. This is important if the BundleWriterHandler does any kind of
   * modifications to the report definition since that would need to be performed before the BundleWriterHandlers that
   * perform the actual file creation based on the report definition.
   * <p/>
   * These values are all relative to each other, so the first BundleWriterHandler processed will be the one which
   * returns the lowest processing order.
   *
   * @return
   */
  public int getProcessingOrder();
}
