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

package net.radai.bob.model.xdr;

import net.radai.bob.model.Identifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Radai Rosenblatt
 */
public class XdrDeclaration implements Identifiable {
    private List<DimensionConstraints> constraints = Collections.singletonList(new DimensionConstraints());
    private String identifier;
    private XdrType type;

    public boolean isOptional() {
        return constraints.get(0).optional;
    }

    public void setOptional(boolean optional) {
        constraints.get(0).optional = optional;
    }

    public boolean isArray() {
        return constraints.get(0).array;
    }

    public void setArray(boolean array) {
        constraints.get(0).array = array;
    }

    public boolean isFixedSize() {
        return constraints.get(0).fixedSize;
    }

    public void setFixedSize(boolean fixedSize) {
        constraints.get(0).fixedSize = fixedSize;
    }

    public XdrValue getSizeLimit() {
        return constraints.get(0).sizeLimit;
    }

    public void setSizeLimit(XdrValue sizeLimit) {
        constraints.get(0).sizeLimit = sizeLimit;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public XdrType getType() {
        return type;
    }

    public void setType(XdrType type) {
        this.type = type;
    }

    public int getDimensionality() {
        return constraints.size();
    }

    public XdrDeclaration getEffectiveDeclaration() {
        XdrDeclaration result = this;
        while (true) {
            XdrType resultType = result.getType();
            if (!(resultType instanceof XdrRefType)) {
                return result; //we hit a concrete type
            }
            XdrRefType refType = (XdrRefType) resultType;
            Identifiable resolved = refType.resolve();
            if (resolved == null) {
                throw new IllegalStateException("unable to resolve " + refType);
            }
            XdrDeclaration resolvedTo = (XdrDeclaration) resolved;
            result = result.combineWith(resolvedTo);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (identifier != null) {
            sb.append(identifier).append(": ");
        }
        sb.append(type.toString());
        for (int i=constraints.size()-1; i>=0; i--) {
            sb.append(constraints.get(i));
        }
        return sb.toString();
    }

    private XdrDeclaration combineWith(XdrDeclaration innerDecl) {
        XdrDeclaration result = new XdrDeclaration();
        result.identifier = this.identifier;
        result.type = innerDecl.type;
        result.constraints = new ArrayList<>(this.constraints);
        result.constraints.addAll(innerDecl.constraints);
        return result;
    }

    public static class DimensionConstraints {
        private boolean optional;
        private boolean array;
        private boolean fixedSize;
        private XdrValue sizeLimit;

        private void validate() throws IllegalArgumentException {
            if (optional) {
                if (array || fixedSize || sizeLimit != null) {
                    throw new IllegalArgumentException("an optional def cannot be combined with any array def");
                }
            } else if (array) {
                if (fixedSize) {
                    if (sizeLimit == null) {
                        throw new IllegalArgumentException("fixed size array must have a limit specified");
                    }
                }
            } else {
                if (fixedSize || sizeLimit!=null) {
                    throw new IllegalArgumentException("not an array yet size limits specified");
                }
            }
        }

        @Override
        public String toString() {
            if (optional) {
                return "*";
            }
            if (array) {
                if (fixedSize) {
                    return "[" + sizeLimit + "]"; //has to be a limit
                } else {
                    return "<" + (sizeLimit == null ? "" : sizeLimit) + ">";
                }
            }
            return "";
        }
    }
}
