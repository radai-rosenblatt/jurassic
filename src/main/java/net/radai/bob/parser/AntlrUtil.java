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

package net.radai.bob.parser;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;

/**
 * @author Radai Rosenblatt
 */
public class AntlrUtil {
    public static TerminalNode resolveToTerminal(Tree t) {
        if (t instanceof TerminalNode) {
            return (TerminalNode) t;
        }
        if (t.getChildCount() != 1) {
            throw new IllegalArgumentException("non-terminal node has != 1 children: " + t);
        }
        return resolveToTerminal(t.getChild(0));
    }

    public static boolean hasChild(Tree t, String terminal) {
        for (int i=0; i<t.getChildCount(); i++) {
            Tree child = t.getChild(i);
            if (!(child instanceof TerminalNode)) {
                continue;
            }
            if (terminal.equals(((TerminalNode) child).getText())) {
                return true;
            }
        }
        return false;
    }
}
