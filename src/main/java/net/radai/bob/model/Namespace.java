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

package net.radai.bob.model;

import net.radai.bob.model.rpc.RpcProgram;
import net.radai.bob.model.xdr.XdrConstant;
import net.radai.bob.model.xdr.XdrDeclaration;

import java.util.LinkedHashMap;

/**
 * not thread safe
 * @author Radai Rosenblatt
 */
public class Namespace implements Scope {
    private final String name;
    //preserve order of declarations
    private final LinkedHashMap<String, XdrConstant> constants = new LinkedHashMap<>();
    private final LinkedHashMap<String, XdrDeclaration> types = new LinkedHashMap<>();
    private final LinkedHashMap<String, RpcProgram> programs = new LinkedHashMap<>();

    public Namespace(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be null or empty");
        }
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    public void register(XdrConstant constant) {
        verifyUnused(constant.getIdentifier());
        constants.put(constant.getIdentifier(), constant);
    }

    public void register(XdrDeclaration type) {
        verifyUnused(type.getIdentifier());
        types.put(type.getIdentifier(), type);
    }

    public void register(RpcProgram program) {
        verifyUnused(program.getName());
        programs.put(program.getName(), program);
    }

    public LinkedHashMap<String, XdrConstant> getConstants() {
        return constants;
    }

    public LinkedHashMap<String, XdrDeclaration> getTypes() {
        return types;
    }

    public LinkedHashMap<String, RpcProgram> getPrograms() {
        return programs;
    }

    @Override
    public Identifiable resolve(String identifier) {
        XdrConstant constant = getConstant(identifier);
        if (constant != null) {
            return constant;
        }
        XdrDeclaration type = getType(identifier);
        if (type != null) {
            return type;
        }
        return getProgram(identifier);
    }

    @Override
    public Scope getParent() {
        return null; //top level
    }

    public XdrConstant getConstant(String name) {
        return constants.get(name);
    }

    public XdrDeclaration getType(String name) {
        return types.get(name);
    }

    public RpcProgram getProgram(String name) {
        return programs.get(name);
    }

    private void verifyUnused(String name) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("must provide a valid name");
        }
        if (resolve(name) != null) {
            throw new IllegalArgumentException("name " + name + " already defined within scope");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!constants.isEmpty()) {
            sb.append(constants.size()).append(" consts, ");
        }
        if (!types.isEmpty()) {
            sb.append(types.size()).append(" types, ");
        }
        if (!programs.isEmpty()) {
            sb.append(programs.size()).append(" programs, ");
        }
        if (sb.length() == 0) {
            return "empty";
        } else {
            sb.delete(sb.length()-2, sb.length()); //last ", "
        }
        return sb.toString();
    }
}
