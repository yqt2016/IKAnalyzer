package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.Analyzer;

public class PinYinAnalyzer extends Analyzer {
	/**
	 * 重载Analyzer接口，构造分词组件
	 */
	@Override
	protected TokenStreamComponents createComponents(String fieldName) {

		return new TokenStreamComponents(new PinYinTokenizer());

	}

}
