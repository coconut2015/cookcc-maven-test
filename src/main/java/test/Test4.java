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

import org.yuanheng.cookcc.CookCCOption;
import org.yuanheng.cookcc.Lex;

/**
 * @author	Heng Yuan
 */
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
