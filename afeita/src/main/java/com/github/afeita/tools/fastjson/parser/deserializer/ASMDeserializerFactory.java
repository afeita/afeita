package com.github.afeita.tools.fastjson.parser.deserializer;

import static com.github.afeita.tools.fastjson.util.ASMUtils.getDesc;
import static com.github.afeita.tools.fastjson.util.ASMUtils.getType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.github.afeita.tools.fastjson.asm.ClassWriter;
import com.github.afeita.tools.fastjson.asm.FieldVisitor;
import com.github.afeita.tools.fastjson.asm.Label;
import com.github.afeita.tools.fastjson.asm.MethodVisitor;
import com.github.afeita.tools.fastjson.asm.Opcodes;
import com.github.afeita.tools.fastjson.parser.DefaultExtJSONParser;
import com.github.afeita.tools.fastjson.parser.DefaultJSONParser;
import com.github.afeita.tools.fastjson.parser.Feature;
import com.github.afeita.tools.fastjson.parser.JSONLexer;
import com.github.afeita.tools.fastjson.parser.JSONScanner;
import com.github.afeita.tools.fastjson.parser.JSONToken;
import com.github.afeita.tools.fastjson.parser.ParserConfig;
import com.github.afeita.tools.fastjson.parser.SymbolTable;
import com.github.afeita.tools.fastjson.parser.deserializer.ASMJavaBeanDeserializer.InnerJavaBeanDeserializer;
import com.github.afeita.tools.fastjson.util.ASMClassLoader;
import com.github.afeita.tools.fastjson.util.FieldInfo;

public class ASMDeserializerFactory implements Opcodes {

    private static final ASMDeserializerFactory instance    = new ASMDeserializerFactory();

    private ASMClassLoader                      classLoader = new ASMClassLoader();

    private final AtomicLong                    seed        = new AtomicLong();

    public String getGenClassName(Class<?> clazz) {
        return "Fastjson_ASM_" + clazz.getSimpleName() + "_" + seed.incrementAndGet();
    }

    public String getGenFieldDeserializer(Class<?> clazz, FieldInfo fieldInfo) {
        Method method = fieldInfo.getMethod();
        return "Fastjson_ASM__Field_" + clazz.getSimpleName() + "_" + method.getName() + "_" + seed.incrementAndGet();
    }

    public ASMDeserializerFactory(){

    }

    public final static ASMDeserializerFactory getInstance() {
        return instance;
    }

    public ObjectDeserializer createJavaBeanDeserializer(ParserConfig config, Class<?> clazz) throws Exception {
        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("not support type :" + clazz.getName());
        }

        String className = getGenClassName(clazz);

