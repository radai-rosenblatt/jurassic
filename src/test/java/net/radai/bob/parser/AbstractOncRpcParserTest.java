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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Radai Rosenblatt
 */
public abstract class AbstractOncRpcParserTest {

    protected ResultsContainer parse(Reader reader) throws IOException {
        OncRpcParser parser = new OncRpcParser();
        return parser.parse(reader);
    }

    protected ResultsContainer parse(String oncrpc) {
        try {
            return parse(new StringReader(oncrpc));
        } catch (IOException e) {
            throw new IllegalStateException(e); //should never happen
        }
    }

    protected ResultsContainer parseFile(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            return parse(reader);
        }
    }

    protected ResultsContainer parseFile(String fileName) throws IOException {
        try (InputStream stream = AbstractOncRpcParserTest.class.getClassLoader().getResourceAsStream(fileName)) {
            if (stream == null) {
                throw new IllegalArgumentException("unable to locate file " + fileName);
            }
            return parse(new InputStreamReader(stream));
        }
    }
}
