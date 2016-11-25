package org.wltea.analyzer.sample;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKSynonymAnalyzer;

public class LuceneSynIndexAndSearchDemo {

	public LuceneSynIndexAndSearchDemo() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// Lucene Document的域名
		String fieldName = "text";
		// 检索内容
		String text1 = "《传奇世界》荣获2016年度金翎奖最佳原创网络游戏";
		String text2 = "终生学习";

		// // 实例化IKAnalyzer分词器
		Analyzer indexanalyzer = new IKAnalyzer(true);
		Analyzer queryanalyzer = new IKSynonymAnalyzer(true);

		Directory directory = null;
		IndexWriter iwriter = null;
		IndexReader ireader = null;
		IndexSearcher isearcher = null;
		try {
			// 建立内存索引对象
			directory = new RAMDirectory();

			// 配置IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LATEST, indexanalyzer);
			iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			iwriter = new IndexWriter(directory, iwConfig);
			// 写入索引
			Document doc = new Document();
			FieldType ft = new FieldType(TextField.TYPE_NOT_STORED);
			ft.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
			ft.setStoreTermVectors(true);
			ft.setStoreTermVectorPositions(true);
			ft.setOmitNorms(true);

			doc.add(new StringField("ID", "10000", Field.Store.YES));
			doc.add(new TextField(fieldName, text1, Field.Store.YES));
			doc.add(new Field("tv", text1, ft));

			iwriter.addDocument(doc);
			doc = new Document();
			doc.add(new StringField("ID", "20000", Field.Store.YES));
			doc.add(new TextField(fieldName, text2, Field.Store.YES));
			doc.add(new Field("tv", text2, ft));

			iwriter.addDocument(doc);
			iwriter.close();

			// 搜索过程**********************************
			// 实例化搜索器
			ireader = DirectoryReader.open(directory);
			isearcher = new IndexSearcher(ireader);

			String keyword = "终生";
			// 使用QueryParser查询分析器构造Query对象
			QueryParser qp = new QueryParser(fieldName, queryanalyzer);
			qp.setDefaultOperator(QueryParser.OR_OPERATOR);
			// qp.setPhraseSlop(3);
			Query query = qp.parse(keyword);
			System.out.println("Query = " + query);

			// 搜索相似度最高的5条记录
			TopDocs topDocs = isearcher.search(query, 5);
			System.out.println("命中：" + topDocs.totalHits);
			// 输出结果
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (int i = 0; i < topDocs.totalHits; i++) {
				Document targetDoc = isearcher.doc(scoreDocs[i].doc);
				System.out.println("内容：" + targetDoc.toString());
			}

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (ireader != null) {
				try {
					ireader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
