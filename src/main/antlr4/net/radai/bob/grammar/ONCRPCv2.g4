grammar ONCRPCv2;

// parser rules

declaration:
    typeSpecifier IDENTIFIER
  | typeSpecifier IDENTIFIER '[' value ']'
  | typeSpecifier IDENTIFIER '<' value? '>'
  | 'opaque' IDENTIFIER '[' value ']'
  | 'opaque' IDENTIFIER '<' value? '>'
  | 'string' IDENTIFIER '<' value? '>'
  | typeSpecifier '*' IDENTIFIER
  | 'void'
;
value: constant | IDENTIFIER;
constant: DECIMAL | HEXADECIMAL | OCTAL;
typeSpecifier:
    'unsigned'? 'int'
  | 'unsigned'? 'hyper'
  | 'float'
  | 'double'
  | 'quadruple'
  | 'bool'
  | enumTypeSpec
  | structTypeSpec
  | unionTypeSpec
  | IDENTIFIER
;
enumTypeSpec: 'enum' enumBody;
enumBody: '{' (IDENTIFIER '=' value) (',' IDENTIFIER '=' value)* '}';
structTypeSpec: 'struct' structBody;
structBody: '{' (declaration ';') (declaration ';')* '}';
unionTypeSpec: 'union' unionBody;
unionBody: 'switch' '(' declaration ')' '{'
        caseSpec
        caseSpec*
        ('default' ':' declaration ';')?
    '}';
caseSpec: ('case' value ':') ('case' value ':')* declaration ';';
constantDef: 'const' IDENTIFIER '=' constant ';';
typeDef:
    'typedef' declaration ';'
  | 'enum' IDENTIFIER enumBody ';'
  | 'struct' IDENTIFIER structBody ';'
  | 'union' IDENTIFIER unionBody ';'
;
definition: typeDef | constantDef;
xdrSpecification: definition+; //this is the top level rule for xdr (rfc 4506)
//oncrpcv2 additions (rfc 5531)
programDef: 'program' IDENTIFIER '{'
        versionDef
        versionDef*
    '}' '=' constant ';';
versionDef: 'version' IDENTIFIER '{'
        procedureDef
        procedureDef*
    '}' '=' constant ';';
procedureDef: procReturn IDENTIFIER '(' procFirstArg (',' typeSpecifier)* ')' '=' constant ';';
procReturn: 'void' | typeSpecifier;
procFirstArg: 'void' | typeSpecifier;
oncrpcv2Specification : (xdrSpecification | programDef)*; //this is the top level rule for oncrpcv2 (rfc 5531)

// lexer rules

COMMENT : '/*' .*? '*/' -> skip;
OCTAL : '0' [1-7] ([0-7])*;
DECIMAL : ('-')? [1-9] ([0-9])*;
HEXADECIMAL : '0x' ([a-fA-F0-9])+;
IDENTIFIER : [a-zA-Z] ([a-zA-Z0-9_])*;
WS : [ \t\r\n]+ -> skip;