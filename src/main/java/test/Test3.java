package test;

import org.yuanheng.cookcc.*;
import java.io.*;

@CookCCOption
class Test3 extends Lexer3
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
