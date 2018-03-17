/*
 * Copyright (c) 2017-2018 Heng Yuan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

import java.util.Stack;

/**
 * @author	Heng Yuan
 */
public abstract class Lexer1
{


	////////////////////////////////////////////////////////////////////////
	//
	// Lexer States
	//
	////////////////////////////////////////////////////////////////////////
	protected final static int INITIAL = 0;

	// an internal class for lazy initiation
	private final static class cc_lexer
	{
		private final static char[] accept = ("\000\001\002").toCharArray ();
		private final static char[] ecs = ("\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\001").toCharArray ();
		private final static char[] base = ("\000\000\000").toCharArray ();
		private final static char[] next = ("\001\002").toCharArray ();
		private final static char[] check = ("\000\000").toCharArray ();
	}

	// for passing value from lexer to parser
	private Object _yyValue;

	private InputStream _yyIs = System.in;
	private byte[] _yyBuffer;
	private int _yyBufferSize = 4096;
	private int _yyMatchStart;
	private int _yyBufferEnd;

	private int _yyBaseState;

	private int _yyTextStart;
	private int _yyLength;

	private Stack<Integer> _yyLexerStack;
	private Stack<Object[]> _yyInputStack;


	/**
	 * Return the object associate with the token.  This function is only generated
	 * when the parser is not specified.
	 *
	 * @return	the object assoicated with the token.
	 */
	public Object yyValue ()
	{
		return _yyValue;
	}
	/**
	 * Set the current input.
	 *
	 * @param	is
	 *			the new input.
	 */
	public void setInput (InputStream is)
	{
		_yyIs = is;
	}

	/**
	 * Obtain the current input.
	 *
	 * @return	the current input
	 */
	public InputStream getInput ()
	{
		return _yyIs;
	}

	/**
	 * Switch the current input to the new input.  The old input and already
	 * buffered characters are pushed onto the stack.
	 *
	 * @param	is
	 * 			the new input
	 */
	public void yyPushInput (InputStream is)
	{
		int len = _yyBufferEnd - _yyMatchStart;
		byte[] leftOver = new byte[len];
		System.arraycopy (_yyBuffer, _yyMatchStart, leftOver, 0, len);

		Object[] states = new Object[4];
		states[0] = _yyIs;
		states[1] = leftOver;

		if (_yyInputStack == null)
			_yyInputStack = new Stack<Object[]> ();
		_yyInputStack.push (states);

		_yyIs = is;
		_yyMatchStart = 0;
		_yyBufferEnd = 0;
	}

	/**
	 * Switch the current input to the old input on stack.  The currently
	 * buffered characters are inserted infront of the old buffered characters.
	 */
	public void yyPopInput ()
	{
		Object[] states = _yyInputStack.pop ();

		_yyIs = (InputStream)states[0];
		byte[] leftOver = (byte[])states[1];

		int curLen = _yyBufferEnd - _yyMatchStart;

		if ((leftOver.length + curLen) > _yyBuffer.length)
		{
			byte[] newBuffer = new byte[leftOver.length + curLen];
			System.arraycopy (_yyBuffer, _yyMatchStart, newBuffer, 0, curLen);
			System.arraycopy (leftOver, 0, newBuffer, curLen, leftOver.length);
			_yyBuffer = newBuffer;
			_yyMatchStart = 0;
			_yyBufferEnd = leftOver.length + curLen;
		}
		else
		{
			int start = _yyMatchStart;
			int end = _yyBufferEnd;
			byte[] buffer = _yyBuffer;

			for (int i = 0; start < end; ++i, ++start)
				buffer[i] = buffer[start];
			System.arraycopy (leftOver, 0, buffer, curLen, leftOver.length);
			_yyMatchStart = 0;
			_yyBufferEnd = leftOver.length + curLen;
		}
	}

	/**
	 * Obtain the number of input objects on the stack.
	 *
	 * @return	the number of input objects on the stack.
	 */
	public int yyInputStackSize ()
	{
		return _yyInputStack == null ? 0 : _yyInputStack.size ();
	}


	/**
	 * Get the current token text.
	 * <p>
	 * Avoid calling this function unless it is absolutely necessary since it creates
	 * a copy of the token string.  The string length can be found by reading _yyLength
	 * or calling yyLength () function.
	 *
	 * @return	the current text token.
	 */
	public String yyText ()
	{
		if (_yyMatchStart == _yyTextStart)		// this is the case when we have EOF
			return null;
		return new String (_yyBuffer, _yyTextStart, _yyMatchStart - _yyTextStart);
	}

