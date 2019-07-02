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
 * Copyright (c) 2006 - 2019 Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.formula;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.formula.common.TestFormulaContext;

import java.util.ArrayList;
import java.util.HashSet;

public class OpenFormulaConformance extends TestCase {
  public static final String SMALL_GROUP =
    "ABS; ACOS; AND; ASIN; ATAN; ATAN2; AVERAGE; AVERAGEIF; CHOOSE; COLUMNS; COS; COUNT; COUNTA; COUNTBLANK; COUNTIF; "
      + "DATE; DAVERAGE; DAY; DCOUNT; DCOUNTA; DDB; DEGREES; DGET; DMAX; DMIN; DPRODUCT; DSTDEV; DSTDEVP; DSUM; "
      + "DVAR; DVARP; EVEN; EXACT; EXP; FACT; FALSE; FIND; FV; HLOOKUP; HOUR; IF; INDEX; INT; IRR; ISBLANK; ISERR; "
      + "ISERROR; ISLOGICAL; ISNA; ISNONTEXT; ISNUMBER; ISTEXT; LEFT; LEN; LN; LOG; LOG10; LOWER; MATCH; MAX; MID; "
      + "MIN; MINUTE; MOD; MONTH; N; NA; NOT; NOW; NPER; NPV; ODD; OR; PI; PMT; POWER; PRODUCT; PROPER; PV; RADIANS; "
      + "RATE; REPLACE; REPT; RIGHT; ROUND; ROWS; SECOND; SIN; SLN; SQRT; STDEV; STDEVP; SUBSTITUTE; SUM; SUMIF; SYD; "
      + "T; TAN; TIME; TODAY; TRIM; TRUE; TRUNC; UPPER; VALUE; VAR; VARP; VLOOKUP; WEEKDAY; YEAR";

  public static final String MEDIUM_GROUP = SMALL_GROUP + "; "
    + "AACCRINT; ACCRINTM; ACOSH; ACOT; ACOTH; ADDRESS; ASINH; ATANH; AVEDEV; BESSELI; BESSELJ; BESSELK; BESSELY; "
    + "BETADIST; BETAINV; BINOMDIST; CEILING; CHAR; CLEAN; CODE; COLUMN; COMBIN; CONCATENATE; CONFIDENCE; CONVERT; "
    + "CORREL; COSH; COT; COTH; COUPDAYBS; COUPDAYS; COUPDAYSNC; COUPNCD; COUPNUM; COUPPCD; COVAR; CRITBINOM; "
    + "CUMIPMT; CUMPRINC; DATEVALUE; DAYS360; DB; DEVSQ; DISC; DOLLARDE; DOLLARFR; DURATION; EFFECT; EOMONTH; ERF; "
    + "ERFC; EXPONDIST; FISHER; FISHERINV; FIXED; FLOOR; FORECAST; FTEST; GAMMADIST; GAMMAINV; GAMMALN; GCD; GEOMEAN; "
    + "HARMEAN; HYPGEOMDIST; INTERCEPT; INTRATE; ISEVEN; ISODD; ISOWEEKNUM; KURT; LARGE; LCM; LEGACY.CHIDIST; "
    + "LEGACY.CHIINV; LEGACY.CHITEST; LEGACY.FDIST; LEGACY.FINV; LEGACY.NORMSDIST; LEGACY.NORMSINV; LEGACY.TDIST; "
    + "LINEST; LOGEST; LOGINV; LOGNORMDIST; LOOKUP; MDURATION; MEDIAN; MINVERSE; MIRR; MMULT; MODE; MROUND; "
    + "MULTINOMIAL; NEGBINOMDIST; NETWORKDAYS; NOMINAL; ODDFPRICE; ODDFYIELD; ODDLPRICE; ODDLYIELD; OFFSET; PEARSON; "
    + "PERCENTILE; PERCENTRANK; PERMUT; POISSON; PRICE; PRICEMAT; PROB; QUARTILE; QUOTIENT; RAND; RANDBETWEEN; RANK; "
    + "RECEIVED; ROMAN; ROUNDDOWN; ROUNDUP; ROW; RSQ; SERIESSUM; SIGN; SINH; SKEW; SKEWP; SLOPE; SMALL; SQRTPI; "
    + "STANDARDIZE; STDEVA; STDEVPA; STEYX; SUBTOTAL; SUMPRODUCT; SUMSQ; SUMX2MY2; SUMX2PY2; SUMXMY2; TANH; TBILLEQ; "
    + "TBILLPRICE; TBILLYIELD; TIMEVALUE; TINV; TRANSPOSE; TREND; TRIMMEAN; TTEST; TYPE; VARA; VDB; WEEKNUM; WEIBULL; "
    + "WORKDAY; XIRR; XNPV; YEARFRAC; YIELD; YIELDDISC; YIELDMAT; ZTEST";

