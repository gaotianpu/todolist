#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web,urllib,urllib2,collections
import json
import da
import config
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

def segment(chinese_text,word_tag=1): 
    _SEGMENT_BASE_URL = 'http://segment.sae.sina.com.cn/urlclient.php'  
    if  config.debug:   
        _SEGMENT_BASE_URL = "http://ftodo.sinaapp.com/segment" 

    payload = urllib.urlencode([('context', chinese_text),])
    args = urllib.urlencode([('word_tag', word_tag), ('encoding', 'UTF-8'),])
    url = _SEGMENT_BASE_URL + '?' + args     
    result = urllib2.urlopen(url, payload).read()
    return json.loads(result)

def make_index(subject):
    #分词服务
    #subject = da.subject.load_by_id(subject_id)
    terms = segment(subject.body) 
    word_count_set = collections.Counter([w['word'] for w in terms if w['word'].strip()])
    uniq_terms = dict(word_count_set) 

    for term,count in uniq_terms.items():
        #写入public_terms
        term_id = da.search.term_insert(term)
        #写入term_doc
        da.search.term_doc_insert(subject.user_id,term_id,subject.pk_id,count) 

def run_make_index():
    (offset,page_size) = (0,100)
    while  True:
        rows = da.subject.load_all(offset,page_size)         
        if not rows:
            print "done"
            break
        for r in rows:
            make_index(r)
        offset = offset + page_size
    da.search.compute_tf_idf()

####------
def search(user_id,keywords):
    terms = segment(keywords)
    word_count_set = collections.Counter([w['word'] for w in terms if w['word'].strip()])
    uniq_terms = dict(word_count_set)
    rows = da.search.load_subjects(user_id,uniq_terms.keys())
    return rows
    

    

if __name__ == "__main__":
    #da.search.compute_tf_idf()
    search(1,u"日期格式化")
    # print segment(u"写所谓感悟和心得体会，对读者而言，是那种成功的心得体会更值得借鉴，还是失败的心得体会跟值得借鉴？我工作了快20年了，打工很长时间，而后在短短的几年里数次创业，而后，要么失败离场，要么成功了，却在权术斗争中黯然退出，看别人坐享果实，自己又从头开始。有时候想，也许自己更适合打工。")