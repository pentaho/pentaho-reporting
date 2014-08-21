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
 *  Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.libraries.base.util;

import java.io.IOException;
import java.io.StringReader;

/**
 * This class represents a parser that will remove SQL comments (both multi-line and single-line) from a string
 * representing a SQL query. It respects the notion of a string literal, such that if a comment appears in a string
 * literal, it is treated as part of the string instead of a comment. Both single-quoted and double-quoted string
 * literals are supported, including nested quotes (whether the SQL dialect supports them or not).
 *
 * @author Matt Burgess
 */
public class SqlCommentScrubber
{

  /**
   * End-of-File (EOF) indicator *
   */
  public static final int EOF = -1;

  /**
   * End-of-Line (EOL) indicator *
   */
  public static final int EOL = 10;

  /**
   * List of characters that can signify a string literal *
   */
  private static final int[] QUOTE_CHARS = {'\'', '"'};

  /**
   * Private constructor to enforce static access
   */
  private SqlCommentScrubber()
  {
  }

  /**
   * Checks to see whether the character is a quote character
   *
   * @param ch the input character to check
   * @return true if the input character is a quote character, false if not
   */
  private static boolean isQuoteChar(final int ch)
  {
    for (final int c : QUOTE_CHARS)
    {
      if (ch == c)
      {
        return true;
      }
    }
    return false;
  }

  /**
   * This method will remove SQL comments (both multi-line and single-line) from a string representing a SQL query. It
   * respects the notion of a string literal, such that if a comment appears in a string literal, it is treated as part
   * of the string instead of a comment. A simple state machine is implemented, keeping track of whether the current
   * character is starting, ending, or inside a comment construct. The state machine also checks to see if the current
   * character is starting, ending, or inside a single-quoted string literal, as this takes precedence over comment
   * constructs. In other words, comments inside strings are not actually comments, they are part of the string literal.
   *
   * @param text a string representing the SQL query to parse and from which to remove comments
   * @return the input string with SQL comments removed, or null if the input string is null
   */
  public static String removeComments(String text)
  {

    if (text == null)
    {
      return null;
    }

    StringBuilder queryWithoutComments = new StringBuilder();
    boolean blkComment = false;
    boolean lineComment = false;
    boolean inString = false;
    StringReader buffer = new StringReader(text);
    int ch;
    char currentStringChar = (char) QUOTE_CHARS[0];
    boolean done = false;

    try
    {
      while (!done)
      {
        switch (ch = buffer.read())
        {
          case EOF:
          { // End Of File
            done = true;
            break;
          }
          case '\'': // NOTE: Add cases for any other quote characters in QUOTE_CHARS
          case '"':
          { // String literals

            // If we're not in a comment, we're either entering or leaving a string
            if (!lineComment && !blkComment)
            {
              char cch = (char) ch;
              if (inString)
              {
                if (currentStringChar == cch)
                {
                  inString = false;
                }
              }
              else
              {
                inString = true;
                currentStringChar = cch;
              }
              queryWithoutComments.append(cch);
            }
            break;
          }
          case '/':
          { // multi-line comments

            // If we're not in a line comment, we might be entering a line or multi-line comment
            if (!lineComment)
            {
              ch = buffer.read();

              // If we see a multi-line comment starter (/*) and we're not in a string or
              // multi-line comment, then we have started a multi-line comment.
              if ((ch == '*') && (!blkComment) && (!inString))
              {
                blkComment = true;
              }
              else
              {
                // Otherwise if we aren't already in a block comment, pass the chars through
                if (!blkComment)
                {
                  queryWithoutComments.append('/');
                  queryWithoutComments.append((char) ch);
                }
              }
            }
            break;
          }
          case '*':
          { // multi-line comments

            // If we're in a multi-line comment, look ahead to see if we're about to exit
            if (blkComment)
            {
              ch = buffer.read();
              if (ch == '/')
              {
                blkComment = false;
              }
            }
            else
            {
              // if we're not in a multi-line or line comment, pass the char through
              if (!lineComment)
              {
                queryWithoutComments.append('*');
              }
            }
            break;
          }
          case '-':
          { // single-line comment

            // if we're not in a multi-line or line comment, we might be entering a line comment
            if (!blkComment && !lineComment)
            {
              ch = buffer.read();
              // If we look ahead to see another dash and we're not in a string, we're entering a line comment
              if (ch == '-' && !inString)
              {
                lineComment = true;
              }
              else
              {
                queryWithoutComments.append('-');
                queryWithoutComments.append((char) ch);
                // If it's a quote character, we're entering or leaving a string
                if (isQuoteChar(ch))
                {
                  char cch = (char) ch;
                  if (inString)
                  {
                    if (currentStringChar == cch)
                    {
                      inString = false;
                    }
                  }
                  else
                  {
                    inString = true;
                    currentStringChar = cch;
                  }
                }
              }
            }
            break;
          }
          case EOL:
          { // End Of Line
            // If we're not in a comment, pass the EOL through
            if (!blkComment && !lineComment)
            {
              queryWithoutComments.append((char) ch);
            }
            lineComment = false;
            break;
          }
          default:
          {
            // if we're not in a comment, pass the character through
            if (!blkComment && !lineComment)
            {
              queryWithoutComments.append((char) ch);
            }
            break;
          }
        }
      }
    }
    catch (IOException e)
    {
      // break on error, exit gracefully with altered query thus far
    }

    return queryWithoutComments.toString();
  }
}