  public static final String LARGE_GROUP = MEDIUM_GROUP + "; "
    + "AMORLINC; ARABIC; AREAS; ASC; AVERAGEA; AVERAGEIFS; BASE; BIN2DEC; BIN2HEX; BIN2OCT; BINOM.DIST.RANGE; BITAND; "
    + "BITLSHIFT; BITOR; BITRSHIFT; BITXOR; CHISQDIST; CHISQINV; COMBINA; COMPLEX; COUNTIFS; CSC; CSCH; DATEDIF; "
    + "DAYS; DDE; DEC2BIN; DEC2HEX; DEC2OCT; DECIMAL; DELTA; EDATE; ERROR.TYPE; EUROCONVERT; FACTDOUBLE; FDIST; "
    + "FINDB; FINV; FORMULA; FREQUENCY; FVSCHEDULE; GAMMA; GAUSS; GESTEP; GETPIVOTDATA; GROWTH; HEX2BIN; HEX2DEC; "
    + "HEX2OCT; HYPERLINK; IFERROR; IFNA; IMABS; IMAGINARY; IMARGUMENT; IMCONJUGATE; IMCOS; IMCOT; IMCSC; IMCSCH; "
    + "IMDIV; IMEXP; IMLN; IMLOG10; IMLOG2; IMPOWER; IMPRODUCT; IMREAL; IMSEC; IMSECH; IMSIN; IMSQRT; IMSUB; IMSUM; "
    + "IMTAN; INDIRECT; INFO; IPMT; ISFORMULA; ISPMT; ISREF; JIS; LEFTB; LENB; MAXA; MDETERM; MULTIPLE.OPERATIONS; "
    + "MUNIT; MIDB; MINA; NORMDIST; NORMINV; NUMBERVALUE; OCT2BIN; OCT2DEC; OCT2HEX; PDURATION; PERMUTATIONA; PHI; "
    + "PPMT; PRICEDISC; REPLACEB; RIGHTB; RRI; SEARCH; SEARCHB; SEC; SECH; SHEET; SHEETS; SUMIFS; TEXT; UNICHAR; "
    + "UNICODE; VARPA; XOR";

  private FormulaContext context;
  private HashSet<String> implementedFunctions;

  public OpenFormulaConformance() {
  }

  public OpenFormulaConformance( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    context = new TestFormulaContext();
    LibFormulaBoot.getInstance().start();

    implementedFunctions = new HashSet<>();
    final String[] functionNames = context.getFunctionRegistry().getFunctionNames();

    for ( String functionName : functionNames ) {
      implementedFunctions.add( functionName );
    }
  }

  public void testIsInSmallGroup() {
    isInGroup( "SmallGroup", SMALL_GROUP );
  }

  public void testIsInMediumGroup() {
    isInGroup( "MediumGroup", MEDIUM_GROUP );
  }

  public void testIsInLargeGroup() {
    isInGroup( "LargeGroup", LARGE_GROUP );
  }

  private void isInGroup( final String groupName, final String groupFunctions ) {
    final ArrayList<String> functionsNotInGroup = new ArrayList<>();
    final String[] split = groupFunctions.split( ";" );
    for ( String s : split ) {
      final String func = s.trim();

      if ( !implementedFunctions.contains( func ) ) {
        functionsNotInGroup.add( func );
      }
    }

    DebugLog.log( "The following "
      + functionsNotInGroup.size() + " (out of " + split.length
      + " requested) functions are not yet implemented for the " + groupName
      + " conformance: " + functionsNotInGroup.toString() );
  }
}
