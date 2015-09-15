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
 * along with Bob. If not, see <http://www.gnu.org/licenses/>.
 */

package net.radai.bob.grammar;

import net.radai.bob.parser.ValidatingErrorListener;
import net.radai.bob.util.Util;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.junit.Assert;
import org.junit.Before;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * @author Radai Rosenblatt
 */
public abstract class AbstractParserRuleTest {
    private Method parsingMethod;

    @Before
    public void resolveParseMethod() throws Exception {
        String methodName = getRuleName();
        parsingMethod = ONCRPCv2Parser.class.getDeclaredMethod(methodName);
    }

    protected ParseTree parse(String expr) throws Exception {
        ValidatingErrorListener errorListener = new ValidatingErrorListener();
        ANTLRInputStream input = new ANTLRInputStream(new StringReader(expr));
        ONCRPCv2Lexer lexer = new ONCRPCv2Lexer(input);
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ONCRPCv2Parser parser = new ONCRPCv2Parser(tokens);
        parser.addErrorListener(errorListener);

        try {
            return (ParseTree) parsingMethod.invoke(parser);
        } catch (InvocationTargetException e) {
            throw (Exception) Util.getRootCause(e);
        }
    }

    protected String getRuleName() {
        String className = getClass().getSimpleName();
        int index = className.lastIndexOf("ParserRuleTest");
        if (index == -1) {
            throw new IllegalStateException("unable to parse rule name from " + className);
        }
        String ruleName = className.substring(0, index);
        ruleName = ruleName.substring(0,1).toLowerCase(Locale.ROOT) + ruleName.substring(1); //lowercase 1st letter
        return ruleName;
    }

    protected void assertCorrect(String expr) throws Exception {
        ParseTree t = parse(expr);
        if (t instanceof RuleNode) {
            RuleNode rn = (RuleNode) t;
            int ri = rn.getRuleContext().getRuleIndex();
            String ruleName = ONCRPCv2Parser.ruleNames[ri];
            Assert.assertEquals(getRuleName(), ruleName);
        } else {
            Assert.fail();
        }
    }
}
