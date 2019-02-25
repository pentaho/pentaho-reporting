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
 * Copyright (c) 2001 - 2019 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.text;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.libraries.fonts.registry.BaselineInfo;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.runner.RunWith;


@RunWith( PowerMockRunner.class )
@PrepareForTest( TextUtility.class )
public class TextUtilityTest {

  @Test
  public void testCreateCorrectBaselineInfoStayEqual() throws Exception {
    PowerMockito.mockStatic( TextUtility.class );
    when(TextUtility.createPaddedBaselineInfo( anyInt(), any(FontMetrics.class), any(BaselineInfo.class) )).thenCallRealMethod();

    long maxHeight = (long) 15000;
    FontMetrics fontMetrics = mock( FontMetrics.class );
    when( fontMetrics.getMaxHeight() ).thenReturn( maxHeight );
    when( fontMetrics.getMaxAscent() ).thenReturn( (long) 9000 );
    when( fontMetrics.getMaxDescent() ).thenReturn( (long) 9000 );
    BaselineInfo baseLineInfo = new BaselineInfo();
    baseLineInfo.setDominantBaseline(BaselineInfo.MIDDLE);
    when( fontMetrics.getBaselines( anyInt(), any(BaselineInfo.class))).thenReturn( baseLineInfo );

    DefaultExtendedBaselineInfo baseLine = (DefaultExtendedBaselineInfo)TextUtility.createPaddedBaselineInfo( 'x', fontMetrics, null );
    Assert.assertEquals( baseLine.getBaselines()[ExtendedBaselineInfo.AFTER_EDGE], RenderableText.convert(maxHeight) );
  }

  @Test
  public void testCreateCorrectBaselineInfoCorrect() throws Exception {
    PowerMockito.mockStatic( TextUtility.class );
    when(TextUtility.createPaddedBaselineInfo( anyInt(), any(FontMetrics.class), any(BaselineInfo.class) )).thenCallRealMethod();

    long maxHeight = (long) 15000;
    FontMetrics fontMetrics = mock( FontMetrics.class );
    when( fontMetrics.getMaxHeight() ).thenReturn( maxHeight );
    when( fontMetrics.getMaxAscent() ).thenReturn( (long) 7500 );
    when( fontMetrics.getMaxDescent() ).thenReturn( (long) 7500 );
    BaselineInfo baseLineInfo = new BaselineInfo();
    baseLineInfo.setDominantBaseline(BaselineInfo.MIDDLE);
    when( fontMetrics.getBaselines( anyInt(), any(BaselineInfo.class))).thenReturn( baseLineInfo );

    DefaultExtendedBaselineInfo baseLine = (DefaultExtendedBaselineInfo)TextUtility.createPaddedBaselineInfo( 'x', fontMetrics, null );
    Assert.assertTrue( baseLine.getBaselines()[ExtendedBaselineInfo.AFTER_EDGE] >= RenderableText.convert(maxHeight) );
  }

  @Test
  public void testCreateCorrectBaselineWithMaxHeightGreaterThanAscentPlusDescent() throws Exception {
    PowerMockito.mockStatic( TextUtility.class );
    when(TextUtility.createPaddedBaselineInfo( anyInt(), any(FontMetrics.class), any(BaselineInfo.class) )).thenCallRealMethod();

    long maxHeight = (long) 15000;
    FontMetrics fontMetrics = mock( FontMetrics.class );
    when( fontMetrics.getMaxHeight() ).thenReturn( maxHeight );
    when( fontMetrics.getMaxAscent() ).thenReturn( (long) 4500 );
    when( fontMetrics.getMaxDescent() ).thenReturn( (long) 4500 );
    BaselineInfo baseLineInfo = new BaselineInfo();
    baseLineInfo.setDominantBaseline(BaselineInfo.MIDDLE);
    when( fontMetrics.getBaselines( anyInt(), any(BaselineInfo.class))).thenReturn( baseLineInfo );

    DefaultExtendedBaselineInfo baseLine = (DefaultExtendedBaselineInfo)TextUtility.createPaddedBaselineInfo( 'x', fontMetrics, null );
    Assert.assertEquals( baseLine.getBaselines()[ExtendedBaselineInfo.AFTER_EDGE], RenderableText.convert(maxHeight) );
  }

  @Test
  public void testCreateCorrectBaselineWithMaxHeightLowerThanAscentPlusDescent() throws Exception {
    PowerMockito.mockStatic( TextUtility.class );
    when(TextUtility.createPaddedBaselineInfo( anyInt(), any(FontMetrics.class), any(BaselineInfo.class) )).thenCallRealMethod();

    long maxHeight = (long) 15000;
    FontMetrics fontMetrics = mock( FontMetrics.class );
    when( fontMetrics.getMaxHeight() ).thenReturn( maxHeight );
    when( fontMetrics.getMaxAscent() ).thenReturn( (long) 15000 );
    when( fontMetrics.getMaxDescent() ).thenReturn( (long) 15000 );
    BaselineInfo baseLineInfo = new BaselineInfo();
    baseLineInfo.setDominantBaseline(BaselineInfo.MIDDLE);
    when( fontMetrics.getBaselines( anyInt(), any(BaselineInfo.class))).thenReturn( baseLineInfo );

    DefaultExtendedBaselineInfo baseLine = (DefaultExtendedBaselineInfo)TextUtility.createPaddedBaselineInfo( 'x', fontMetrics, null );
    Assert.assertTrue( baseLine.getBaselines()[ExtendedBaselineInfo.AFTER_EDGE] >= RenderableText.convert(maxHeight) );
  }
}



