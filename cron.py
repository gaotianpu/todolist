#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
import urllib
import urllib2
import json 
import collections
import math
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
import config
import da

def segment(chinese_text,word_tag=0): 
    _SEGMENT_BASE_URL = 'http://segment.sae.sina.com.cn/urlclient.php'  
    if  config.debug:   
        _SEGMENT_BASE_URL = "http://ftodo.sinaapp.com/segment" 

    payload = urllib.urlencode([('context', chinese_text),])
    args = urllib.urlencode([('word_tag', word_tag), ('encoding', 'UTF-8'),])
    url = _SEGMENT_BASE_URL + '?' + args     
    result = urllib2.urlopen(url, payload).read()
    return result #json.loads(result)

def get_term_count(chinese_text):
    tmp = segment(chinese_text)
    if not tmp: return ''
    words = json.loads(tmp)
    word_count_set = collections.Counter([w['word'] for w in words])
    return dict(word_count_set)

def parse_term_count(str_term_count):
    rows = [line.split(' ') for line in str_term_count.split('\n')]
    return rows 
         
def update_term_count(subject):
    word_count_set = get_term_count(subject.body)
    str_term_count = '\n'.join(['%s %s'%(k,v) for k,v in dict(word_count_set).items()])
    da.subject.update(subject.pk_id,terms=str_term_count)

def test():
    rows = True
    page_index = 2
    page_size = 100
    terms = {}
    doc_count = 0
    while rows:
        rows = da.subject.load_all(page_index*page_size,page_size)
        for r in rows: 
            termsl = parse_term_count(r.terms)    
            # update_term_count(r)           
            for t in termsl:
                if t[0] in terms:
                    terms[t[0]] = terms[t[0]] + 1
                else:
                    terms[t[0]] = 1  
        doc_count = doc_count + len(rows) 
        page_index = page_index + 1

    #update idf        
    set_new = set([k for k,v in terms.items()]) 
    set_old = set(da.termdoc.load_all())
    set_insert = set_new-set_old
    da.termdoc.insert(list(set_insert))
    for k,v in terms.items():
        if k in set_old:
            idf = math.log(float(doc_count)/(v+1))
            da.termdoc.update(k,v,idf)  


if __name__ == "__main__":
    test()
    # ctf("中文分词指的是将一个汉字序列切分成一个一个单独的词。中文分词是文本挖掘的基础，对于输入的一段中文，成功的进行中文分词，可以达到电脑自动识别语句含义的效果。SAE分词系统基于隐马模型开发出的汉语分析系統，主要功能包括中文分词、词性标注、命名实体识别、新词识别。")