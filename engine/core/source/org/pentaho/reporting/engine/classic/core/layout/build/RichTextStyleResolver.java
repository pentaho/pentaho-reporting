/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.build;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;
import org.pentaho.reporting.engine.classic.core.util.AbstractStructureVisitor;

public class RichTextStyleResolver extends AbstractStructureVisitor
{
  private SimpleStyleResolver simpleStyleResolver;
  private ResolverStyleSheet resolveStyleSheet;

  public RichTextStyleResolver()
  {
    this.simpleStyleResolver = new SimpleStyleResolver();
    this.resolveStyleSheet = new ResolverStyleSheet();
  }

  protected void traverseSection(final Section section)
  {
    traverseSectionWithoutSubReports(section);
  }

  public void resolve(final Section section)
  {
    inspectElement(section);
    traverseSection(section);
  }

  protected void inspectElement(final ReportElement element)
  {
    simpleStyleResolver.resolve(element, resolveStyleSheet);
    element.setComputedStyle(new SimpleStyleSheet(resolveStyleSheet));
  }

  public static void resolveStyle(final Section section)
  {
    final RichTextStyleResolver richTextStyleResolver = new RichTextStyleResolver();
    richTextStyleResolver.resolve(section);
  }


}
