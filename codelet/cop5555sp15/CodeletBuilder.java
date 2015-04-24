package cop5555sp15;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.List;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;


import cop5555sp15.ast.ASTNode;
import cop5555sp15.ast.CodeGenVisitor;
import cop5555sp15.ast.Program;
import cop5555sp15.ast.TypeCheckVisitor;
import cop5555sp15.ast.TypeCheckVisitor.TypeCheckException;
import cop5555sp15.symbolTable.SymbolTable;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;







public class CodeletBuilder {

    public static class DynamicClassLoader extends ClassLoader {
        public DynamicClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> define(String className, byte[] bytecode) {
            return super.defineClass(className, bytecode, 0, bytecode.length);
        }
    };    

    private static ASTNode typeCheckCorrectAST(ASTNode ast) throws Exception {
		SymbolTable symbolTable = new SymbolTable();
		TypeCheckVisitor v = new TypeCheckVisitor(symbolTable);
		try {
			ast.visit(v, null);
		} catch (TypeCheckException e) {
			System.out.println(e.getMessage());
		}
		return ast;
    }

    private static ASTNode parseCorrectInput(String input) {
		TokenStream stream = new TokenStream(input);
		Scanner scanner = new Scanner(stream);
		scanner.scan();
		Parser parser = new Parser(stream);
		System.out.println();
		ASTNode ast = parser.parse();
		if (ast == null) {
			System.out.println("errors " + parser.getErrors());
		}
		return ast;
	}

    public static void dumpBytecode(byte[] bytecode){   
	int flags = ClassReader.SKIP_DEBUG;
	ClassReader cr;
	cr = new ClassReader(bytecode); 
	cr.accept(new TraceClassVisitor(new PrintWriter(System.out)), flags);
    }

    private static byte[] generateByteCode(ASTNode ast) throws Exception {
	CodeGenVisitor v = new CodeGenVisitor();
	byte[] bytecode = (byte[]) ast.visit(v, null);
	dumpBytecode(bytecode);
	return bytecode;
    }

    public static Codelet newInstance(String source) throws Exception{
	Program program = (Program) parseCorrectInput(source);

    	typeCheckCorrectAST(program);
	byte[] bytecode = generateByteCode(program);
	String name = program.JVMName;
	DynamicClassLoader loader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
	Class<?> testClass = loader.define(name, bytecode);
        return (Codelet) testClass.newInstance();
    }
    public static Codelet newInstance(File file) throws Exception {
	
	byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
	String source = new String(encoded, Charset.defaultCharset());
	Program program = (Program) parseCorrectInput(source);

    	typeCheckCorrectAST(program);
	byte[] bytecode = generateByteCode(program);
	String name = program.JVMName;
	DynamicClassLoader loader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
	Class<?> testClass = loader.define(name, bytecode);
        return (Codelet) testClass.newInstance();
    }
    @SuppressWarnings("rawtypes")
    public static List getList(Codelet codelet, String name) throws Exception{
	Class<? extends Codelet> codeletClass = codelet.getClass();
	Field l1Field = codeletClass.getDeclaredField(name);
	l1Field.setAccessible(true);
	List i = (List) l1Field.get(codelet);
	return i;
    }
    public static int getInt(Codelet codelet, String name) throws Exception{
	Class<? extends Codelet> codeletClass = codelet.getClass();
	Field l1Field = codeletClass.getDeclaredField(name);
	l1Field.setAccessible(true);
	int i = (int) l1Field.get(codelet);
	return i;
    }

    public static void setInt(Codelet codelet, String name, int value) throws
	Exception{
	Class<? extends Codelet> codeletClass = codelet.getClass();
	Field l1Field = codeletClass.getDeclaredField(name);
	l1Field.setAccessible(true);
	l1Field.set(codelet, value);
    }

    public static String getString(Codelet codelet, String name) throws Exception{
    	Class<? extends Codelet> codeletClass = codelet.getClass();
	Field l1Field = codeletClass.getDeclaredField(name);
	l1Field.setAccessible(true);
	String i = (String) l1Field.get(codelet);
	return i;
    }
    public static void setString(Codelet codelet, String name, String value)
    	throws Exception{
    	Class<? extends Codelet> codeletClass = codelet.getClass();
	Field l1Field = codeletClass.getDeclaredField(name);
	l1Field.setAccessible(true);
	l1Field.set(codelet, value);
    }
    public static boolean getBoolean(Codelet codelet, String name) throws
    	Exception{
    	Class<? extends Codelet> codeletClass = codelet.getClass();
	Field l1Field = codeletClass.getDeclaredField(name);
	l1Field.setAccessible(true);
	boolean i = (boolean) l1Field.get(codelet);
	return i;
    }
    public static void setBoolean(Codelet codelet, String name, boolean value)
    	throws Exception{
    	Class<? extends Codelet> codeletClass = codelet.getClass();
	Field l1Field = codeletClass.getDeclaredField(name);
	l1Field.setAccessible(true);
	l1Field.set(codelet, value);
    }
}
