package test;

import org.yuanheng.cookcc.*;
import java.io.*;

@CookCCOption
class Test4 extends Lexer4
{
	@Lex (pattern=".|\\n")
	void scanIgnore ()
	{
	}

	@Lex (pattern="<<EOF>>")
	int scanEof ()
	{
		return 0;
	}
}
