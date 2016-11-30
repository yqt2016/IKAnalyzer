package org.wltea.analyzer.lucene;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class PinYinTokenizer extends Tokenizer {

	// IK分词器实现
	private IKSegmenter _IKImplement;

	// 词元文本属性
	private final CharTermAttribute termAtt;
	// 词元位移属性
	private final OffsetAttribute offsetAtt;
	// 词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）
	private final TypeAttribute typeAtt;
	// 记录最后一个词元的结束位置
	private int endPosition;
	private final StringBuilder sb = new StringBuilder();
	private final char[] buffer = new char[8192];
	private boolean isLegal;
	private int index;
	private Matcher matcher;
	private Pattern pattern;
	public String pinyinSegRegEx = "[^aoeiuv]?h?[iuv]?(ai|ei|ao|ou|er|ang?|eng?|ong|a|o|e|i|u|ng|n)?";
	public String pinyinRegEx = "(a[io]?|ou?|e[inr]?|ang?|ng|[bmp](a[io]?|[aei]ng?|ei|ie?|ia[no]|o|u)|pou|me|m[io]u|[fw](a|[ae]ng?|ei|o|u)|fou|wai|[dt](a[io]?|an|e|[aeio]ng|ie?|ia[no]|ou|u[ino]?|uan)|dei|diu|[nl](a[io]?|ei?|[eio]ng|i[eu]?|i?ang?|iao|in|ou|u[eo]?|ve?|uan)|nen|lia|lun|[ghk](a[io]?|[ae]ng?|e|ong|ou|u[aino]?|uai|uang?)|[gh]ei|[jqx](i(ao?|ang?|e|ng?|ong|u)?|u[en]?|uan)|([csz]h?|r)([ae]ng?|ao|e|i|ou|u[ino]?|uan)|[csz](ai?|ong)|[csz]h(ai?|uai|uang)|zei|[sz]hua|([cz]h|r)ong|y(ao?|[ai]ng?|e|i|ong|ou|u[en]?|uan)|\\s)+";

	public PinYinTokenizer() {
		super();
		pattern = Pattern.compile(pinyinSegRegEx);
		matcher = pattern.matcher("");
		offsetAtt = addAttribute(OffsetAttribute.class);
		termAtt = addAttribute(CharTermAttribute.class);
		typeAtt = addAttribute(TypeAttribute.class);
		_IKImplement = new IKSegmenter(input, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.analysis.TokenStream#incrementToken()
	 */
	@Override
	public boolean incrementToken() throws IOException {

		// 清除所有的词元属性
		clearAttributes();
		if (isLegal) {
			if (index >= sb.length())
				return false;
			while (matcher.find()) {
				index = matcher.start(0);
				final int endIndex = matcher.end(0);
				if (index == endIndex)
					continue;
				termAtt.setEmpty().append(sb, index, endIndex);
				offsetAtt.setOffset(correctOffset(index), correctOffset(endIndex));
				return true;
			}
			index = Integer.MAX_VALUE; // mark exhausted
			return false;
		} else {
			Lexeme nextLexeme = _IKImplement.next();
			if (nextLexeme != null) {
				// 将Lexeme转成Attributes
				// 设置词元文本
				termAtt.append(nextLexeme.getLexemeText());
				// 设置词元长度
				termAtt.setLength(nextLexeme.getLength());
				// 设置词元位移
				offsetAtt.setOffset(nextLexeme.getBeginPosition(), nextLexeme.getEndPosition());
				// 记录分词的最后位置
				endPosition = nextLexeme.getEndPosition();
				// 记录词元分类
				typeAtt.setType(nextLexeme.getLexemeTypeString());
				// 返会true告知还有下个词元
				return true;
			}
			// 返会false告知词元输出完毕
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.analysis.Tokenizer#reset(java.io.Reader)
	 */
	@Override
	public void reset() throws IOException {
		super.reset();
		fillBuffer(sb, this.input);
		String str = sb.toString();
		isLegal = str.matches(pinyinRegEx);
		if (isLegal) {
			matcher.reset(str);
			index = 0;
		} else {
			_IKImplement.reset(new StringReader(str));
		}
	}

	private void fillBuffer(StringBuilder sb, Reader input) throws IOException {
		int len;
		sb.setLength(0);
		while ((len = input.read(buffer)) > 0) {
			sb.append(buffer, 0, len);
		}
		input.close();
	}

	@Override
	public final void end() {
		int finalOffset = correctOffset(this.endPosition);
		offsetAtt.setOffset(finalOffset, finalOffset);
	}
}
