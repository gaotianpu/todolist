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
    return [[k,v] for k,v in dict(word_count_set).items()]

def update_term_count_by_id(subject_id):
    subject = da.subject.load_by_id(subject_id)
    update_term_count(subject)

def update_idf():
    #此版本的idf，每次需重新计算，不能增量计算?
    rows = True
    page_index = 0
    page_size = 100
    terms = {}  #全部数据都放在该容器中，数据量大的时候就挂掉了，计算term-doc idf, hadoop计算
    while rows:
        rows = da.subject.load_all(page_index*page_size,page_size)  #last_update > last_comput_tfidf ?          
        for r in rows:  
            termsl = parse_term_count(r.terms)  if r.terms else  update_term_count(r)  
            for t in termsl:
                if t[0] in terms:
                    terms[t[0]] = terms[t[0]] + 1
                else:
                    terms[t[0]] = 1   
        page_index = page_index + 1 

    #update idf        
    set_new = set([k for k,v in terms.items()])     
    set_old = set([r.term for r in  da.termdoc.load_all()])

    doc_count = da.subject.load_count()    

    #insert new terms            
    linsert = []
    for term in list(set_new-set_old):
        if term in terms:
            idf = math.log(float(doc_count)/(terms[term]+1))            
            linsert.append([term,terms[term],idf]) 
    da.termdoc.insert(linsert)
     
    #update exist terms
    for k,v in terms.items():
        if k in set_old:
            idf = math.log(float(doc_count)/(v+1))
            da.termdoc.update(k,v,idf)

def update_tf_idf():
    #compute tf-idf 
    term_idfs = {}
    terms_idf = da.termdoc.load_all()
    for t in terms_idf:  
        if not t.term.strip() : continue    
        term_idfs[t.term] = t.idf

    rows = True
    page_index = 0
    page_size = 100
    while rows:
        rows = da.subject.load_all(page_index*page_size,page_size)
        for r in rows: 
            termsl = parse_term_count(r.terms) 
            l=[]
            for t in termsl:                
                if not t[-1].strip():
                    print t 
                    continue
                if t[0] in term_idfs:
                    # print t[-1],t[0],term_idfs[t[0]]
                    tf_idf = int(t[-1])*term_idfs[t[0]] 
                    l.append([t[0],tf_idf])                     
                else:
                    print t[0]
                    pass
            # print l
            l.sort(cmp=lambda x,y : cmp(y[1], x[1]))
            da.subject.update(r.pk_id,tf_idf=' '.join(x[0] for x in l)) 

        page_index = page_index + 1

import math
def combination(n,k=2):
    return math.factorial(n) / math.factorial(n-k)/ math.factorial(k)
    
if __name__ == "__main__":     
    print combination(3)
    print combination(4)
    print combination(5)
    print combination(6)

    # update_idf()
    # update_tf_idf()

    # doc_count = da.subject.load_count()
    # print doc_count    
    # ctf("中文分词指的是将一个汉字序列切分成一个一个单独的词。中文分词是文本挖掘的基础，对于输入的一段中文，成功的进行中文分词，可以达到电脑自动识别语句含义的效果。SAE分词系统基于隐马模型开发出的汉语分析系統，主要功能包括中文分词、词性标注、命名实体识别、新词识别。")