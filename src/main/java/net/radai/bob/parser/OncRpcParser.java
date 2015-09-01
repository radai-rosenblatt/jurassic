/*
 * This file is part of Bob.
 *
 * Bob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.radai.bob.parser;

import net.radai.bob.grammar.ONCRPCv2Lexer;
import net.radai.bob.grammar.ONCRPCv2Parser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Radai Rosenblatt
 */
public class OncRpcParser {
    public ResultsContainer parse(Reader reader) throws IOException {
        ValidatingErrorListener errorListener = new ValidatingErrorListener();
        ANTLRInputStream input = new ANTLRInputStream(reader);
        ONCRPCv2Lexer lexer = new ONCRPCv2Lexer(input);
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ONCRPCv2Parser parser = new ONCRPCv2Parser(tokens);
        parser.addErrorListener(errorListener);
        ONCRPCv2Parser.Oncrpcv2SpecificationContext tree = parser.oncrpcv2Specification();
        ResultsContainer results = new ResultsContainer();
        OncRpcTreeListener listener = new OncRpcTreeListener(results);
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);
        return results;
    }
}
