package org.snipsnap.snip;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

import java.io.Reader;

/**
 * Splits reader input into tokens.
 * Tokens are made out of letters
 * and digits
 *
 * @author stephan
 * @version $Id$
 **/

public final class SnipTokenizer extends Tokenizer {

  public SnipTokenizer(String field, Reader in) {
    input = in;
  }

  private int offset = 0, bufferIndex = 0, dataLen = 0;
  private final static int MAX_WORD_LEN = 255;
  private final static int IO_BUFFER_SIZE = 1024;
  private final char[] buffer = new char[MAX_WORD_LEN];
  private final char[] ioBuffer = new char[IO_BUFFER_SIZE];

  public final Token next() throws java.io.IOException {
    int length = 0;
    int start = offset;

    while (true) {
      char c;

      offset++;
      if (bufferIndex >= dataLen) {
        dataLen = input.read(ioBuffer);
        bufferIndex = 0;
      }
      ;
      if (dataLen == -1) {
        if (length > 0)
          break;
        else
          return null;
      } else
        c = (char) ioBuffer[bufferIndex++];

      if (Character.isLetterOrDigit(c)) {
        if (length == 0)			  // start of token
          start = offset - 1;

        buffer[length++] = Character.toLowerCase(c);
        // buffer it
        if (length == MAX_WORD_LEN)		  // buffer overflow!
          break;

      } else if (length > 0)			  // at non-Letter w/ chars
        break;					  // return 'em

    }

    return new Token(new String(buffer, 0, length), start, start + length);
  }
}
