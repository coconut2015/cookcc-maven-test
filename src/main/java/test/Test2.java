package test;

import org.yuanheng.cookcc.*;
import java.io.*;

@CookCCOption(lexerTable="ecs")
class Test2 extends Lexer2
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
