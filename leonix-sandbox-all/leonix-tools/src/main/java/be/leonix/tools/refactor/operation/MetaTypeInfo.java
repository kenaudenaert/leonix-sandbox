package be.leonix.tools.refactor.operation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;

/**
 * This class encapsulates the info about a data-model meta-type.
 * 
 * @author leonix
 */
public final class MetaTypeInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(MetaTypeInfo.class);
	
	// The identifier for the 'unique-id' data-model attribute.
	private static final String UNIQUE_ID = "UNIQUE_IDENTIFIER";
	
	// The set of ignored alternative names for constants.
	private static final Set<String> IGNORED_OVERRIDES = Set.of(UNIQUE_ID);
	
	// The set of ignored invalid (non-prefix) constants.
	private static final Set<String> IGNORED_NO_PREFIX = Set.of("roleField");
	
	private final File sourceFile;
	private final String className;
	private final String packageID;
	private final String keyPrefix;
	
	private final List<MetaTypeID> formulaIDs  = new ArrayList<>();
	private final List<MetaTypeID> constantIDs = new ArrayList<>();
	
	// The formulas (identifiers) by string value (literal).
	private final Map<String, String> formulas = new LinkedHashMap<>();
	
	// The constants (identifiers) by string value (literal).
	private final Map<String, String> constants = new LinkedHashMap<>();
	
	/**
	 * Creates meta-type-info from the specified meta-type source-file.
	 * 
	 * @param sourceFile The required (non-null) source-file.
	 */
	public MetaTypeInfo(File sourceFile) {
		this.sourceFile = Objects.requireNonNull(sourceFile);
		
		// Check the class-name of the meta-type.
		String fileName = sourceFile.getName();
		if (! fileName.endsWith("Meta.java")) {
			throw new RuntimeException("Invalid (filename) meta-type file: " + fileName);
		}
		className = StringUtils.removeEnd(fileName, ".java");
		
		logger.debug("Loading MetaType from: {}", sourceFile);
		try {
			CompilationUnit javaSource = StaticJavaParser.parse(sourceFile);
			packageID = javaSource.getPackageDeclaration().orElseThrow(
					() -> new RuntimeException("No package for meta-type.")).getNameAsString();
			
			// Get the class with the constants.
			TypeDeclaration<?> metaType = javaSource.getPrimaryType().orElseThrow(
					() -> new RuntimeException("No meta-type class found."));
			for (FieldDeclaration field : metaType.getFields()) {
				if (field.isPublic() && field.isStatic() && field.isFinal()) {
					for (VariableDeclarator variable : field.getVariables()) {
						String identifier = variable.getNameAsString();
						
						Expression value = variable.getInitializer().orElseThrow(
								() -> new RuntimeException("Missing value for final field."));
						if (value.isLiteralStringValueExpr()) {
							String literal = value.asLiteralStringValueExpr().getValue();
							
							MetaTypeID metaID = new MetaTypeID(identifier, literal);
							constantIDs.add(metaID);
							
							// Ensure unique identifier for a constant.
							String oldIdentifier = constants.put(literal, identifier);
							if (oldIdentifier != null) {
								if (IGNORED_OVERRIDES.contains(identifier)) {
									constants.put(literal, oldIdentifier);
								} else {
									throw new RuntimeException("Found constant override in " + className + ": " + identifier);
								}
							}
						}
					}
				}
			}
			
			// Get the class with the formulas.
			for (BodyDeclaration<?> member : metaType.getMembers()) {
				if (member.isClassOrInterfaceDeclaration()) {
					ClassOrInterfaceDeclaration memberType = member.asClassOrInterfaceDeclaration();
					if (memberType.isPublic() && memberType.getNameAsString().equals("Formulas")) {
						
						for (FieldDeclaration field : memberType.getFields()) {
							for (VariableDeclarator variable : field.getVariables()) {
								String identifier = variable.getNameAsString();
								
								Expression value = variable.getInitializer().orElseThrow(
										() -> new RuntimeException("Missing value for final field."));
								if (value.isLiteralStringValueExpr()) {
									String literal = value.asLiteralStringValueExpr().getValue();
									
									MetaTypeID metaID = new MetaTypeID(identifier, literal);
									formulaIDs.add(metaID);
									
									// Ensure unique identifier for a formula.
									String oldIdentifier = formulas.put(literal, identifier);
									if (oldIdentifier != null) {
										throw new RuntimeException("Found formula override in " + className + ": " + identifier);
									}
								}
							}
						}
					}
				}
			}
		} catch (IOException | RuntimeException ex) {
			throw new RuntimeException("Could not parse meta-type file: " + sourceFile, ex);
		}
		
		// Get the prefix and check literals for constants.
		List<String> keys = new ArrayList<>(constants.keySet());
		Collections.reverse(keys);
		String lastKey = keys.iterator().next();
		if (lastKey == null) {
			throw new RuntimeException("Invalid (no prefix) meta-type file: " + fileName);
		} else {
			keyPrefix = lastKey.substring(0, lastKey.indexOf('_'));
			if (keyPrefix.length() != 4) {
				throw new RuntimeException("Invalid (bad prefix) meta-type file: " + keyPrefix);
			}
			
			Iterator<String> iterator = constants.keySet().iterator();
			while (iterator.hasNext()) {
				String value = iterator.next();
				if (! value.startsWith(keyPrefix + "_")) {
					if (! IGNORED_NO_PREFIX.contains(value)) {
						throw new RuntimeException("Invalid (bad prefix) meta-type constant: " + value);
					}
					iterator.remove();
				}
			}
		}
	}
	
	public File getSourceFile() {
		return sourceFile;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getPackageID() {
		return packageID;
	}
	
	public String getKeyPrefix() {
		return keyPrefix;
	}
	
	public List<MetaTypeID> getFormulaIDs() {
		return Collections.unmodifiableList(formulaIDs);
	}
	
	public List<MetaTypeID> getConstantIDs() {
		return Collections.unmodifiableList(constantIDs);
	}
	
	public MetaTypeID getConstantID(String name) {
		for (MetaTypeID constantID : constantIDs) {
			if (constantID.getIdentifier().equals(name)) {
				return constantID;
			}
		}
		return null;
	}
	
	public MetaTypeID getUniqueID() {
		MetaTypeID standardUID = getConstantID(UNIQUE_ID);
		if (standardUID != null) {
			String alternative = constants.get(standardUID.getLiteral());
			if (alternative != null) {
				return getConstantID(alternative);
			}
		}
		return standardUID;
	}
	
	/**
	 * Returns the (read-only) formulas (identifiers) by their literal.
	 */
	public Map<String, String> getFormulas() {
		return Collections.unmodifiableMap(formulas);
	}
	
	/**
	 * Returns the (read-only) constants (identifiers) by their literal.
	 */
	public Map<String, String> getConstants() {
		return Collections.unmodifiableMap(constants);
	}
	
	public static void main(String[] args) {
		MetaTypeInfo metaInfo = new MetaTypeInfo(new File("/Users/leonix/Desktop/AvatarMeta.java"));
		for (MetaTypeID constantID : metaInfo.getConstantIDs()) {
			logger.info("Found constant: {} [{}]", constantID.getIdentifier(), constantID.getLiteral());
		}
		for (MetaTypeID formulaID : metaInfo.getFormulaIDs()) {
			logger.info("Found formula: {} [{}]", formulaID.getIdentifier(), formulaID.getLiteral());
		}
	}
}
