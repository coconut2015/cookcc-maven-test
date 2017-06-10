package test;

import org.yuanheng.cookcc.*;
import java.io.*;

@CookCCOption
public class Test1 extends Lexer1
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
