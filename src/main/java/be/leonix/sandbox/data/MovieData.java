package be.leonix.sandbox.data;

import static be.leonix.sandbox.data.MovieDataMapping.DESCRIPTION;
import static be.leonix.sandbox.data.MovieDataMapping.TAGS;
import static be.leonix.sandbox.data.MovieDataMapping.TITLE;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author leonix
 */
public class MovieData {
	
	@JsonProperty(TITLE)
	private String title;
	@JsonProperty(DESCRIPTION)
	private String description;
	@JsonProperty(TAGS)
	private Set<String> tags;
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Set<String> getTags() {
		return tags;
	}
	
	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
}
