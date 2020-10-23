/**
 * 
 */
package com.hong.entity;

/**
 * @author hong
 *
 */
public class WordEntity {
	private int id;
	private String word;
	private String meaning;
	private double searchCount;
	private double negativeCount;
	private double positiveCount;
	private double correctRate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getMeaning() {
		return meaning;
	}

	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}

	public double getSearchCount() {
		return searchCount;
	}

	public void setSearchCount(int searchCount) {
		this.searchCount = searchCount;
	}

	public double getNegativeCount() {
		return negativeCount;
	}

	public void setNegativeCount(double negativeCount) {
		this.negativeCount = negativeCount;
	}

	public double getPositiveCount() {
		return positiveCount;
	}

	public void setPositiveCount(double positiveCount) {
		this.positiveCount = positiveCount;
	}

	public double getCorrectRate() {
		if (searchCount == 0 || positiveCount == 0) {
			return 0D;
		} else {
			return positiveCount / searchCount;
		}
	}

	public void setCorrectRate(double correctRate) {
		this.correctRate = correctRate;
	}

	@Override
	public String toString() {
		return "WordEntity [id=" + id + ", word=" + word + ", meaning=" + meaning + ", searchCount=" + searchCount
				+ ", negativeCount=" + negativeCount + ", positiveCount=" + positiveCount + ", correctRate="
				+ correctRate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordEntity other = (WordEntity) obj;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}

}
