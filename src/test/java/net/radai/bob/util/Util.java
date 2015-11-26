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

package net.radai.bob.util;

import net.radai.bob.model.Namespace;
import net.radai.bob.parser.OncRpcParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Radai Rosenblatt
 */
public class Util {
    
    public static Throwable getRootCause(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    public static Namespace parse(Reader reader) throws IOException {
        OncRpcParser parser = new OncRpcParser();
        return parser.parse(reader, "Test");
    }

    public static Namespace parse(InputStream inputStream) throws IOException {
        return parse(new InputStreamReader(inputStream));
    }

    public static Namespace parse(String oncrpc) {
        try {
            return parse(new StringReader(oncrpc));
        } catch (IOException e) {
            throw new IllegalStateException(e); //should never happen
        }
    }

    public static Namespace parse(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            return parse(reader);
        }
    }
}
