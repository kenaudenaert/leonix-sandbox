package be.leonix.sandbox.domain.mongo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
	
	@Override
	public ObjectId deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		TreeNode treeNode = parser.readValueAsTree();
		return new ObjectId(((JsonNode) treeNode).asText());
	}
}
