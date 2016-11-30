package org.wltea.analyzer.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class NGramAnalyzerTest {

	public NGramAnalyzerTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// 构建IK分词器，使用smart分词模式
		Analyzer analyzer = getGramAnalyzer();
		// Analyzer analyzer=new SimpleAnalyzer();

		// 获取Lucene的TokenStream对象
		TokenStream ts = null;
		try {
			ts = analyzer.tokenStream("textgrams", new StringReader("平方英里"));
			// 获取词元位置属性
			OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
			// 获取词元文本属性
			CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
			// 获取词元文本属性
			// TypeAttribute type = ts.addAttribute(TypeAttribute.class);
			PositionLengthAttribute length = ts.addAttribute(PositionLengthAttribute.class);
			PositionIncrementAttribute ince = ts.addAttribute(PositionIncrementAttribute.class);

			// 重置TokenStream（重置StringReader）
			ts.reset();
			// 迭代获取分词结果
			while (ts.incrementToken()) {
				System.out.println(offset.startOffset() + " - " + offset.endOffset() + " : " + term.toString() + "|"
						+ ince.getPositionIncrement() +"|"+ length.getPositionLength());
			}
			// 关闭TokenStream（关闭StringReader）
			ts.end(); // Perform end-of-stream operations, e.g. set the final
			System.out.println(offset.endOffset());
			// offset.

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (analyzer != null) {
				analyzer.close();
				analyzer = null;
			}
			// 释放TokenStream的所有资源

			if (ts != null) {
				try {
					ts.close();
					ts = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static Analyzer getGramAnalyzer() {
		return new AnalyzerWrapper(Analyzer.PER_FIELD_REUSE_STRATEGY) {
			@Override
			protected Analyzer getWrappedAnalyzer(String fieldName) {
				return new IKAnalyzer(true);
			}

			@Override
			protected TokenStreamComponents wrapComponents(String fieldName, TokenStreamComponents components) {
				if (fieldName.equals("textgrams") && 4 > 0) {

					return new TokenStreamComponents(components.getTokenizer(),
							new EdgeNGramTokenFilter(components.getTokenStream(), 1, 3));
				} else {
					return components;
				}
			}
		};
	}

}