        ClassWriter cw = new ClassWriter();
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, getType(ASMJavaBeanDeserializer.class), null);

        List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();
        JavaBeanDeserializer.computeSetters(clazz, fieldInfoList);

        _init(cw, new Context(fieldInfoList, className, config, clazz, 3));
        _createInstance(cw, new Context(fieldInfoList, className, config, clazz, 3));
        _parseField(cw, new Context(fieldInfoList, className, config, clazz, 4));
        _deserialze(cw, new Context(fieldInfoList, className, config, clazz, 3));

        byte[] code = cw.toByteArray();

        // org.apache.commons.io.IOUtils.write(code, new
        // java.io.FileOutputStream("/usr/alibaba/workspace/fastjson-asm/target/classes/" + className + ".class"));

        Class<?> exampleClass = classLoader.defineClassPublic(className, code, 0, code.length);

        Constructor<?> constructor = exampleClass.getConstructor(ParserConfig.class, Class.class);
        Object instance = constructor.newInstance(config, clazz);

        return (ObjectDeserializer) instance;
    }

    void _deserialze(ClassWriter cw, Context context) {
        if (context.getFieldInfoList().size() == 0) {
            return;
        }

        for (FieldInfo fieldInfo : context.getFieldInfoList()) {
            Class<?> fieldClass = fieldInfo.getMethod().getParameterTypes()[0];
            Type fieldType = fieldInfo.getMethod().getGenericParameterTypes()[0];

            if (fieldClass == char.class) {
                return;
            }

            if (Collection.class.isAssignableFrom(fieldClass)) {
                if (fieldType instanceof ParameterizedType) {
                    Type itemType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
                    if (itemType instanceof Class) {
                        continue;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }

        Collections.sort(context.getFieldInfoList());

        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "deserialze", "(" + getDesc(DefaultExtJSONParser.class)
                                                                    + getDesc(Type.class) + ")Ljava/lang/Object;",
                                          null, null);

        Label reset_ = new Label();
        Label super_ = new Label();
        Label end_ = new Label();

        mw.visitVarInsn(ALOAD, 1);
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(DefaultJSONParser.class), "getLexer", "()" + getDesc(JSONLexer.class));
        mw.visitTypeInsn(CHECKCAST, getType(JSONScanner.class)); // cast
        mw.visitVarInsn(ASTORE, context.var("lexer"));

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitFieldInsn(GETSTATIC, getType(Feature.class), "SortFeidFastMatch", "L" + getType(Feature.class) + ";");
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "isEnabled", "(" + "L" + getType(Feature.class)
                                                                                   + ";" + ")Z");
        mw.visitJumpInsn(IFEQ, super_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "getBufferPosition", "()I");
        mw.visitVarInsn(ISTORE, context.var("mark"));

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "getCurrent", "()C");
        mw.visitVarInsn(ISTORE, context.var("mark_ch"));

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "token", "()I");
        mw.visitVarInsn(ISTORE, context.var("mark_token"));

        for (int i = 0, size = context.getFieldInfoList().size(); i < size; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);
            Class<?> fieldClass = fieldInfo.getMethod().getParameterTypes()[0];
            Type fieldType = fieldInfo.getMethod().getGenericParameterTypes()[0];

            mw.visitVarInsn(ALOAD, context.var("lexer"));
            mw.visitVarInsn(ALOAD, 0);
            mw.visitFieldInsn(GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
            if (fieldClass == boolean.class) {
                mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "scanFieldBoolean", "([C)Z");
                mw.visitVarInsn(ISTORE, context.var(fieldInfo.getName() + "_asm"));

            } else if (fieldClass == byte.class) {
                mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "scanFieldInt", "([C)I");
                mw.visitVarInsn(ISTORE, context.var(fieldInfo.getName() + "_asm"));

            } else if (fieldClass == short.class) {
                mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "scanFieldInt", "([C)I");
                mw.visitVarInsn(ISTORE, context.var(fieldInfo.getName() + "_asm"));

            } else if (fieldClass == int.class) {
                mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "scanFieldInt", "([C)I");
                mw.visitVarInsn(ISTORE, context.var(fieldInfo.getName() + "_asm"));

            } else if (fieldClass == long.class) {
                mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "scanFieldLong", "([C)J");
                mw.visitVarInsn(LSTORE, context.var(fieldInfo.getName() + "_asm", 2));

            } else if (fieldClass == float.class) {
                mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "scanFieldFloat", "([C)F");
                mw.visitVarInsn(FSTORE, context.var(fieldInfo.getName() + "_asm"));

            } else if (fieldClass == double.class) {
                mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "scanFieldDouble", "([C)D");
                mw.visitVarInsn(DSTORE, context.var(fieldInfo.getName() + "_asm", 2));

            } else if (fieldClass == String.class) {
                mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "scanFieldString",
                                   "([C)Ljava/lang/String;");
                mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));

            } else if (fieldClass.isEnum()) {
                Label enumNull_ = new Label();
                mw.visitInsn(ACONST_NULL);
                mw.visitTypeInsn(CHECKCAST, getType(fieldClass)); // cast
                mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));

                mw.visitVarInsn(ALOAD, 1);
                mw.visitMethodInsn(INVOKEVIRTUAL, getType(DefaultExtJSONParser.class), "getSymbolTable",
                                   "()" + getDesc(SymbolTable.class));

                mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "scanFieldSymbol",
                                   "([C" + getDesc(SymbolTable.class) + ")Ljava/lang/String;");
                mw.visitInsn(DUP);
                mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm_enumName"));

                mw.visitJumpInsn(IFNULL, enumNull_);
                mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm_enumName"));
                mw.visitMethodInsn(INVOKESTATIC, getType(fieldClass), "valueOf", "(Ljava/lang/String;)"
                                                                                 + getDesc(fieldClass));
                mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));
                mw.visitLabel(enumNull_);

            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                Class<?> itemType = (Class<?>) ((ParameterizedType) fieldType).getActualTypeArguments()[0];
                if (itemType == String.class) {
                    mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "scanFieldStringArray",
                                       "([C)" + getDesc(ArrayList.class));
                    mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));
                } else {
                    _deserialze_list_obj(context, mw, reset_, fieldInfo, fieldClass, itemType);

                    if (i == size - 1) {
                        _deserialize_endCheck(context, mw, reset_);
                    }
                    continue;
                }

            } else {
                _deserialze_obj(context, mw, reset_, fieldInfo, fieldClass);

                if (i == size - 1) {
                    _deserialize_endCheck(context, mw, reset_);
                }

                continue;
            }

            mw.visitVarInsn(ALOAD, context.var("lexer"));
            mw.visitFieldInsn(GETFIELD, getType(JSONScanner.class), "matchStat", "I");
            mw.visitFieldInsn(GETSTATIC, getType(JSONScanner.class), "NOT_MATCH", "I");
            mw.visitJumpInsn(IF_ICMPEQ, reset_);

            if (i == size - 1) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, getType(JSONScanner.class), "matchStat", "I");
                mw.visitFieldInsn(GETSTATIC, getType(JSONScanner.class), "END", "I");
                mw.visitJumpInsn(IF_ICMPNE, reset_);
            }
        }

        mw.visitTypeInsn(NEW, getType(context.getClazz()));
        mw.visitInsn(DUP);
        mw.visitMethodInsn(INVOKESPECIAL, getType(context.getClazz()), "<init>", "()V");
        mw.visitVarInsn(ASTORE, context.var("instance"));

        for (int i = 0, size = context.getFieldInfoList().size(); i < size; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);
            Class<?> fieldClass = fieldInfo.getMethod().getParameterTypes()[0];
            Type fieldType = fieldInfo.getMethod().getGenericParameterTypes()[0];

            mw.visitVarInsn(ALOAD, context.var("instance"));
            if (fieldClass == boolean.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == byte.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == short.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == int.class) {
                mw.visitVarInsn(ILOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == long.class) {
                mw.visitVarInsn(LLOAD, context.var(fieldInfo.getName() + "_asm"));
                mw.visitMethodInsn(INVOKEVIRTUAL, getType(context.getClazz()), fieldInfo.getMethod().getName(), "(J)V");
                continue;
            } else if (fieldClass == float.class) {
                mw.visitVarInsn(FLOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == double.class) {
                mw.visitVarInsn(DLOAD, context.var(fieldInfo.getName() + "_asm", 2));
            } else if (fieldClass == String.class) {
                mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass.isEnum()) {
                mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                Type itemType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
                if (itemType == String.class) {
                    mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
                    mw.visitTypeInsn(CHECKCAST, getType(fieldClass)); // cast
                } else {
                    mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
                }
            } else {
                mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
            }
            mw.visitMethodInsn(INVOKEVIRTUAL, getType(fieldInfo.getMethod().getDeclaringClass()),
                               fieldInfo.getMethod().getName(), getDesc(fieldInfo.getMethod()));
        }

        mw.visitVarInsn(ALOAD, context.var("instance"));
        mw.visitInsn(ARETURN);

        mw.visitLabel(reset_);

        // void reset(int mark, char mark_ch)
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitVarInsn(ILOAD, context.var("mark"));
        mw.visitVarInsn(ILOAD, context.var("mark_ch"));
        mw.visitVarInsn(ILOAD, context.var("mark_token"));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "reset", "(ICI)V");

        mw.visitLabel(super_);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitVarInsn(ALOAD, 1);
        mw.visitVarInsn(ALOAD, 2);
        mw.visitMethodInsn(INVOKESPECIAL, getType(ASMJavaBeanDeserializer.class), "deserialze",
                           "(" + getDesc(DefaultExtJSONParser.class) + getDesc(Type.class) + ")Ljava/lang/Object;");
        mw.visitInsn(ARETURN);

        mw.visitLabel(end_);

        mw.visitMaxs(4, context.getVariantCount());
        mw.visitEnd();
    }

    private void _deserialize_endCheck(Context context, MethodVisitor mw, Label reset_) {
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "token", "()I");
        mw.visitFieldInsn(GETSTATIC, getType(JSONToken.class), "RBRACE", "I");
        mw.visitJumpInsn(IF_ICMPNE, reset_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitFieldInsn(GETSTATIC, getType(JSONToken.class), "COMMA", "I");
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "nextToken", "(I)V");
    }

    private void _deserialze_list_obj(Context context, MethodVisitor mw, Label reset_, FieldInfo fieldInfo,
                                      Class<?> fieldClass, Class<?> itemType) {
        // _asm_list_item_deser__

        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "matchField", "([C)Z");
        mw.visitJumpInsn(IFEQ, reset_);

        Label notNull_ = new Label();
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_list_item_deser__",
                          getDesc(ObjectDeserializer.class));
        mw.visitJumpInsn(IFNONNULL, notNull_);

        mw.visitVarInsn(ALOAD, 0);

        mw.visitVarInsn(ALOAD, 1);
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(DefaultExtJSONParser.class), "getConfig",
                           "()" + getDesc(ParserConfig.class));
        mw.visitLdcInsn(com.github.afeita.tools.fastjson.asm.Type.getType(getDesc(itemType)));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(ParserConfig.class), "getDeserializer",
                           "(" + getDesc(Type.class) + ")" + getDesc(ObjectDeserializer.class));

        mw.visitFieldInsn(PUTFIELD, context.getClassName(), fieldInfo.getName() + "_asm_list_item_deser__",
                          getDesc(ObjectDeserializer.class));

        mw.visitLabel(notNull_);

        // if (lexer.token() != JSONToken.LBRACKET) reset
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "token", "()I");
        mw.visitFieldInsn(GETSTATIC, getType(JSONToken.class), "LBRACKET", "I");
        mw.visitJumpInsn(IF_ICMPNE, reset_);

        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_list_item_deser__",
                          getDesc(ObjectDeserializer.class));
        mw.visitMethodInsn(INVOKEINTERFACE, getType(ObjectDeserializer.class), "getFastMatchToken", "()I");
        mw.visitVarInsn(ISTORE, context.var("fastMatchToken"));

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitVarInsn(ILOAD, context.var("fastMatchToken"));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "nextToken", "(I)V");

        mw.visitTypeInsn(NEW, getType(ArrayList.class));
        mw.visitInsn(DUP);
        mw.visitMethodInsn(INVOKESPECIAL, getType(ArrayList.class), "<init>", "()V");
        mw.visitTypeInsn(CHECKCAST, getType(fieldClass)); // cast
        mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));

        Label loop_ = new Label();
        Label loop_end_ = new Label();

        // for (;;) {
        mw.visitLabel(loop_);
        // if (lexer.isEnabled(Feature.AllowArbitraryCommas)) {
        // while (lexer.token() == JSONToken.COMMA) {
        // lexer.nextToken();
        // continue;
        // }
        // }
        // mw.visitVarInsn(ALOAD, context.var("lexer"));
        // mw.visitFieldInsn(GETSTATIC, getType(Feature.class), "AllowArbitraryCommas", "L" + getType(Feature.class) +
        // ";");
        // mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "isEnabled", "(" + "L" + getType(Feature.class)
        // + ";" + ")Z");

        // if (lexer.token() == JSONToken.RBRACKET) {
        // break;
        // }
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "token", "()I");
        mw.visitFieldInsn(GETSTATIC, getType(JSONToken.class), "RBRACKET", "I");
        mw.visitJumpInsn(IF_ICMPEQ, loop_end_);

        // Object value = itemDeserializer.deserialze(parser, null);
        // array.add(value);

        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_list_item_deser__",
                          getDesc(ObjectDeserializer.class));
        mw.visitVarInsn(ALOAD, 1);
        mw.visitInsn(ACONST_NULL);
        mw.visitMethodInsn(INVOKEINTERFACE, getType(ObjectDeserializer.class), "deserialze",
                           "(Lcom/alibaba/fastjson/parser/DefaultExtJSONParser;Ljava/lang/reflect/Type;)Ljava/lang/Object;");
        mw.visitVarInsn(ASTORE, context.var("list_item_value"));

        mw.visitVarInsn(ALOAD, context.var(fieldInfo.getName() + "_asm"));
        mw.visitVarInsn(ALOAD, context.var("list_item_value"));
        if (fieldClass.isInterface()) {
            mw.visitMethodInsn(INVOKEINTERFACE, getType(fieldClass), "add", "(Ljava/lang/Object;)Z");
        } else {
            mw.visitMethodInsn(INVOKEVIRTUAL, getType(fieldClass), "add", "(Ljava/lang/Object;)Z");
        }
        mw.visitInsn(POP);

        // if (lexer.token() == JSONToken.COMMA) {
        // lexer.nextToken(itemDeserializer.getFastMatchToken());
        // continue;
        // }
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "token", "()I");
        mw.visitFieldInsn(GETSTATIC, getType(JSONToken.class), "COMMA", "I");
        mw.visitJumpInsn(IF_ICMPNE, loop_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitVarInsn(ILOAD, context.var("fastMatchToken"));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "nextToken", "(I)V");
        mw.visitJumpInsn(GOTO, loop_);

        mw.visitLabel(loop_end_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "token", "()I");
        mw.visitFieldInsn(GETSTATIC, getType(JSONToken.class), "RBRACKET", "I");
        mw.visitJumpInsn(IF_ICMPNE, reset_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitFieldInsn(GETSTATIC, getType(JSONToken.class), "COMMA", "I");
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "nextToken", "(I)V");
        // lexer.nextToken(JSONToken.COMMA);

    }

    private void _deserialze_obj(Context context, MethodVisitor mw, Label reset_, FieldInfo fieldInfo,
                                 Class<?> fieldClass) {
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JSONScanner.class), "matchField", "([C)Z");
        mw.visitJumpInsn(IFEQ, reset_);

        Label notNull_ = new Label();
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_deser__",
                          getDesc(ObjectDeserializer.class));
        mw.visitJumpInsn(IFNONNULL, notNull_);

        mw.visitVarInsn(ALOAD, 0);

        mw.visitVarInsn(ALOAD, 1);
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(DefaultExtJSONParser.class), "getConfig",
                           "()" + getDesc(ParserConfig.class));
        mw.visitLdcInsn(com.github.afeita.tools.fastjson.asm.Type.getType(getDesc(fieldInfo.getMethod().getParameterTypes()[0])));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(ParserConfig.class), "getDeserializer",
                           "(" + getDesc(Type.class) + ")" + getDesc(ObjectDeserializer.class));

        mw.visitFieldInsn(PUTFIELD, context.getClassName(), fieldInfo.getName() + "_asm_deser__",
                          getDesc(ObjectDeserializer.class));

        mw.visitLabel(notNull_);

        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_deser__",
                          getDesc(ObjectDeserializer.class));
        mw.visitVarInsn(ALOAD, 1);
        mw.visitLdcInsn(com.github.afeita.tools.fastjson.asm.Type.getType(getDesc(fieldInfo.getMethod().getParameterTypes()[0])));
        mw.visitMethodInsn(INVOKEINTERFACE, getType(ObjectDeserializer.class), "deserialze",
                           "(" + getDesc(DefaultExtJSONParser.class) + getDesc(Type.class) + ")Ljava/lang/Object;");
        mw.visitTypeInsn(CHECKCAST, getType(fieldClass)); // cast
        mw.visitVarInsn(ASTORE, context.var(fieldInfo.getName() + "_asm"));

    }

    public FieldDeserializer createFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo)
                                                                                                               throws Exception {
        Class<?> fieldClass = fieldInfo.getFieldClass();

        if (fieldClass == int.class || fieldClass == long.class || fieldClass == String.class) {
            return createStringFieldDeserializer(mapping, clazz, fieldInfo);
        }

        FieldDeserializer fieldDeserializer = mapping.createFieldDeserializerWithoutASM(mapping, clazz, fieldInfo);
        return fieldDeserializer;
    }

    public FieldDeserializer createStringFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo)
                                                                                                                     throws Exception {
        Class<?> fieldClass = fieldInfo.getFieldClass();
        Method method = fieldInfo.getMethod();

        String className = getGenFieldDeserializer(clazz, fieldInfo);

        ClassWriter cw = new ClassWriter();
        Class<?> superClass;
        if (fieldClass == int.class) {
            superClass = IntegerFieldDeserializer.class;
        } else if (fieldClass == long.class) {
            superClass = LongFieldDeserializer.class;
        } else {
            superClass = StringFieldDeserializer.class;
        }

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, getType(superClass), null);

        {
            MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + getDesc(ParserConfig.class)
                                                                    + getDesc(Class.class) + getDesc(FieldInfo.class)
                                                                    + ")V", null, null);
            mw.visitVarInsn(ALOAD, 0);
            mw.visitVarInsn(ALOAD, 1);
            mw.visitVarInsn(ALOAD, 2);
            mw.visitVarInsn(ALOAD, 3);
            mw.visitMethodInsn(INVOKESPECIAL, getType(superClass), "<init>", "(" + getDesc(ParserConfig.class)
                                                                             + getDesc(Class.class)
                                                                             + getDesc(FieldInfo.class) + ")V");

            mw.visitInsn(RETURN);
            mw.visitMaxs(4, 6);
            mw.visitEnd();
        }

        if (fieldClass == int.class) {
            MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "setValue", "(" + getDesc(Object.class) + "I)V", null, null);
            mw.visitVarInsn(ALOAD, 1);
            mw.visitTypeInsn(CHECKCAST, getType(method.getDeclaringClass())); // cast
            mw.visitVarInsn(ILOAD, 2);
            mw.visitMethodInsn(INVOKEVIRTUAL, getType(method.getDeclaringClass()), method.getName(), "(I)V");

            mw.visitInsn(RETURN);
            mw.visitMaxs(3, 3);
            mw.visitEnd();
        } else if (fieldClass == long.class) {
            MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "setValue", "(" + getDesc(Object.class) + "J)V", null, null);
            mw.visitVarInsn(ALOAD, 1);
            mw.visitTypeInsn(CHECKCAST, getType(method.getDeclaringClass())); // cast
            mw.visitVarInsn(LLOAD, 2);
            mw.visitMethodInsn(INVOKEVIRTUAL, getType(method.getDeclaringClass()), method.getName(), "(J)V");

            mw.visitInsn(RETURN);
            mw.visitMaxs(3, 4);
            mw.visitEnd();
        } else {
            // public void setValue(Object object, Object value)
            MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "setValue", "(" + getDesc(Object.class)
                                                                      + getDesc(Object.class) + ")V", null, null);
            mw.visitVarInsn(ALOAD, 1);
            mw.visitTypeInsn(CHECKCAST, getType(method.getDeclaringClass())); // cast
            mw.visitVarInsn(ALOAD, 2);
            mw.visitTypeInsn(CHECKCAST, getType(fieldClass)); // cast
            mw.visitMethodInsn(INVOKEVIRTUAL, getType(method.getDeclaringClass()), method.getName(),
                               "(" + getDesc(fieldClass) + ")V");

            mw.visitInsn(RETURN);
            mw.visitMaxs(3, 3);
            mw.visitEnd();
        }

        byte[] code = cw.toByteArray();

        Class<?> exampleClass = classLoader.defineClassPublic(className, code, 0, code.length);

        Constructor<?> constructor = exampleClass.getConstructor(ParserConfig.class, Class.class, FieldInfo.class);
        Object instance = constructor.newInstance(mapping, clazz, fieldInfo);

        return (FieldDeserializer) instance;
    }

    static class Context {

        private int                  variantIndex = 4;

        private Map<String, Integer> variants     = new HashMap<String, Integer>();

        private Class<?>             clazz;
        private List<FieldInfo>      fieldInfoList;
        private String               className;

        public Context(List<FieldInfo> fieldInfoList, String className, ParserConfig config, Class<?> clazz,
                       int initVariantIndex){
            this.className = className;
            this.fieldInfoList = fieldInfoList;
            this.clazz = clazz;
            this.variantIndex = initVariantIndex;
        }

        public String getClassName() {
            return className;
        }

        public List<FieldInfo> getFieldInfoList() {
            return fieldInfoList;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public int getVariantCount() {
            return variantIndex;
        }

        public int var(String name, int increment) {
            Integer i = variants.get(name);
            if (i == null) {
                variants.put(name, variantIndex);
                variantIndex += increment;
            }
            i = variants.get(name);
            return i.intValue();
        }

        public int var(String name) {
            Integer i = variants.get(name);
            if (i == null) {
                variants.put(name, variantIndex++);
            }
            i = variants.get(name);
            return i.intValue();
        }
    }

    private void _init(ClassWriter cw, Context context) {
        for (int i = 0, size = context.getFieldInfoList().size(); i < size; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);

            // public FieldVisitor visitField(final int access, final String name, final String desc, final String
            // signature, final Object value) {
            FieldVisitor fw = cw.visitField(ACC_PUBLIC, fieldInfo.getName() + "_asm_prefix__", "[C");
            fw.visitEnd();
        }

        for (int i = 0, size = context.getFieldInfoList().size(); i < size; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);
            Class<?> fieldClass = fieldInfo.getMethod().getParameterTypes()[0];

            if (fieldClass.isPrimitive()) {
                continue;
            }

            if (fieldClass.isEnum()) {

            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                FieldVisitor fw = cw.visitField(ACC_PUBLIC, fieldInfo.getName() + "_asm_list_item_deser__",
                                                getDesc(ObjectDeserializer.class));
                fw.visitEnd();
            } else {
                FieldVisitor fw = cw.visitField(ACC_PUBLIC, fieldInfo.getName() + "_asm_deser__",
                                                getDesc(ObjectDeserializer.class));
                fw.visitEnd();
            }
        }

        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + getDesc(ParserConfig.class)
                                                                + getDesc(Class.class) + ")V", null, null);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitVarInsn(ALOAD, 1);
        mw.visitVarInsn(ALOAD, 2);
        mw.visitMethodInsn(INVOKESPECIAL, getType(ASMJavaBeanDeserializer.class), "<init>",
                           "(" + getDesc(ParserConfig.class) + getDesc(Class.class) + ")V");

        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, getType(ASMJavaBeanDeserializer.class), "serializer",
                          getDesc(InnerJavaBeanDeserializer.class));
        mw.visitMethodInsn(INVOKEVIRTUAL, getType(JavaBeanDeserializer.class), "getFieldDeserializerMap",
                           "()" + getDesc(Map.class));
        mw.visitInsn(POP);

        // init fieldNamePrefix
        for (int i = 0, size = context.getFieldInfoList().size(); i < size; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);

            mw.visitVarInsn(ALOAD, 0);
            mw.visitLdcInsn("\"" + fieldInfo.getName() + "\":"); // public char[] toCharArray()
            mw.visitMethodInsn(INVOKEVIRTUAL, getType(String.class), "toCharArray", "()" + getDesc(char[].class));
            mw.visitFieldInsn(PUTFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");

        }

        mw.visitInsn(RETURN);
        mw.visitMaxs(4, 4);
        mw.visitEnd();
    }

    private void _createInstance(ClassWriter cw, Context context) {
        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "createInstance", "(" + getDesc(DefaultExtJSONParser.class)
                                                                        + getDesc(Type.class) + ")Ljava/lang/Object;",
                                          null, null);
        mw.visitTypeInsn(NEW, getType(context.getClazz()));
        mw.visitInsn(DUP);
        mw.visitMethodInsn(INVOKESPECIAL, getType(context.getClazz()), "<init>", "()V");
        mw.visitInsn(ARETURN);
        mw.visitMaxs(3, 3);
        mw.visitEnd();
    }

    private void _parseField(ClassWriter cw, Context context) {
        // public boolean parseField(DefaultExtJSONParser parser, String key, Object object) {

        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "parseField", "(" + getDesc(DefaultExtJSONParser.class)
                                                                    + getDesc(String.class) + getDesc(Object.class)
                                                                    + ")Z", null, null);

        mw.visitVarInsn(ALOAD, 0);
        mw.visitVarInsn(ALOAD, 1);
        mw.visitVarInsn(ALOAD, 2);
        mw.visitVarInsn(ALOAD, 3);
        mw.visitMethodInsn(INVOKESPECIAL, getType(ASMJavaBeanDeserializer.class), "parseField",
                           "(" + getDesc(DefaultExtJSONParser.class) + getDesc(String.class) + getDesc(Object.class)
                                   + ")Z");
        mw.visitInsn(IRETURN);
        mw.visitMaxs(5, context.getVariantCount() + 1);
        mw.visitEnd();

    }

}
