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
