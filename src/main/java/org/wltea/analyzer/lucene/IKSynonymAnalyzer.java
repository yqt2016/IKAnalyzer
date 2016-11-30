package org.wltea.analyzer.lucene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.util.ClasspathResourceLoader;
import org.apache.lucene.util.Version;

/**
 * IK同义词分词器，Lucene Analyzer接口实现 兼容Lucene 5.0版本
 */
public class IKSynonymAnalyzer extends Analyzer {
	private Version version = Version.LATEST;

	private boolean useSmart;

	public String getDicPath() {
		return dicPath;
	}

	public void setDicPath(String dicPath) {
		this.dicPath = dicPath;
	}

	public void addDicPath(String dicPath) {
		this.dicPath = this.dicPath + "," + dicPath;
	}

	private String dicPath = "org/wltea/analyzer/dic/syns.dic";

	public boolean useSmart() {
		return useSmart;
	}

	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}

	/**
	 * IK同义词分词器Lucene Analyzer接口实现类
	 * 
	 * 默认细粒度切分算法
	 */
	public IKSynonymAnalyzer() {
		this(false);
	}

	/**
	 * IK同义词分词器器Lucene Analyzer接口实现类
	 * 
	 * @param useSmart
	 *            当为true时，分词器进行智能切分
	 */
	public IKSynonymAnalyzer(boolean useSmart) {
		super();
		this.useSmart = useSmart;
	}

	/**
	 * 重载Analyzer接口，构造分词组件
	 */
	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer _IKTokenizer = new IKTokenizer(this.useSmart());
		Map<String, String> filterArgs = new HashMap<String, String>();
		filterArgs.put("luceneMatchVersion", version.toString());
		filterArgs.put("synonyms", dicPath);
		filterArgs.put("expand", "true");
		filterArgs.put("ignoreCase", "true");
		SynonymFilterFactory factory = new SynonymFilterFactory(filterArgs);
		try {
			factory.inform(new ClasspathResourceLoader());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new TokenStreamComponents(_IKTokenizer, factory.create(_IKTokenizer));
	}

}