	/**
	 * Get the current text token's length.  Actions specified in the CookCC file
	 * can directly access the variable _yyLength.
	 *
	 * @return	the string token length
	 */
	public int yyLength ()
	{
		return _yyLength;
	}

	/**
	 * Print the current string token to the standard output.
	 */
	public void echo ()
	{
		System.out.print (yyText ());
	}

	/**
	 * Put all but n characters back to the input stream.  Be aware that calling
	 * yyLess (0) is allowed, but be sure to change the state some how to avoid
	 * an endless loop.
	 *
	 * @param	n
	 * 			The number of characters.
	 */
	protected void yyLess (int n)
	{
		if (n < 0)
			throw new IllegalArgumentException ("yyLess function requires a non-zero value.");
		if (n > (_yyMatchStart - _yyTextStart))
			throw new IndexOutOfBoundsException ("yyLess function called with a too large index value " + n + ".");
		_yyMatchStart = _yyTextStart + n;
	}

	/**
	 * Set the lexer's current state.
	 *
	 * @param	baseState
	 *			the base state index
	 */
	protected void begin (int baseState)
	{
		_yyBaseState = baseState;
	}

	/**
	 * Push the current state onto lexer state onto stack and
	 * begin the new state specified by the user.
	 *
	 * @param	newState
	 *			the new state.
	 */
	protected void yyPushLexerState (int newState)
	{
		if (_yyLexerStack == null)
			_yyLexerStack = new Stack<Integer> ();

		_yyLexerStack.push (new Integer (_yyBaseState));
		begin (newState);
	}

	/**
	 * Restore the previous lexer state.
	 */
	protected void yyPopLexerState ()
	{
		begin (_yyLexerStack.pop ());
	}


	// read more data from the input
	protected boolean yyRefreshBuffer () throws IOException
	{
		if (_yyBuffer == null)
			_yyBuffer = new byte[_yyBufferSize];
		if (_yyMatchStart > 0)
		{
			if (_yyBufferEnd > _yyMatchStart)
			{
				System.arraycopy (_yyBuffer, _yyMatchStart, _yyBuffer, 0, _yyBufferEnd - _yyMatchStart);
				_yyBufferEnd -= _yyMatchStart;
				_yyMatchStart = 0;
			}
			else
			{
				_yyMatchStart = 0;
				_yyBufferEnd = 0;
			}
		}
		else if (_yyBufferEnd == _yyBuffer.length)
		{
			byte[] newBuffer = new byte[_yyBuffer.length + _yyBuffer.length / 2];

			System.arraycopy (_yyBuffer, 0, newBuffer, 0, _yyBufferEnd);
			_yyBuffer = newBuffer;
		}

		int readSize = _yyIs.read (_yyBuffer, _yyBufferEnd, _yyBuffer.length - _yyBufferEnd);
		if (readSize > 0)
			_yyBufferEnd += readSize;
		else if (readSize < 0 && !yyWrap ())		// since we are at EOF, call yyWrap ().  If the return value of yyWrap is false, refresh buffer again
			return yyRefreshBuffer ();
		return readSize >= 0;
	}

	/**
	 * Reset the internal buffer.
	 */
	public void yyResetBuffer ()
	{
		_yyMatchStart = 0;
		_yyBufferEnd = 0;
	}

	/**
	 * Set the internal buffer size.  This action can only be performed
	 * when the buffer is empty.  Having a large buffer is useful to read
	 * a whole file in to increase the performance sometimes.
	 *
	 * @param	bufferSize
	 *			the new buffer size.
	 */
	public void setBufferSize (int bufferSize)
	{
		if (_yyBufferEnd > _yyMatchStart)
			throw new IllegalArgumentException ("Cannot change lexer buffer size at this moment.");
		_yyBufferSize = bufferSize;
		_yyMatchStart = 0;
		_yyBufferEnd = 0;
		if (_yyBuffer != null && bufferSize != _yyBuffer.length)
			_yyBuffer = new byte[bufferSize];
	}

	/**
	 * Reset the internal state to reuse the same parser.
	 * <p>
	 * Note, it does not change the buffer size, the input buffer, and the input stream.
	 * <p>
	 * Making this function protected so that it can be enabled only if the child class
	 * decides to make it public.
	 */
	protected void reset ()
	{

		// reset lexer state
		_yyMatchStart = 0;
		_yyBufferEnd = 0;
		_yyBaseState = 0;
		_yyTextStart = 0;
		_yyLength = 0;

		if (_yyLexerStack != null)
			_yyLexerStack.clear ();
		if (_yyInputStack != null)
			_yyInputStack.clear ();

	}

