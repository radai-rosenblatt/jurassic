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

package net.radai.bob.model.rpc;

import net.radai.bob.model.Identifiable;
import net.radai.bob.model.xdr.XdrType;
import net.radai.bob.model.xdr.XdrTypes;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Radai Rosenblatt
 */
public class RpcProcedure implements Identifiable {
    private String name;
    private XdrType returnType;
    private BigInteger procedureNumber;
    private List<XdrType> arguments = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public XdrType getReturnType() {
        return returnType;
    }

    public void setReturnType(XdrType returnType) {
        this.returnType = returnType;
    }

    public BigInteger getProcedureNumber() {
        return procedureNumber;
    }

    public void setProcedureNumber(BigInteger procedureNumber) {
        this.procedureNumber = procedureNumber;
    }

    public void add(XdrType argument) {
        XdrTypes type = argument.getType();
        if (type == XdrTypes.ENUM || type == XdrTypes.STRUCT || type == XdrTypes.UNION) {
            //grammatically valid, but ONCRPC doesnt support anonymous classes
            throw new IllegalArgumentException("attempt to define anonymous enum/struct/union as arg to method " + name);
        }
        arguments.add(argument);
    }

    @Override
    public String getIdentifier() {
        return getName();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType).append(" ").append(name).append("(");
        if (arguments != null && !arguments.isEmpty()) {
            for (XdrType arg : arguments) {
                sb.append(arg).append(", ");
            }
            sb.delete(sb.length()-2, sb.length()); //last ", "
        }
        sb.append(") = ").append(procedureNumber);
        return sb.toString();
    }
}
