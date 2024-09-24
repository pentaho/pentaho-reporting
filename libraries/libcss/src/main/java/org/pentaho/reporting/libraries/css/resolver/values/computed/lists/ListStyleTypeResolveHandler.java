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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.resolver.values.computed.lists;

import org.pentaho.reporting.libraries.css.keys.list.ListStyleTypeAlgorithmic;
import org.pentaho.reporting.libraries.css.keys.list.ListStyleTypeAlphabetic;
import org.pentaho.reporting.libraries.css.keys.list.ListStyleTypeGlyphs;
import org.pentaho.reporting.libraries.css.keys.list.ListStyleTypeNumeric;
import org.pentaho.reporting.libraries.css.keys.list.ListStyleTypeOther;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Todo: On NORMAL, this resolver should query the counter-style definition for "list-item".
 */
public class ListStyleTypeResolveHandler extends ConstantsResolveHandler {
  public ListStyleTypeResolveHandler() {
    addNormalizeValue( ListStyleTypeOther.ASTERISKS );
    addNormalizeValue( ListStyleTypeOther.CIRCLED_DECIMAL );
    addNormalizeValue( ListStyleTypeOther.CIRCLED_LOWER_LATIN );
    addNormalizeValue( ListStyleTypeOther.CIRCLED_UPPER_LATIN );
    addNormalizeValue( ListStyleTypeOther.DOTTED_DECIMAL );
    addNormalizeValue( ListStyleTypeOther.DOUBLE_CIRCLED_DECIMAL );
    addNormalizeValue( ListStyleTypeOther.FILLED_CIRCLED_DECIMAL );
    addNormalizeValue( ListStyleTypeOther.FOOTNOTES );
    addNormalizeValue( ListStyleTypeOther.PARANTHESISED_DECIMAL );
    addNormalizeValue( ListStyleTypeOther.PARANTHESISED_LOWER_LATIN );

    addNormalizeValue( ListStyleTypeNumeric.ARABIC_INDIC );
    addNormalizeValue( ListStyleTypeNumeric.BENGALI );
    addNormalizeValue( ListStyleTypeNumeric.BINARY );
    addNormalizeValue( ListStyleTypeNumeric.CAMBODIAN );
    addNormalizeValue( ListStyleTypeNumeric.DECIMAL );
    addNormalizeValue( ListStyleTypeNumeric.DECIMAL_LEADING_ZERO );
    addNormalizeValue( ListStyleTypeNumeric.DEVANAGARI );
    addNormalizeValue( ListStyleTypeNumeric.GUJARATI );
    addNormalizeValue( ListStyleTypeNumeric.GUJARATI );
    addNormalizeValue( ListStyleTypeNumeric.GURMUKHI );
    addNormalizeValue( ListStyleTypeNumeric.KANNADA );
    addNormalizeValue( ListStyleTypeNumeric.KHMER );
    addNormalizeValue( ListStyleTypeNumeric.LAO );
    addNormalizeValue( ListStyleTypeNumeric.LOWER_HEXADECIMAL );
    addNormalizeValue( ListStyleTypeNumeric.MALAYALAM );
    addNormalizeValue( ListStyleTypeNumeric.MONGOLIAN );
    addNormalizeValue( ListStyleTypeNumeric.MYANMAR );
    addNormalizeValue( ListStyleTypeNumeric.OCTAL );
    addNormalizeValue( ListStyleTypeNumeric.ORIYA );
    addNormalizeValue( ListStyleTypeNumeric.PERSIAN );
    addNormalizeValue( ListStyleTypeNumeric.TELUGU );
    addNormalizeValue( ListStyleTypeNumeric.THAI );
    addNormalizeValue( ListStyleTypeNumeric.TIBETIAN );
    addNormalizeValue( ListStyleTypeNumeric.UPPER_HEXADECIMAL );
    addNormalizeValue( ListStyleTypeNumeric.URDU );

    addNormalizeValue( ListStyleTypeGlyphs.BOX );
    addNormalizeValue( ListStyleTypeGlyphs.CHECK );
    addNormalizeValue( ListStyleTypeGlyphs.CIRCLE );
    addNormalizeValue( ListStyleTypeGlyphs.DIAMOND );
    addNormalizeValue( ListStyleTypeGlyphs.DISC );
    addNormalizeValue( ListStyleTypeGlyphs.HYPHEN );
    addNormalizeValue( ListStyleTypeGlyphs.SQUARE );

    addNormalizeValue( ListStyleTypeAlphabetic.AFAR );
    addNormalizeValue( ListStyleTypeAlphabetic.AMHARIC );
    addNormalizeValue( ListStyleTypeAlphabetic.AMHARIC_ABEGEDE );
    addNormalizeValue( ListStyleTypeAlphabetic.AMHARIC_ABEGEDE );
    addNormalizeValue( ListStyleTypeAlphabetic.CJK_EARTHLY_BRANCH );
    addNormalizeValue( ListStyleTypeAlphabetic.CJK_HEAVENLY_STEM );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_ABEGEDE );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_ABEGEDE_AM_ET );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_ABEGEDE_GEZ );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_ABEGEDE_TI_ER );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_ABEGEDE_TI_ER );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_ABEGEDE_TI_ET );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_HALEHAME_AA_ER );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_HALEHAME_AA_ET );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_HALEHAME_AM_ET );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_HALEHAME_GEZ );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_HALEHAME_OM_ET );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_HALEHAME_SID_ET );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_HALEHAME_SO_ET );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_HALEHAME_TI_ER );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_HALEHAME_TI_ET );
    addNormalizeValue( ListStyleTypeAlphabetic.ETHIOPIC_HALEHAME_TIG );
    addNormalizeValue( ListStyleTypeAlphabetic.HANGUL );
    addNormalizeValue( ListStyleTypeAlphabetic.HANGUL_CONSONANT );
    addNormalizeValue( ListStyleTypeAlphabetic.HIRAGANA );
    addNormalizeValue( ListStyleTypeAlphabetic.HIRAGANA_IROHA );
    addNormalizeValue( ListStyleTypeAlphabetic.KATAKANA );
    addNormalizeValue( ListStyleTypeAlphabetic.KATAKANA_IROHA );
    addNormalizeValue( ListStyleTypeAlphabetic.LOWER_ALPHA );
    addNormalizeValue( ListStyleTypeAlphabetic.LOWER_GREEK );
    addNormalizeValue( ListStyleTypeAlphabetic.LOWER_LATIN );
    addNormalizeValue( ListStyleTypeAlphabetic.LOWER_NORWEGIAN );
    addNormalizeValue( ListStyleTypeAlphabetic.OROMO );
    addNormalizeValue( ListStyleTypeAlphabetic.SIDAMA );
    addNormalizeValue( ListStyleTypeAlphabetic.SOMALI );
    addNormalizeValue( ListStyleTypeAlphabetic.TIGRE );
    addNormalizeValue( ListStyleTypeAlphabetic.TIGRINYA_ER );
    addNormalizeValue( ListStyleTypeAlphabetic.TIGRINYA_ER_ABEGEDE );
    addNormalizeValue( ListStyleTypeAlphabetic.TIGRINYA_ET );
    addNormalizeValue( ListStyleTypeAlphabetic.TIGRINYA_ET_ABEGEDE );
    addNormalizeValue( ListStyleTypeAlphabetic.UPPER_ALPHA );
    addNormalizeValue( ListStyleTypeAlphabetic.UPPER_GREEK );
    addNormalizeValue( ListStyleTypeAlphabetic.UPPER_LATIN );
    addNormalizeValue( ListStyleTypeAlphabetic.UPPER_NORWEGIAN );

    addNormalizeValue( ListStyleTypeAlgorithmic.ARMENIAN );
    addNormalizeValue( ListStyleTypeAlgorithmic.CJK_IDEOGRAPHIC );
    addNormalizeValue( ListStyleTypeAlgorithmic.ETHIOPIC_NUMERIC );
    addNormalizeValue( ListStyleTypeAlgorithmic.GEORGIAN );
    addNormalizeValue( ListStyleTypeAlgorithmic.HEBREW );
    addNormalizeValue( ListStyleTypeAlgorithmic.JAPANESE_FORMAL );
    addNormalizeValue( ListStyleTypeAlgorithmic.JAPANESE_INFORMAL );
    addNormalizeValue( ListStyleTypeAlgorithmic.LOWER_ARMENIAN );
    addNormalizeValue( ListStyleTypeAlgorithmic.LOWER_ROMAN );
    addNormalizeValue( ListStyleTypeAlgorithmic.SIMP_CHINESE_FORMAL );
    addNormalizeValue( ListStyleTypeAlgorithmic.SIMP_CHINESE_INFORMAL );
    addNormalizeValue( ListStyleTypeAlgorithmic.SYRIAC );
    addNormalizeValue( ListStyleTypeAlgorithmic.TAMIL );
    addNormalizeValue( ListStyleTypeAlgorithmic.TRAD_CHINESE_FORMAL );
    addNormalizeValue( ListStyleTypeAlgorithmic.TRAD_CHINESE_INFORMAL );
    addNormalizeValue( ListStyleTypeAlgorithmic.UPPER_ARMENIAN );
    addNormalizeValue( ListStyleTypeAlgorithmic.UPPER_ROMAN );

    setFallback( ListStyleTypeNumeric.DECIMAL );
  }
}
