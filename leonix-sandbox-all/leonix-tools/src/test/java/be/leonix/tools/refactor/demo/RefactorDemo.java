package be.leonix.tools.refactor.demo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.printer.YamlPrinter;

public class RefactorDemo {

	public static void refactorFile(File sourceFile) throws IOException {
		CompilationUnit compilationUnit = StaticJavaParser.parse(sourceFile);
		System.out.println("source=" + sourceFile);
		System.out.println("package=" + compilationUnit.getPackageDeclaration().orElseThrow().getNameAsString());

		// Change the visibility of all static fields from private to public.
		for (FieldDeclaration field : compilationUnit.findAll(FieldDeclaration.class)) {
			if (field.isStatic() && field.isPrivate()) {
				field.setPrivate(false);
				field.setPublic(true);
			}
		}

		FileUtils.write(sourceFile, compilationUnit.toString(), StandardCharsets.UTF_8);
		System.out.println(compilationUnit);
	}

	/**
	 * See https://javaparser.org/inspecting-an-ast/
	 */
	public static void printSyntaxTree(File sourceFile) throws IOException {
		CompilationUnit compilationUnit = StaticJavaParser.parse(sourceFile);
		YamlPrinter printer = new YamlPrinter(true);
		System.out.println(printer.output(compilationUnit));
	}

	public static void main(String[] args) {
		try {
			File sourceFile = new File("leonix-sandbox-all/leonix-tools/src/test/resources/be/leonix/tools/refactor/demo/HelloWorld.java");

			// Analyze the syntax tree
			printSyntaxTree(sourceFile);

			// Try out the refactoring
			refactorFile(sourceFile);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
