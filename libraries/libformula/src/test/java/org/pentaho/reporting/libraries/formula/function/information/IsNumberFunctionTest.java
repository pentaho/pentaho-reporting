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
 * Copyright (c) 2006 - 2024 Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.formula.function.information;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * @author Cedric Pronzato
 */
public class IsNumberFunctionTest extends FormulaTestBase {

	public void testDefault() throws Exception {
		runDefaultTest();
	}

	public Object[][] createDataTest() {
		return new Object[][] { { "ISNUMBER(1)", Boolean.TRUE },
				                { "ISNUMBER(\"1\")", Boolean.FALSE }, };
	}

	@Test
	public void runAdditionalTest() throws EvaluationException {
		IsNumberFunction function = new IsNumberFunction();

		ParameterCallback cbIntMock = mock(ParameterCallback.class);
		when(cbIntMock.getParameterCount()).thenReturn(1);
		when(cbIntMock.getType(anyInt())).thenReturn(NumberType.GENERIC_NUMBER);
		when(cbIntMock.getValue(anyInt())).thenReturn(new Long(55));

		ParameterCallback cbStrMock = mock(ParameterCallback.class);
		when(cbStrMock.getParameterCount()).thenReturn(1);
		when(cbStrMock.getType(anyInt())).thenReturn(TextType.TYPE);
		when(cbStrMock.getValue(anyInt())).thenReturn(new String("55"));

		TypeValuePair result = function.evaluate(getContext(), cbIntMock);
		assertEquals(result.getValue(), Boolean.TRUE);

		result = function.evaluate(getContext(), cbStrMock);
		assertEquals(result.getValue(), Boolean.FALSE);
	}
}
