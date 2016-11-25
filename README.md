# 定制化IKAnalyzer，支持通常的IK分词，同义词搜索，拼音搜索，基于Lucene 4.10.4分词器接口
分别对应三个主要的分词类，IKAnalyzer,IKSynonymAnalyzer,PinYinAnalyzer
#IKAnalyzer
常规的IK分词器
#同义词类 IKSynonymAnalyzer
系统集成了一个同义词词典（如果系统不集成而用户又不配置的话，Lucene会报错），同时支持自定义词典，用户可以在代码里配置（addDicPath（）），默认路径基于ClassPath，同义词支持双向，忽略大小写。
#拼音搜索的类 PinYinAnalyzer
主要基于正则表达式完成对拼音字符串的判断、匹配、提取，如果用户输入的完整字符串不符合拼音的规则，则以默认的IK分词器代替。比如：zhangsanfeng是符合拼音规则的，拼音分词器分词的结果为zhang/san/feng；而zhangsf不符合拼音规则，分词的结果是zhangsf。空格不影响规则判断，比如zhang san feng也符合拼音规则的判断。

