# 定制化IKAnalyzer，支持通常的IK分词，同义词搜索，拼音搜索，基于Lucene 4.10.4分词器接口
分别对应三个主要的分词类，IKAnalyzer,IKSynonymAnalyzer,PinYinAnalyzer
#IKAnalyzer
常规的IK分词器
#同义词的类 IKSynonymAnalyzer
系统集成了一个同义词词典（如果不集成，用户又不配置的话，Lucene会报错），同时支持自定义词典，用户可以在代码里配置（addDicPath（）），默认的参数是是基于ClassPath参数，同义词是基于双向的，忽略大小写。
#拼音搜索的类 PinYinAnalyzer
主要基于正则表达式完成对拼音字符串的判断、匹配、提取，如果用户的输入的整个字符串不符合拼音的规则，则以默认的IK分词器代替。比如：zhangsanfeng是符合拼音规则的，拼音分词器分词的结果是zhang/san/feng；而zhangsf则不符合拼音规则，分词的结果是zhangsf，空格不影响规则的判断，比如zhang san feng也符合拼音规则的判断。

