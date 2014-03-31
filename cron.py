#!/usr/bin/env python
# -*- coding: utf-8 -*-
import urllib
import urllib2
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
import config

def segment(chinese_text,word_tag): 
    _SEGMENT_BASE_URL = 'http://segment.sae.sina.com.cn/urlclient.php'  
    if  config.debug:   
        _SEGMENT_BASE_URL = "http://ftodo.sinaapp.com/segment" 

    payload = urllib.urlencode([('context', chinese_text),])
    args = urllib.urlencode([('word_tag', word_tag), ('encoding', 'UTF-8'),])
    url = _SEGMENT_BASE_URL + '?' + args     
    result = urllib2.urlopen(url, payload).read()
    return result


if __name__ == "__main__":
    print segment("中文分词指的是将一个汉字序列切分成一个一个单独的词。中文分词是文本挖掘的基础，对于输入的一段中文，成功的进行中文分词，可以达到电脑自动识别语句含义的效果。SAE分词系统基于隐马模型开发出的汉语分析系統，主要功能包括中文分词、词性标注、命名实体识别、新词识别。")