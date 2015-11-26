package net.radai.bob.codegen;

import net.radai.bob.model.Namespace;
import net.radai.bob.model.xdr.XdrConstant;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;

import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Radai Rosenblatt
 */
public class OncRpcGen {
    /**
     * @param namespace an oncrpc namespace to generate code for
     * @return a map of relative paths to file contents under each path
     */
    public Map<Path, String> generate(Namespace namespace) {

        List<Map.Entry<String, XdrConstant>> sortedConstants = new ArrayList<>(namespace.getConstants().entrySet());
        Collections.sort(sortedConstants, (e1, e2) -> e1.getKey().compareTo(e2.getKey())); //by name

        List<JavaType> generatedTypes = new ArrayList<>();
        JavaInterfaceSource constsClass = Roaster.create(JavaInterfaceSource.class);

        constsClass.setName(namespace.getName() + "Consts");
        for (Map.Entry<String, XdrConstant> constantEntry : sortedConstants) {
            String constantName = constantEntry.getKey();
            XdrConstant constant = constantEntry.getValue();
            FieldSource<JavaInterfaceSource> field = constsClass.addField().setName(constantName);
            if (constant.fitsInt()) {
                field.setType(int.class).setLiteralInitializer(constant.asInt() + "");
            } else if (constant.fitsLong()) {
                field.setType(long.class).setLiteralInitializer(constant.asLong() + "L");
            } else {
                field.setType(BigInteger.class).setLiteralInitializer("new BigInteger(\"" + constant.asBigInteger().toString() + "\")");
            }
        }
        generatedTypes.add(constsClass);

        Map<Path, String> javaFiles = new HashMap<>();
        for (JavaType generatedType : generatedTypes) {
            String fqcn = generatedType.getCanonicalName();
            String[] pathBits = fqcn.split("\\.");
            String first = pathBits[0];
            if (pathBits.length > 1) {
                pathBits = Arrays.copyOfRange(pathBits, 1, pathBits.length); //[1,len]
            } else {
                pathBits = null;
            }
            Path relativePath = pathBits != null ? Paths.get(first, pathBits) : Paths.get(first);
            String sourceCode = Roaster.format(constsClass.toUnformattedString());
            javaFiles.put(relativePath, sourceCode);
        }

        return javaFiles;
    }
}
