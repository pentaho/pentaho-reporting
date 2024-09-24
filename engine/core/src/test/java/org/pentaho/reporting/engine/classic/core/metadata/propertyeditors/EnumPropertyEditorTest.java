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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata.propertyeditors;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.CrosstabDetailMode;
import org.pentaho.reporting.engine.classic.core.CrosstabHeaderMode;
import org.pentaho.reporting.engine.classic.core.CrosstabNormalizationMode;
import org.pentaho.reporting.engine.classic.core.CrosstabSummaryPosition;
import org.pentaho.reporting.engine.classic.core.modules.misc.survey.SurveyScaleShapeType;
import org.pentaho.reporting.engine.classic.core.modules.misc.survey.SurveyScaleShapeTypePropertyEditor;
import org.pentaho.reporting.engine.classic.core.style.TableLayout;

public class EnumPropertyEditorTest {

  @Test
  public void testCrosstabSummaryPositionPropertyEditor() {
    EnumPropertyEditorChecker checker =
        new EnumPropertyEditorChecker( new CrosstabSummaryPositionPropertyEditor(), CrosstabSummaryPosition.class );
    checker.checkAll();
  }

  @Test
  public void testCrosstabDetailModePropertyEditor() {
    EnumPropertyEditorChecker checker =
        new EnumPropertyEditorChecker( new CrosstabDetailModePropertyEditor(), CrosstabDetailMode.class );
    checker.checkAll();
  }

  @Test
  public void testCrosstabHeaderModePropertyEditor() {
    EnumPropertyEditorChecker checker =
        new EnumPropertyEditorChecker( new CrosstabHeaderModePropertyEditor(), CrosstabHeaderMode.class );
    checker.checkAll();
  }

  @Test
  public void testCrosstabNormalizationModePropertyEditor() {
    EnumPropertyEditorChecker checker =
        new EnumPropertyEditorChecker( new CrosstabNormalizationModePropertyEditor(), CrosstabNormalizationMode.class );
    checker.checkAll();
  }

  @Test
  public void testSurveyScaleShapeTypePropertyEditor() {
    EnumPropertyEditorChecker checker =
        new EnumPropertyEditorChecker( new SurveyScaleShapeTypePropertyEditor(), SurveyScaleShapeType.class );
    checker.checkAll();
  }

  @Test
  public void testTableLayoutPropertyEditor() {
    EnumPropertyEditorChecker checker =
        new EnumPropertyEditorChecker( new TableLayoutPropertyEditor(), TableLayout.class );
    checker.checkAll();
  }
}
