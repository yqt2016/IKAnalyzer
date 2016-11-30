package org.wltea.analyzer.sample;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
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
import org.wltea.analyzer.lucene.PinYinAnalyzer;

public class LucenePinYinIndexAndSearchDemo {
	public static void main(String[] args) {
		// Lucene Document的域名
		String fieldName = "text";
		// 检索内容
		String text = "zheshiyigelizi";

		// 实例化IKAnalyzer分词器
		Analyzer analyzer = new PinYinAnalyzer();

		Directory directory = null;
		IndexWriter iwriter = null;
		IndexReader ireader = null;
		IndexSearcher isearcher = null;
		try {
			// 建立内存索引对象
			directory = new RAMDirectory();

			// 配置IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
			iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			iwriter = new IndexWriter(directory, iwConfig);
			// 写入索引
			Document doc = new Document();
			Field f1 = new StringField("ID", "", Field.Store.YES);
			Field f2 = new TextField(fieldName, "", Field.Store.YES);
			doc.add(f1);
			doc.add(f2);

			f1.setStringValue("10000");
			f2.setStringValue(text);
			System.out.println(doc);
			iwriter.addDocument(doc);
			text = "zheyeshiyigelizi";
			f1.setStringValue("20000");
			f2.setStringValue(text);
			System.out.println(doc);
			iwriter.addDocument(doc);
			text = "zheyoushiyigelizi";

			f1.setStringValue("30000");
			f2.setStringValue(text);
			System.out.println(doc);
			iwriter.addDocument(doc);

			// Document doc2 = new Document();
			// doc2.add(new StringField("ID", "20000", Field.Store.YES));
			// doc2.add(new TextField(fieldName, text, Field.Store.YES));
			// System.out.println(doc2);
			// iwriter.addDocument(doc2);
			// text = "zheyoushiyigelizi";
			// Document doc3 = new Document();
			// doc3.add(new StringField("ID", "30000", Field.Store.YES));
			// doc3.add(new TextField(fieldName, text, Field.Store.YES));
			// System.out.println(doc3);
			// iwriter.addDocument(doc3);
			//
			iwriter.close();

			// 搜索过程**********************************
			// 实例化搜索器
			ireader = DirectoryReader.open(directory);
			isearcher = new IndexSearcher(ireader);

			String keyword = "zhe";
			// 使用QueryParser查询分析器构造Query对象
			QueryParser qp = new QueryParser(fieldName, analyzer);
			// System.out.println(qp.getAnalyzer());
			qp.setDefaultOperator(QueryParser.OR_OPERATOR);
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