	/**
	 * Call this function to start the scanning of the input.
	 *
	 * @return	a token or status value.
	 * @throws	IOException
	 *			in case of I/O error.
	 */
	public int yyLex () throws IOException
	{

		char[] cc_ecs = cc_lexer.ecs;
		char[] cc_next = cc_lexer.next;
		char[] cc_check = cc_lexer.check;
		char[] cc_base = cc_lexer.base;
		char[] cc_accept = cc_lexer.accept;

		byte[] buffer = _yyBuffer;

		while (true)
		{
			// initiate variables necessary for lookup
			int cc_matchedState = _yyBaseState;

			int matchedLength = 0;

			int internalBufferEnd = _yyBufferEnd;
			int lookahead = _yyMatchStart;


			// the DFA lookup
			while (true)
			{
				// check buffer status
				if (lookahead < internalBufferEnd)
				{
					// now okay to process the character
					int cc_toState;
					int symbol = cc_ecs[buffer[lookahead] & 0xff];
					cc_toState = cc_matchedState;
					if (cc_check[symbol + cc_base[cc_matchedState]] == cc_matchedState)
						cc_toState = cc_next[symbol + cc_base[cc_matchedState]];
					else
						cc_toState = 0;

					if (cc_toState == 0)
						break;

					cc_matchedState = cc_toState;
					++lookahead;
					++matchedLength;

				}
				else
				{
					int lookPos = lookahead - _yyMatchStart;
					boolean refresh = yyRefreshBuffer ();
					buffer = _yyBuffer;
					internalBufferEnd = _yyBufferEnd;
					lookahead = _yyMatchStart + lookPos;
					if (! refresh)
					{
						// <<EOF>>
						int cc_toState;
						int symbol = cc_ecs[256];
						if (cc_check[symbol + cc_base[cc_matchedState]] == cc_matchedState)
							cc_toState = cc_next[symbol + cc_base[cc_matchedState]];
						else
							cc_toState = 0;

						if (cc_toState != 0)
							cc_matchedState = cc_toState;
						break;
					}
				}
			}

			_yyTextStart = _yyMatchStart;
			_yyMatchStart += matchedLength;
			_yyLength = matchedLength;


			switch (cc_accept[cc_matchedState])
			{
				case 1:	// .|\n
				{
					m_this.scanIgnore ();
				}
				case 6: break;
				case 2:	// <<EOF>>
				{
					return m_this.scanEof ();
				}
				case 7: break;
				case 3:	// .|\n
				{
					echo ();			// default character action
				}
				case 8: break;
				case 4:	// <<EOF>>
				{
					return 0;			// default EOF action
				}
				case 9: break;
				default:
					throw new IOException ("Internal error in Lexer1 lexer.");
			}

		}
	}


	private final test.Test1 m_this = (test.Test1)this;

	/**
	 * This function is used to change the initial state for the lexer.
	 *
	 * @param	state
	 *			the name of the state
	 */
	protected void begin (String state)
	{
		if ("INITIAL".equals (state))
		{
			begin (INITIAL);
			return;
		}
		throw new IllegalArgumentException ("Unknown lexer state: " + state);
	}

	/**
	 * Push the current state onto lexer state onto stack and
	 * begin the new state specified by the user.
	 *
	 * @param	state
	 *			the new state.
	 */
	protected void yyPushLexerState (String state)
	{
		if ("INITIAL".equals (state))
		{
			yyPushLexerState (INITIAL);
			return;
		}
		throw new IllegalArgumentException ("Unknown lexer state: " + state);
	}

	/**
	 * Check if there are more inputs.  This function is called when EOF is
	 * encountered.
	 *
	 * @return	true to indicate no more inputs.
	 * @throws	IOException
	 * 			in case of an IO error
	 */
	protected boolean yyWrap () throws IOException
	{
		if (yyInputStackSize () > 0)
		{
			yyPopInput ();
			return false;
		}
		return true;
	}


	protected static InputStream open (String file) throws IOException
	{
		return new FileInputStream (file);
	}


/*
 * lexer properties:
 * unicode = false
 * bol = false
 * backup = false
 * cases = 4
 * table = compressed
 * ecs = 2
 * states = 3
 * max symbol value = 256
 *
 * memory usage:
 * full table = 771
 * ecs table = 263
 * next = 2
 * check = 2
 * compressed table = 261
 *
 */
}
