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

package org.pentaho.openformula.ui;

import junit.framework.TestCase;
import org.pentaho.openformula.ui.model2.FormulaDocument;
import org.pentaho.openformula.ui.model2.FormulaRootElement;

import javax.swing.text.BadLocationException;

public class FormulaDocumentTest extends TestCase {
  public FormulaDocumentTest() {
  }

  public FormulaDocumentTest( final String s ) {
    super( s );
  }

  public void testParseFunction() throws BadLocationException {
    FormulaDocument doc = new FormulaDocument();
    final String str = "=IF(IF([a];[b];\"C\");[c]; [d]) ";
    doc.insertString( 0, str, null );
    final FormulaRootElement element = doc.getRootElement();
    assertEquals( "Length", str.length(), doc.getLength() );
    assertEquals( "Number of elements: ", 17, element.getElementCount() );
  }

  public void testParseFunctionBad() throws BadLocationException {
    FormulaDocument doc = new FormulaDocument();
    final String str = "=IF(IF([a];[b];\"C\"));[c]; [d]) ";
    doc.insertString( 0, str, null );
    final FormulaRootElement element = doc.getRootElement();
    assertEquals( "Length", str.length(), doc.getLength() );
    assertEquals( "Number of elements: ", 18, element.getElementCount() );
  }

  public void testParseFunctionBad2() throws BadLocationException {
    FormulaDocument doc = new FormulaDocument();
    final String str = "=IF(([a];[b];\"C\"));[c]; [d]) ";
    doc.insertString( 0, str, null );
    final FormulaRootElement element = doc.getRootElement();
    assertEquals( "Length", str.length(), doc.getLength() );
    assertEquals( "Number of elements: ", 17, element.getElementCount() );
  }
}
